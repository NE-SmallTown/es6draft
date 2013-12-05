/**
 * Copyright (c) 2012-2013 André Bargull
 * Alle Rechte vorbehalten / All Rights Reserved.  Use is subject to license terms.
 *
 * <https://github.com/anba/es6draft>
 */
package com.github.anba.es6draft.repl;

import static com.github.anba.es6draft.repl.SourceBuilder.ToSource;
import static com.github.anba.es6draft.runtime.AbstractOperations.CreateArrayFromList;
import static com.github.anba.es6draft.runtime.types.Undefined.UNDEFINED;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.anba.es6draft.Script;
import com.github.anba.es6draft.ScriptLoader;
import com.github.anba.es6draft.compiler.CompilationException;
import com.github.anba.es6draft.compiler.Compiler;
import com.github.anba.es6draft.parser.Parser;
import com.github.anba.es6draft.parser.ParserEOFException;
import com.github.anba.es6draft.parser.ParserException;
import com.github.anba.es6draft.repl.StopExecutionException.Reason;
import com.github.anba.es6draft.runtime.ExecutionContext;
import com.github.anba.es6draft.runtime.Realm;
import com.github.anba.es6draft.runtime.World;
import com.github.anba.es6draft.runtime.internal.CompatibilityOption;
import com.github.anba.es6draft.runtime.internal.ObjectAllocator;
import com.github.anba.es6draft.runtime.internal.ScriptCache;
import com.github.anba.es6draft.runtime.internal.ScriptException;
import com.github.anba.es6draft.runtime.internal.Strings;
import com.github.anba.es6draft.runtime.types.PropertyDescriptor;
import com.github.anba.es6draft.runtime.types.ScriptObject;

/**
 * Simple REPL
 */
public class Repl {
    private static final int STACKTRACE_DEPTH = 20;

    public static void main(String[] args) {
        try {
            EnumSet<Option> options = Option.fromArgs(args);
            StartScript startScript = StartScript.fromArgs(args);
            new Repl(options, startScript, System.console()).loop();
        } catch (Throwable e) {
            printStackTrace(e);
        }
    }

    private static void printStackTrace(Throwable e) {
        StackTraceElement[] stackTrace = e.getStackTrace();
        if (stackTrace.length > STACKTRACE_DEPTH) {
            int omitted = stackTrace.length - STACKTRACE_DEPTH;
            stackTrace = Arrays.copyOf(stackTrace, STACKTRACE_DEPTH + 1);
            stackTrace[STACKTRACE_DEPTH] = new StackTraceElement("..", "", "Frames omitted",
                    omitted);
            e.setStackTrace(stackTrace);
        }
        e.printStackTrace();
    }

    private enum Option {
        CompileOnly, Debug, StackTrace, Strict, SimpleShell, MozillaShell, V8Shell;

        static EnumSet<Option> fromArgs(String[] args) {
            EnumSet<Option> options = EnumSet.noneOf(Option.class);
            for (String arg : args) {
                switch (arg) {
                case "--compile-only":
                    options.add(CompileOnly);
                    break;
                case "--debug":
                    options.add(CompileOnly);
                    options.add(Debug);
                    break;
                case "--stacktrace":
                    options.add(StackTrace);
                    break;
                case "--strict":
                    options.add(Strict);
                    break;
                case "--shell=simple":
                    options.add(SimpleShell);
                    break;
                case "--shell=mozilla":
                    options.add(MozillaShell);
                    break;
                case "--shell=v8":
                    options.add(V8Shell);
                    break;
                case "--help":
                    System.out.print(getHelp());
                    System.exit(0);
                    break;
                default:
                    if (arg.length() > 1 && arg.charAt(0) == '-') {
                        System.err.printf("invalid option '%s'\n\n", arg);
                        System.out.print(getHelp());
                        System.exit(0);
                    }
                    break;
                }
            }
            return options;
        }
    }

