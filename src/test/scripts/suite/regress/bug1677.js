/*
 * Copyright (c) 2012-2014 André Bargull
 * Alle Rechte vorbehalten / All Rights Reserved.  Use is subject to license terms.
 *
 * <https://github.com/anba/es6draft>
 */

const {
  assertThrows, assertSame
} = Assert;

// 15.13.6: [[Call]] and [[Construct]] behaviour missing for TypedArray constructors
// https://bugs.ecmascript.org/show_bug.cgi?id=1677

const global = this;
for (const type of ["Int8", "Uint8", "Uint8Clamped", "Int16", "Uint16", "Int32", "Uint32", "Float32", "Float64"]) {
  const ctor = global[`${type}Array`];
  assertSame(0, new ctor(0).length);
  assertThrows(() => ctor(), TypeError);
  assertSame(0, ctor.call(ctor[Symbol.create](), 0).length);
}
