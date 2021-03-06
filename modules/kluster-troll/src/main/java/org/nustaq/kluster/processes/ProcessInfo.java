package org.nustaq.kluster.processes;

import java.io.Serializable;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Created by ruedi on 16/04/16.
 */
public class ProcessInfo implements Serializable {

    String id;
    String starterName;
    String starterId;
    String[] cmdLine;
    ProcStartSpec spec;
    transient Process proc;

    public Process getProc() {
        return proc;
    }

    public ProcessInfo proc(final Process proc) {
        this.proc = proc;
        return this;
    }

    public ProcStartSpec getSpec() {
        return spec;
    }

    public ProcessInfo spec(final ProcStartSpec spec) {
        this.spec = spec;
        return this;
    }

    public String getId() {
        return id;
    }

    public String[] getCmdLine() {
        return cmdLine;
    }

    public ProcessInfo cmdLine(final String cmdLine[]) {
        this.cmdLine = cmdLine;
        return this;
    }

    public ProcessInfo id(final String id) {
        this.id = id;
        return this;
    }

    public ProcessInfo starterName(final String starterName) {
        this.starterName = starterName;
        return this;
    }

    public ProcessInfo starterId(final String starterId) {
        this.starterId = starterId;
        return this;
    }

    public String getStarterId() {
        return starterId;
    }

    public String getStarterName() {
        return starterName;
    }

    @Override
    public String toString() {
        return "" +
            "" + starterName +
            " '" + Arrays.stream(cmdLine).collect(Collectors.joining(" ")) + "'" +
            " wd='" + spec.getWorkingDir() + '\'' +
            " vpid='" + id + '\'' +
            " sid='" + starterId + '\'';
    }
}
