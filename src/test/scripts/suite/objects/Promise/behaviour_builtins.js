/*
 * Copyright (c) 2012-2014 André Bargull
 * Alle Rechte vorbehalten / All Rights Reserved.  Use is subject to license terms.
 *
 * <https://github.com/anba/es6draft>
 */

const {
  fail,
  assertTrue,
  assertSame,
  assertUndefined,
  assertNativeFunction,
} = Assert;

loadRelativeToScript("../../lib/promises.js");

const {
  reportFailure
} = Promises;

// Test access to built-in functions:
// - DeferredConstructionFunction
// - ResolvePromiseFunction
// - RejectPromiseFunction
// - IdentityFunction
// - ThrowerFunction
// - PromiseAllCountdownFunction

// Access to 'DeferredConstructionFunction'
{
  function F(resolver) {
    Object.assign(this, {resolver});
    resolver(() => {}, () => {});
  }

  let o = Promise.resolve.call(F);
  assertNativeFunction(o.resolver, undefined, 2);
}

// Access to 'ResolvePromiseFunction' and 'RejectPromiseFunction'
{
  let result = {};
  new Promise((resolve, reject) => {
    Object.assign(result, {resolve, reject});
  });
  assertNativeFunction(result.resolve, undefined, 1);
  assertNativeFunction(result.reject, undefined, 1);
}

// Access to 'ResolvePromiseFunction' and 'RejectPromiseFunction'
{
  let stepCount = 0;
  let builtins = {
    identity: void 0,
    thrower: void 0,
    resolvePromise: void 0,
    rejectPromise: void 0,
  };
  class XPromise extends Promise {
    then(fulfill, reject) {
      let step = ++stepCount;
      assertTrue(1 <= step && step <= 4, `step = ${step}`);
      switch (step) {
        case 1:
          assertUndefined(fulfill);
          assertUndefined(reject);
          break;
        case 2:
          assertSame(testBuiltinFunctions, fulfill);
          assertUndefined(reject);
          break;
        case 3:
          assertUndefined(fulfill);
          assertSame(reportFailure, reject);
          break;
        case 4:
          // 'ResolvePromiseFunction' and 'RejectPromiseFunction'
          builtins.resolvePromise = fulfill;
          builtins.rejectPromise = reject;
          assertNativeFunction(fulfill, undefined, 1);
          assertNativeFunction(reject, undefined, 1);
          break;
      }
      return super(fulfill, reject);
    }

    static deferred() {
      let result = {};
      result.promise = new this((resolve, reject) => {
        Object.assign(result, {resolve, reject});
      });
      return result;
    }
  }

  function testBuiltinFunctions() {
    assertSame(4, stepCount);

    // calling these functions should have no effect
    builtins.resolvePromise();
    builtins.rejectPromise();
  }

  let d = XPromise.deferred();
  d.promise.then()
           .then(testBuiltinFunctions)
           .catch(reportFailure);
  d.resolve(XPromise.resolve());
}

// Access to 'PromiseAllCountdownFunction'
{
  let allCountdownFunction;
  class P {
    then(fulfill) {
      allCountdownFunction = fulfill;
    }
  }
  class F {
    constructor(r) {
      r(() => {}, () => {});
    }

    static resolve(v) {
      return v;
    }
  }
  Promise.all.call(F, [new P]);

  assertNativeFunction(allCountdownFunction, undefined, 1);
}
