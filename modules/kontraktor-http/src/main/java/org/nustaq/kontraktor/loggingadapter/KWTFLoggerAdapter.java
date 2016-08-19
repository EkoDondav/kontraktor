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
package org.nustaq.kontraktor.loggingadapter;

import org.nustaq.kontraktor.util.Log;
import org.slf4j.Logger;
import org.slf4j.Marker;

import java.util.Arrays;

/**
 * Created by ruedi on 11/06/15.
 */
public class KWTFLoggerAdapter implements Logger {

    String name;

    public KWTFLoggerAdapter(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isTraceEnabled() {
        return false;
    }

    @Override
    public void trace(String s) {
        Log.sDebug(s);
    }

    @Override
    public void trace(String s, Object o) {
        Log.sDebug(s + " " + o);
    }

    @Override
    public void trace(String s, Object o, Object o1) {
        Log.sDebug(s + " " + o + " " + o1); // wtf
    }

    @Override
    public void trace(String s, Object... objects) {
        Log.sDebug(s + " " + Arrays.toString(objects)); // wtf
    }

    @Override
    public void trace(String s, Throwable throwable) {
        Log.sInfo(s, throwable);
    }

    @Override
    public boolean isTraceEnabled(Marker marker) {
        return false;
    }

    @Override
    public void trace(Marker marker, String s) {

    }

    @Override
    public void trace(Marker marker, String s, Object o) {

    }

    @Override
    public void trace(Marker marker, String s, Object o, Object o1) {

    }

    @Override
    public void trace(Marker marker, String s, Object... objects) {

    }

    @Override
    public void trace(Marker marker, String s, Throwable throwable) {

    }

    @Override
    public boolean isDebugEnabled() {
        if ( ForwardDebug )
            return Log.Lg.getSeverity() < Log.INFO;
        return false;
    }

    public static boolean ForwardDebug = false;
    @Override
    public void debug(String s) {
        if ( ForwardDebug )
            Log.sDebug(name, s);
    }

    @Override
    public void debug(String s, Object o) {
        if ( ForwardDebug )
            Log.sDebug(name, s + " " + o);
    }

    @Override
    public void debug(String s, Object o, Object o1) {
        if ( ForwardDebug )
            Log.sDebug(name, s + " " + o + " " + o1);
    }

    @Override
    public void debug(String s, Object... objects) {
        if ( ForwardDebug )
            Log.sDebug(name, s + " " + Arrays.toString(objects));
    }

    @Override
    public void debug(String s, Throwable throwable) {
        if ( ForwardDebug )
            Log.sInfo(name, throwable, s);
    }

    @Override
    public boolean isDebugEnabled(Marker marker) {
        return false;
    }

    @Override
    public void debug(Marker marker, String s) {

    }

    @Override
    public void debug(Marker marker, String s, Object o) {

    }

    @Override
    public void debug(Marker marker, String s, Object o, Object o1) {

    }

    @Override
    public void debug(Marker marker, String s, Object... objects) {

    }

    @Override
    public void debug(Marker marker, String s, Throwable throwable) {

    }

    @Override
    public boolean isInfoEnabled() {
        return Log.Lg.getSeverity() <= Log.INFO;
    }

    @Override
    public void info(String s) {
        Log.sInfo(name, s);
    }

    @Override
    public void info(String s, Object o) {
        Log.sInfo(name, s + " " + o);
    }

    @Override
    public void info(String s, Object o, Object o1) {
        Log.sInfo(name, s + " " + o + ", " + o1);
    }

    @Override
    public void info(String s, Object... objects) {
        Log.sInfo(name, s + " " + Arrays.toString(objects));
    }

    @Override
    public void info(String s, Throwable throwable) {
        Log.sInfo(name, throwable, s);
    }

    @Override
    public boolean isInfoEnabled(Marker marker) {
        return false;
    }

    @Override
    public void info(Marker marker, String s) {

    }

    @Override
    public void info(Marker marker, String s, Object o) {

    }

    @Override
    public void info(Marker marker, String s, Object o, Object o1) {

    }

    @Override
    public void info(Marker marker, String s, Object... objects) {

    }

    @Override
    public void info(Marker marker, String s, Throwable throwable) {

    }

    @Override
    public boolean isWarnEnabled() {
        return Log.Lg.getSeverity() <= Log.WARN;
    }

    @Override
    public void warn(String s) {
        Log.sWarn(name, s);
    }

    @Override
    public void warn(String s, Object o) {
        Log.sWarn(name, s + " " + o);
    }

    @Override
    public void warn(String s, Object... objects) {
        Log.sWarn(name, s + " " + Arrays.toString(objects));
    }

    @Override
    public void warn(String s, Object o, Object o1) {
        Log.sWarn(name, s + " " + o + ", " + o1);
    }

    @Override
    public void warn(String s, Throwable throwable) {
        Log.sWarn(name, throwable, s);
    }

    @Override
    public boolean isWarnEnabled(Marker marker) {
        return false;
    }

    @Override
    public void warn(Marker marker, String s) {

    }

    @Override
    public void warn(Marker marker, String s, Object o) {

    }

    @Override
    public void warn(Marker marker, String s, Object o, Object o1) {

    }

    @Override
    public void warn(Marker marker, String s, Object... objects) {

    }

    @Override
    public void warn(Marker marker, String s, Throwable throwable) {

    }

    @Override
    public boolean isErrorEnabled() {
        return Log.Lg.getSeverity() <= Log.ERROR;
    }

    @Override
    public void error(String s) {
        Log.sError(name, s);
    }

    @Override
    public void error(String s, Object o) {
        Log.sError(name, s + " " + o);
    }

    @Override
    public void error(String s, Object o, Object o1) {
        Log.sError(name, s + " " + o + ", " + o1);
    }

    @Override
    public void error(String s, Object... objects) {
        Log.sError(name, s + " " + Arrays.toString(objects));
    }

    @Override
    public void error(String s, Throwable throwable) {
        Log.sError(name, throwable, s);
    }

    @Override
    public boolean isErrorEnabled(Marker marker) {
        return false;
    }

    @Override
    public void error(Marker marker, String s) {

    }

    @Override
    public void error(Marker marker, String s, Object o) {

    }

    @Override
    public void error(Marker marker, String s, Object o, Object o1) {

    }

    @Override
    public void error(Marker marker, String s, Object... objects) {

    }

    @Override
    public void error(Marker marker, String s, Throwable throwable) {

    }
}
