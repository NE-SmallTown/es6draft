/*
 * Copyright (c) 2012-2016 André Bargull
 * Alle Rechte vorbehalten / All Rights Reserved.  Use is subject to license terms.
 *
 * <https://github.com/anba/es6draft>
 */
const {
  assertSame, assertThrows, assertSyntaxError,
} = Assert;

// 12.2.5.9 PropertyDefinitionEvaluation: Don't set [[HomeObject]]
// https://bugs.ecmascript.org/show_bug.cgi?id=3931

assertSyntaxError(`
var o = {
  f: function() { super.m }
};
`);

assertSyntaxError(`
var o = {
  g: function*() { super.m }
};
`);

var obj = {
  __proto__: {
    m() {
      return "ok";
    }
  },
  m() {
    return eval("super.m()");
  },
  f: function() {
    return eval("super.m()");
  },
  g: function*() {
    return eval("super.m()");
  },
};

assertSame("ok", obj.m());
assertThrows(ReferenceError, () => obj.f());
assertThrows(ReferenceError, () => obj.g().next());
