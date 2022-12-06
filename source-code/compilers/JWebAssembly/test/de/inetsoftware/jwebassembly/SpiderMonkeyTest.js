load( "{test.wasm}.js" );
var wasm = read( "{test.wasm}", "binary" ); // https://developer.mozilla.org/en-US/docs/Mozilla/Projects/SpiderMonkey/Shell_global_objects
var testData = JSON.parse( read( "testdata.json" ) );

// save the test result
function saveResults(result) {
    const original = redirect( "testresult.json" );
    putstr( JSON.stringify(result) );
    redirect( original );
}

function callExport( instance, wasmImports ) {
    wasmImports.exports = instance.exports;
    var result = {};
    for (var method in testData) {
        try{
            result[method] = String(instance.exports[method]( ...testData[method] ));
        }catch(err){
            result[method] = err.toString() + '\n' + err.stack;
        }
    }
    saveResults(result);
}

WebAssembly.instantiate( wasm, wasmImports ).then(
  obj => callExport( obj.instance, wasmImports ),
  reason => console.log(reason)
);
