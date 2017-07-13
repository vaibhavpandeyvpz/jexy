package com.github.vaibhavpandeyvpz.jexy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

final class GobblerThread extends Thread {

    private final List<String> mOutput = new ArrayList<>();

    private final InputStream mStream;

    GobblerThread(InputStream stream) {
        mStream = stream;
    }

    List<String> getOutput() {
        return mOutput;
    }

    @Override
    public void run() {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(mStream));
            String line;
            while (null != (line = reader.readLine()))
                mOutput.add(line);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != reader) {
                try {
                    reader.close();
                } catch (IOException ignore) {
                }
            }
        }
    }
}
