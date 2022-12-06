/*
   Copyright 2018 - 2022 Volker Berlin (i-net software)

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

*/
package de.inetsoftware.jwebassembly.module;

import java.io.IOException;

import javax.annotation.Nonnull;

import de.inetsoftware.jwebassembly.WasmException;
import de.inetsoftware.jwebassembly.javascript.JavaScriptSyntheticFunctionName;
import de.inetsoftware.jwebassembly.module.TypeManager.StructType;
import de.inetsoftware.jwebassembly.wasm.AnyType;
import de.inetsoftware.jwebassembly.wasm.ArrayOperator;
import de.inetsoftware.jwebassembly.wasm.ArrayType;
import de.inetsoftware.jwebassembly.wasm.ValueType;

/**
 * WasmInstruction for an array operation.
 * 
 * @author Volker Berlin
 *
 */
class WasmArrayInstruction extends WasmInstruction {

    @Nonnull
    private final ArrayOperator   op;

    private final AnyType         type;

    private final ArrayType       arrayType;

    private final TypeManager     types;

    private SyntheticFunctionName functionName;

    /**
     * Create an instance of an array operation.
     * 
     * @param op
     *            the array operation
     * @param type
     *            the type of the parameters
     * @param types
     *            the type manager
     * @param javaCodePos
     *            the code position/offset in the Java method
     * @param lineNumber
     *            the line number in the Java source code
     */
    WasmArrayInstruction( @Nonnull ArrayOperator op, @Nonnull AnyType type, TypeManager types, int javaCodePos, int lineNumber ) {
        super( javaCodePos, lineNumber );
        this.op = op;
        this.type = type;
        this.types = types;
        this.arrayType = types.arrayType( type );
    }

    /**
     * Create the synthetic function of this instruction if required for the operation.
     * 
     * @param useGC true, with GC code
     * @return the function or null if not needed
     */
    SyntheticFunctionName createNonGcFunction( boolean useGC ) {
        if( useGC ) {
            switch( op ) {
                case NEW:
                    functionName = new WatCodeSyntheticFunctionName( "array_new_" + validJsName( type ), "", ValueType.i32, null, arrayType ) {
                        @Override
                        protected String getCode() {
                            String nativeArrayTypeName = ((ArrayType)arrayType.getNativeArrayType()).getName();
                            return "i32.const " + arrayType.getVTable() + " i32.const 0" // hashcode
                                            + " local.get 0" // array size
                                            + " rtt.canon " + nativeArrayTypeName //
                                            + " array.new_default_with_rtt " + nativeArrayTypeName //
                                            + " rtt.canon " + arrayType.getName() //
                                            + " struct.new_with_rtt " + arrayType.getName() //
                                            + " return";
                        }
                    };
            }
        } else {
            // i8 and i16 are not valid in function signatures
            AnyType functionType = type == ValueType.i8 || type == ValueType.i16 ? ValueType.i32 : type;
            switch( op ) {
                case NEW:
                    String cmd;
                    if( type.isRefType() ) {
                        cmd = "Object.seal(new Array(l).fill(null))";
                    } else {
                        switch( (ValueType)type ) {
                            case i8:
                                cmd = "new Uint8Array(l)";
                                break;
                            case i16:
                                cmd = "new Int16Array(l)";
                                break;
                            case i32:
                                cmd = "new Int32Array(l)";
                                break;
                            case i64:
                                cmd = "new BigInt64Array(l)";
                                break;
                            case f32:
                                cmd = "new Float32Array(l)";
                                break;
                            case f64:
                                cmd = "new Float64Array(l)";
                                break;
                            default:
                                cmd = "Object.seal(new Array(l).fill(null))";
                        }
                    }
                    functionName = new JavaScriptSyntheticFunctionName( "NonGC", "array_new_" + validJsName( type ), () -> {
                        // create the default values of a new type
                        return new StringBuilder( "(l)=>Object.seal({0:" ) // fix count of elements
                                        .append( arrayType.getVTable() ) // .vtable
                                        .append( ",1:0,2:" ) // .hashCode
                                        .append( cmd ) // the array data
                                        .append( "})" ) //
                                        .toString();
                    }, ValueType.i32, null, ValueType.externref );
                    break;
                case GET:
                case GET_S:
                case GET_U:
                    functionName = new JavaScriptSyntheticFunctionName( "NonGC", "array_get_" + validJsName( functionType ), () -> "(a,i)=>a[2][i]", ValueType.externref, ValueType.i32, null, functionType );
                    break;
                case SET:
                    functionName = new JavaScriptSyntheticFunctionName( "NonGC", "array_set_" + validJsName( functionType ), () -> "(a,i,v)=>a[2][i]=v", ValueType.externref, ValueType.i32, functionType, null, null );
                    break;
                case LEN:
                    functionName = new JavaScriptSyntheticFunctionName( "NonGC", "array_len", () -> "(a)=>a[2].length", ValueType.externref, null, ValueType.i32 );
                    break;
            }
        }
        return functionName;
    }

    /**
     * Get a valid JavaScript name.
     * 
     * @param type
     *            the type
     * @return the identifier that is valid
     */
    private static String validJsName( AnyType type ) {
        if( type.isRefType() ) {
            return ((StructType)type).getName().replace( '[', '_' ).replace( '/', '_' ).replace( '.', '_' ).replace( ";", "" );
        }
        return type.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    Type getType() {
        return Type.Array;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeTo( @Nonnull ModuleWriter writer ) throws IOException {
        if( functionName != null ) { // nonGC
            writer.writeFunctionCall( functionName, null );
        } else {
            writer.writeArrayOperator( op, arrayType );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    AnyType getPushValueType() {
        switch( op ) {
            case NEW:
            case NEW_ARRAY_WITH_RTT:
                return arrayType;
            case GET:
                return type;
            case GET_S:
            case GET_U:
                if( type == ValueType.i8 || type == ValueType.i16 ) {
                    return ValueType.i32;
                }
                return type;
            case SET:
                return null;
            case LEN:
                return ValueType.i32;
            default:
                throw new WasmException( "Unknown array operation: " + op, -1 );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    int getPopCount() {
        switch( op ) {
            case GET:
            case GET_S:
            case GET_U:
            case NEW_ARRAY_WITH_RTT:
                return 2;
            case NEW:
            case LEN:
                return 1;
            case SET:
                return 3;
            default:
                throw new WasmException( "Unknown array operation: " + op, -1 );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    AnyType[] getPopValueTypes() {
        switch( op ) {
            case GET:
            case GET_S:
            case GET_U:
                return new AnyType[] { arrayType, ValueType.i32 };
            case NEW_ARRAY_WITH_RTT:
                return new AnyType[] { ValueType.i32, ValueType.i32 }; // size, rtt type
            case NEW:
                return new AnyType[] { ValueType.i32 };
            case LEN:
                return new AnyType[] { arrayType };
            case SET:
                return new AnyType[] { arrayType, ValueType.i32, type };
            default:
                throw new WasmException( "Unknown array operation: " + op, -1 );
        }
    }
}
