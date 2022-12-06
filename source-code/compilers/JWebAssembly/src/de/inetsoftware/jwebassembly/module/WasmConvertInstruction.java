/*
 * Copyright 2018 - 2021 Volker Berlin (i-net software)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.inetsoftware.jwebassembly.module;

import java.io.IOException;

import de.inetsoftware.jwebassembly.wasm.AnyType;
import de.inetsoftware.jwebassembly.wasm.ValueType;

/**
 * Cast operations for converting one data type to another
 * 
 * @author Volker Berlin
 *
 */
class WasmConvertInstruction extends WasmInstruction {

    private ValueTypeConvertion conversion;

    /**
     * Create an instance of a convert instruction
     * 
     * @param conversion
     *            the conversion type
     * @param javaCodePos
     *            the code position/offset in the Java method
     * @param lineNumber
     *            the line number in the Java source code
     */
    WasmConvertInstruction( ValueTypeConvertion conversion, int javaCodePos, int lineNumber ) {
        super( javaCodePos, lineNumber );
        this.conversion = conversion;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    Type getType() {
        return Type.Convert;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeTo( ModuleWriter writer ) throws IOException {
        writer.writeCast( conversion );
    }

    /**
     * {@inheritDoc}
     */
    AnyType getPushValueType() {
        switch( conversion ) {
            case l2i:
            case f2i:
            case d2i:
            case i2b:
            case i2c:
            case i2s:
                return ValueType.i32;
            case i2l:
            case f2l:
            case d2l:
                return ValueType.i64;
            case i2f:
            case l2f:
            case d2f:
                return ValueType.f32;
            case i2d:
            case l2d:
            case f2d:
                return ValueType.f64;
            default:
                throw new Error( conversion.toString() );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    int getPopCount() {
        return 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    AnyType[] getPopValueTypes() {
        switch( conversion ) {
            case i2b:
            case i2c:
            case i2s:
            case i2l:
            case i2f:
            case i2d:
                return new AnyType[] { ValueType.i32 };
            case l2i:
            case l2f:
            case l2d:
                return new AnyType[] { ValueType.i64 };
            case f2i:
            case f2l:
            case f2d:
                return new AnyType[] { ValueType.f32 };
            case d2i:
            case d2l:
            case d2f:
                return new AnyType[] { ValueType.f64 };
            default:
                throw new Error( conversion.toString() );
        }
    }
}
