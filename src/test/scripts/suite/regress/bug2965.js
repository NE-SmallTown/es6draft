/*
 * Copyright (c) 2012-2014 André Bargull
 * Alle Rechte vorbehalten / All Rights Reserved.  Use is subject to license terms.
 *
 * <https://github.com/anba/es6draft>
 */

const {
  assertSame, assertNotSame, fail
} = Assert;

// 7.3.16 GetPrototypeFromConstructor, 22.1.3.* Array.prototype.*: Use BoundFunctionTargetRealm to retrieve [[Realm]] of bound functions?
// https://bugs.ecmascript.org/show_bug.cgi?id=2965

const {
  Array: foreignArray,
  Object: foreignObject,
} = (new Reflect.Realm).global;

// GetPrototypeFromConstructor tests
{
  function C() {}
  C.prototype = {};
  let o = Object[Symbol.create].call(C);
  assertSame(C.prototype, Object.getPrototypeOf(o));
}
{
  function C() {}
  C.prototype = null;
  let o = Object[Symbol.create].call(C);
  assertSame(Object.prototype, Object.getPrototypeOf(o));
}
{
  function C() {}
  C.prototype = {};
  let o = foreignObject[Symbol.create].call(C);
  assertSame(C.prototype, Object.getPrototypeOf(o));
}
{
  function C() {}
  C.prototype = null;
  let o = foreignObject[Symbol.create].call(C);
  assertSame(Object.prototype, Object.getPrototypeOf(o));
}

// Array.prototype.concat
// Array.prototype.slice
// Array.prototype.splice
// Array.prototype.map
// Array.prototype.filter
for (
let {method, args} of [
  {method: "concat", args: []},
  {method: "slice", args: []},
  {method: "splice", args: []},
  {method: "map", args: [() => {}]},
  {method: "filter", args: [() => {}]},
]) {
  function testSameRealm(fn) {
    let src = [], dst = [];
    fn(src, dst);
    assertSame(dst, src[method](...args), `method = ${method}`);
  }
  function testCrossRealm(fn) {
    let src = new foreignArray(), dst = [];
    fn(src, dst);
    assertNotSame(dst, src[method](...args), `method = ${method}`);
  }
  for (let test of [testSameRealm, testCrossRealm]) {
    test(
      (src, dst) => src.constructor = function() { return dst }
    );
    test(
      (src, dst) => src.constructor = function() { return dst }.bind(null)
    );
    test(
      (src, dst) => src.constructor = new Proxy(function() { fail `trap not called` }, {construct() { return dst }})
    );
  }
}
