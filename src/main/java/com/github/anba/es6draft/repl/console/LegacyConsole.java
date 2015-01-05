/**
 * Copyright (c) 2012-2015 André Bargull
 * Alle Rechte vorbehalten / All Rights Reserved.  Use is subject to license terms.
 *
 * <https://github.com/anba/es6draft>
 */
package com.github.anba.es6draft.repl.console;

import java.io.BufferedReader;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Formatter;

import com.github.anba.es6draft.runtime.Realm;

/**
 * {@link ReplConsole} implementation for legacy consoles
 */
public final class LegacyConsole implements ReplConsole {
    private final PrintWriter out;
    private final BufferedReader reader;
    private final Formatter formatter;

    public LegacyConsole() {
        this(System.out, System.in);
    }

    public LegacyConsole(PrintStream out, InputStream in) {
        this(new PrintWriter(out), new InputStreamReader(in, Charset.defaultCharset()));
    }

    public LegacyConsole(Writer out, Reader in) {
        this(new PrintWriter(out), in);
    }

    public LegacyConsole(PrintWriter out, Reader in) {
        this.out = out;
        this.reader = new BufferedReader(in);
        this.formatter = new Formatter(out);
    }

    @Override
    public void addCompletion(Realm realm) {
    }

    @Override
    public boolean isAnsiSupported() {
        return false;
    }

    @Override
    public void printf(String format, Object... args) {
        formatter.format(format, args).flush();
    }

    @Override
    public String readLine(String prompt) {
        if (!prompt.isEmpty()) {
            putstr(prompt);
        }
        return readLine();
    }

    @Override
    public String readLine() {
        try {
            return reader.readLine();
        } catch (IOException e) {
            throw new IOError(e);
        }
    }

    @Override
    public void putstr(String s) {
        out.print(s);
        out.flush();
    }

    @Override
    public void print(String s) {
        out.println(s);
        out.flush();
    }

    @Override
    public void printErr(String s) {
        System.err.println(s);
    }
}
