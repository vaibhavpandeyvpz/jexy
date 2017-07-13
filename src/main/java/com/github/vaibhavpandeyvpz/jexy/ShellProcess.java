package com.github.vaibhavpandeyvpz.jexy;

import java.io.*;
import java.util.*;

public class ShellProcess {

    public static final int EXIT_CODE_START_FAILED = 126;

    private final String[] mArguments;

    private final String mCommand;

    private List<String> mCommands;

    private final File mWorkingDirectory;

    public ShellProcess(String command, String... args) {
        this(command, args, null);
    }

    public ShellProcess(String command, String[] args, File cwd) {
        mCommand = command;
        mArguments = args;
        mWorkingDirectory = cwd;
    }

    public void add(String command, String... args) {
        if (null == mCommands) {
            mCommands = new ArrayList<>();
        }
        StringBuilder cmdline = new StringBuilder(command);
        if (null != args) {
            for (String arg : args)
                cmdline.append(' ').append(arg);
        }
        mCommands.add(cmdline.toString());
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
            ProcessBuilder builder;
            if (null != mArguments) {
                List<String> cmdline = new ArrayList<>();
                cmdline.add(mCommand);
                cmdline.addAll(Arrays.asList(mArguments));
                builder = new ProcessBuilder(cmdline);
            } else {
                builder = new ProcessBuilder(mCommand);
            }
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
                for (String command : mCommands) {
                    writer.write(command);
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
