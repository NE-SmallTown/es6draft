/*
 * Copyright (c) André Bargull
 * Alle Rechte vorbehalten / All Rights Reserved.  Use is subject to license terms.
 *
 * <https://github.com/anba/es6draft>
 */
/*---
esid: sec-intl.getcanonicallocales
description: >
  Call Intl.getCanonicalLocales function with valid language tags.
info: >
  8.2.1 Intl.getCanonicalLocales (locales)
    1. Let ll be ? CanonicalizeLocaleList(locales).
    2. Return CreateArrayFromList(ll).

  9.2.1 CanonicalizeLocaleList (locales)
    ...
    7. Repeat, while k < len
      a. Let Pk be ToString(k).
      b. Let kPresent be ? HasProperty(O, Pk).
      c. If kPresent is true, then
        i. Let kValue be ? Get(O, Pk).
        ...
        iii. Let tag be ? ToString(kValue).
        ...
        v. Let canonicalizedTag be CanonicalizeLanguageTag(tag).
        vi. If canonicalizedTag is not an element of seen, append canonicalizedTag as the last element of seen.
      ...
includes: [testIntl.js]
---*/

var canonicalizedTags = {
  "de": "de",
  "DE-de": "de-DE",
  "de-DE": "de-DE",
  "cmn": "cmn",
  "CMN-hANS": "cmn-Hans",
  "cmn-hans-cn": "cmn-Hans-CN",
  "es-419": "es-419",
  "es-419-u-nu-latn": "es-419-u-nu-latn",
  "cmn-hans-cn-u-ca-t-ca-x-t-u": "cmn-Hans-CN-t-ca-u-ca-x-t-u",
  "de-gregory-u-ca-gregory": "de-gregory-u-ca-gregory",
  "no-nyn": "nn",
  "i-klingon": "tlh",
  "sgn-GR": "gss",
  "ji": "yi",
  "de-DD": "de-DE",
  "zh-hak-CN": "hak-CN",
  "sgn-ils": "ils",
  "in": "id",
  "x-foo": "x-foo",
  "sr-cyrl-ekavsk": "sr-Cyrl-ekavsk",
  "en-ca-newfound": "en-CA-newfound",
  "sl-rozaj-biske-1994": "sl-rozaj-biske-1994",
  "da-u-attr": "da-u-attr",
  "da-u-attr-co-search": "da-u-attr-co-search",
};

// make sure the data above is correct
Object.getOwnPropertyNames(canonicalizedTags).forEach(function (tag) {
  var canonicalizedTag = canonicalizedTags[tag];
  assert(
    isCanonicalizedStructurallyValidLanguageTag(canonicalizedTag),
    "Test data \"" + canonicalizedTag + "\" is not canonicalized and structurally valid language tag."
  );
});

Object.getOwnPropertyNames(canonicalizedTags).forEach(function (tag) {
  var canonicalLocales = Intl.getCanonicalLocales(tag);
  assert.sameValue(canonicalLocales.length, 1);
  assert.sameValue(canonicalLocales[0], canonicalizedTags[tag]);
});
