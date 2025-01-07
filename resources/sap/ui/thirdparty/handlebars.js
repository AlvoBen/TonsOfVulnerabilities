﻿/*

Copyright (C) 2011 by Yehuda Katz

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.

*/

var Handlebars = {};
(function(H, u) {;
    H.VERSION = "1.0.0";
    H.COMPILER_REVISION = 4;
    H.REVISION_CHANGES = {
        1: '<= 1.0.rc.2',
        2: '== 1.0.0-rc.3',
        3: '== 1.0.0-rc.4',
        4: '>= 1.0.0'
    };
    H.helpers = {};
    H.partials = {};
    var t = Object.prototype.toString,
        f = '[object Function]',
        o = '[object Object]';
    H.registerHelper = function(n, a, b) {
        if (t.call(n) === o) {
            if (b || a) {
                throw new H.Exception('Arg not supported with multiple helpers')
            }
            H.Utils.extend(this.helpers, n)
        } else {
            if (b) {
                a.not = b
            }
            this.helpers[n] = a
        }
    };
    H.registerPartial = function(n, a) {
        if (t.call(n) === o) {
            H.Utils.extend(this.partials, n)
        } else {
            this.partials[n] = a
        }
    };
    H.registerHelper('helperMissing', function(a) {
        if (arguments.length === 2) {
            return u
        } else {
            throw new Error("Missing helper: '" + a + "'")
        }
    });
    H.registerHelper('blockHelperMissing', function(c, a) {
        var b = a.inverse || function() {}, j = a.fn;
        var n = t.call(c);
        if (n === f) {
            c = c.call(this)
        }
        if (c === true) {
            return j(this)
        } else if (c === false || c == null) {
            return b(this)
        } else if (n === "[object Array]") {
            if (c.length > 0) {
                return H.helpers.each(c, a)
            } else {
                return b(this)
            }
        } else {
            return j(c)
        }
    });
    H.K = function() {};
    H.createFrame = Object.create || function(a) {
        H.K.prototype = a;
        var b = new H.K();
        H.K.prototype = null;
        return b
    };
    H.logger = {
        DEBUG: 0,
        INFO: 1,
        WARN: 2,
        ERROR: 3,
        level: 3,
        methodMap: {
            0: 'debug',
            1: 'info',
            2: 'warn',
            3: 'error'
        },
        log: function(a, b) {
            if (H.logger.level <= a) {
                var c = H.logger.methodMap[a];
                if (typeof console !== 'undefined' && console[c]) {
                    console[c].call(console, b)
                }
            }
        }
    };
    H.log = function(a, b) {
        H.logger.log(a, b)
    };
    H.registerHelper('each', function(c, a) {
        var b = a.fn,
            n = a.inverse;
        var i = 0,
            r = "",
            p;
        var v = t.call(c);
        if (v === f) {
            c = c.call(this)
        }
        if (a.data) {
            p = H.createFrame(a.data)
        }
        if (c && typeof c === 'object') {
            if (c instanceof Array) {
                for (var j = c.length; i < j; i++) {
                    if (p) {
                        p.index = i
                    }
                    r = r + b(c[i], {
                        data: p
                    })
                }
            } else {
                for (var w in c) {
                    if (c.hasOwnProperty(w)) {
                        if (p) {
                            p.key = w
                        }
                        r = r + b(c[w], {
                            data: p
                        });
                        i++
                    }
                }
            }
        }
        if (i === 0) {
            r = n(this)
        }
        return r
    });
    H.registerHelper('if', function(c, a) {
        var b = t.call(c);
        if (b === f) {
            c = c.call(this)
        }
        if (!c || H.Utils.isEmpty(c)) {
            return a.inverse(this)
        } else {
            return a.fn(this)
        }
    });
    H.registerHelper('unless', function(c, a) {
        return H.helpers['if'].call(this, c, {
            fn: a.inverse,
            inverse: a.fn
        })
    });
    H.registerHelper('with', function(c, a) {
        var b = t.call(c);
        if (b === f) {
            c = c.call(this)
        }
        if (!H.Utils.isEmpty(c)) return a.fn(c)
    });
    H.registerHelper('log', function(c, a) {
        var b = a.data && a.data.level != null ? parseInt(a.data.level, 10) : 1;
        H.log(b, c)
    });;
    var h = (function() {
        var b = {
            trace: function trace() {},
            yy: {},
            symbols_: {
                "error": 2,
                "root": 3,
                "program": 4,
                "EOF": 5,
                "simpleInverse": 6,
                "statements": 7,
                "statement": 8,
                "openInverse": 9,
                "closeBlock": 10,
                "openBlock": 11,
                "mustache": 12,
                "partial": 13,
                "CONTENT": 14,
                "COMMENT": 15,
                "OPEN_BLOCK": 16,
                "inMustache": 17,
                "CLOSE": 18,
                "OPEN_INVERSE": 19,
                "OPEN_ENDBLOCK": 20,
                "path": 21,
                "OPEN": 22,
                "OPEN_UNESCAPED": 23,
                "CLOSE_UNESCAPED": 24,
                "OPEN_PARTIAL": 25,
                "partialName": 26,
                "params": 27,
                "hash": 28,
                "dataName": 29,
                "param": 30,
                "STRING": 31,
                "INTEGER": 32,
                "BOOLEAN": 33,
                "hashSegments": 34,
                "hashSegment": 35,
                "ID": 36,
                "EQUALS": 37,
                "DATA": 38,
                "pathSegments": 39,
                "SEP": 40,
                "$accept": 0,
                "$end": 1
            },
            terminals_: {
                2: "error",
                5: "EOF",
                14: "CONTENT",
                15: "COMMENT",
                16: "OPEN_BLOCK",
                18: "CLOSE",
                19: "OPEN_INVERSE",
                20: "OPEN_ENDBLOCK",
                22: "OPEN",
                23: "OPEN_UNESCAPED",
                24: "CLOSE_UNESCAPED",
                25: "OPEN_PARTIAL",
                31: "STRING",
                32: "INTEGER",
                33: "BOOLEAN",
                36: "ID",
                37: "EQUALS",
                38: "DATA",
                40: "SEP"
            },
            productions_: [0, [3, 2],
                [4, 2],
                [4, 3],
                [4, 2],
                [4, 1],
                [4, 1],
                [4, 0],
                [7, 1],
                [7, 2],
                [8, 3],
                [8, 3],
                [8, 1],
                [8, 1],
                [8, 1],
                [8, 1],
                [11, 3],
                [9, 3],
                [10, 3],
                [12, 3],
                [12, 3],
                [13, 3],
                [13, 4],
                [6, 2],
                [17, 3],
                [17, 2],
                [17, 2],
                [17, 1],
                [17, 1],
                [27, 2],
                [27, 1],
                [30, 1],
                [30, 1],
                [30, 1],
                [30, 1],
                [30, 1],
                [28, 1],
                [34, 2],
                [34, 1],
                [35, 3],
                [35, 3],
                [35, 3],
                [35, 3],
                [35, 3],
                [26, 1],
                [26, 1],
                [26, 1],
                [29, 2],
                [21, 1],
                [39, 3],
                [39, 1]
            ],
            performAction: function anonymous(y, a, c, n, p, $, _) {
                var r = $.length - 1;
                switch (p) {
                    case 1:
                        return $[r - 1];
                        break;
                    case 2:
                        this.$ = new n.ProgramNode([], $[r]);
                        break;
                    case 3:
                        this.$ = new n.ProgramNode($[r - 2], $[r]);
                        break;
                    case 4:
                        this.$ = new n.ProgramNode($[r - 1], []);
                        break;
                    case 5:
                        this.$ = new n.ProgramNode($[r]);
                        break;
                    case 6:
                        this.$ = new n.ProgramNode([], []);
                        break;
                    case 7:
                        this.$ = new n.ProgramNode([]);
                        break;
                    case 8:
                        this.$ = [$[r]];
                        break;
                    case 9:
                        $[r - 1].push($[r]);
                        this.$ = $[r - 1];
                        break;
                    case 10:
                        this.$ = new n.BlockNode($[r - 2], $[r - 1].inverse, $[r - 1], $[r]);
                        break;
                    case 11:
                        this.$ = new n.BlockNode($[r - 2], $[r - 1], $[r - 1].inverse, $[r]);
                        break;
                    case 12:
                        this.$ = $[r];
                        break;
                    case 13:
                        this.$ = $[r];
                        break;
                    case 14:
                        this.$ = new n.ContentNode($[r]);
                        break;
                    case 15:
                        this.$ = new n.CommentNode($[r]);
                        break;
                    case 16:
                        this.$ = new n.MustacheNode($[r - 1][0], $[r - 1][1]);
                        break;
                    case 17:
                        this.$ = new n.MustacheNode($[r - 1][0], $[r - 1][1]);
                        break;
                    case 18:
                        this.$ = $[r - 1];
                        break;
                    case 19:
                        this.$ = new n.MustacheNode($[r - 1][0], $[r - 1][1], $[r - 2][2] === '&');
                        break;
                    case 20:
                        this.$ = new n.MustacheNode($[r - 1][0], $[r - 1][1], true);
                        break;
                    case 21:
                        this.$ = new n.PartialNode($[r - 1]);
                        break;
                    case 22:
                        this.$ = new n.PartialNode($[r - 2], $[r - 1]);
                        break;
                    case 23:
                        break;
                    case 24:
                        this.$ = [
                            [$[r - 2]].concat($[r - 1]), $[r]
                        ];
                        break;
                    case 25:
                        this.$ = [
                            [$[r - 1]].concat($[r]), null];
                        break;
                    case 26:
                        this.$ = [
                            [$[r - 1]], $[r]
                        ];
                        break;
                    case 27:
                        this.$ = [
                            [$[r]], null];
                        break;
                    case 28:
                        this.$ = [
                            [$[r]], null];
                        break;
                    case 29:
                        $[r - 1].push($[r]);
                        this.$ = $[r - 1];
                        break;
                    case 30:
                        this.$ = [$[r]];
                        break;
                    case 31:
                        this.$ = $[r];
                        break;
                    case 32:
                        this.$ = new n.StringNode($[r]);
                        break;
                    case 33:
                        this.$ = new n.IntegerNode($[r]);
                        break;
                    case 34:
                        this.$ = new n.BooleanNode($[r]);
                        break;
                    case 35:
                        this.$ = $[r];
                        break;
                    case 36:
                        this.$ = new n.HashNode($[r]);
                        break;
                    case 37:
                        $[r - 1].push($[r]);
                        this.$ = $[r - 1];
                        break;
                    case 38:
                        this.$ = [$[r]];
                        break;
                    case 39:
                        this.$ = [$[r - 2], $[r]];
                        break;
                    case 40:
                        this.$ = [$[r - 2], new n.StringNode($[r])];
                        break;
                    case 41:
                        this.$ = [$[r - 2], new n.IntegerNode($[r])];
                        break;
                    case 42:
                        this.$ = [$[r - 2], new n.BooleanNode($[r])];
                        break;
                    case 43:
                        this.$ = [$[r - 2], $[r]];
                        break;
                    case 44:
                        this.$ = new n.PartialNameNode($[r]);
                        break;
                    case 45:
                        this.$ = new n.PartialNameNode(new n.StringNode($[r]));
                        break;
                    case 46:
                        this.$ = new n.PartialNameNode(new n.IntegerNode($[r]));
                        break;
                    case 47:
                        this.$ = new n.DataNode($[r]);
                        break;
                    case 48:
                        this.$ = new n.IdNode($[r]);
                        break;
                    case 49:
                        $[r - 2].push({
                            part: $[r],
                            separator: $[r - 1]
                        });
                        this.$ = $[r - 2];
                        break;
                    case 50:
                        this.$ = [{
                            part: $[r]
                        }];
                        break
                }
            },
            table: [{
                3: 1,
                4: 2,
                5: [2, 7],
                6: 3,
                7: 4,
                8: 6,
                9: 7,
                11: 8,
                12: 9,
                13: 10,
                14: [1, 11],
                15: [1, 12],
                16: [1, 13],
                19: [1, 5],
                22: [1, 14],
                23: [1, 15],
                25: [1, 16]
            }, {
                1: [3]
            }, {
                5: [1, 17]
            }, {
                5: [2, 6],
                7: 18,
                8: 6,
                9: 7,
                11: 8,
                12: 9,
                13: 10,
                14: [1, 11],
                15: [1, 12],
                16: [1, 13],
                19: [1, 19],
                20: [2, 6],
                22: [1, 14],
                23: [1, 15],
                25: [1, 16]
            }, {
                5: [2, 5],
                6: 20,
                8: 21,
                9: 7,
                11: 8,
                12: 9,
                13: 10,
                14: [1, 11],
                15: [1, 12],
                16: [1, 13],
                19: [1, 5],
                20: [2, 5],
                22: [1, 14],
                23: [1, 15],
                25: [1, 16]
            }, {
                17: 23,
                18: [1, 22],
                21: 24,
                29: 25,
                36: [1, 28],
                38: [1, 27],
                39: 26
            }, {
                5: [2, 8],
                14: [2, 8],
                15: [2, 8],
                16: [2, 8],
                19: [2, 8],
                20: [2, 8],
                22: [2, 8],
                23: [2, 8],
                25: [2, 8]
            }, {
                4: 29,
                6: 3,
                7: 4,
                8: 6,
                9: 7,
                11: 8,
                12: 9,
                13: 10,
                14: [1, 11],
                15: [1, 12],
                16: [1, 13],
                19: [1, 5],
                20: [2, 7],
                22: [1, 14],
                23: [1, 15],
                25: [1, 16]
            }, {
                4: 30,
                6: 3,
                7: 4,
                8: 6,
                9: 7,
                11: 8,
                12: 9,
                13: 10,
                14: [1, 11],
                15: [1, 12],
                16: [1, 13],
                19: [1, 5],
                20: [2, 7],
                22: [1, 14],
                23: [1, 15],
                25: [1, 16]
            }, {
                5: [2, 12],
                14: [2, 12],
                15: [2, 12],
                16: [2, 12],
                19: [2, 12],
                20: [2, 12],
                22: [2, 12],
                23: [2, 12],
                25: [2, 12]
            }, {
                5: [2, 13],
                14: [2, 13],
                15: [2, 13],
                16: [2, 13],
                19: [2, 13],
                20: [2, 13],
                22: [2, 13],
                23: [2, 13],
                25: [2, 13]
            }, {
                5: [2, 14],
                14: [2, 14],
                15: [2, 14],
                16: [2, 14],
                19: [2, 14],
                20: [2, 14],
                22: [2, 14],
                23: [2, 14],
                25: [2, 14]
            }, {
                5: [2, 15],
                14: [2, 15],
                15: [2, 15],
                16: [2, 15],
                19: [2, 15],
                20: [2, 15],
                22: [2, 15],
                23: [2, 15],
                25: [2, 15]
            }, {
                17: 31,
                21: 24,
                29: 25,
                36: [1, 28],
                38: [1, 27],
                39: 26
            }, {
                17: 32,
                21: 24,
                29: 25,
                36: [1, 28],
                38: [1, 27],
                39: 26
            }, {
                17: 33,
                21: 24,
                29: 25,
                36: [1, 28],
                38: [1, 27],
                39: 26
            }, {
                21: 35,
                26: 34,
                31: [1, 36],
                32: [1, 37],
                36: [1, 28],
                39: 26
            }, {
                1: [2, 1]
            }, {
                5: [2, 2],
                8: 21,
                9: 7,
                11: 8,
                12: 9,
                13: 10,
                14: [1, 11],
                15: [1, 12],
                16: [1, 13],
                19: [1, 19],
                20: [2, 2],
                22: [1, 14],
                23: [1, 15],
                25: [1, 16]
            }, {
                17: 23,
                21: 24,
                29: 25,
                36: [1, 28],
                38: [1, 27],
                39: 26
            }, {
                5: [2, 4],
                7: 38,
                8: 6,
                9: 7,
                11: 8,
                12: 9,
                13: 10,
                14: [1, 11],
                15: [1, 12],
                16: [1, 13],
                19: [1, 19],
                20: [2, 4],
                22: [1, 14],
                23: [1, 15],
                25: [1, 16]
            }, {
                5: [2, 9],
                14: [2, 9],
                15: [2, 9],
                16: [2, 9],
                19: [2, 9],
                20: [2, 9],
                22: [2, 9],
                23: [2, 9],
                25: [2, 9]
            }, {
                5: [2, 23],
                14: [2, 23],
                15: [2, 23],
                16: [2, 23],
                19: [2, 23],
                20: [2, 23],
                22: [2, 23],
                23: [2, 23],
                25: [2, 23]
            }, {
                18: [1, 39]
            }, {
                18: [2, 27],
                21: 44,
                24: [2, 27],
                27: 40,
                28: 41,
                29: 48,
                30: 42,
                31: [1, 45],
                32: [1, 46],
                33: [1, 47],
                34: 43,
                35: 49,
                36: [1, 50],
                38: [1, 27],
                39: 26
            }, {
                18: [2, 28],
                24: [2, 28]
            }, {
                18: [2, 48],
                24: [2, 48],
                31: [2, 48],
                32: [2, 48],
                33: [2, 48],
                36: [2, 48],
                38: [2, 48],
                40: [1, 51]
            }, {
                21: 52,
                36: [1, 28],
                39: 26
            }, {
                18: [2, 50],
                24: [2, 50],
                31: [2, 50],
                32: [2, 50],
                33: [2, 50],
                36: [2, 50],
                38: [2, 50],
                40: [2, 50]
            }, {
                10: 53,
                20: [1, 54]
            }, {
                10: 55,
                20: [1, 54]
            }, {
                18: [1, 56]
            }, {
                18: [1, 57]
            }, {
                24: [1, 58]
            }, {
                18: [1, 59],
                21: 60,
                36: [1, 28],
                39: 26
            }, {
                18: [2, 44],
                36: [2, 44]
            }, {
                18: [2, 45],
                36: [2, 45]
            }, {
                18: [2, 46],
                36: [2, 46]
            }, {
                5: [2, 3],
                8: 21,
                9: 7,
                11: 8,
                12: 9,
                13: 10,
                14: [1, 11],
                15: [1, 12],
                16: [1, 13],
                19: [1, 19],
                20: [2, 3],
                22: [1, 14],
                23: [1, 15],
                25: [1, 16]
            }, {
                14: [2, 17],
                15: [2, 17],
                16: [2, 17],
                19: [2, 17],
                20: [2, 17],
                22: [2, 17],
                23: [2, 17],
                25: [2, 17]
            }, {
                18: [2, 25],
                21: 44,
                24: [2, 25],
                28: 61,
                29: 48,
                30: 62,
                31: [1, 45],
                32: [1, 46],
                33: [1, 47],
                34: 43,
                35: 49,
                36: [1, 50],
                38: [1, 27],
                39: 26
            }, {
                18: [2, 26],
                24: [2, 26]
            }, {
                18: [2, 30],
                24: [2, 30],
                31: [2, 30],
                32: [2, 30],
                33: [2, 30],
                36: [2, 30],
                38: [2, 30]
            }, {
                18: [2, 36],
                24: [2, 36],
                35: 63,
                36: [1, 64]
            }, {
                18: [2, 31],
                24: [2, 31],
                31: [2, 31],
                32: [2, 31],
                33: [2, 31],
                36: [2, 31],
                38: [2, 31]
            }, {
                18: [2, 32],
                24: [2, 32],
                31: [2, 32],
                32: [2, 32],
                33: [2, 32],
                36: [2, 32],
                38: [2, 32]
            }, {
                18: [2, 33],
                24: [2, 33],
                31: [2, 33],
                32: [2, 33],
                33: [2, 33],
                36: [2, 33],
                38: [2, 33]
            }, {
                18: [2, 34],
                24: [2, 34],
                31: [2, 34],
                32: [2, 34],
                33: [2, 34],
                36: [2, 34],
                38: [2, 34]
            }, {
                18: [2, 35],
                24: [2, 35],
                31: [2, 35],
                32: [2, 35],
                33: [2, 35],
                36: [2, 35],
                38: [2, 35]
            }, {
                18: [2, 38],
                24: [2, 38],
                36: [2, 38]
            }, {
                18: [2, 50],
                24: [2, 50],
                31: [2, 50],
                32: [2, 50],
                33: [2, 50],
                36: [2, 50],
                37: [1, 65],
                38: [2, 50],
                40: [2, 50]
            }, {
                36: [1, 66]
            }, {
                18: [2, 47],
                24: [2, 47],
                31: [2, 47],
                32: [2, 47],
                33: [2, 47],
                36: [2, 47],
                38: [2, 47]
            }, {
                5: [2, 10],
                14: [2, 10],
                15: [2, 10],
                16: [2, 10],
                19: [2, 10],
                20: [2, 10],
                22: [2, 10],
                23: [2, 10],
                25: [2, 10]
            }, {
                21: 67,
                36: [1, 28],
                39: 26
            }, {
                5: [2, 11],
                14: [2, 11],
                15: [2, 11],
                16: [2, 11],
                19: [2, 11],
                20: [2, 11],
                22: [2, 11],
                23: [2, 11],
                25: [2, 11]
            }, {
                14: [2, 16],
                15: [2, 16],
                16: [2, 16],
                19: [2, 16],
                20: [2, 16],
                22: [2, 16],
                23: [2, 16],
                25: [2, 16]
            }, {
                5: [2, 19],
                14: [2, 19],
                15: [2, 19],
                16: [2, 19],
                19: [2, 19],
                20: [2, 19],
                22: [2, 19],
                23: [2, 19],
                25: [2, 19]
            }, {
                5: [2, 20],
                14: [2, 20],
                15: [2, 20],
                16: [2, 20],
                19: [2, 20],
                20: [2, 20],
                22: [2, 20],
                23: [2, 20],
                25: [2, 20]
            }, {
                5: [2, 21],
                14: [2, 21],
                15: [2, 21],
                16: [2, 21],
                19: [2, 21],
                20: [2, 21],
                22: [2, 21],
                23: [2, 21],
                25: [2, 21]
            }, {
                18: [1, 68]
            }, {
                18: [2, 24],
                24: [2, 24]
            }, {
                18: [2, 29],
                24: [2, 29],
                31: [2, 29],
                32: [2, 29],
                33: [2, 29],
                36: [2, 29],
                38: [2, 29]
            }, {
                18: [2, 37],
                24: [2, 37],
                36: [2, 37]
            }, {
                37: [1, 65]
            }, {
                21: 69,
                29: 73,
                31: [1, 70],
                32: [1, 71],
                33: [1, 72],
                36: [1, 28],
                38: [1, 27],
                39: 26
            }, {
                18: [2, 49],
                24: [2, 49],
                31: [2, 49],
                32: [2, 49],
                33: [2, 49],
                36: [2, 49],
                38: [2, 49],
                40: [2, 49]
            }, {
                18: [1, 74]
            }, {
                5: [2, 22],
                14: [2, 22],
                15: [2, 22],
                16: [2, 22],
                19: [2, 22],
                20: [2, 22],
                22: [2, 22],
                23: [2, 22],
                25: [2, 22]
            }, {
                18: [2, 39],
                24: [2, 39],
                36: [2, 39]
            }, {
                18: [2, 40],
                24: [2, 40],
                36: [2, 40]
            }, {
                18: [2, 41],
                24: [2, 41],
                36: [2, 41]
            }, {
                18: [2, 42],
                24: [2, 42],
                36: [2, 42]
            }, {
                18: [2, 43],
                24: [2, 43],
                36: [2, 43]
            }, {
                5: [2, 18],
                14: [2, 18],
                15: [2, 18],
                16: [2, 18],
                19: [2, 18],
                20: [2, 18],
                22: [2, 18],
                23: [2, 18],
                25: [2, 18]
            }],
            defaultActions: {
                17: [2, 1]
            },
            parseError: function parseError(a, c) {
                throw new Error(a)
            },
            parse: function parse(c) {
                var v = this,
                    w = [0],
                    x = [null],
                    y = [],
                    z = this.table,
                    A = "",
                    B = 0,
                    D = 0,
                    E = 0,
                    T = 2,
                    F = 1;
                this.lexer.setInput(c);
                this.lexer.yy = this.yy;
                this.yy.lexer = this.lexer;
                this.yy.parser = this;
                if (typeof this.lexer.yylloc == "undefined") this.lexer.yylloc = {};
                var G = this.lexer.yylloc;
                y.push(G);
                var I = this.lexer.options && this.lexer.options.ranges;
                if (typeof this.yy.parseError === "function") this.parseError = this.yy.parseError;

                function K(n) {
                    w.length = w.length - 2 * n;
                    x.length = x.length - n;
                    y.length = y.length - n
                }
                function M() {
                    var n;
                    n = v.lexer.lex() || 1;
                    if (typeof n !== "number") {
                        n = v.symbols_[n] || n
                    }
                    return n
                }
                var N, O, Q, R, a, r, S = {}, p, U, V, W;
                while (true) {
                    Q = w[w.length - 1];
                    if (this.defaultActions[Q]) {
                        R = this.defaultActions[Q]
                    } else {
                        if (N === null || typeof N == "undefined") {
                            N = M()
                        }
                        R = z[Q] && z[Q][N]
                    }
                    if (typeof R === "undefined" || !R.length || !R[0]) {
                        var X = "";
                        if (!E) {
                            W = [];
                            for (p in z[Q]) if (this.terminals_[p] && p > 2) {
                                W.push("'" + this.terminals_[p] + "'")
                            }
                            if (this.lexer.showPosition) {
                                X = "Parse error on line " + (B + 1) + ":\n" + this.lexer.showPosition() + "\nExpecting " + W.join(", ") + ", got '" + (this.terminals_[N] || N) + "'"
                            } else {
                                X = "Parse error on line " + (B + 1) + ": Unexpected " + (N == 1 ? "end of input" : "'" + (this.terminals_[N] || N) + "'")
                            }
                            this.parseError(X, {
                                text: this.lexer.match,
                                token: this.terminals_[N] || N,
                                line: this.lexer.yylineno,
                                loc: G,
                                expected: W
                            })
                        }
                    }
                    if (R[0] instanceof Array && R.length > 1) {
                        throw new Error("Parse Error: multiple actions possible at state: " + Q + ", token: " + N)
                    }
                    switch (R[0]) {
                        case 1:
                            w.push(N);
                            x.push(this.lexer.yytext);
                            y.push(this.lexer.yylloc);
                            w.push(R[1]);
                            N = null;
                            if (!O) {
                                D = this.lexer.yyleng;
                                A = this.lexer.yytext;
                                B = this.lexer.yylineno;
                                G = this.lexer.yylloc;
                                if (E > 0) E--
                            } else {
                                N = O;
                                O = null
                            }
                            break;
                        case 2:
                            U = this.productions_[R[1]][1];
                            S.$ = x[x.length - U];
                            S._$ = {
                                first_line: y[y.length - (U || 1)].first_line,
                                last_line: y[y.length - 1].last_line,
                                first_column: y[y.length - (U || 1)].first_column,
                                last_column: y[y.length - 1].last_column
                            };
                            if (I) {
                                S._$.range = [y[y.length - (U || 1)].range[0], y[y.length - 1].range[1]]
                            }
                            r = this.performAction.call(S, A, D, B, this.yy, R[1], x, y);
                            if (typeof r !== "undefined") {
                                return r
                            }
                            if (U) {
                                w = w.slice(0, -1 * U * 2);
                                x = x.slice(0, -1 * U);
                                y = y.slice(0, -1 * U)
                            }
                            w.push(this.productions_[R[1]][0]);
                            x.push(S.$);
                            y.push(S._$);
                            V = z[w[w.length - 2]][w[w.length - 1]];
                            w.push(V);
                            break;
                        case 3:
                            return true
                    }
                }
                return true
            }
        };
        var j = (function() {
            var j = ({
                EOF: 1,
                parseError: function parseError(a, c) {
                    if (this.yy.parser) {
                        this.yy.parser.parseError(a, c)
                    } else {
                        throw new Error(a)
                    }
                },
                setInput: function(a) {
                    this._input = a;
                    this._more = this._less = this.done = false;
                    this.yylineno = this.yyleng = 0;
                    this.yytext = this.matched = this.match = '';
                    this.conditionStack = ['INITIAL'];
                    this.yylloc = {
                        first_line: 1,
                        first_column: 0,
                        last_line: 1,
                        last_column: 0
                    };
                    if (this.options.ranges) this.yylloc.range = [0, 0];
                    this.offset = 0;
                    return this
                },
                input: function() {
                    var c = this._input[0];
                    this.yytext += c;
                    this.yyleng++;
                    this.offset++;
                    this.match += c;
                    this.matched += c;
                    var a = c.match(/(?:\r\n?|\n).*/g);
                    if (a) {
                        this.yylineno++;
                        this.yylloc.last_line++
                    } else {
                        this.yylloc.last_column++
                    }
                    if (this.options.ranges) this.yylloc.range[1]++;
                    this._input = this._input.slice(1);
                    return c
                },
                unput: function(c) {
                    var a = c.length;
                    var n = c.split(/(?:\r\n?|\n)/g);
                    this._input = c + this._input;
                    this.yytext = this.yytext.substr(0, this.yytext.length - a - 1);
                    this.offset -= a;
                    var p = this.match.split(/(?:\r\n?|\n)/g);
                    this.match = this.match.substr(0, this.match.length - 1);
                    this.matched = this.matched.substr(0, this.matched.length - 1);
                    if (n.length - 1) this.yylineno -= n.length - 1;
                    var r = this.yylloc.range;
                    this.yylloc = {
                        first_line: this.yylloc.first_line,
                        last_line: this.yylineno + 1,
                        first_column: this.yylloc.first_column,
                        last_column: n ? (n.length === p.length ? this.yylloc.first_column : 0) + p[p.length - n.length].length - n[0].length : this.yylloc.first_column - a
                    };
                    if (this.options.ranges) {
                        this.yylloc.range = [r[0], r[0] + this.yyleng - a]
                    }
                    return this
                },
                more: function() {
                    this._more = true;
                    return this
                },
                less: function(n) {
                    this.unput(this.match.slice(n))
                },
                pastInput: function() {
                    var p = this.matched.substr(0, this.matched.length - this.match.length);
                    return (p.length > 20 ? '...' : '') + p.substr(-20).replace(/\n/g, "")
                },
                upcomingInput: function() {
                    var n = this.match;
                    if (n.length < 20) {
                        n += this._input.substr(0, 20 - n.length)
                    }
                    return (n.substr(0, 20) + (n.length > 20 ? '...' : '')).replace(/\n/g, "")
                },
                showPosition: function() {
                    var p = this.pastInput();
                    var c = new Array(p.length + 1).join("-");
                    return p + this.upcomingInput() + "\n" + c + "^"
                },
                next: function() {
                    if (this.done) {
                        return this.EOF
                    }
                    if (!this._input) this.done = true;
                    var a, c, n, p, r, v;
                    if (!this._more) {
                        this.yytext = '';
                        this.match = ''
                    }
                    var w = this._currentRules();
                    for (var i = 0; i < w.length; i++) {
                        n = this._input.match(this.rules[w[i]]);
                        if (n && (!c || n[0].length > c[0].length)) {
                            c = n;
                            p = i;
                            if (!this.options.flex) break
                        }
                    }
                    if (c) {
                        v = c[0].match(/(?:\r\n?|\n).*/g);
                        if (v) this.yylineno += v.length;
                        this.yylloc = {
                            first_line: this.yylloc.last_line,
                            last_line: this.yylineno + 1,
                            first_column: this.yylloc.last_column,
                            last_column: v ? v[v.length - 1].length - v[v.length - 1].match(/\r?\n?/)[0].length : this.yylloc.last_column + c[0].length
                        };
                        this.yytext += c[0];
                        this.match += c[0];
                        this.matches = c;
                        this.yyleng = this.yytext.length;
                        if (this.options.ranges) {
                            this.yylloc.range = [this.offset, this.offset += this.yyleng]
                        }
                        this._more = false;
                        this._input = this._input.slice(c[0].length);
                        this.matched += c[0];
                        a = this.performAction.call(this, this.yy, this, w[p], this.conditionStack[this.conditionStack.length - 1]);
                        if (this.done && this._input) this.done = false;
                        if (a) return a;
                        else return
                    }
                    if (this._input === "") {
                        return this.EOF
                    } else {
                        return this.parseError('Lexical error on line ' + (this.yylineno + 1) + '. Unrecognized text.\n' + this.showPosition(), {
                            text: "",
                            token: null,
                            line: this.yylineno
                        })
                    }
                },
                lex: function lex() {
                    var r = this.next();
                    if (typeof r !== 'undefined') {
                        return r
                    } else {
                        return this.lex()
                    }
                },
                begin: function begin(c) {
                    this.conditionStack.push(c)
                },
                popState: function popState() {
                    return this.conditionStack.pop()
                },
                _currentRules: function _currentRules() {
                    return this.conditions[this.conditionStack[this.conditionStack.length - 1]].rules
                },
                topState: function() {
                    return this.conditionStack[this.conditionStack.length - 2]
                },
                pushState: function begin(c) {
                    this.begin(c)
                }
            });
            j.options = {};
            j.performAction = function anonymous(y, a, $, Y) {
                var c = Y;
                switch ($) {
                    case 0:
                        a.yytext = "\\";
                        return 14;
                        break;
                    case 1:
                        if (a.yytext.slice(-1) !== "\\") this.begin("mu");
                        if (a.yytext.slice(-1) === "\\") a.yytext = a.yytext.substr(0, a.yyleng - 1), this.begin("emu");
                        if (a.yytext) return 14;
                        break;
                    case 2:
                        return 14;
                        break;
                    case 3:
                        if (a.yytext.slice(-1) !== "\\") this.popState();
                        if (a.yytext.slice(-1) === "\\") a.yytext = a.yytext.substr(0, a.yyleng - 1);
                        return 14;
                        break;
                    case 4:
                        a.yytext = a.yytext.substr(0, a.yyleng - 4);
                        this.popState();
                        return 15;
                        break;
                    case 5:
                        return 25;
                        break;
                    case 6:
                        return 16;
                        break;
                    case 7:
                        return 20;
                        break;
                    case 8:
                        return 19;
                        break;
                    case 9:
                        return 19;
                        break;
                    case 10:
                        return 23;
                        break;
                    case 11:
                        return 22;
                        break;
                    case 12:
                        this.popState();
                        this.begin('com');
                        break;
                    case 13:
                        a.yytext = a.yytext.substr(3, a.yyleng - 5);
                        this.popState();
                        return 15;
                        break;
                    case 14:
                        return 22;
                        break;
                    case 15:
                        return 37;
                        break;
                    case 16:
                        return 36;
                        break;
                    case 17:
                        return 36;
                        break;
                    case 18:
                        return 40;
                        break;
                    case 19:
                        break;
                    case 20:
                        this.popState();
                        return 24;
                        break;
                    case 21:
                        this.popState();
                        return 18;
                        break;
                    case 22:
                        a.yytext = a.yytext.substr(1, a.yyleng - 2).replace(/\\"/g, '"');
                        return 31;
                        break;
                    case 23:
                        a.yytext = a.yytext.substr(1, a.yyleng - 2).replace(/\\'/g, "'");
                        return 31;
                        break;
                    case 24:
                        return 38;
                        break;
                    case 25:
                        return 33;
                        break;
                    case 26:
                        return 33;
                        break;
                    case 27:
                        return 32;
                        break;
                    case 28:
                        return 36;
                        break;
                    case 29:
                        a.yytext = a.yytext.substr(1, a.yyleng - 2);
                        return 36;
                        break;
                    case 30:
                        return 'INVALID';
                        break;
                    case 31:
                        return 5;
                        break
                }
            };
            j.rules = [/^(?:\\\\(?=(\{\{)))/, /^(?:[^\x00]*?(?=(\{\{)))/, /^(?:[^\x00]+)/, /^(?:[^\x00]{2,}?(?=(\{\{|$)))/, /^(?:[\s\S]*?--\}\})/, /^(?:\{\{>)/, /^(?:\{\{#)/, /^(?:\{\{\/)/, /^(?:\{\{\^)/, /^(?:\{\{\s*else\b)/, /^(?:\{\{\{)/, /^(?:\{\{&)/, /^(?:\{\{!--)/, /^(?:\{\{![\s\S]*?\}\})/, /^(?:\{\{)/, /^(?:=)/, /^(?:\.(?=[}\/ ]))/, /^(?:\.\.)/, /^(?:[\/.])/, /^(?:\s+)/, /^(?:\}\}\})/, /^(?:\}\})/, /^(?:"(\\["]|[^"])*")/, /^(?:'(\\[']|[^'])*')/, /^(?:@)/, /^(?:true(?=[}\s]))/, /^(?:false(?=[}\s]))/, /^(?:-?[0-9]+(?=[}\s]))/, /^(?:[^\s!"#%-,\.\/;->@\[-\^`\{-~]+(?=[=}\s\/.]))/, /^(?:\[[^\]]*\])/, /^(?:.)/, /^(?:$)/];
            j.conditions = {
                "mu": {
                    "rules": [5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31],
                    "inclusive": false
                },
                "emu": {
                    "rules": [3],
                    "inclusive": false
                },
                "com": {
                    "rules": [4],
                    "inclusive": false
                },
                "INITIAL": {
                    "rules": [0, 1, 2, 31],
                    "inclusive": true
                }
            };
            return j
        })();
        b.lexer = j;

        function P() {
            this.yy = {}
        }
        P.prototype = b;
        b.Parser = P;
        return new P
    })();;
    H.Parser = h;
    H.parse = function(a) {
        if (a.constructor === H.AST.ProgramNode) {
            return a
        }
        H.Parser.yy = H.AST;
        return H.Parser.parse(a)
    };;
    H.AST = {};
    H.AST.ProgramNode = function(a, b) {
        this.type = "program";
        this.statements = a;
        if (b) {
            this.inverse = new H.AST.ProgramNode(b)
        }
    };
    H.AST.MustacheNode = function(r, a, b) {
        this.type = "mustache";
        this.escaped = !b;
        this.hash = a;
        var c = this.id = r[0];
        var p = this.params = r.slice(1);
        var j = this.eligibleHelper = c.isSimple;
        this.isHelper = j && (p.length || a)
    };
    H.AST.PartialNode = function(p, c) {
        this.type = "partial";
        this.partialName = p;
        this.context = c
    };
    H.AST.BlockNode = function(a, p, b, c) {
        var v = function(j, c) {
            if (j.original !== c.original) {
                throw new H.Exception(j.original + " doesn't match " + c.original)
            }
        };
        v(a.id, c);
        this.type = "block";
        this.mustache = a;
        this.program = p;
        this.inverse = b;
        if (this.inverse && !this.program) {
            this.isInverse = true
        }
    };
    H.AST.ContentNode = function(a) {
        this.type = "content";
        this.string = a
    };
    H.AST.HashNode = function(p) {
        this.type = "hash";
        this.pairs = p
    };
    H.AST.IdNode = function(p) {
        this.type = "ID";
        var a = "",
            b = [],
            c = 0;
        for (var i = 0, l = p.length; i < l; i++) {
            var j = p[i].part;
            a += (p[i].separator || '') + j;
            if (j === ".." || j === "." || j === "this") {
                if (b.length > 0) {
                    throw new H.Exception("Invalid path: " + a)
                } else if (j === "..") {
                    c++
                } else {
                    this.isScoped = true
                }
            } else {
                b.push(j)
            }
        }
        this.original = a;
        this.parts = b;
        this.string = b.join('.');
        this.depth = c;
        this.isSimple = p.length === 1 && !this.isScoped && c === 0;
        this.stringModeValue = this.string
    };
    H.AST.PartialNameNode = function(n) {
        this.type = "PARTIAL_NAME";
        this.name = n.original
    };
    H.AST.DataNode = function(a) {
        this.type = "DATA";
        this.id = a
    };
    H.AST.StringNode = function(a) {
        this.type = "STRING";
        this.original = this.string = this.stringModeValue = a
    };
    H.AST.IntegerNode = function(a) {
        this.type = "INTEGER";
        this.original = this.integer = a;
        this.stringModeValue = Number(a)
    };
    H.AST.BooleanNode = function(b) {
        this.type = "BOOLEAN";
        this.bool = b;
        this.stringModeValue = b === "true"
    };
    H.AST.CommentNode = function(c) {
        this.type = "comment";
        this.comment = c
    };;
    var e = ['description', 'fileName', 'lineNumber', 'message', 'name', 'number', 'stack'];
    H.Exception = function(a) {
        var b = Error.prototype.constructor.apply(this, arguments);
        for (var c = 0; c < e.length; c++) {
            this[e[c]] = b[e[c]]
        }
    };
    H.Exception.prototype = new Error();
    H.SafeString = function(a) {
        this.string = a
    };
    H.SafeString.prototype.toString = function() {
        return this.string.toString()
    };
    var d = {
        "&": "&amp;",
        "<": "&lt;",
        ">": "&gt;",
        '"': "&quot;",
        "'": "&#x27;",
        "`": "&#x60;"
    };
    var g = /[&<>"'`]/g;
    var k = /[&<>"'`]/;
    var m = function(c) {
        return d[c] || "&amp;"
    };
    H.Utils = {
        extend: function(a, v) {
            for (var b in v) {
                if (v.hasOwnProperty(b)) {
                    a[b] = v[b]
                }
            }
        },
        escapeExpression: function(a) {
            if (a instanceof H.SafeString) {
                return a.toString()
            } else if (a == null || a === false) {
                return ""
            }
            a = a.toString();
            if (!k.test(a)) {
                return a
            }
            return a.replace(g, m)
        },
        isEmpty: function(v) {
            if (!v && v !== 0) {
                return true
            } else if (t.call(v) === "[object Array]" && v.length === 0) {
                return true
            } else {
                return false
            }
        }
    };;
    var C = H.Compiler = function() {};
    var J = H.JavaScriptCompiler = function() {};
    C.prototype = {
        compiler: C,
        disassemble: function() {
            var a = this.opcodes,
                b, c = [],
                p, n;
            for (var i = 0, l = a.length; i < l; i++) {
                b = a[i];
                if (b.opcode === 'DECLARE') {
                    c.push("DECLARE " + b.name + "=" + b.value)
                } else {
                    p = [];
                    for (var j = 0; j < b.args.length; j++) {
                        n = b.args[j];
                        if (typeof n === "string") {
                            n = "\"" + n.replace("\n", "\\n") + "\""
                        }
                        p.push(n)
                    }
                    c.push(b.opcode + " " + p.join(" "))
                }
            }
            return c.join("\n")
        },
        equals: function(a) {
            var b = this.opcodes.length;
            if (a.opcodes.length !== b) {
                return false
            }
            for (var i = 0; i < b; i++) {
                var c = this.opcodes[i],
                    n = a.opcodes[i];
                if (c.opcode !== n.opcode || c.args.length !== n.args.length) {
                    return false
                }
                for (var j = 0; j < c.args.length; j++) {
                    if (c.args[j] !== n.args[j]) {
                        return false
                    }
                }
            }
            b = this.children.length;
            if (a.children.length !== b) {
                return false
            }
            for (i = 0; i < b; i++) {
                if (!this.children[i].equals(a.children[i])) {
                    return false
                }
            }
            return true
        },
        guid: 0,
        compile: function(p, a) {
            this.children = [];
            this.depths = {
                list: []
            };
            this.options = a;
            var b = this.options.knownHelpers;
            this.options.knownHelpers = {
                'helperMissing': true,
                'blockHelperMissing': true,
                'each': true,
                'if': true,
                'unless': true,
                'with': true,
                'log': true
            };
            if (b) {
                for (var n in b) {
                    this.options.knownHelpers[n] = b[n]
                }
            }
            return this.program(p)
        },
        accept: function(n) {
            return this[n.type](n)
        },
        program: function(p) {
            var c = p.statements,
                j;
            this.opcodes = [];
            for (var i = 0, l = c.length; i < l; i++) {
                j = c[i];
                this[j.type](j)
            }
            this.isSimple = l === 1;
            this.depths.list = this.depths.list.sort(function(a, b) {
                return a - b
            });
            return this
        },
        compileProgram: function(p) {
            var r = new this.compiler().compile(p, this.options);
            var a = this.guid++,
                b;
            this.usePartial = this.usePartial || r.usePartial;
            this.children[a] = r;
            for (var i = 0, l = r.depths.list.length; i < l; i++) {
                b = r.depths.list[i];
                if (b < 2) {
                    continue
                } else {
                    this.addDepth(b - 1)
                }
            }
            return a
        },
        block: function(b) {
            var a = b.mustache,
                p = b.program,
                c = b.inverse;
            if (p) {
                p = this.compileProgram(p)
            }
            if (c) {
                c = this.compileProgram(c)
            }
            var j = this.classifyMustache(a);
            if (j === "helper") {
                this.helperMustache(a, p, c)
            } else if (j === "simple") {
                this.simpleMustache(a);
                this.opcode('pushProgram', p);
                this.opcode('pushProgram', c);
                this.opcode('emptyHash');
                this.opcode('blockValue')
            } else {
                this.ambiguousMustache(a, p, c);
                this.opcode('pushProgram', p);
                this.opcode('pushProgram', c);
                this.opcode('emptyHash');
                this.opcode('ambiguousBlockValue')
            }
            this.opcode('append')
        },
        hash: function(a) {
            var p = a.pairs,
                b, v;
            this.opcode('pushHash');
            for (var i = 0, l = p.length; i < l; i++) {
                b = p[i];
                v = b[1];
                if (this.options.stringParams) {
                    if (v.depth) {
                        this.addDepth(v.depth)
                    }
                    this.opcode('getContext', v.depth || 0);
                    this.opcode('pushStringParam', v.stringModeValue, v.type)
                } else {
                    this.accept(v)
                }
                this.opcode('assignToHash', b[0])
            }
            this.opcode('popHash')
        },
        partial: function(p) {
            var a = p.partialName;
            this.usePartial = true;
            if (p.context) {
                this.ID(p.context)
            } else {
                this.opcode('push', 'depth0')
            }
            this.opcode('invokePartial', a.name);
            this.opcode('append')
        },
        content: function(c) {
            this.opcode('appendContent', c.string)
        },
        mustache: function(a) {
            var b = this.options;
            var c = this.classifyMustache(a);
            if (c === "simple") {
                this.simpleMustache(a)
            } else if (c === "helper") {
                this.helperMustache(a)
            } else {
                this.ambiguousMustache(a)
            }
            if (a.escaped && !b.noEscape) {
                this.opcode('appendEscaped')
            } else {
                this.opcode('append')
            }
        },
        ambiguousMustache: function(a, p, b) {
            var c = a.id,
                n = c.parts[0],
                j = p != null || b != null;
            this.opcode('getContext', c.depth);
            this.opcode('pushProgram', p);
            this.opcode('pushProgram', b);
            this.opcode('invokeAmbiguous', n, j)
        },
        simpleMustache: function(a) {
            var b = a.id;
            if (b.type === 'DATA') {
                this.DATA(b)
            } else if (b.parts.length) {
                this.ID(b)
            } else {
                this.addDepth(b.depth);
                this.opcode('getContext', b.depth);
                this.opcode('pushContext')
            }
            this.opcode('resolvePossibleLambda')
        },
        helperMustache: function(a, p, b) {
            var c = this.setupFullMustacheParams(a, p, b),
                n = a.id.parts[0];
            if (this.options.knownHelpers[n]) {
                this.opcode('invokeKnownHelper', c.length, n)
            } else if (this.options.knownHelpersOnly) {
                throw new Error("You specified knownHelpersOnly, but used the unknown helper " + n)
            } else {
                this.opcode('invokeHelper', c.length, n)
            }
        },
        ID: function(a) {
            this.addDepth(a.depth);
            this.opcode('getContext', a.depth);
            var n = a.parts[0];
            if (!n) {
                this.opcode('pushContext')
            } else {
                this.opcode('lookupOnContext', a.parts[0])
            }
            for (var i = 1, l = a.parts.length; i < l; i++) {
                this.opcode('lookup', a.parts[i])
            }
        },
        DATA: function(a) {
            this.options.data = true;
            if (a.id.isScoped || a.id.depth) {
                throw new H.Exception('Scoped data references are not supported: ' + a.original)
            }
            this.opcode('lookupData');
            var p = a.id.parts;
            for (var i = 0, l = p.length; i < l; i++) {
                this.opcode('lookup', p[i])
            }
        },
        STRING: function(a) {
            this.opcode('pushString', a.string)
        },
        INTEGER: function(a) {
            this.opcode('pushLiteral', a.integer)
        },
        BOOLEAN: function(b) {
            this.opcode('pushLiteral', b.bool)
        },
        comment: function() {},
        opcode: function(n) {
            this.opcodes.push({
                opcode: n,
                args: [].slice.call(arguments, 1)
            })
        },
        declare: function(n, v) {
            this.opcodes.push({
                opcode: 'DECLARE',
                name: n,
                value: v
            })
        },
        addDepth: function(a) {
            if (isNaN(a)) {
                throw new Error("EWOT")
            }
            if (a === 0) {
                return
            }
            if (!this.depths[a]) {
                this.depths[a] = true;
                this.depths.list.push(a)
            }
        },
        classifyMustache: function(a) {
            var b = a.isHelper;
            var c = a.eligibleHelper;
            var j = this.options;
            if (c && !b) {
                var n = a.id.parts[0];
                if (j.knownHelpers[n]) {
                    b = true
                } else if (j.knownHelpersOnly) {
                    c = false
                }
            }
            if (b) {
                return "helper"
            } else if (c) {
                return "ambiguous"
            } else {
                return "simple"
            }
        },
        pushParams: function(p) {
            var i = p.length,
                a;
            while (i--) {
                a = p[i];
                if (this.options.stringParams) {
                    if (a.depth) {
                        this.addDepth(a.depth)
                    }
                    this.opcode('getContext', a.depth || 0);
                    this.opcode('pushStringParam', a.stringModeValue, a.type)
                } else {
                    this[a.type](a)
                }
            }
        },
        setupMustacheParams: function(a) {
            var p = a.params;
            this.pushParams(p);
            if (a.hash) {
                this.hash(a.hash)
            } else {
                this.opcode('emptyHash')
            }
            return p
        },
        setupFullMustacheParams: function(a, p, b) {
            var c = a.params;
            this.pushParams(c);
            this.opcode('pushProgram', p);
            this.opcode('pushProgram', b);
            if (a.hash) {
                this.hash(a.hash)
            } else {
                this.opcode('emptyHash')
            }
            return c
        }
    };
    var L = function(v) {
        this.value = v
    };
    J.prototype = {
        nameLookup: function(p, n) {
            if (/^[0-9]+$/.test(n)) {
                return p + "[" + n + "]"
            } else if (J.isValidJavaScriptVariableName(n)) {
                return p + "." + n
            } else {
                return p + "['" + n + "']"
            }
        },
        appendToBuffer: function(a) {
            if (this.environment.isSimple) {
                return "return " + a + ";"
            } else {
                return {
                    appendToBuffer: true,
                    content: a,
                    toString: function() {
                        return "buffer += " + a + ";"
                    }
                }
            }
        },
        initializeBuffer: function() {
            return this.quotedString("")
        },
        namespace: "Handlebars",
        compile: function(a, b, c, j) {
            this.environment = a;
            this.options = b || {};
            H.log(H.logger.DEBUG, this.environment.disassemble() + "\n\n");
            this.name = this.environment.name;
            this.isChild = !! c;
            this.context = c || {
                programs: [],
                environments: [],
                aliases: {}
            };
            this.preamble();
            this.stackSlot = 0;
            this.stackVars = [];
            this.registers = {
                list: []
            };
            this.compileStack = [];
            this.inlineStack = [];
            this.compileChildren(a, b);
            var n = a.opcodes,
                p;
            this.i = 0;
            for (l = n.length; this.i < l; this.i++) {
                p = n[this.i];
                if (p.opcode === 'DECLARE') {
                    this[p.name] = p.value
                } else {
                    this[p.opcode].apply(this, p.args)
                }
            }
            return this.createFunctionContext(j)
        },
        nextOpcode: function() {
            var a = this.environment.opcodes;
            return a[this.i + 1]
        },
        eat: function() {
            this.i = this.i + 1
        },
        preamble: function() {
            var a = [];
            if (!this.isChild) {
                var n = this.namespace;
                var c = "helpers = this.merge(helpers, " + n + ".helpers);";
                if (this.environment.usePartial) {
                    c = c + " partials = this.merge(partials, " + n + ".partials);"
                }
                if (this.options.data) {
                    c = c + " data = data || {};"
                }
                a.push(c)
            } else {
                a.push('')
            }
            if (!this.environment.isSimple) {
                a.push(", buffer = " + this.initializeBuffer())
            } else {
                a.push("")
            }
            this.lastContext = 0;
            this.source = a
        },
        createFunctionContext: function(a) {
            var b = this.stackVars.concat(this.registers.list);
            if (b.length > 0) {
                this.source[1] = this.source[1] + ", " + b.join(", ")
            }
            if (!this.isChild) {
                for (var c in this.context.aliases) {
                    if (this.context.aliases.hasOwnProperty(c)) {
                        this.source[1] = this.source[1] + ', ' + c + '=' + this.context.aliases[c]
                    }
                }
            }
            if (this.source[1]) {
                this.source[1] = "var " + this.source[1].substring(2) + ";"
            }
            if (!this.isChild) {
                this.source[1] += '\n' + this.context.programs.join('\n') + '\n'
            }
            if (!this.environment.isSimple) {
                this.source.push("return buffer;")
            }
            var p = this.isChild ? ["depth0", "data"] : ["Handlebars", "depth0", "helpers", "partials", "data"];
            for (var i = 0, l = this.environment.depths.list.length; i < l; i++) {
                p.push("depth" + this.environment.depths.list[i])
            }
            var j = this.mergeSource();
            if (!this.isChild) {
                var r = H.COMPILER_REVISION,
                    v = H.REVISION_CHANGES[r];
                j = "this.compilerInfo = [" + r + ",'" + v + "'];\n" + j
            }
            if (a) {
                p.push(j);
                return Function.apply(this, p)
            } else {
                var n = 'function ' + (this.name || '') + '(' + p.join(',') + ') {\n  ' + j + '}';
                H.log(H.logger.DEBUG, n + "\n\n");
                return n
            }
        },
        mergeSource: function() {
            var a = '',
                b;
            for (var i = 0, c = this.source.length; i < c; i++) {
                var j = this.source[i];
                if (j.appendToBuffer) {
                    if (b) {
                        b = b + '\n    + ' + j.content
                    } else {
                        b = j.content
                    }
                } else {
                    if (b) {
                        a += 'buffer += ' + b + ';\n  ';
                        b = u
                    }
                    a += j + '\n  '
                }
            }
            return a
        },
        blockValue: function() {
            this.context.aliases.blockHelperMissing = 'helpers.blockHelperMissing';
            var p = ["depth0"];
            this.setupParams(0, p);
            this.replaceStack(function(c) {
                p.splice(1, 0, c);
                return "blockHelperMissing.call(" + p.join(", ") + ")"
            })
        },
        ambiguousBlockValue: function() {
            this.context.aliases.blockHelperMissing = 'helpers.blockHelperMissing';
            var p = ["depth0"];
            this.setupParams(0, p);
            var c = this.topStack();
            p.splice(1, 0, c);
            p[p.length - 1] = 'options';
            this.source.push("if (!" + this.lastHelper + ") { " + c + " = blockHelperMissing.call(" + p.join(", ") + "); }")
        },
        appendContent: function(c) {
            this.source.push(this.appendToBuffer(this.quotedString(c)))
        },
        append: function() {
            this.flushInline();
            var a = this.popStack();
            this.source.push("if(" + a + " || " + a + " === 0) { " + this.appendToBuffer(a) + " }");
            if (this.environment.isSimple) {
                this.source.push("else { " + this.appendToBuffer("''") + " }")
            }
        },
        appendEscaped: function() {
            this.context.aliases.escapeExpression = 'this.escapeExpression';
            this.source.push(this.appendToBuffer("escapeExpression(" + this.popStack() + ")"))
        },
        getContext: function(a) {
            if (this.lastContext !== a) {
                this.lastContext = a
            }
        },
        lookupOnContext: function(n) {
            this.push(this.nameLookup('depth' + this.lastContext, n, 'context'))
        },
        pushContext: function() {
            this.pushStackLiteral('depth' + this.lastContext)
        },
        resolvePossibleLambda: function() {
            this.context.aliases.functionType = '"function"';
            this.replaceStack(function(c) {
                return "typeof " + c + " === functionType ? " + c + ".apply(depth0) : " + c
            })
        },
        lookup: function(n) {
            this.replaceStack(function(c) {
                return c + " == null || " + c + " === false ? " + c + " : " + this.nameLookup(c, n, 'context')
            })
        },
        lookupData: function(a) {
            this.push('data')
        },
        pushStringParam: function(a, b) {
            this.pushStackLiteral('depth' + this.lastContext);
            this.pushString(b);
            if (typeof a === 'string') {
                this.pushString(a)
            } else {
                this.pushStackLiteral(a)
            }
        },
        emptyHash: function() {
            this.pushStackLiteral('{}');
            if (this.options.stringParams) {
                this.register('hashTypes', '{}');
                this.register('hashContexts', '{}')
            }
        },
        pushHash: function() {
            this.hash = {
                values: [],
                types: [],
                contexts: []
            }
        },
        popHash: function() {
            var a = this.hash;
            this.hash = u;
            if (this.options.stringParams) {
                this.register('hashContexts', '{' + a.contexts.join(',') + '}');
                this.register('hashTypes', '{' + a.types.join(',') + '}')
            }
            this.push('{\n    ' + a.values.join(',\n    ') + '\n  }')
        },
        pushString: function(a) {
            this.pushStackLiteral(this.quotedString(a))
        },
        push: function(a) {
            this.inlineStack.push(a);
            return a
        },
        pushLiteral: function(v) {
            this.pushStackLiteral(v)
        },
        pushProgram: function(a) {
            if (a != null) {
                this.pushStackLiteral(this.programExpression(a))
            } else {
                this.pushStackLiteral(null)
            }
        },
        invokeHelper: function(p, n) {
            this.context.aliases.helperMissing = 'helpers.helperMissing';
            var a = this.lastHelper = this.setupHelper(p, n, true);
            var b = this.nameLookup('depth' + this.lastContext, n, 'context');
            this.push(a.name + ' || ' + b);
            this.replaceStack(function(n) {
                return n + ' ? ' + n + '.call(' + a.callParams + ") " + ": helperMissing.call(" + a.helperMissingParams + ")"
            })
        },
        invokeKnownHelper: function(p, n) {
            var a = this.setupHelper(p, n);
            this.push(a.name + ".call(" + a.callParams + ")")
        },
        invokeAmbiguous: function(n, a) {
            this.context.aliases.functionType = '"function"';
            this.pushStackLiteral('{}');
            var b = this.setupHelper(0, n, a);
            var c = this.lastHelper = this.nameLookup('helpers', n, 'helper');
            var j = this.nameLookup('depth' + this.lastContext, n, 'context');
            var p = this.nextStack();
            this.source.push('if (' + p + ' = ' + c + ') { ' + p + ' = ' + p + '.call(' + b.callParams + '); }');
            this.source.push('else { ' + p + ' = ' + j + '; ' + p + ' = typeof ' + p + ' === functionType ? ' + p + '.apply(depth0) : ' + p + '; }')
        },
        invokePartial: function(n) {
            var p = [this.nameLookup('partials', n, 'partial'), "'" + n + "'", this.popStack(), "helpers", "partials"];
            if (this.options.data) {
                p.push("data")
            }
            this.context.aliases.self = "this";
            this.push("self.invokePartial(" + p.join(", ") + ")")
        },
        assignToHash: function(a) {
            var v = this.popStack(),
                c, b;
            if (this.options.stringParams) {
                b = this.popStack();
                c = this.popStack()
            }
            var j = this.hash;
            if (c) {
                j.contexts.push("'" + a + "': " + c)
            }
            if (b) {
                j.types.push("'" + a + "': " + b)
            }
            j.values.push("'" + a + "': (" + v + ")")
        },
        compiler: J,
        compileChildren: function(a, b) {
            var c = a.children,
                j, n;
            for (var i = 0, l = c.length; i < l; i++) {
                j = c[i];
                n = new this.compiler();
                var p = this.matchExistingProgram(j);
                if (p == null) {
                    this.context.programs.push('');
                    p = this.context.programs.length;
                    j.index = p;
                    j.name = 'program' + p;
                    this.context.programs[p] = n.compile(j, b, this.context);
                    this.context.environments[p] = j
                } else {
                    j.index = p;
                    j.name = 'program' + p
                }
            }
        },
        matchExistingProgram: function(c) {
            for (var i = 0, a = this.context.environments.length; i < a; i++) {
                var b = this.context.environments[i];
                if (b && b.equals(c)) {
                    return i
                }
            }
        },
        programExpression: function(a) {
            this.context.aliases.self = "this";
            if (a == null) {
                return "self.noop"
            }
            var c = this.environment.children[a],
                b = c.depths.list,
                j;
            var p = [c.index, c.name, "data"];
            for (var i = 0, l = b.length; i < l; i++) {
                j = b[i];
                if (j === 1) {
                    p.push("depth0")
                } else {
                    p.push("depth" + (j - 1))
                }
            }
            return (b.length === 0 ? "self.program(" : "self.programWithDepth(") + p.join(", ") + ")"
        },
        register: function(n, v) {
            this.useRegister(n);
            this.source.push(n + " = " + v + ";")
        },
        useRegister: function(n) {
            if (!this.registers[n]) {
                this.registers[n] = true;
                this.registers.list.push(n)
            }
        },
        pushStackLiteral: function(a) {
            return this.push(new L(a))
        },
        pushStack: function(a) {
            this.flushInline();
            var b = this.incrStack();
            if (a) {
                this.source.push(b + " = " + a + ";")
            }
            this.compileStack.push(b);
            return b
        },
        replaceStack: function(c) {
            var p = '',
                a = this.isInline(),
                b;
            if (a) {
                var j = this.popStack(true);
                if (j instanceof L) {
                    b = j.value
                } else {
                    var n = this.stackSlot ? this.topStackName() : this.incrStack();
                    p = '(' + this.push(n) + ' = ' + j + '),';
                    b = this.topStack()
                }
            } else {
                b = this.topStack()
            }
            var r = c.call(this, b);
            if (a) {
                if (this.inlineStack.length || this.compileStack.length) {
                    this.popStack()
                }
                this.push('(' + p + r + ')')
            } else {
                if (!/^stack/.test(b)) {
                    b = this.nextStack()
                }
                this.source.push(b + " = (" + p + r + ");")
            }
            return b
        },
        nextStack: function() {
            return this.pushStack()
        },
        incrStack: function() {
            this.stackSlot++;
            if (this.stackSlot > this.stackVars.length) {
                this.stackVars.push("stack" + this.stackSlot)
            }
            return this.topStackName()
        },
        topStackName: function() {
            return "stack" + this.stackSlot
        },
        flushInline: function() {
            var a = this.inlineStack;
            if (a.length) {
                this.inlineStack = [];
                for (var i = 0, b = a.length; i < b; i++) {
                    var c = a[i];
                    if (c instanceof L) {
                        this.compileStack.push(c)
                    } else {
                        this.pushStack(c)
                    }
                }
            }
        },
        isInline: function() {
            return this.inlineStack.length
        },
        popStack: function(w) {
            var a = this.isInline(),
                b = (a ? this.inlineStack : this.compileStack).pop();
            if (!w && (b instanceof L)) {
                return b.value
            } else {
                if (!a) {
                    this.stackSlot--
                }
                return b
            }
        },
        topStack: function(w) {
            var a = (this.isInline() ? this.inlineStack : this.compileStack),
                b = a[a.length - 1];
            if (!w && (b instanceof L)) {
                return b.value
            } else {
                return b
            }
        },
        quotedString: function(a) {
            return '"' + a.replace(/\\/g, '\\\\').replace(/"/g, '\\"').replace(/\n/g, '\\n').replace(/\r/g, '\\r').replace(/\u2028/g, '\\u2028').replace(/\u2029/g, '\\u2029') + '"'
        },
        setupHelper: function(p, n, a) {
            var b = [];
            this.setupParams(p, b, a);
            var c = this.nameLookup('helpers', n, 'helper');
            return {
                params: b,
                name: c,
                callParams: ["depth0"].concat(b).join(", "),
                helperMissingParams: a && ["depth0", this.quotedString(n)].concat(b).join(", ")
            }
        },
        setupParams: function(p, a, b) {
            var c = [],
                j = [],
                n = [],
                r, v, w;
            c.push("hash:" + this.popStack());
            v = this.popStack();
            w = this.popStack();
            if (w || v) {
                if (!w) {
                    this.context.aliases.self = "this";
                    w = "self.noop"
                }
                if (!v) {
                    this.context.aliases.self = "this";
                    v = "self.noop"
                }
                c.push("inverse:" + v);
                c.push("fn:" + w)
            }
            for (var i = 0; i < p; i++) {
                r = this.popStack();
                a.push(r);
                if (this.options.stringParams) {
                    n.push(this.popStack());
                    j.push(this.popStack())
                }
            }
            if (this.options.stringParams) {
                c.push("contexts:[" + j.join(",") + "]");
                c.push("types:[" + n.join(",") + "]");
                c.push("hashContexts:hashContexts");
                c.push("hashTypes:hashTypes")
            }
            if (this.options.data) {
                c.push("data:data")
            }
            c = "{" + c.join(",") + "}";
            if (b) {
                this.register('options', c);
                a.push('options')
            } else {
                a.push(c)
            }
            return a.join(", ")
        }
    };
    var q = ("break else new var" + " case finally return void" + " catch for switch while" + " continue function this with" + " default if throw" + " delete in try" + " do instanceof typeof" + " abstract enum int short" + " boolean export interface static" + " byte extends long super" + " char final native synchronized" + " class float package throws" + " const goto private transient" + " debugger implements protected volatile" + " double import public let yield").split(" ");
    var s = J.RESERVED_WORDS = {};
    for (var i = 0, l = q.length; i < l; i++) {
        s[q[i]] = true
    }
    J.isValidJavaScriptVariableName = function(n) {
        if (!J.RESERVED_WORDS[n] && /^[a-zA-Z_$][0-9a-zA-Z_$]+$/.test(n)) {
            return true
        }
        return false
    };
    H.precompile = function(a, b) {
        if (a == null || (typeof a !== 'string' && a.constructor !== H.AST.ProgramNode)) {
            throw new H.Exception("You must pass a string or Handlebars AST to Handlebars.precompile. You passed " + a)
        }
        b = b || {};
        if (!('data' in b)) {
            b.data = true
        }
        var c = H.parse(a);
        var j = new C().compile(c, b);
        return new J().compile(j, b)
    };
    H.compile = function(a, b) {
        if (a == null || (typeof a !== 'string' && a.constructor !== H.AST.ProgramNode)) {
            throw new H.Exception("You must pass a string or Handlebars AST to Handlebars.compile. You passed " + a)
        }
        b = b || {};
        if (!('data' in b)) {
            b.data = true
        }
        var c;

        function j() {
            var n = H.parse(a);
            var p = new C().compile(n, b);
            var r = new J().compile(p, b, u, true);
            return H.template(r)
        }
        return function(n, b) {
            if (!c) {
                c = j()
            }
            return c.call(this, n, b)
        }
    };;
    H.VM = {
        template: function(a) {
            var c = {
                escapeExpression: H.Utils.escapeExpression,
                invokePartial: H.VM.invokePartial,
                programs: [],
                program: function(i, b, j) {
                    var p = this.programs[i];
                    if (j) {
                        p = H.VM.program(i, b, j)
                    } else if (!p) {
                        p = this.programs[i] = H.VM.program(i, b)
                    }
                    return p
                },
                merge: function(p, b) {
                    var r = p || b;
                    if (p && b) {
                        r = {};
                        H.Utils.extend(r, b);
                        H.Utils.extend(r, p)
                    }
                    return r
                },
                programWithDepth: H.VM.programWithDepth,
                noop: H.VM.noop,
                compilerInfo: null
            };
            return function(b, j) {
                j = j || {};
                var r = a.call(c, H, b, j.helpers, j.partials, j.data);
                var n = c.compilerInfo || [],
                    p = n[0] || 1,
                    v = H.COMPILER_REVISION;
                if (p !== v) {
                    if (p < v) {
                        var w = H.REVISION_CHANGES[v],
                            x = H.REVISION_CHANGES[p];
                        throw "Template was precompiled with an older version of Handlebars than the current runtime. " + "Please update your precompiler to a newer version (" + w + ") or downgrade your runtime to an older version (" + x + ")."
                    } else {
                        throw "Template was precompiled with a newer version of Handlebars than the current runtime. " + "Please update your runtime to a newer version (" + n[1] + ")."
                    }
                }
                return r
            }
        },
        programWithDepth: function(i, a, b) {
            var c = Array.prototype.slice.call(arguments, 3);
            var p = function(j, n) {
                n = n || {};
                return a.apply(this, [j, n.data || b].concat(c))
            };
            p.program = i;
            p.depth = c.length;
            return p
        },
        program: function(i, a, b) {
            var p = function(c, j) {
                j = j || {};
                return a(c, j.data || b)
            };
            p.program = i;
            p.depth = 0;
            return p
        },
        noop: function() {
            return ""
        },
        invokePartial: function(p, n, c, a, b, j) {
            var r = {
                helpers: a,
                partials: b,
                data: j
            };
            if (p === u) {
                throw new H.Exception("The partial " + n + " could not be found")
            } else if (p instanceof Function) {
                return p(c, r)
            } else if (!H.compile) {
                throw new H.Exception("The partial " + n + " could not be compiled when running in runtime-only mode")
            } else {
                b[n] = H.compile(p, {
                    data: j !== u
                });
                return b[n](c, r)
            }
        }
    };
    H.template = H.VM.template;
})(Handlebars);;