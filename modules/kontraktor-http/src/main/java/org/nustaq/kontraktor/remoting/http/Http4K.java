/*
Kontraktor-Http Copyright (c) Ruediger Moeller, All rights reserved.

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 3.0 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

See https://www.gnu.org/licenses/lgpl.txt
*/

package org.nustaq.kontraktor.remoting.http;

import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.encoding.EncodingHandler;
import io.undertow.server.handlers.resource.FileResourceManager;
import io.undertow.server.handlers.resource.ResourceHandler;
import org.nustaq.kontraktor.IPromise;
import org.nustaq.kontraktor.remoting.base.ActorServer;
import org.nustaq.kontraktor.remoting.http.builder.BldFourK;
import org.nustaq.kontraktor.remoting.http.javascript.DynamicResourceManager;
import org.nustaq.kontraktor.remoting.websockets.WebSocketPublisher;
import org.nustaq.kontraktor.util.Pair;

import javax.net.ssl.SSLContext;
import javax.xml.ws.spi.http.HttpExchange;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by ruedi on 25/05/15.
 *
 * singleton to manage http server instances. Currently tied to Undertow however implicitely shields kontraktor-http
 * from getting too dependent on Undertow (which is an excellent piece of software, so no plans to migrate anytime soon)
 *
 */
public class Http4K {

    public static int UNDERTOW_IO_THREADS = 8;
    public static int UNDERTOW_WORKER_THREADS = 8;
    protected static Http4K instance;
    public static void set(Http4K http) {
        instance = http;
    }

    public static Http4K get() {
        synchronized (Http4K.class) {
            if ( instance == null ) {
                System.setProperty("org.jboss.logging.provider","slf4j");
                instance = new Http4K();
            }
            return instance;
        }
    }

    public static BldFourK Build( String hostName, int port, SSLContext ctx ) {
        return get().builder(hostName,port,ctx);
    }

    public static BldFourK Build( String hostName, int port) {
        return get().builder(hostName, port, null);
    }

    // a map of port=>server
    protected Map<Integer, Pair<PathHandler,Undertow>> serverMap = new HashMap<>();

    /**
     * creates or gets an undertow web server instance mapped by port.
     * hostname must be given in case a new server instance has to be instantiated
     *
     * @param port
     * @param hostName
     * @return
     */
    public synchronized Pair<PathHandler, Undertow> getServer(int port, String hostName) {
        return getServer(port,hostName,null);
    }

    public synchronized Pair<PathHandler, Undertow> getServer(int port, String hostName, SSLContext context) {
        Pair<PathHandler, Undertow> pair = serverMap.get(port);
        if (pair == null) {
            PathHandler pathHandler = new PathHandler();
            Undertow.Builder builder = Undertow.builder()
                                           .setIoThreads(UNDERTOW_IO_THREADS)
                                           .setWorkerThreads(UNDERTOW_WORKER_THREADS);
            Undertow server = customize(builder,pathHandler,port,hostName,context).build();
            server.start();
            pair = new Pair<>(pathHandler,server);
            serverMap.put(port,pair);
        }
        return pair;
    }

    public BldFourK builder(String hostName, int port, SSLContext ctx) {
        return new BldFourK(hostName,port,ctx);
    }

    public BldFourK builder(String hostName, int port) {
        return new BldFourK(hostName,port,null);
    }

    protected Undertow.Builder customize(Undertow.Builder builder, PathHandler rootPathHandler, int port, String hostName, SSLContext context) {
        if ( context == null ) {
            return builder
                       .addHttpListener(port, hostName)
                       .setHandler(rootPathHandler);
        } else {
            return builder
                       .addHttpsListener(port,hostName,context)
                       .setHandler(rootPathHandler);
        }
    }

    /**
     * publishes given file root
     * @param hostName
     * @param urlPath - prefixPath (e.g. /myapp/resource)
     * @param port
     * @param root - directory to be published
     */
    public Http4K publishFileSystem( String hostName, String urlPath, int port, File root ) {
        if ( ! root.isDirectory() ) {
            throw new RuntimeException("root must be an existing direcory:"+root.getAbsolutePath());
        }
        Pair<PathHandler, Undertow> server = getServer(port, hostName);
        server.car().addPrefixPath(urlPath, new ResourceHandler(new FileResourceManager(root,100)));
        return this;
    }

    // FIXME: exposes Undertow class
    public Http4K publishFileSystem( String hostName, String urlPath, int port, FileResourceManager man ) {
        if ( ! man.getBase().isDirectory() ) {
            throw new RuntimeException("root must be an existing direcory:"+man.getBase().getAbsolutePath());
        }
        Pair<PathHandler, Undertow> server = getServer(port, hostName);
        server.car().addPrefixPath(urlPath, new ResourceHandler(man ));
        return this;
    }
    public Http4K publishResourcePath( String hostName, String urlPath, int port, DynamicResourceManager man, boolean compress ) {
        return publishResourcePath(hostName,urlPath,port,man,compress,null);
    }

    public Http4K publishResourcePath(String hostName, String urlPath, int port, DynamicResourceManager man, boolean compress, Function<HttpServerExchange,Boolean> interceptor ) {
        Pair<PathHandler, Undertow> server = getServer(port, hostName);
        ResourceHandler handler = new ResourceHandler(man);
        if ( compress ) {
            HttpHandler httpHandler = new EncodingHandler.Builder().build(new HashMap<>()).wrap(handler);
            if ( interceptor != null ) {
                server.car().addPrefixPath( urlPath, httpExchange -> {
                    boolean apply = interceptor.apply(httpExchange);
                    if ( ! apply ) {
                        httpHandler.handleRequest(httpExchange);
                    }
                });
            } else {
                server.car().addPrefixPath( urlPath, httpHandler);
            }
        } else {
            if ( interceptor != null ) {
                server.car().addPrefixPath( urlPath, httpExchange -> {
                    boolean apply = interceptor.apply(httpExchange);
                    if ( ! apply ) {
                        handler.handleRequest(httpExchange);
                    }
                });
            } else {
                server.car().addPrefixPath( urlPath, handler);
            }
        }
        return this;
    }

    /**
     * utility, just redirects to approriate connector
     *
     * Publishes an actor/service via websockets protocol with given encoding.
     * if this should be connectable from non-java code recommended coding is 'new Coding(SerializerType.JsonNoRefPretty)' (dev),
     * 'new Coding(SerializerType.JsonNoRef)' (production)
     *
     * SerializerType.FSTSer is the most effective for java to java communication.
     *
     */
    public IPromise<ActorServer> publish( WebSocketPublisher publisher ) {
        return publisher.publish();
    }

    /**
     * utility, just redirects to approriate connector.
     */
    public IPromise<ActorServer> publish( HttpPublisher publisher ) {
        return publisher.publish();
    }

    public Http4K publishHandler(String hostName, String urlPath, int port, HttpHandler handler) {
        Pair<PathHandler, Undertow> server = getServer(port, hostName);
        server.car().addPrefixPath( urlPath, handler);
        return this;
    }
}
