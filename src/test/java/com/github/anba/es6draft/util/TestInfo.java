/**
 * Copyright (c) 2012-2013 André Bargull
 * Alle Rechte vorbehalten / All Rights Reserved.  Use is subject to license terms.
 *
 * <https://github.com/anba/es6draft>
 */
package com.github.anba.es6draft.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.junit.runners.Parameterized;

import com.github.anba.es6draft.util.Functional.BiFunction;
import com.github.anba.es6draft.util.Functional.Function;

/**
 * Base class to store test information
 */
public class TestInfo {
    public Path script;
    public boolean enable = true;
    public boolean expect = true;

    public TestInfo(Path script) {
        this.script = script;
    }

    @Override
    public String toString() {
        return script.toString();
    }

    /**
     * {@link Parameterized} expects a list of {@code Object[]}
     */
    public static Iterable<TestInfo[]> toObjectArray(Iterable<? extends TestInfo> iterable) {
        List<TestInfo[]> list = new ArrayList<TestInfo[]>();
        for (TestInfo o : iterable) {
            list.add(new TestInfo[] { o });
        }
        return list;
    }

    /**
     * Recursively searches for js-file test cases in {@code searchdir} and its sub-directories
     */
    public static <T extends TestInfo> List<T> loadTests(Path searchdir, final Path basedir,
            final Set<String> excludeDirs, final Set<String> excludeFiles,
            final BiFunction<Path, Iterator<String>, T> create) throws IOException {
        return loadTests(searchdir, basedir, excludeDirs, excludeFiles, StandardCharsets.UTF_8,
                create);
    }

    /**
     * Recursively searches for js-file test cases in {@code searchdir} and its sub-directories
     */
    public static <T extends TestInfo> List<T> loadTests(Path searchdir, final Path basedir,
            Set<String> excludeDirs, Set<String> excludeFiles, final Charset charset,
            final BiFunction<Path, Iterator<String>, T> create) throws IOException {
        final List<T> tests = new ArrayList<>();
        Files.walkFileTree(searchdir, new TestFileVisitor(excludeDirs, excludeFiles) {
            @Override
            public void visitFile(Path file) throws IOException {
                try (BufferedReader reader = Files.newBufferedReader(file, charset)) {
                    tests.add(create.apply(basedir.relativize(file), new LineIterator(reader)));
                } catch (UncheckedIOException e) {
                    throw e.getCause();
                }
            }
        });
        return tests;
    }

    private static final class LineIterator implements Iterator<String> {
        private final BufferedReader reader;
        private String line = null;

        LineIterator(BufferedReader reader) {
            this.reader = reader;
        }

        @Override
        public boolean hasNext() {
            if (line == null) {
                try {
                    line = reader.readLine();
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
            return line != null;
        }

        @Override
        public String next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            String line = this.line;
            this.line = null;
            return line;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private static final Function<Path, TestInfo> defaultCreate = new Function<Path, TestInfo>() {
        @Override
        public TestInfo apply(Path script) {
            return new TestInfo(script);
        }
    };

    /**
     * Recursively searches for js-file test cases in {@code searchdir} and its sub-directories
     */
    public static List<TestInfo> loadTests(Path searchdir, final Path basedir,
            final Set<String> excludeDirs, final Set<String> excludeFiles) throws IOException {
        return loadTests(searchdir, basedir, excludeDirs, excludeFiles, defaultCreate);
    }

    /**
     * Recursively searches for js-file test cases in {@code searchdir} and its sub-directories
     */
    public static <T extends TestInfo> List<T> loadTests(Path searchdir, final Path basedir,
            Set<String> excludeDirs, Set<String> excludeFiles, final Function<Path, T> create)
            throws IOException {
        final List<T> tests = new ArrayList<>();
        Files.walkFileTree(searchdir, new TestFileVisitor(excludeDirs, excludeFiles) {
            @Override
            public void visitFile(Path file) throws IOException {
                tests.add(create.apply(basedir.relativize(file)));
            }
        });
        return tests;
    }

    private static abstract class TestFileVisitor extends SimpleFileVisitor<Path> {
        private final Set<String> excludeDirs;
        private final Set<String> excludeFiles;

        TestFileVisitor(Set<String> excludeDirs, Set<String> excludeFiles) {
            this.excludeDirs = excludeDirs;
            this.excludeFiles = excludeFiles;
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                throws IOException {
            if (excludeDirs.contains(dir.getFileName().toString())) {
                return FileVisitResult.SKIP_SUBTREE;
            }
            return super.preVisitDirectory(dir, attrs);
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            if (attrs.isRegularFile() && attrs.size() != 0L) {
                String name = file.getFileName().toString();
                if (!excludeFiles.contains(name) && name.endsWith(".js")) {
                    visitFile(file);
                }
            }
            return FileVisitResult.CONTINUE;
        }

        protected abstract void visitFile(Path path) throws IOException;
    }

    /**
     * Filter the initially collected test cases
     */
    public static <T extends TestInfo> List<T> filterTests(List<T> tests, String filename)
            throws IOException {
        // list->map
        Map<Path, TestInfo> map = new LinkedHashMap<>();
        for (TestInfo test : tests) {
            map.put(test.script, test);
        }
        // disable tests
        List<TestInfo> disabledTests = new ArrayList<>();
        InputStream res = TestInfo.class.getResourceAsStream(filename);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(res,
                StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("#") || line.isEmpty()) {
                    continue;
                }
                TestInfo t = map.get(Paths.get(line));
                if (t == null) {
                    System.err.printf("detected stale entry '%s'\n", line);
                    continue;
                }
                disabledTests.add(t);
                t.enable = false;
            }
        }
        System.out.printf("disabled %d tests of %d in total%n", disabledTests.size(), tests.size());
        return tests;
    }
}
