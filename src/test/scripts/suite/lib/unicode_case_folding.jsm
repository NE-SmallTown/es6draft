/*
 * Copyright (c) 2012-2015 André Bargull
 * Alle Rechte vorbehalten / All Rights Reserved.  Use is subject to license terms.
 *
 * <https://github.com/anba/es6draft>
 */
import range from "lib/range.jsm";

export function UnicodeCaseFolding(test, {latin, basic, supplementary}) {
  test(range('A', 'Z'), range('a', 'z'), latin);

  // Generated from CaseFolding-6.3.0.txt
  test(range(0x41, 0x49, 1), range(0x61, 0x69, 1), latin);
  test(range(0x4a, 0x5a, 1), range(0x6a, 0x7a, 1), latin);
  test(range(0xb5, 0xb5, 1), range(0x3bc, 0x3bc, 1), basic);
  test(range(0xc0, 0xd6, 1), range(0xe0, 0xf6, 1), latin);
  test(range(0xd8, 0xde, 1), range(0xf8, 0xfe, 1), latin);
  test(range(0x100, 0x12e, 2), range(0x101, 0x12f, 2), basic);
  test(range(0x132, 0x136, 2), range(0x133, 0x137, 2), basic);
  test(range(0x139, 0x147, 2), range(0x13a, 0x148, 2), basic);
  test(range(0x14a, 0x176, 2), range(0x14b, 0x177, 2), basic);
  test(range(0x178, 0x178, 1), range(0xff, 0xff, 1), basic);
  test(range(0x179, 0x17d, 2), range(0x17a, 0x17e, 2), basic);
  test(range(0x17f, 0x17f, 1), range(0x73, 0x73, 1), basic, {unicode: true});
  test(range(0x181, 0x181, 1), range(0x253, 0x253, 1), basic);
  test(range(0x182, 0x184, 2), range(0x183, 0x185, 2), basic);
  test(range(0x186, 0x186, 1), range(0x254, 0x254, 1), basic);
  test(range(0x187, 0x187, 1), range(0x188, 0x188, 1), basic);
  test(range(0x189, 0x18a, 1), range(0x256, 0x257, 1), basic);
  test(range(0x18b, 0x18b, 1), range(0x18c, 0x18c, 1), basic);
  test(range(0x18e, 0x18e, 1), range(0x1dd, 0x1dd, 1), basic);
  test(range(0x18f, 0x18f, 1), range(0x259, 0x259, 1), basic);
  test(range(0x190, 0x190, 1), range(0x25b, 0x25b, 1), basic);
  test(range(0x191, 0x191, 1), range(0x192, 0x192, 1), basic);
  test(range(0x193, 0x193, 1), range(0x260, 0x260, 1), basic);
  test(range(0x194, 0x194, 1), range(0x263, 0x263, 1), basic);
  test(range(0x196, 0x196, 1), range(0x269, 0x269, 1), basic);
  test(range(0x197, 0x197, 1), range(0x268, 0x268, 1), basic);
  test(range(0x198, 0x198, 1), range(0x199, 0x199, 1), basic);
  test(range(0x19c, 0x19c, 1), range(0x26f, 0x26f, 1), basic);
  test(range(0x19d, 0x19d, 1), range(0x272, 0x272, 1), basic);
  test(range(0x19f, 0x19f, 1), range(0x275, 0x275, 1), basic);
  test(range(0x1a0, 0x1a4, 2), range(0x1a1, 0x1a5, 2), basic);
  test(range(0x1a6, 0x1a6, 1), range(0x280, 0x280, 1), basic);
  test(range(0x1a7, 0x1a7, 1), range(0x1a8, 0x1a8, 1), basic);
  test(range(0x1a9, 0x1a9, 1), range(0x283, 0x283, 1), basic);
  test(range(0x1ac, 0x1ac, 1), range(0x1ad, 0x1ad, 1), basic);
  test(range(0x1ae, 0x1ae, 1), range(0x288, 0x288, 1), basic);
  test(range(0x1af, 0x1af, 1), range(0x1b0, 0x1b0, 1), basic);
  test(range(0x1b1, 0x1b2, 1), range(0x28a, 0x28b, 1), basic);
  test(range(0x1b3, 0x1b5, 2), range(0x1b4, 0x1b6, 2), basic);
  test(range(0x1b7, 0x1b7, 1), range(0x292, 0x292, 1), basic);
  test(range(0x1b8, 0x1bc, 4), range(0x1b9, 0x1bd, 4), basic);
  test(range(0x1c4, 0x1c4, 1), range(0x1c6, 0x1c6, 1), basic);
  test(range(0x1c5, 0x1c5, 1), range(0x1c6, 0x1c6, 1), basic);
  test(range(0x1c7, 0x1c7, 1), range(0x1c9, 0x1c9, 1), basic);
  test(range(0x1c8, 0x1c8, 1), range(0x1c9, 0x1c9, 1), basic);
  test(range(0x1ca, 0x1ca, 1), range(0x1cc, 0x1cc, 1), basic);
  test(range(0x1cb, 0x1db, 2), range(0x1cc, 0x1dc, 2), basic);
  test(range(0x1de, 0x1ee, 2), range(0x1df, 0x1ef, 2), basic);
  test(range(0x1f1, 0x1f1, 1), range(0x1f3, 0x1f3, 1), basic);
  test(range(0x1f2, 0x1f4, 2), range(0x1f3, 0x1f5, 2), basic);
  test(range(0x1f6, 0x1f6, 1), range(0x195, 0x195, 1), basic);
  test(range(0x1f7, 0x1f7, 1), range(0x1bf, 0x1bf, 1), basic);
  test(range(0x1f8, 0x21e, 2), range(0x1f9, 0x21f, 2), basic);
  test(range(0x220, 0x220, 1), range(0x19e, 0x19e, 1), basic);
  test(range(0x222, 0x232, 2), range(0x223, 0x233, 2), basic);
  test(range(0x23a, 0x23a, 1), range(0x2c65, 0x2c65, 1), basic);
  test(range(0x23b, 0x23b, 1), range(0x23c, 0x23c, 1), basic);
  test(range(0x23d, 0x23d, 1), range(0x19a, 0x19a, 1), basic);
  test(range(0x23e, 0x23e, 1), range(0x2c66, 0x2c66, 1), basic);
  test(range(0x241, 0x241, 1), range(0x242, 0x242, 1), basic);
  test(range(0x243, 0x243, 1), range(0x180, 0x180, 1), basic);
  test(range(0x244, 0x244, 1), range(0x289, 0x289, 1), basic);
  test(range(0x245, 0x245, 1), range(0x28c, 0x28c, 1), basic);
  test(range(0x246, 0x24e, 2), range(0x247, 0x24f, 2), basic);
  test(range(0x345, 0x345, 1), range(0x3b9, 0x3b9, 1), basic);
  test(range(0x370, 0x372, 2), range(0x371, 0x373, 2), basic);
  test(range(0x376, 0x376, 1), range(0x377, 0x377, 1), basic);
  test(range(0x386, 0x386, 1), range(0x3ac, 0x3ac, 1), basic);
  test(range(0x388, 0x38a, 1), range(0x3ad, 0x3af, 1), basic);
  test(range(0x38c, 0x38c, 1), range(0x3cc, 0x3cc, 1), basic);
  test(range(0x38e, 0x38f, 1), range(0x3cd, 0x3ce, 1), basic);
  test(range(0x391, 0x3a1, 1), range(0x3b1, 0x3c1, 1), basic);
  test(range(0x3a3, 0x3ab, 1), range(0x3c3, 0x3cb, 1), basic);
  test(range(0x3c2, 0x3c2, 1), range(0x3c3, 0x3c3, 1), basic);
  test(range(0x3cf, 0x3cf, 1), range(0x3d7, 0x3d7, 1), basic);
  test(range(0x3d0, 0x3d0, 1), range(0x3b2, 0x3b2, 1), basic);
  test(range(0x3d1, 0x3d1, 1), range(0x3b8, 0x3b8, 1), basic);
  test(range(0x3d5, 0x3d5, 1), range(0x3c6, 0x3c6, 1), basic);
  test(range(0x3d6, 0x3d6, 1), range(0x3c0, 0x3c0, 1), basic);
  test(range(0x3d8, 0x3ee, 2), range(0x3d9, 0x3ef, 2), basic);
  test(range(0x3f0, 0x3f0, 1), range(0x3ba, 0x3ba, 1), basic);
  test(range(0x3f1, 0x3f1, 1), range(0x3c1, 0x3c1, 1), basic);
  test(range(0x3f4, 0x3f4, 1), range(0x3b8, 0x3b8, 1), basic, {unicode: true});
  test(range(0x3f5, 0x3f5, 1), range(0x3b5, 0x3b5, 1), basic);
  test(range(0x3f7, 0x3f7, 1), range(0x3f8, 0x3f8, 1), basic);
  test(range(0x3f9, 0x3f9, 1), range(0x3f2, 0x3f2, 1), basic);
  test(range(0x3fa, 0x3fa, 1), range(0x3fb, 0x3fb, 1), basic);
  test(range(0x3fd, 0x3ff, 1), range(0x37b, 0x37d, 1), basic);
  test(range(0x400, 0x40f, 1), range(0x450, 0x45f, 1), basic);
  test(range(0x410, 0x42f, 1), range(0x430, 0x44f, 1), basic);
  test(range(0x460, 0x480, 2), range(0x461, 0x481, 2), basic);
  test(range(0x48a, 0x4be, 2), range(0x48b, 0x4bf, 2), basic);
  test(range(0x4c0, 0x4c0, 1), range(0x4cf, 0x4cf, 1), basic);
  test(range(0x4c1, 0x4cd, 2), range(0x4c2, 0x4ce, 2), basic);
  test(range(0x4d0, 0x526, 2), range(0x4d1, 0x527, 2), basic);
  test(range(0x531, 0x556, 1), range(0x561, 0x586, 1), basic);
  test(range(0x10a0, 0x10c5, 1), range(0x2d00, 0x2d25, 1), basic);
  // test(range(0x10c7, 0x10cd, 6), range(0x2d27, 0x2d2d, 6), basic); // requires Unicode Data update
  test(range(0x1e00, 0x1e94, 2), range(0x1e01, 0x1e95, 2), basic);
  test(range(0x1e9b, 0x1e9b, 1), range(0x1e61, 0x1e61, 1), basic);
  test(range(0x1e9e, 0x1e9e, 1), range(0xdf, 0xdf, 1), basic, {unicode: true});
  test(range(0x1ea0, 0x1efe, 2), range(0x1ea1, 0x1eff, 2), basic);
  test(range(0x1f08, 0x1f0f, 1), range(0x1f00, 0x1f07, 1), basic);
  test(range(0x1f18, 0x1f1d, 1), range(0x1f10, 0x1f15, 1), basic);
  test(range(0x1f28, 0x1f2f, 1), range(0x1f20, 0x1f27, 1), basic);
  test(range(0x1f38, 0x1f3f, 1), range(0x1f30, 0x1f37, 1), basic);
  test(range(0x1f48, 0x1f4d, 1), range(0x1f40, 0x1f45, 1), basic);
  test(range(0x1f59, 0x1f5f, 2), range(0x1f51, 0x1f57, 2), basic);
  test(range(0x1f68, 0x1f6f, 1), range(0x1f60, 0x1f67, 1), basic);
  test(range(0x1f88, 0x1f88, 1), range(0x1f80, 0x1f80, 1), basic);
  test(range(0x1f89, 0x1f89, 1), range(0x1f81, 0x1f81, 1), basic);
  test(range(0x1f8a, 0x1f8a, 1), range(0x1f82, 0x1f82, 1), basic);
  test(range(0x1f8b, 0x1f8b, 1), range(0x1f83, 0x1f83, 1), basic);
  test(range(0x1f8c, 0x1f8c, 1), range(0x1f84, 0x1f84, 1), basic);
  test(range(0x1f8d, 0x1f8d, 1), range(0x1f85, 0x1f85, 1), basic);
  test(range(0x1f8e, 0x1f8e, 1), range(0x1f86, 0x1f86, 1), basic);
  test(range(0x1f8f, 0x1f8f, 1), range(0x1f87, 0x1f87, 1), basic);
  test(range(0x1f98, 0x1f98, 1), range(0x1f90, 0x1f90, 1), basic);
  test(range(0x1f99, 0x1f99, 1), range(0x1f91, 0x1f91, 1), basic);
  test(range(0x1f9a, 0x1f9a, 1), range(0x1f92, 0x1f92, 1), basic);
  test(range(0x1f9b, 0x1f9b, 1), range(0x1f93, 0x1f93, 1), basic);
  test(range(0x1f9c, 0x1f9c, 1), range(0x1f94, 0x1f94, 1), basic);
  test(range(0x1f9d, 0x1f9d, 1), range(0x1f95, 0x1f95, 1), basic);
  test(range(0x1f9e, 0x1f9e, 1), range(0x1f96, 0x1f96, 1), basic);
  test(range(0x1f9f, 0x1f9f, 1), range(0x1f97, 0x1f97, 1), basic);
  test(range(0x1fa8, 0x1fa8, 1), range(0x1fa0, 0x1fa0, 1), basic);
  test(range(0x1fa9, 0x1fa9, 1), range(0x1fa1, 0x1fa1, 1), basic);
  test(range(0x1faa, 0x1faa, 1), range(0x1fa2, 0x1fa2, 1), basic);
  test(range(0x1fab, 0x1fab, 1), range(0x1fa3, 0x1fa3, 1), basic);
  test(range(0x1fac, 0x1fac, 1), range(0x1fa4, 0x1fa4, 1), basic);
  test(range(0x1fad, 0x1fad, 1), range(0x1fa5, 0x1fa5, 1), basic);
  test(range(0x1fae, 0x1fae, 1), range(0x1fa6, 0x1fa6, 1), basic);
  test(range(0x1faf, 0x1faf, 1), range(0x1fa7, 0x1fa7, 1), basic);
  test(range(0x1fb8, 0x1fb9, 1), range(0x1fb0, 0x1fb1, 1), basic);
  test(range(0x1fba, 0x1fbb, 1), range(0x1f70, 0x1f71, 1), basic);
  test(range(0x1fbc, 0x1fbc, 1), range(0x1fb3, 0x1fb3, 1), basic);
  test(range(0x1fbe, 0x1fbe, 1), range(0x3b9, 0x3b9, 1), basic);
  test(range(0x1fc8, 0x1fcb, 1), range(0x1f72, 0x1f75, 1), basic);
  test(range(0x1fcc, 0x1fcc, 1), range(0x1fc3, 0x1fc3, 1), basic);
  test(range(0x1fd8, 0x1fd9, 1), range(0x1fd0, 0x1fd1, 1), basic);
  test(range(0x1fda, 0x1fdb, 1), range(0x1f76, 0x1f77, 1), basic);
  test(range(0x1fe8, 0x1fe9, 1), range(0x1fe0, 0x1fe1, 1), basic);
  test(range(0x1fea, 0x1feb, 1), range(0x1f7a, 0x1f7b, 1), basic);
  test(range(0x1fec, 0x1fec, 1), range(0x1fe5, 0x1fe5, 1), basic);
  test(range(0x1ff8, 0x1ff9, 1), range(0x1f78, 0x1f79, 1), basic);
  test(range(0x1ffa, 0x1ffb, 1), range(0x1f7c, 0x1f7d, 1), basic);
  test(range(0x1ffc, 0x1ffc, 1), range(0x1ff3, 0x1ff3, 1), basic);
  test(range(0x2126, 0x2126, 1), range(0x3c9, 0x3c9, 1), basic, {unicode: true});
  test(range(0x212a, 0x212a, 1), range(0x6b, 0x6b, 1), basic, {unicode: true});
  test(range(0x212b, 0x212b, 1), range(0xe5, 0xe5, 1), basic, {unicode: true});
  test(range(0x2132, 0x2132, 1), range(0x214e, 0x214e, 1), basic);
  test(range(0x2160, 0x216f, 1), range(0x2170, 0x217f, 1), basic);
  test(range(0x2183, 0x2183, 1), range(0x2184, 0x2184, 1), basic);
  test(range(0x24b6, 0x24cf, 1), range(0x24d0, 0x24e9, 1), basic);
  test(range(0x2c00, 0x2c2e, 1), range(0x2c30, 0x2c5e, 1), basic);
  test(range(0x2c60, 0x2c60, 1), range(0x2c61, 0x2c61, 1), basic);
  test(range(0x2c62, 0x2c62, 1), range(0x26b, 0x26b, 1), basic);
  test(range(0x2c63, 0x2c63, 1), range(0x1d7d, 0x1d7d, 1), basic);
  test(range(0x2c64, 0x2c64, 1), range(0x27d, 0x27d, 1), basic);
  test(range(0x2c67, 0x2c6b, 2), range(0x2c68, 0x2c6c, 2), basic);
  test(range(0x2c6d, 0x2c6d, 1), range(0x251, 0x251, 1), basic);
  test(range(0x2c6e, 0x2c6e, 1), range(0x271, 0x271, 1), basic);
  test(range(0x2c6f, 0x2c6f, 1), range(0x250, 0x250, 1), basic);
  test(range(0x2c70, 0x2c70, 1), range(0x252, 0x252, 1), basic);
  test(range(0x2c72, 0x2c75, 3), range(0x2c73, 0x2c76, 3), basic);
  test(range(0x2c7e, 0x2c7f, 1), range(0x23f, 0x240, 1), basic);
  test(range(0x2c80, 0x2ce2, 2), range(0x2c81, 0x2ce3, 2), basic);
  test(range(0x2ceb, 0x2ced, 2), range(0x2cec, 0x2cee, 2), basic);
  // test(range(0x2cf2, 0xa640, 31054), range(0x2cf3, 0xa641, 31054), basic); // requires Unicode Data update
  test(range(0xa642, 0xa66c, 2), range(0xa643, 0xa66d, 2), basic);
  test(range(0xa680, 0xa696, 2), range(0xa681, 0xa697, 2), basic);
  test(range(0xa722, 0xa72e, 2), range(0xa723, 0xa72f, 2), basic);
  test(range(0xa732, 0xa76e, 2), range(0xa733, 0xa76f, 2), basic);
  test(range(0xa779, 0xa77b, 2), range(0xa77a, 0xa77c, 2), basic);
  test(range(0xa77d, 0xa77d, 1), range(0x1d79, 0x1d79, 1), basic);
  test(range(0xa77e, 0xa786, 2), range(0xa77f, 0xa787, 2), basic);
  test(range(0xa78b, 0xa78b, 1), range(0xa78c, 0xa78c, 1), basic);
  test(range(0xa78d, 0xa78d, 1), range(0x265, 0x265, 1), basic);
  // test(range(0xa790, 0xa792, 2), range(0xa791, 0xa793, 2), basic); // requires Unicode Data update
  test(range(0xa7a0, 0xa7a8, 2), range(0xa7a1, 0xa7a9, 2), basic);
  // test(range(0xa7aa, 0xa7aa, 1), range(0x266, 0x266, 1), basic); // requires Unicode Data update
  test(range(0xff21, 0xff3a, 1), range(0xff41, 0xff5a, 1), basic);
  test(range(0x10400, 0x10427, 1), range(0x10428, 0x1044f, 1), supplementary);
}
