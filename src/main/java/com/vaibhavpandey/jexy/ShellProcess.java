package com.vaibhavpandey.jexy;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ShellProcess {

    public static final int EXIT_CODE_START_FAILED = 126;

    private final String[] mCmdline;

    private List<String[]> mCommands;

    private File mWorkingDirectory;

    public ShellProcess(String... cmdline) {
        this(cmdline, null);
    }

    public ShellProcess(String[] cmdline, File cwd) {
        mCmdline = cmdline;
        mWorkingDirectory = cwd;
    }

    public void add(String... command) {
        if (null == mCommands) {
            mCommands = new ArrayList<>();
        }
        mCommands.add(command);
    }

    public int execute() {
        return execute(false).getExitCode();
    }

    public ExecutionResult execute(boolean stdout) {
        return execute(stdout, false);
    }

    public ExecutionResult execute(boolean stdout, boolean stderr) {
        int code = EXIT_CODE_START_FAILED;
        Process process;
        try {
            ProcessBuilder builder = new ProcessBuilder(mCmdline);
            if (null != mWorkingDirectory) {
                builder.directory(mWorkingDirectory);
            }
            process = builder.start();
        } catch (IOException e) {
            return new ExecutionResult(code);
        }
        Writer writer = null;
        GobblerThread STDERR = null, STDOUT = null;
        try {
            if (null != mCommands) {
                writer = new OutputStreamWriter(process.getOutputStream());
                for (String[] command : mCommands) {
                    writer.write(command[0]);
                    if (command.length > 1) {
                        for (int i = 1; i < command.length; i++)
                            writer.write(' ' + command[i]);
                    }
                    writer.write("\n");
                    writer.flush();
                }
                writer.close();
                writer = null;
            }
            if (stderr) {
                STDERR = new GobblerThread(process.getErrorStream());
                STDERR.start();
            }
            if (stdout) {
                STDOUT = new GobblerThread(process.getInputStream());
                STDOUT.start();
            }
            code = process.waitFor();
            process = null;
            if (null != STDERR) {
                STDERR.join();
            }
            if (null != STDOUT) {
                STDOUT.join();
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        } finally {
            if (null != writer) {
                try {
                    writer.close();
                } catch (Exception ignore) {
                }
            }
            if (null != process) {
                try {
                    process.destroy();
                } catch (Exception ignore) {
                }
            }
        }
        return new ExecutionResult(code,
                null != STDOUT ? STDOUT.getOutput() : null,
                null != STDERR ? STDERR.getOutput() : null);
    }
}
