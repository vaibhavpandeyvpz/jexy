package com.vaibhavpandey.jexy;

import java.util.List;

final public class ExecutionResult {

    private final int mExitCode;

    private final List<String> mStdErr;

    private final List<String> mStdOut;

    ExecutionResult(int code) {
        this(code, null, null);
    }

    ExecutionResult(int code, List<String> stdout, List<String> stderr) {
        mExitCode = code;
        mStdOut = stdout;
        mStdErr = stderr;
    }

    public int getExitCode() {
        return mExitCode;
    }

    public List<String> getStdErr() {
        return mStdErr;
    }

    public List<String> getStdOut() {
        return mStdOut;
    }
}
