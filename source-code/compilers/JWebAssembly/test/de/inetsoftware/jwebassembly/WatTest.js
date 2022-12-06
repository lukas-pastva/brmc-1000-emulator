#!/usr/bin/env node

var fs = require('fs');
require("wabt")().then(wabt => {

const wasmImports = require( "./{test}.wasm.js" );
var filename = '{test}.wat';
var text = fs.readFileSync(filename, "utf8");
var testData = JSON.parse( fs.readFileSync( "testdata.json", "utf8" ) );

var features = {'sat_float_to_int':true, 'sign_extension':true, 'exceptions':true, 'reference_types':true, 'gc':true};
var wasm = wabt.parseWat(filename, text, features);
wasm = wasm.toBinary({}).buffer;

// save the test result
function saveResults(result) {
    fs.writeFileSync( "testresult.json", JSON.stringify(result) );
}

function callExport( instance, wasmImports ) {
    wasmImports.exports = instance.exports;
    var result = {};
    for (var method in testData) {
        try{
            result[method] = String(instance.exports[method]( ...testData[method] ));
        }catch(err){
            result[method] = err.stack;
        }
    }
    saveResults(result);
}

WebAssembly.instantiate( wasm, wasmImports ).then(
  obj => callExport( obj.instance, wasmImports ),
  reason => console.log(reason)
);

}); // wabt