/**
 * Copyright (c) 2012-2013 André Bargull
 * Alle Rechte vorbehalten / All Rights Reserved.  Use is subject to license terms.
 *
 * <https://github.com/anba/es6draft>
 */
package com.github.anba.es6draft.test262;

import static com.github.anba.es6draft.util.Functional.filterMap;
import static java.util.Collections.emptyList;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.interpol.ConfigurationInterpolator;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.lang.text.StrLookup;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.github.anba.es6draft.util.Functional.Mapper;
import com.github.anba.es6draft.util.Functional.Predicate;

/**
 * 
 */
final class Resources {
    private Resources() {
    }

    /**
     * Loads all test cases from the individual javascript files
     * 
     */
    public static List<Object[]> loadTestCases(Configuration c) throws IOException {
        final String testpath = c.getString("");
        final List<?> exclude = c.getList("exclude", emptyList());
        final List<?> include = c.getList("include", emptyList());
        final boolean only_excluded = c.getBoolean("only_excluded", false);
        final Pattern excludePattern = Pattern.compile(c.getString("exclude_re", ""));

        // base directory to search for test javascript files
        final Path base = Paths.get(testpath);

        // set of test-case id to exclude from testing
        final Set<String> excludes = readExcludeXMLs(exclude);
        final Set<String> includes = readExcludeXMLs(include);

        final List<Object[]> files = new ArrayList<>();
        Set<FileVisitOption> options = Collections.emptySet();
        int depth = Integer.MAX_VALUE;
        final Pattern pattern = Pattern.compile("(.+?)(?:\\.([^.]*)$|$)");
        final PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:**/*.js");

        FileVisitor<Path> visitor = new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                    throws IOException {
                L1: {
                    if (!attrs.isRegularFile()) {
                        break L1;
                    }
                    String rel = base.relativize(file).toString();
                    if (excludePattern.matcher(rel).matches()) {
                        break L1;
                    }
                    if (!matcher.matches(file)) {
                        break L1;
                    }
                    String filename = file.getFileName().toString();
                    Matcher matcher = pattern.matcher(filename);
                    if (!matcher.matches()) {
                        assert false : "regexp failure";
                    }
                    String testname = matcher.group(1);
                    if (excludes.contains(testname) ^ only_excluded) {
                        break L1;
                    }
                    if (!includes.isEmpty() && !includes.contains(testname)) {
                        break L1;
                    }
                    files.add(array(testname, file.toString()));
                }

                return FileVisitResult.CONTINUE;
            }
        };

        Files.walkFileTree(base, options, depth, visitor);

        return files;
    }

    /**
     * {@link ConfigurationInterpolator} which reports an error for missing variables
     */
    private static final ConfigurationInterpolator MISSING_VAR = new ConfigurationInterpolator() {
        private final StrLookup errorLookup = new StrLookup() {
            @Override
            public String lookup(String key) {
                String msg = String.format("mandatory variable '%s' is not set", key);
                throw new NoSuchElementException(msg);
            }
        };

        @Override
        public String lookup(String var) {
            throw new UnsupportedOperationException();
        }

        @Override
        protected StrLookup fetchLookupForPrefix(String prefix) {
            return errorLookup;
        }

        @Override
        protected StrLookup fetchNoPrefixLookup() {
            return errorLookup;
        }
    };

    /**
     * Loads the configuration file
     */
    public static Configuration loadConfiguration(String uri) {
        try {
            PropertiesConfiguration config = new PropertiesConfiguration();
            // entries are mandatory unless an explicit default value was given
            config.setThrowExceptionOnMissing(true);
            config.getInterpolator().setParentInterpolator(MISSING_VAR);
            config.load(resource(uri), "UTF-8");
            // test load for required property "test262"
            config.getString("test262");
            return config;
        } catch (NoSuchElementException | ConfigurationException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Loads the named resource through {@link Class#getResourceAsStream(String)} if the uri is
     * prepended with "resource:", otherwise loads the resource with
     * {@link Files#newInputStream(Path, java.nio.file.OpenOption...)}
     */
    private static InputStream resource(String uri) throws IOException {
        final String RESOURCE = "resource:";
        if (uri.startsWith(RESOURCE)) {
            String name = "/" + uri.substring(RESOURCE.length());
            InputStream res = Resources.class.getResourceAsStream(name);
            if (res == null) {
                throw new IOException("resource not found: " + name);
            }
            return res;
        } else {
            return Files.newInputStream(Paths.get(uri));
        }
    }

    /**
     * Reads all exlusion xml-files from the configuration
     */
    private static Set<String> readExcludeXMLs(List<?> values) throws IOException {
        Set<String> exclude = new HashSet<>();
        for (String s : filterMap(values, notEmptyString, toString)) {
            try (InputStream res = resource(s)) {
                exclude.addAll(readExcludeXML(res));
            }
        }
        return exclude;
    }

    /**
     * Load the exclusion xml-list for invalid test cases from the {@link InputStream}
     */
    private static Set<String> readExcludeXML(InputStream is) throws IOException {
        Set<String> exclude = new HashSet<>();
        Reader reader = new InputStreamReader(new BOMInputStream(is), "UTF-8");
        Document doc = xml(reader);
        NodeList ns = doc.getDocumentElement().getElementsByTagName("test");
        for (int i = 0, len = ns.getLength(); i < len; ++i) {
            NamedNodeMap attrs = ns.item(i).getAttributes();
            String id = attrs.getNamedItem("id").getNodeValue();
            exclude.add(id);
        }
        return exclude;
    }

    /**
     * Reads the xml-structure from the {@link Reader} and returns the corresponding
     * {@link Document}
     */
    private static Document xml(Reader xml) throws IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        // turn off any validation or namespace features
        factory.setNamespaceAware(false);
        factory.setValidating(false);
        List<String> features = Arrays.asList("http://xml.org/sax/features/namespaces",
                "http://xml.org/sax/features/validation",
                "http://apache.org/xml/features/nonvalidating/load-dtd-grammar",
                "http://apache.org/xml/features/nonvalidating/load-external-dtd");
        for (String feature : features) {
            try {
                factory.setFeature(feature, false);
            } catch (ParserConfigurationException e) {
                // ignore invalid feature names
            }
        }

        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource source = new InputSource(xml);
            Document doc = builder.parse(source);
            return doc;
        } catch (ParserConfigurationException | SAXException e) {
            throw new IOException(e);
        }
    }

    @SafeVarargs
    private static <T> T[] array(T... rest) {
        return rest;
    }

    private static final Predicate<Object> notEmptyString = new Predicate<Object>() {
        @Override
        public boolean eval(Object value) {
            return (value != null && !value.toString().isEmpty());
        }
    };

    private static final Mapper<Object, String> toString = new Mapper<Object, String>() {
        @Override
        public String map(Object t) {
            return t.toString();
        }
    };
}
