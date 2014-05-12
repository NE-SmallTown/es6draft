/*
 * Copyright (c) 2012-2014 André Bargull
 * Alle Rechte vorbehalten / All Rights Reserved.  Use is subject to license terms.
 *
 * <https://github.com/anba/es6draft>
 */

const {
  assertSame, assertThrows
} = Assert;

// 26.2.3.7.1 Reflect.Realm.prototype.directEval: Type checks don't match NOTE
// https://bugs.ecmascript.org/show_bug.cgi?id=2788

let source = "123";
assertThrows(() => Reflect.Realm.prototype.directEval.call(void 0, source), TypeError);
assertSame(source, Reflect.Realm.prototype.directEval.call(new Reflect.Realm, source));
