/*
 * Copyright (c) 2012-2014 André Bargull
 * Alle Rechte vorbehalten / All Rights Reserved.  Use is subject to license terms.
 *
 * <https://github.com/anba/es6draft>
 */

(function Generator() {
"use strict";

const global = %GlobalObject();

const {
  Object, Symbol, TypeError,
} = global;

const Object_getPrototypeOf = Object.getPrototypeOf;
const Generator = Object_getPrototypeOf(function*(){});

Object.defineProperty(Generator, Symbol.hasInstance, {
  value(O) {
    // OrdinaryHasInstance() without steps 1-2
    let C = this;
    if (Object(O) !== O) {
      return false;
    }
    let P = C.prototype;
    if (Object(P) !== P) {
      throw TypeError();
    }
    for (;;) {
      O = Object_getPrototypeOf(O);
      if (O === null) {
        return false;
      }
      if (P === O) {
        return true;
      }
    }
  },
  writable: false, enumerable: false, configurable: true
});

Object.defineProperty(Object.prototype, Symbol.iterator, {
  get() {
    if (typeof this.next === "function") {
      return () => ({__proto__: null, next: () => Object(this.next())})
    }
  },
  enumerable: false, configurable: true
});

})();
