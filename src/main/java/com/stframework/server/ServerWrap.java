package com.stframework.server;

import com.stframework.core.Util;
import com.stframework.server.player.FakePlayer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ServerWrap {

    private Process process;
    private PrintWriter out;
    private List<String> processOutput;
    private StringBuilder lastOutputLine;

    public ServerWrap() throws Exception {
        File workingDir = new File(System.getProperty("user.dir"));
        String serverPath = getPathToServerJar(workingDir);
        if (serverPath == null) {
            serverPath = getPathToServerJar(new File(workingDir, "server"));
        }
        if (serverPath == null) {
            throw new RuntimeException("Could not find server jar");
        }
        startProcess(serverPath);
        readThread();
        inputThread();
        exitThread();
    }

    private String getPathToServerJar(File workingDir) {
        for (File file : workingDir.listFiles()) {
            String name = file.getName().toLowerCase();
            if (name.startsWith("spigot") && name.endsWith(".jar") && !name.contains("testing")) {
                return file.getAbsolutePath();
            }
        }

        return null;
    }

    private void startProcess(String serverPath) throws Exception {
        ProcessBuilder pb = new ProcessBuilder("java", "-jar", "\"" + serverPath + "\"");
        pb.directory(new File(serverPath).getParentFile());
        process = pb.start();
        out = new PrintWriter(process.getOutputStream());
        processOutput = new ArrayList<>();
        lastOutputLine = new StringBuilder();
    }

    private void readThread() {
        new Thread(() -> {
            InputStream in = process.getInputStream();
            while (true) {
                try {
                    while (in.available() > 0) {
                        char c = (char) in.read();
                        if (c == '\n') {
                            processOutput.add(lastOutputLine.toString());
                            System.out.println(lastOutputLine.toString());
                            lastOutputLine.setLength(0);
                        } else {
                            lastOutputLine.append(c);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void inputThread() {
        new Thread(() -> {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                String line;
                while((line = reader.readLine()) != null) {
                    //write(line);
                    new FakePlayer("bob");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void exitThread() {
        new Thread(() -> {
            try {
                process.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Detected process exit, stopping");
            System.exit(0);
        }).start();
    }

    public void write(String cmd) {
        out.println(cmd);
        out.flush();
    }

    public void waitForOutput(String output) {
        waitForOutput(output, 200);
    }

    public void waitForOutput(String output, long sleepDelay) {
        int curOutput = processOutput.size();
        while (true) {
            for (; curOutput < processOutput.size(); curOutput++) {
                String line = processOutput.get(curOutput);
                if (line.contains(output)) {
                    return;
                }
            }
            Util.sleep(sleepDelay);
        }
    }
}
