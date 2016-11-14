var input = './lib.js'
var output = './bundle.js'
if(process.argv.length>2)
 input = process.argv[2]
if(process.argv.length>3)
  output = process.argv[3]

var fs = require("fs")
var browserify = require('browserify');
var b = browserify(input, { standalone: 'Bundle'});
var out = fs.createWriteStream(output)
b.bundle().pipe(out)
