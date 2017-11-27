/*
 * Copyright (c) André Bargull
 * Alle Rechte vorbehalten / All Rights Reserved.  Use is subject to license terms.
 *
 * <https://github.com/anba/es6draft>
 */
function test(start, end) {
  for (let cp = start; cp <= end;) {
    let source = "var obj = {};\n";
    for (let i = 0; cp <= end && i < 1000; ++cp, ++i) {
      source += `obj.A${String.fromCodePoint(cp)};\n`;
    }
    eval(source);
  }
}

// Delta compared to Unicode 9.0
test(0x0860, 0x086a);
test(0x09fc, 0x09fc);
test(0x0afa, 0x0aff);
test(0x0d00, 0x0d01);
test(0x0d3b, 0x0d3c);
test(0x1cf7, 0x1cf7);
test(0x1dc0, 0x1df9);
test(0x3105, 0x312e);
test(0x4e00, 0x9fea);
test(0x1032d, 0x10340);
test(0x11a00, 0x11a00);
test(0x11a01, 0x11a06);
test(0x11a07, 0x11a08);
test(0x11a09, 0x11a0a);
test(0x11a0b, 0x11a32);
test(0x11a33, 0x11a38);
test(0x11a39, 0x11a39);
test(0x11a3a, 0x11a3a);
test(0x11a3b, 0x11a3e);
test(0x11a47, 0x11a47);
test(0x11a50, 0x11a50);
test(0x11a51, 0x11a56);
test(0x11a57, 0x11a58);
test(0x11a59, 0x11a5b);
test(0x11a5c, 0x11a83);
test(0x11a86, 0x11a89);
test(0x11a8a, 0x11a96);
test(0x11a97, 0x11a97);
test(0x11a98, 0x11a99);
test(0x11d00, 0x11d06);
test(0x11d08, 0x11d09);
test(0x11d0b, 0x11d30);
test(0x11d31, 0x11d36);
test(0x11d3a, 0x11d3a);
test(0x11d3c, 0x11d3d);
test(0x11d3f, 0x11d45);
test(0x11d46, 0x11d46);
test(0x11d47, 0x11d47);
test(0x11d50, 0x11d59);
test(0x16fe0, 0x16fe1);
test(0x1b000, 0x1b11e);
test(0x1b170, 0x1b2fb);
test(0x2ceb0, 0x2ebe0);