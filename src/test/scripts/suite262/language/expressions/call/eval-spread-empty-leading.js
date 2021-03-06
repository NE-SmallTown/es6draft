/*
 * Copyright (c) André Bargull
 * Alle Rechte vorbehalten / All Rights Reserved.  Use is subject to license terms.
 *
 * <https://github.com/anba/es6draft>
 */
/*---
id: sec-function-calls-runtime-semantics-evaluation
info: |
  Direct eval call with empty leading spread.
description: >
  12.3.4.1 Runtime Semantics: Evaluation
    ...
    3. If Type(ref) is Reference and IsPropertyReference(ref) is false and GetReferencedName(ref) is "eval", then
      a. If SameValue(func, %eval%) is true, then
        i. Let argList be ? ArgumentListEvaluation(Arguments).
        ii. If argList has no elements, return undefined.
        iii. Let evalText be the first element of argList.
        ...

features: [Symbol.iterator]
---*/

var nextCount = 0;
var iter = {};
iter[Symbol.iterator] = function() {
  return {
    next: function() {
      var i = nextCount++;
      return {done: true, value: undefined};
    }
  };
};

var x = "global";

(function() {
  var x = "local";
  eval(...iter, "x = 0;");
  assert.sameValue(x, 0);
})();

assert.sameValue(x, "global");
assert.sameValue(nextCount, 1);
