/**
 * Copyright (c) 2012-2013 André Bargull
 * Alle Rechte vorbehalten / All Rights Reserved.  Use is subject to license terms.
 *
 * <https://github.com/anba/es6draft>
 */
package com.github.anba.es6draft.runtime.modules;

public final class ExportBinding {
    /** [[Module]] */
    final ModuleObject module;

    /** [[LocalName]] */
    final String localName;

    public ExportBinding(ModuleObject module, String localName) {
        this.module = module;
        this.localName = localName;
    }
}
