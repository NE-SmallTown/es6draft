/**
 * Copyright (c) 2012-2016 André Bargull
 * Alle Rechte vorbehalten / All Rights Reserved.  Use is subject to license terms.
 *
 * <https://github.com/anba/es6draft>
 */
package com.github.anba.es6draft.webkit;

import java.io.IOException;
import java.net.URISyntaxException;

import com.github.anba.es6draft.compiler.CompilationException;
import com.github.anba.es6draft.parser.ParserException;
import com.github.anba.es6draft.repl.global.BaseShellFunctions;
import com.github.anba.es6draft.repl.global.ShellGlobalObject;
import com.github.anba.es6draft.runtime.Realm;
import com.github.anba.es6draft.runtime.internal.NativeCode;

/**
 * 
 */
final class WebKitTestGlobalObject extends ShellGlobalObject {
    WebKitTestGlobalObject(Realm realm) {
        super(realm);
    }

    static void testLoadInitializationScript() throws IOException {
        NativeCode.getScriptURL("webkitlegacy.js");
    }

    @Override
    public void initializeScripted() throws IOException, URISyntaxException, ParserException, CompilationException {
        NativeCode.load(getRealm(), "webkitlegacy.js");
    }

    @Override
    public void initializeExtensions() {
        createGlobalProperties(new BaseShellFunctions(), BaseShellFunctions.class);
        createGlobalProperties(new TestingFunctions(), TestingFunctions.class);
    }
}
