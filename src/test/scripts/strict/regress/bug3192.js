/*
 * Copyright (c) 2012-2015 André Bargull
 * Alle Rechte vorbehalten / All Rights Reserved.  Use is subject to license terms.
 *
 * <https://github.com/anba/es6draft>
 */

// 21.2.1 Patterns: Allow to escape $ in IdentityEscape
// https://bugs.ecmascript.org/show_bug.cgi?id=3192

RegExp(`/\\$/`);
