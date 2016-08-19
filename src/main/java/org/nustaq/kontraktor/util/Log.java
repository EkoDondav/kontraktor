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

package org.nustaq.kontraktor.util;

import org.nustaq.kontraktor.Actor;
import org.nustaq.kontraktor.ActorProxy;
import org.nustaq.kontraktor.Actors;
import org.nustaq.kontraktor.annotations.CallerSideMethod;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ruedi on 24.08.14.
 *
 * A wrapper for logging + metrics. This logger facade is asynchronous (so does not block by IO).
 * In order to redirect logging, use Log.Lg.setLogWrapper( .. );
 * Note its possible to log to a remote host using kontraktor remoting as the core logging class
 * is an actor.
 */
public class Log extends Actor<Log> {

    public static final int DEBUG = 0;
    public static final int INFO = 1;
    public static final int WARN = 2;
    public static final int ERROR = 3;

    public static Log Lg = Actors.asActor(Log.class, 100000);

    public static void setSynchronous() {
        if ( Lg instanceof ActorProxy) { // only do once
            Log old = Lg;
            Lg = new Log();
            old.stop();
        }
    }

    /**
     * Sets the logging level to the specified value.
     *
     * @param level = Log.DEBUG | Log.INFO | Log.WARN | Log.ERROR
     * @return the previously set severity
     */
    public static int setLevel( int level ) {
        int oldSeverity = Lg.getSeverity();
        Lg.setSeverity(level);
        return oldSeverity;
    }
    public static void sInfo(Object source, String msg) {
        Lg.info(source, msg);
    }
    public static void sInfo(Object source, Throwable ex) {
        Lg.infoLong(source, ex, null);
    }
    public static void sDebug(String msg) {
        Lg.debug(null, msg);
    }
    public static void sDebug(Object source, String msg) {
        Lg.debug(source,msg);
    }
    public static void sDebug(Object source, Throwable th) {
        Lg.debugLong(source, th, null);
    }
    public static void sInfo(Object source, Throwable t, String msg) {
        Lg.infoLong(source,t,msg);
    }
    public static void sWarn(Object source, Throwable t, String msg) {
        Lg.warnLong(source,t,msg);
    }
    public static void sWarn(Object source, String msg) {
        Lg.warnLong(source,null,msg);
    }
    public static void sWarn(Object source, Throwable ex) {
        Lg.warnLong(source,ex,null);
    }
    public static void sError(Object source, String s) {
        sError(source, null, s);
    }
    public static void sError(Object source, Throwable th) {
        sError(source, th, null);
    }
    public static void sError(Object source, Throwable th, String s) {
        Lg.error(source,th,s);
    }

    public static interface LogWrapper {
        public void msg(Thread t, int severity, Object source, Throwable ex, String msg);
    }

    public LogWrapper defaultLogger = new LogWrapper() {
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss:SSS");
        boolean initialized = false;
        @Override
        public void msg(Thread t, int sev, Object source, Throwable ex, String msg) {
            if ( ! initialized ) {
                initialized = true;
                Thread.currentThread().setName("kontraktor async logger");
            }
            if ( severity <= sev ) {
                if ( source == null )
                    source = "null";
                else if ( source instanceof String == false ) {
                    if ( source instanceof Class ) {
                        source = ((Class) source).getName();
                    } else
                        source = source.getClass().getSimpleName();
                }
                String tname = t == null ? "-" : t.getName();
                String svString = "I ";
                switch (sev) {
                    case WARN: svString = "W "; break;
                    case DEBUG: svString = "D "; break;
                    case ERROR: svString = "E "; break;
                }
                System.out.println(svString+formatter.format(new Date())+" : "+ tname +" : "+source+" : "+msg);
                if ( ex != null ) {
                    if ( sev == INFO ) {
                        System.out.println(ex.toString());
                    } else {
                        ex.printStackTrace(System.out);
                    }
                }
            }
        }
    };

    LogWrapper logger = defaultLogger;

    volatile int severity = INFO;

    public void setLogWrapper(LogWrapper delegate) {
        this.logger = delegate;
    }


    public void setSeverity(int severity) {
        this.severity = severity;
    }

    @Override
    protected Log self() {
        if ( Lg instanceof ActorProxy)
            return super.self();
        else
            return this;
    }

    /////////////////////////////////////////////////////////////////////
    // caller side wrappers are here to enable stacktrace capture etc.
    //

    @CallerSideMethod public int getSeverity() {
        return getActor().severity;
    }

    @CallerSideMethod public void resetToSysout() {
        this.logger = defaultLogger;
    }

    @CallerSideMethod public void infoLong(Object source, Throwable ex, String msg) {
        self().msg(Thread.currentThread(), INFO, source, ex, msg);
    }

    @CallerSideMethod public void debug( Object source, String msg ) {
        self().msg(Thread.currentThread(), DEBUG, source, null, msg);
    }

    @CallerSideMethod public void debugLong( Object source, Throwable th, String msg ) {
        self().msg(Thread.currentThread(), DEBUG, source, th, msg);
    }

    @CallerSideMethod public void info( Object source, String msg ) {
        self().msg(Thread.currentThread(), INFO, source, null, msg);
    }

    @CallerSideMethod public void warnLong( Object source, Throwable ex, String msg ) {
        self().msg(Thread.currentThread(), WARN, source, ex, msg);
    }

    @CallerSideMethod public void warn( Object source, String msg ) {
        self().msg(Thread.currentThread(), WARN, source, null, msg);
    }

    @CallerSideMethod public void error( Object source, Throwable ex, String msg ) {
        self().msg(Thread.currentThread(), ERROR, source, ex, msg);
    }

    ////////////////////////////

    // async mother method
    public void msg( Thread t, int severity, Object source, Throwable ex, String msg ) {
        logger.msg( t, severity,source,ex,msg);
    }

}
