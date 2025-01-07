﻿/**

JSZip - A Javascript class for generating Zip files
<http://stuartk.com/jszip>

(c) 2009 Stuart Knightley <stuart [at] stuartk.com>
Licenced under the GPLv3 and the MIT licences

Usage:
   zip = new JSZip();
   zip.file("hello.txt", "Hello, World!").add("tempfile", "nothing");
   zip.folder("images").file("smile.gif", base64Data, {base64: true});
   zip.file("Xmas.txt", "Ho ho ho !", {date : new Date("December 25, 2007 00:00:01")});
   zip.remove("tempfile");

   base64zip = zip.generate();

**/

var JSZip = function(d, o) {
    this.files = {};
    this.root = "";
    if (d) this.load(d, o)
};
JSZip.signature = {
    LOCAL_FILE_HEADER: "\x50\x4b\x03\x04",
    CENTRAL_FILE_HEADER: "\x50\x4b\x01\x02",
    CENTRAL_DIRECTORY_END: "\x50\x4b\x05\x06",
    ZIP64_CENTRAL_DIRECTORY_LOCATOR: "\x50\x4b\x06\x07",
    ZIP64_CENTRAL_DIRECTORY_END: "\x50\x4b\x06\x06",
    DATA_DESCRIPTOR: "\x50\x4b\x07\x08"
};
JSZip.defaults = {
    base64: false,
    binary: false,
    dir: false,
    date: null
};
JSZip.prototype = (function() {
    var Z = function(n, c, o) {
        this.name = n;
        this.data = c;
        this.options = o
    };
    Z.prototype = {
        asText: function() {
            return this.options.binary ? JSZip.prototype.utf8decode(this.data) : this.data
        },
        asBinary: function() {
            return this.options.binary ? this.data : JSZip.prototype.utf8encode(this.data)
        }
    };
    var d = function(c, h) {
        var j = "",
            i;
        for (i = 0; i < h; i++) {
            j += String.fromCharCode(c & 0xff);
            c = c >>> 8
        }
        return j
    };
    var e = function() {
        var r = {}, i, c;
        for (i = 0; i < arguments.length; i++) {
            for (c in arguments[i]) {
                if (typeof r[c] === "undefined") {
                    r[c] = arguments[i][c]
                }
            }
        }
        return r
    };
    var p = function(o) {
        o = o || {};
        if (o.base64 === true && o.binary == null) o.binary = true;
        o = e(o, JSZip.defaults);
        o.date = o.date || new Date();
        return o
    };
    var f = function(n, c, o) {
        var h = a(n);
        if (h) b.call(this, h);
        o = p(o);
        return this.files[n] = {
            name: n,
            data: c,
            options: o
        }
    };
    var a = function(c) {
        if (c.slice(-1) == '/') {
            c = c.substring(0, c.length - 1)
        }
        var l = c.lastIndexOf('/');
        return (l > 0) ? c.substring(0, l) : ""
    };
    var b = function(n) {
        if (n.slice(-1) != "/") n += "/";
        if (!this.files[n]) {
            var c = a(n);
            if (c) b.call(this, c);
            f.call(this, n, '', {
                dir: true
            })
        }
        return this.files[n]
    };
    var g = function(c, u, h) {
        var i = u !== c.name,
            j = c.data,
            o = c.options,
            k, l;
        k = o.date.getHours();
        k = k << 6;
        k = k | o.date.getMinutes();
        k = k << 5;
        k = k | o.date.getSeconds() / 2;
        l = o.date.getFullYear() - 1980;
        l = l << 4;
        l = l | (o.date.getMonth() + 1);
        l = l << 5;
        l = l | o.date.getDate();
        if (o.base64 === true) j = JSZipBase64.decode(j);
        if (o.binary === false) j = this.utf8encode(j);
        var m = JSZip.compressions[h];
        var n = m.compress(j);
        var q = "";
        q += "\x0A\x00";
        q += i ? "\x00\x08" : "\x00\x00";
        q += m.magic;
        q += d(k, 2);
        q += d(l, 2);
        q += d(this.crc32(j), 4);
        q += d(n.length, 4);
        q += d(j.length, 4);
        q += d(u.length, 2);
        q += "\x00\x00";
        return {
            header: q,
            compressedData: n
        }
    };
    return {
        load: function(s, o) {
            throw new Error("Load method is not defined. Is the file jszip-load.js included ?")
        },
        filter: function(s) {
            var r = [],
                c, h, i, j;
            for (c in this.files) {
                i = this.files[c];
                j = new Z(i.name, i.data, e(i.options));
                h = c.slice(this.root.length, c.length);
                if (c.slice(0, this.root.length) === this.root && s(h, j)) {
                    r.push(j)
                }
            }
            return r
        },
        file: function(n, c, o) {
            if (arguments.length === 1) {
                if (n instanceof RegExp) {
                    var r = n;
                    return this.filter(function(h, i) {
                        return !i.options.dir && r.test(h)
                    })
                } else {
                    return this.filter(function(h, i) {
                        return !i.options.dir && h === n
                    })[0] || null
                }
            } else {
                n = this.root + n;
                f.call(this, n, c, o)
            }
            return this
        },
        folder: function(c) {
            if (!c) {
                throw new Error("folder : wrong argument")
            }
            if (c instanceof RegExp) {
                return this.filter(function(i, j) {
                    return j.options.dir && c.test(i)
                })
            }
            var n = this.root + c;
            var h = b.call(this, n);
            var r = this.clone();
            r.root = h.name;
            return r
        },
        remove: function(n) {
            n = this.root + n;
            var c = this.files[n];
            if (!c) {
                if (n.slice(-1) != "/") n += "/";
                c = this.files[n]
            }
            if (c) {
                if (!c.options.dir) {
                    delete this.files[n]
                } else {
                    var k = this.filter(function(r, c) {
                        return c.name.slice(0, n.length) === n
                    });
                    for (var i = 0; i < k.length; i++) {
                        delete this.files[k[i].name]
                    }
                }
            }
            return this
        },
        generate: function(o) {
            o = e(o || {}, {
                base64: true,
                compression: "STORE"
            });
            var c = o.compression.toUpperCase();
            var h = [],
                i = [],
                j = 0;
            if (!JSZip.compressions[c]) {
                throw c + " is not a valid compression method !"
            }
            for (var n in this.files) {
                if (!this.files.hasOwnProperty(n)) {
                    continue
                }
                var k = this.files[n];
                var u = this.utf8encode(k.name);
                var l = "",
                    m = "",
                    q = g.call(this, k, u, c);
                l = JSZip.signature.LOCAL_FILE_HEADER + q.header + u + q.compressedData;
                m = JSZip.signature.CENTRAL_FILE_HEADER + "\x14\x00" + q.header + "\x00\x00" + "\x00\x00" + "\x00\x00" + (this.files[n].dir === true ? "\x10\x00\x00\x00" : "\x00\x00\x00\x00") + d(j, 4) + u;
                j += l.length;
                i.push(l);
                h.push(m)
            }
            var r = i.join("");
            var s = h.join("");
            var t = "";
            t = JSZip.signature.CENTRAL_DIRECTORY_END + "\x00\x00" + "\x00\x00" + d(i.length, 2) + d(i.length, 2) + d(s.length, 4) + d(r.length, 4) + "\x00\x00";
            var z = r + s + t;
            return (o.base64) ? JSZipBase64.encode(z) : z
        },
        crc32: function(s, c) {
            if (s === "" || typeof s === "undefined") return 0;
            var t = "00000000 77073096 EE0E612C 990951BA 076DC419 706AF48F E963A535 9E6495A3 0EDB8832 79DCB8A4 E0D5E91E 97D2D988 09B64C2B 7EB17CBD E7B82D07 90BF1D91 1DB71064 6AB020F2 F3B97148 84BE41DE 1ADAD47D 6DDDE4EB F4D4B551 83D385C7 136C9856 646BA8C0 FD62F97A 8A65C9EC 14015C4F 63066CD9 FA0F3D63 8D080DF5 3B6E20C8 4C69105E D56041E4 A2677172 3C03E4D1 4B04D447 D20D85FD A50AB56B 35B5A8FA 42B2986C DBBBC9D6 ACBCF940 32D86CE3 45DF5C75 DCD60DCF ABD13D59 26D930AC 51DE003A C8D75180 BFD06116 21B4F4B5 56B3C423 CFBA9599 B8BDA50F 2802B89E 5F058808 C60CD9B2 B10BE924 2F6F7C87 58684C11 C1611DAB B6662D3D 76DC4190 01DB7106 98D220BC EFD5102A 71B18589 06B6B51F 9FBFE4A5 E8B8D433 7807C9A2 0F00F934 9609A88E E10E9818 7F6A0DBB 086D3D2D 91646C97 E6635C01 6B6B51F4 1C6C6162 856530D8 F262004E 6C0695ED 1B01A57B 8208F4C1 F50FC457 65B0D9C6 12B7E950 8BBEB8EA FCB9887C 62DD1DDF 15DA2D49 8CD37CF3 FBD44C65 4DB26158 3AB551CE A3BC0074 D4BB30E2 4ADFA541 3DD895D7 A4D1C46D D3D6F4FB 4369E96A 346ED9FC AD678846 DA60B8D0 44042D73 33031DE5 AA0A4C5F DD0D7CC9 5005713C 270241AA BE0B1010 C90C2086 5768B525 206F85B3 B966D409 CE61E49F 5EDEF90E 29D9C998 B0D09822 C7D7A8B4 59B33D17 2EB40D81 B7BD5C3B C0BA6CAD EDB88320 9ABFB3B6 03B6E20C 74B1D29A EAD54739 9DD277AF 04DB2615 73DC1683 E3630B12 94643B84 0D6D6A3E 7A6A5AA8 E40ECF0B 9309FF9D 0A00AE27 7D079EB1 F00F9344 8708A3D2 1E01F268 6906C2FE F762575D 806567CB 196C3671 6E6B06E7 FED41B76 89D32BE0 10DA7A5A 67DD4ACC F9B9DF6F 8EBEEFF9 17B7BE43 60B08ED5 D6D6A3E8 A1D1937E 38D8C2C4 4FDFF252 D1BB67F1 A6BC5767 3FB506DD 48B2364B D80D2BDA AF0A1B4C 36034AF6 41047A60 DF60EFC3 A867DF55 316E8EEF 4669BE79 CB61B38C BC66831A 256FD2A0 5268E236 CC0C7795 BB0B4703 220216B9 5505262F C5BA3BBE B2BD0B28 2BB45A92 5CB36A04 C2D7FFA7 B5D0CF31 2CD99E8B 5BDEAE1D 9B64C2B0 EC63F226 756AA39C 026D930A 9C0906A9 EB0E363F 72076785 05005713 95BF4A82 E2B87A14 7BB12BAE 0CB61B38 92D28E9B E5D5BE0D 7CDCEFB7 0BDBDF21 86D3D2D4 F1D4E242 68DDB3F8 1FDA836E 81BE16CD F6B9265B 6FB077E1 18B74777 88085AE6 FF0F6A70 66063BCA 11010B5C 8F659EFF F862AE69 616BFFD3 166CCF45 A00AE278 D70DD2EE 4E048354 3903B3C2 A7672661 D06016F7 4969474D 3E6E77DB AED16A4A D9D65ADC 40DF0B66 37D83BF0 A9BCAE53 DEBB9EC5 47B2CF7F 30B5FFE9 BDBDF21C CABAC28A 53B39330 24B4A3A6 BAD03605 CDD70693 54DE5729 23D967BF B3667A2E C4614AB8 5D681B02 2A6F2B94 B40BBE37 C30C8EA1 5A05DF1B 2D02EF8D";
            if (typeof(c) == "undefined") {
                c = 0
            }
            var x = 0;
            var y = 0;
            c = c ^ (-1);
            for (var i = 0, T = s.length; i < T; i++) {
                y = (c ^ s.charCodeAt(i)) & 0xFF;
                x = "0x" + t.substr(y * 9, 8);
                c = (c >>> 8) ^ x
            }
            return c ^ (-1)
        },
        clone: function() {
            var n = new JSZip();
            for (var i in this) {
                if (typeof this[i] !== "function") {
                    n[i] = this[i]
                }
            }
            return n
        },
        utf8encode: function(s) {
            s = s.replace(/\r\n/g, "\n");
            var u = "";
            for (var n = 0; n < s.length; n++) {
                var c = s.charCodeAt(n);
                if (c < 128) {
                    u += String.fromCharCode(c)
                } else if ((c > 127) && (c < 2048)) {
                    u += String.fromCharCode((c >> 6) | 192);
                    u += String.fromCharCode((c & 63) | 128)
                } else {
                    u += String.fromCharCode((c >> 12) | 224);
                    u += String.fromCharCode(((c >> 6) & 63) | 128);
                    u += String.fromCharCode((c & 63) | 128)
                }
            }
            return u
        },
        utf8decode: function(u) {
            var s = "";
            var i = 0;
            var c = 0,
                h = 0,
                j = 0,
                k = 0;
            while (i < u.length) {
                c = u.charCodeAt(i);
                if (c < 128) {
                    s += String.fromCharCode(c);
                    i++
                } else if ((c > 191) && (c < 224)) {
                    j = u.charCodeAt(i + 1);
                    s += String.fromCharCode(((c & 31) << 6) | (j & 63));
                    i += 2
                } else {
                    j = u.charCodeAt(i + 1);
                    k = u.charCodeAt(i + 2);
                    s += String.fromCharCode(((c & 15) << 12) | ((j & 63) << 6) | (k & 63));
                    i += 3
                }
            }
            return s
        }
    }
})();
JSZip.compressions = {
    "STORE": {
        magic: "\x00\x00",
        compress: function(c) {
            return c
        },
        uncompress: function(c) {
            return c
        }
    }
};
var JSZipBase64 = function() {
    var _ = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
    return {
        encode: function(a, u) {
            var o = "";
            var c, b, d, e, f, g, h;
            var i = 0;
            while (i < a.length) {
                c = a.charCodeAt(i++);
                b = a.charCodeAt(i++);
                d = a.charCodeAt(i++);
                e = c >> 2;
                f = ((c & 3) << 4) | (b >> 4);
                g = ((b & 15) << 2) | (d >> 6);
                h = d & 63;
                if (isNaN(b)) {
                    g = h = 64
                } else if (isNaN(d)) {
                    h = 64
                }
                o = o + _.charAt(e) + _.charAt(f) + _.charAt(g) + _.charAt(h)
            }
            return o
        },
        decode: function(a, u) {
            var o = "";
            var c, b, d;
            var e, f, g, h;
            var i = 0;
            a = a.replace(/[^A-Za-z0-9\+\/\=]/g, "");
            while (i < a.length) {
                e = _.indexOf(a.charAt(i++));
                f = _.indexOf(a.charAt(i++));
                g = _.indexOf(a.charAt(i++));
                h = _.indexOf(a.charAt(i++));
                c = (e << 2) | (f >> 4);
                b = ((f & 15) << 4) | (g >> 2);
                d = ((g & 3) << 6) | h;
                o = o + String.fromCharCode(c);
                if (g != 64) {
                    o = o + String.fromCharCode(b)
                }
                if (h != 64) {
                    o = o + String.fromCharCode(d)
                }
            }
            return o
        }
    }
}();