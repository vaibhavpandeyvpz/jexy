package com.vaibhavpandey.jexy;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class ShellProcessTest {

    @Test
    public void testExitCode() {
        ShellProcess process = new ShellProcess("sh", "-c", "exit");
        assertEquals(0, process.execute());
    }

    @Test
    public void testExitCodeStartFailed() {
        ShellProcess process = new ShellProcess("jexy");
        assertEquals(ShellProcess.EXIT_CODE_START_FAILED, process.execute());
    }

    @Test
    public void testExitCodeReal() {
        ShellProcess process = new ShellProcess("sh", "-z");
        assertEquals(2, process.execute());
    }

    @Test
    public void testStdOut() {
        ShellProcess process = new ShellProcess("sh", "-c", "echo 'VPZ'");
        ExecutionResult result = process.execute(true);
        assertEquals(0, result.getExitCode());
        assertNotNull(result.getStdOut());
        assertEquals("VPZ", result.getStdOut().get(0));
        assertNull(result.getStdErr());
    }

    @Test
    public void testStdErr() {
        ShellProcess process = new ShellProcess("sh", "-c", "echo 'VPZ' >&2");
        ExecutionResult result = process.execute(false, true);
        assertEquals(0, result.getExitCode());
        assertNull(result.getStdOut());
        assertNotNull(result.getStdErr());
        assertEquals("VPZ", result.getStdErr().get(0));
    }

    @Test
    public void testStdOutAndErr() {
        ShellProcess process = new ShellProcess("sh");
        process.add("echo", "VPZ");
        process.add("echo", new String[] { "ZPV", ">&2" });
        ExecutionResult result = process.execute(true, true);
        assertEquals(0, result.getExitCode());
        assertNotNull(result.getStdOut());
        assertEquals("VPZ", result.getStdOut().get(0));
        assertNotNull(result.getStdErr());
        assertEquals("ZPV", result.getStdErr().get(0));
    }

    @Test
    public void testWorkingDirectory() {
        ShellProcess process = new ShellProcess("sh", "-c", "pwd");
        ExecutionResult result = process.execute(true);
        assertEquals(0, result.getExitCode());
        assertNotNull(result.getStdOut());
        assertNotEquals("/etc", result.getStdOut().get(0));
    }

    @Test
    public void testWorkingDirectoryCustom() {
        ShellProcess process = new ShellProcess("sh", new String[] { "-c", "pwd" }, new File("/etc"));
        ExecutionResult result = process.execute(true);
        assertEquals(0, result.getExitCode());
        assertNotNull(result.getStdOut());
        assertEquals("/etc", result.getStdOut().get(0));
    }
}
