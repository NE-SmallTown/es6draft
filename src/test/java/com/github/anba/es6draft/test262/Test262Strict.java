/**
 * Copyright (c) 2012-2015 André Bargull
 * Alle Rechte vorbehalten / All Rights Reserved.  Use is subject to license terms.
 *
 * <https://github.com/anba/es6draft>
 */
package com.github.anba.es6draft.test262;

import static com.github.anba.es6draft.test262.Test262GlobalObject.newGlobalObjectAllocator;
import static com.github.anba.es6draft.util.Resources.loadConfiguration;
import static com.github.anba.es6draft.util.matchers.ErrorMessageMatcher.hasErrorMessage;
import static com.github.anba.es6draft.util.matchers.PatternMatcher.matchesPattern;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.apache.commons.configuration.Configuration;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TestWatcher;
import org.junit.rules.Timeout;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.Parameterized.UseParametersRunnerFactory;

import com.github.anba.es6draft.repl.console.ShellConsole;
import com.github.anba.es6draft.runtime.internal.ObjectAllocator;
import com.github.anba.es6draft.runtime.internal.Properties;
import com.github.anba.es6draft.runtime.internal.ScriptCache;
import com.github.anba.es6draft.runtime.internal.Strings;
import com.github.anba.es6draft.util.Functional.BiFunction;
import com.github.anba.es6draft.util.Parallelized;
import com.github.anba.es6draft.util.ParameterizedRunnerFactory;
import com.github.anba.es6draft.util.Resources;
import com.github.anba.es6draft.util.TestConfiguration;
import com.github.anba.es6draft.util.TestGlobals;
import com.github.anba.es6draft.util.rules.ExceptionHandlers.ScriptExceptionHandler;
import com.github.anba.es6draft.util.rules.ExceptionHandlers.StandardErrorHandler;

/**
 * The standard test262 test suite (strict)
 */
@RunWith(Parallelized.class)
@UseParametersRunnerFactory(ParameterizedRunnerFactory.class)
@TestConfiguration(name = "test262.test.strict", file = "resource:/test-configuration.properties")
public final class Test262Strict {
    private static final Configuration configuration = loadConfiguration(Test262Strict.class);
    private static final DefaultMode unmarkedDefault = DefaultMode.forName(configuration
            .getString("unmarked_default"));

    @Parameters(name = "{0}")
    public static List<Test262Info> suiteValues() throws IOException {
        return Resources.loadTests(configuration, new BiFunction<Path, Path, Test262Info>() {
            @Override
            public Test262Info apply(Path basedir, Path file) {
                return new Test262Info(basedir, file);
            }
        });
    }

    @ClassRule
    public static TestGlobals<Test262GlobalObject, Test262Info> globals = new TestGlobals<Test262GlobalObject, Test262Info>(
            configuration) {
        @Override
        protected ObjectAllocator<Test262GlobalObject> newAllocator(ShellConsole console,
                Test262Info test, ScriptCache scriptCache) {
            return newGlobalObjectAllocator(console, test, scriptCache);
        }
    };

    @Rule
    public TestWatcher watcher = new TestWatcher() {
        @Override
        protected void starting(Description description) {
            isStrictTest = description.getAnnotation(Strict.class) != null;
        }
    };
    private boolean isStrictTest = false;

    @Rule
    public Timeout maxTime = new Timeout(120, TimeUnit.SECONDS);

    @Rule
    public StandardErrorHandler errorHandler = StandardErrorHandler.none();

    @Rule
    public ScriptExceptionHandler exceptionHandler = ScriptExceptionHandler.none();

    @Rule
    public ExpectedException expected = ExpectedException.none();

    @Parameter(0)
    public Test262Info test;

    private Test262GlobalObject global;
    private AsyncHelper async;
    private String sourceCode;
    private int preambleLines;

    @Before
    public void setUp() throws Throwable {
        // Filter disabled tests
        assumeTrue(test.isEnabled());

        String fileContent = test.readFile();
        if (isStrictTest) {
            assumeTrue(!test.isNoStrict()
                    && (test.isOnlyStrict() || unmarkedDefault != DefaultMode.NonStrict));
        } else {
            assumeTrue(!test.isOnlyStrict()
                    && (test.isNoStrict() || unmarkedDefault != DefaultMode.Strict));
        }

        final String preamble;
        if (isStrictTest) {
            preamble = "\"use strict\";\nvar strict_mode = true;\n";
        } else {
            preamble = "//\"use strict\";\nvar strict_mode = false;\n";
        }
        sourceCode = Strings.concat(preamble, fileContent);
        preambleLines = 2;

        global = globals.newGlobal(new Test262Console(), test);
        exceptionHandler.setExecutionContext(global.getRealm().defaultContext());

        if (!test.isNegative()) {
            errorHandler.match(StandardErrorHandler.defaultMatcher());
            exceptionHandler.match(ScriptExceptionHandler.defaultMatcher());
        } else {
            expected.expect(Matchers.either(StandardErrorHandler.defaultMatcher())
                    .or(ScriptExceptionHandler.defaultMatcher())
                    .or(instanceOf(Test262AssertionError.class)));
            String errorType = test.getErrorType();
            if (errorType != null) {
                expected.expect(hasErrorMessage(global.getRealm().defaultContext(),
                        matchesPattern(errorType, Pattern.CASE_INSENSITIVE)));
            }
        }

        // Load test includes
        for (String name : test.getIncludes()) {
            global.include(name);
        }

        if (test.isAsync()) {
            // "doneprintHandle.js" is replaced with AsyncHelper
            global.include("timer.js");
            async = global.install(new AsyncHelper(), AsyncHelper.class);
        }

        // Install test hooks
        global.install(global, Test262GlobalObject.class);
    }

    @After
    public void tearDown() {
        if (global != null) {
            global.getScriptLoader().getExecutor().shutdown();
        }
    }

    @Test
    public void runTest() throws Throwable {
        // Evaluate actual test-script
        global.eval(test.toFile(), sourceCode, 1 - preambleLines);

        // Wait for pending tasks to finish
        if (test.isAsync()) {
            assertFalse(async.doneCalled);
            global.getRealm().getWorld().runEventLoop();
            assertTrue(async.doneCalled);
        } else {
            global.getRealm().getWorld().runEventLoop();
        }
    }

    @Test
    @Strict
    public void runTestStrict() throws Throwable {
        // Evaluate actual test-script
        global.eval(test.toFile(), sourceCode, 1 - preambleLines);

        // Wait for pending tasks to finish
        if (test.isAsync()) {
            assertFalse(async.doneCalled);
            global.getRealm().getWorld().runEventLoop();
            assertTrue(async.doneCalled);
        } else {
            global.getRealm().getWorld().runEventLoop();
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.METHOD })
    public @interface Strict {
    }

    public static final class AsyncHelper {
        boolean doneCalled = false;

        @Properties.Function(name = "$DONE", arity = 0)
        public void done(boolean argument) {
            assertFalse(doneCalled);
            doneCalled = true;
            if (argument) {
                throw new Test262AssertionError(argument);
            }
        }
    }
}