    private static String getHelp() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("es6draft (%s)\n\n", getBuildDate()));
        sb.append("Options: \n");
        sb.append("  --compile-only    Disable interpreter\n");
        sb.append("  --debug           Print generated Java bytecode\n");
        sb.append("  --stacktrace      Print stack-trace on error\n");
        sb.append("  --strict          Strict semantics without web compatibility\n");
        sb.append("  --shell=[mode]    Set default shell emulation [simple, mozilla, v8] (default = simple)\n");
        sb.append("  --help            Print this help\n");
        return sb.toString();
    }

    private static String getBuildDate() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                Repl.class.getResourceAsStream("/build-date"), StandardCharsets.UTF_8))) {
            return reader.readLine();
        } catch (IOException e) {
            return "<unknown build>";
        }
    }

    private static class StartScript {
        private Path script = Paths.get("-");
        private List<String> arguments = new ArrayList<>();

        static StartScript fromArgs(String[] args) {
            StartScript startScript = new StartScript();
            boolean inOptions = true;
            for (String arg : args) {
                if (inOptions && arg.length() > 1 && arg.charAt(0) == '-') {
                    // skip options
                    continue;
                }
                if (inOptions) {
                    inOptions = false;
                    startScript.script = Paths.get(arg);
                } else if (!arg.isEmpty()) {
                    startScript.arguments.add(arg);
                }
            }
            return startScript;
        }
    }

    @SuppressWarnings("serial")
    private static class ParserExceptionWithSource extends RuntimeException {
        private final String source;

        ParserExceptionWithSource(ParserException e, String source) {
            super(e);
            this.source = source;
        }

        @Override
        public ParserException getCause() {
            return (ParserException) super.getCause();
        }

        public String getSource() {
            return source;
        }
    }

    private final EnumSet<Option> options;
    private final StartScript startScript;
    private final Console console;
    private AtomicInteger scriptCounter = new AtomicInteger(0);

    private Repl(EnumSet<Option> options, StartScript startScript, Console console) {
        this.options = options;
        this.startScript = startScript;
        this.console = console;
    }

    private void handleException(Throwable e) {
        String message = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
        console.printf("%s%n", message);
        if (options.contains(Option.StackTrace)) {
            printStackTrace(e);
        }
    }

    private void handleException(Realm realm, ScriptException e) {
        console.printf("uncaught exception: %s%n", e.getMessage(realm.defaultContext()));
        if (options.contains(Option.StackTrace)) {
            printStackTrace(e);
        }
    }

    private void handleException(ParserExceptionWithSource exception, int lineOffset) {
        ParserException e = exception.getCause();
        String source = exception.getSource();

        String sourceInfo = String.format("%s:%d:%d", e.getFile(), e.getLine(), e.getColumn());
        int start = skipLines(source, e.getLine() - lineOffset);
        int end = nextLineTerminator(source, start);
        String offendingLine = source.substring(start, end);
        String marker = Strings.repeat('.', Math.max(e.getColumn() - 1, 0)) + '^';

        console.printf("%s %s: %s%n", sourceInfo, e.getType(), e.getFormattedMessage());
        console.printf("%s %s%n", sourceInfo, offendingLine);
        console.printf("%s %s%n", sourceInfo, marker);
        if (options.contains(Option.StackTrace)) {
            printStackTrace(e);
        }
    }

    private static int skipLines(String s, int n) {
        int index = 0;
        for (int length = s.length(); n > 0; --n) {
            int lineEnd = nextLineTerminator(s, index);
            if (lineEnd + 1 < length && s.charAt(lineEnd) == '\r' && s.charAt(lineEnd + 1) == '\n') {
                index = lineEnd + 2;
            } else {
                index = lineEnd + 1;
            }
        }
        return index;
    }

    private static int nextLineTerminator(String s, int index) {
        for (int length = s.length(); index < length && !Strings.isLineTerminator(s.charAt(index)); ++index) {
        }
        return index;
    }

    private void printException(Exception e) {
        System.err.println(e);
        if (options.contains(Option.StackTrace)) {
            printStackTrace(e);
        }
    }

    /**
     * REPL: Read
     */
    private com.github.anba.es6draft.ast.Script read(Realm realm, int line) {
        StringBuilder source = new StringBuilder();
        for (;;) {
            String s = console.readLine();
            if (s == null) {
                continue;
            }
            source.append(s).append('\n');
            try {
                EnumSet<Parser.Option> options = Parser.Option.from(realm.getOptions());
                Parser parser = new Parser("typein", line, options);
                return parser.parseScript(source);
            } catch (ParserEOFException e) {
                continue;
            } catch (ParserException e) {
                throw new ParserExceptionWithSource(e, source.toString());
            }
        }
    }

    /**
     * REPL: Eval
     */
    private Object eval(Realm realm, com.github.anba.es6draft.ast.Script parsedScript) {
        String className = "typein_" + scriptCounter.incrementAndGet();
        Script script;
        if (options.contains(Option.CompileOnly)) {
            script = ScriptLoader.compile(className, parsedScript, realm.getCompilerOptions());
        } else {
            script = ScriptLoader.load(className, parsedScript, realm.getCompilerOptions());
        }
        return ScriptLoader.ScriptEvaluation(script, realm, false);
    }

    /**
     * REPL: Print
     */
    private void print(Realm realm, Object result) {
        if (result != UNDEFINED) {
            console.printf("%s%n", ToSource(realm.defaultContext(), result));
        }
    }

    /**
     * REPL: Loop
     */
    private void loop() {
        ShellGlobalObject global = newGlobal();
        runStartScript(global);

        Realm realm = global.getRealm();
        for (int line = 1;; line += 1) {
            drainTaskQueue(realm);
            try {
                console.printf("js> ");
                com.github.anba.es6draft.ast.Script parsedScript = read(realm, line);
                if (parsedScript.getStatements().isEmpty()) {
                    continue;
                }
                Object result = eval(realm, parsedScript);
                print(realm, result);
            } catch (StopExecutionException e) {
                if (e.getReason() == Reason.Quit) {
                    System.exit(0);
                }
            } catch (ParserExceptionWithSource e) {
                handleException(e, line);
            } catch (ScriptException e) {
                handleException(realm, e);
            } catch (ParserException | CompilationException | StackOverflowError e) {
                handleException(e);
            } catch (BootstrapMethodError e) {
                handleException(e.getCause());
            }
        }
    }

    private void drainTaskQueue(Realm realm) {
        World<?> world = realm.getWorld();
        while (world.hasTasks()) {
            try {
                world.executeTasks(realm.defaultContext());
            } catch (StopExecutionException e) {
                if (e.getReason() == Reason.Quit) {
                    System.exit(0);
                }
            } catch (ScriptException e) {
                handleException(realm, e);
            } catch (StackOverflowError e) {
                handleException(e);
            } catch (BootstrapMethodError e) {
                handleException(e.getCause());
            }
        }
    }

    private ShellGlobalObject newGlobal() {
        ReplConsole console = new ReplConsole(this.console);
        Path baseDir = Paths.get("").toAbsolutePath();
        Path script = Paths.get("./.");
        Set<CompatibilityOption> compatibilityOptions;
        if (options.contains(Option.Strict)) {
            compatibilityOptions = CompatibilityOption.StrictCompatibility();
        } else if (options.contains(Option.MozillaShell)) {
            compatibilityOptions = CompatibilityOption.MozCompatibility();
        } else {
            compatibilityOptions = CompatibilityOption.WebCompatibility();
        }
        Set<Compiler.Option> compilerOptions = EnumSet.noneOf(Compiler.Option.class);
        if (options.contains(Option.Debug)) {
            compilerOptions.add(Compiler.Option.Debug);
        }
        ScriptCache scriptCache = new ScriptCache(Parser.Option.from(compatibilityOptions));

        List<String> initScripts;
        ObjectAllocator<? extends ShellGlobalObject> allocator;
        if (options.contains(Option.MozillaShell)) {
            Path libDir = Paths.get("");
            initScripts = asList("mozlegacy.js");
            allocator = MozShellGlobalObject.newGlobalObjectAllocator(console, baseDir, script,
                    libDir, scriptCache);
        } else if (options.contains(Option.V8Shell)) {
            initScripts = asList("v8legacy.js");
            allocator = V8ShellGlobalObject.newGlobalObjectAllocator(console, baseDir, script,
                    scriptCache);
        } else {
            initScripts = emptyList();
            allocator = SimpleShellGlobalObject.newGlobalObjectAllocator(console, baseDir, script,
                    scriptCache);
        }

        World<? extends ShellGlobalObject> world = new World<>(allocator, compatibilityOptions,
                compilerOptions);
        ShellGlobalObject global = world.newGlobal();

        for (String name : initScripts) {
            try {
                global.eval(ShellGlobalObject.compileScript(scriptCache, name));
            } catch (ParserException | CompilationException | IOException e) {
                printException(e);
            }
        }

        return global;
    }

    private void runStartScript(ShellGlobalObject global) {
        ExecutionContext cx = global.getRealm().defaultContext();

        ScriptObject arguments = CreateArrayFromList(cx, startScript.arguments);
        global.defineOwnProperty(cx, "arguments", new PropertyDescriptor(arguments, true, false,
                true));

        Path script = startScript.script;
        if (!script.toString().equals("-")) {
            try {
                global.eval(script, script);
            } catch (ParserException | CompilationException | IOException e) {
                printException(e);
            }
        }
    }

    private static class ReplConsole implements ShellConsole {
        private Console console;

        ReplConsole(Console console) {
            this.console = console;
        }

        @Override
        public String readLine() {
            return console.readLine();
        }

        @Override
        public void putstr(String s) {
            console.writer().print(s);
        }

        @Override
        public void print(String s) {
            console.writer().println(s);
        }

        @Override
        public void printErr(String s) {
            System.err.println(s);
        }
    }
}
