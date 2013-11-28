/*
 * Copyright (c) 2012-2013 André Bargull
 * Alle Rechte vorbehalten / All Rights Reserved.  Use is subject to license terms.
 *
 * <https://github.com/anba/es6draft>
 */

(function RegExpExtensions(global) {
"use strict";

const {
  Object, RegExp
} = global;

Object.defineProperty(RegExp, "$input", Object.getOwnPropertyDescriptor(RegExp, "input"));

})(this);
