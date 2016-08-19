/*
Kontraktor Copyright (c) Ruediger Moeller, All rights reserved.

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

package org.nustaq.kontraktor.remoting.encoding;

import org.nustaq.kontraktor.Callback;
import org.nustaq.kontraktor.remoting.base.RemoteRegistry;
import org.nustaq.kontraktor.remoting.base.ObjectSocket;
import org.nustaq.kontraktor.remoting.base.RemotedCallback;
import org.nustaq.kontraktor.util.Log;
import org.nustaq.serialization.FSTBasicObjectSerializer;
import org.nustaq.serialization.FSTClazzInfo;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;
import org.nustaq.serialization.util.*;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by ruedi on 09.08.14.
 */
public class CallbackRefSerializer extends FSTBasicObjectSerializer {

    RemoteRegistry reg;

    public CallbackRefSerializer(RemoteRegistry reg) {
        this.reg = reg;
    }

    @Override
    public void readObject(FSTObjectInput in, Object toRead, FSTClazzInfo clzInfo, FSTClazzInfo.FSTFieldInfo referencedBy) throws Exception {
    }

    public class MyRemotedCallback implements Callback, RemotedCallback {
        AtomicReference<ObjectSocket> chan;
        long id;

        public MyRemotedCallback(AtomicReference<ObjectSocket> chan, long id) {
            this.chan = chan;
            this.id = id;
        }

        public int getChanId() {
            return chan.get().getId();
        }

        public long getId() {
            return id;
        }

        @Override
        public void complete(Object result, Object error) {
            try {
                reg.receiveCBResult(chan.get(),id,result,error);
            } catch (Exception e) {
                Log.sWarn(this, e, "");
                FSTUtil.rethrow(e);
            }
        }

        @Override
        public boolean isTerminated() {
            boolean terminated = reg.isTerminated();
            if ( terminated )
                return true;
            boolean closed = chan.get().isClosed();
            if ( closed ) {
                Log.sError(this, "registry alive, but socket closed");
            }
            return closed;
        }

    }

    @Override
    public Object instantiate(Class objectClass, FSTObjectInput in, FSTClazzInfo serializationInfo, FSTClazzInfo.FSTFieldInfo referencee, int streamPositioin) throws Exception {
        // fixme: detect local actors returned from foreign
        long id = in.readLong();
        AtomicReference<ObjectSocket> chan = reg.getWriteObjectSocket();
        MyRemotedCallback cb = new MyRemotedCallback(chan, id);
        in.registerObject(cb, streamPositioin, serializationInfo, referencee);
        return cb;
    }

    @Override
    public void writeObject(FSTObjectOutput out, Object toWrite, FSTClazzInfo clzInfo, FSTClazzInfo.FSTFieldInfo referencedBy, int streamPosition) throws IOException {
        // fixme: catch republish of foreign actor
        long id = reg.registerPublishedCallback((Callback) toWrite); // register published host side
        out.writeLong(id);
    }

}
