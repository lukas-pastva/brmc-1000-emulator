/*
 * Copyright 2017 - 2019 Volker Berlin (i-net software)
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
package de.inetsoftware.jwebassembly.runtime;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.ClassRule;
import org.junit.runners.Parameterized.Parameters;

import de.inetsoftware.jwebassembly.ScriptEngine;
import de.inetsoftware.jwebassembly.WasmRule;
import de.inetsoftware.jwebassembly.api.annotation.Export;

/**
 * @author Volker Berlin
 */
public class MathOperations extends AbstractBaseTest {
    
    @ClassRule
    public static WasmRule rule = new WasmRule( TestClass.class ); 

    public MathOperations( ScriptEngine script, String method, Object[] params ) {
        super( rule, script, method, params );
    }

    @Parameters(name="{0}-{1}")
    public static Collection<Object[]> data() {
        ArrayList<Object[]> list = new ArrayList<>();
        for( ScriptEngine script : ScriptEngine.testEngines() ) {
            addParam( list, script, "intConst" );
            addParam( list, script, "floatConst" );
            addParam( list, script, "doubleConst" );
            addParam( list, script, "addInt", 1, 3 );
            addParam( list, script, "addLong" );
            addParam( list, script, "addFloat", 1F, 3.5F );
            addParam( list, script, "addDouble", 1.0, 3.5 );
            addParam( list, script, "subInt", 1, 3 );
            addParam( list, script, "subLong" );
            addParam( list, script, "subFloat", 1F, 3.5F );
            addParam( list, script, "subDouble", 1.0, 3.5 );
            addParam( list, script, "mulDivInt" );
            addParam( list, script, "mulDivLong" );
            addParam( list, script, "mulDivFloat" );
            addParam( list, script, "mulDivDouble" );
            addParam( list, script, "intBits" );
            addParam( list, script, "longBits" );
            addParam( list, script, "byteInc", (byte)127 );
            addParam( list, script, "byteDec", (byte)-128 );
            addParam( list, script, "shortInc", (short)-32768 );
            addParam( list, script, "charOp", (char)0xFFFF );
            addParam( list, script, "castNumberOverflow" );
            addParam( list, script, "doubleNaN" );
            addParam( list, script, "floatNaN" );
        }
        rule.setTestParameters( list );
        return list;
    }

    static class TestClass {

        @Export
        static int intConst() {
            return 42;
        }

        @Export
        static float floatConst() {
            return 42.5F;
        }

        @Export
        static double doubleConst() {
            return 42.5;
        }

        @Export
        static int addInt( int a, int b ) {
            int c = 1234567;
            int e = -1;
            e = -e;
            double d = -1234567;
            int i = 1;
            long l = 2;
            float f = 3;
            b++;
            return a + b + c + e + (int)d + i + (int)l + (int)f;
        }

        @Export
        static int addLong() {
            long a = 1L;
            long b = -3L;
            b = -b;
            return (int)(a + b);
        }

        @Export
        static float addFloat( float a, float b ) {
            float c = -1;
            float e = 1.25F;
            double d = 1234;
            e = -e;
            int i = 1;
            long l = 2;
            float f = 3;
            return a + b + c + e + (float)d + i + l + f;
        }

        @Export
        static double addDouble( double a, double b ) {
            double c = -1;
            double d = 1234;
            double e = 1.25;
            e = -e;
            int i = 1;
            long l = 2;
            float f = 3;
            return a + b + c + d + e + i + l + f;
        }

        @Export
        static int subInt( int a, int b ) {
            return a - b;
        }

        @Export
        static int subLong() {
            long a = -1L;
            long b = 3L;
            long c = -1L;
            double d = 1234;
            int i = 1;
            long l = 2;
            float f = 3;
            a--;
            return (int)(a - b - c - (long)d - i - l - (long)f);
        }

        @Export
        static float subFloat( float a, float b ) {
            return a - b;
        }

        @Export
        static double subDouble( double a, double b ) {
            return a - b;
        }

        @Export
        static int mulDivInt() {
            int a = 420;
            a *= 3;
            a /= -5;
            a %= 37;
            return a;
        }

        @Export
        static int mulDivLong() {
            long a = -54321;
            a *= 3;
            a /= -5;
            a %= 37;
            return (int)a;
        }

        @Export
        static float mulDivFloat() {
            float a = -54321F;
            a *= 3F;
            a /= -8F;
            a %= 37.5F;
            return a;
        }

        @Export
        static double mulDivDouble() {
            double a = -54321.0;
            a *= 3F;
            a /= -5F;
            a %= 37.5;
            return a;
        }

        @Export
        static int intBits() {
            int a = 1;
            a = a << 1;
            a = a | 15;
            a = a & 4;
            int b = Integer.MIN_VALUE;
            b = b >>> 1;
            b = b >> 2;
            return a ^ b;
        }

        @Export
        static int longBits() {
            long a = 1;
            a = a << 1;
            a = a | 15;
            a = a & 4;
            long b = Long.MIN_VALUE;
            b = b >>> 1;
            b = b >> 2;
            return (int)(a ^ (b >> 32));
        }

        @Export
        static byte byteInc( byte a ) {
            a++;
            a += 2;
            return a;
        }

        @Export
        static byte byteDec( byte a ) {
            a--;
            a -= 2;
            return a;
        }

        @Export
        static short shortInc( short a ) {
            a++;
            a += 2;
            return a;
        }

        @Export
        static char charOp( char a ) {
            a++;
            a += 60;
            return a;
        }

        @Export
        static int castNumberOverflow() {
            int result = 0;
            float f = 2E30F;
            double d = f;

            int i = (int)f;
            if( i == Integer.MAX_VALUE) {
                result |= 0x1;
            }
            i = (int)-f;
            if( i == Integer.MIN_VALUE) {
                result |= 0x2;
            }

            long l = (long)f;
            if( l == Long.MAX_VALUE) {
                result |= 0x4;
            }
            l = (long)-f;
            if( l == Long.MIN_VALUE) {
                result |= 0x8;
            }

            i = (int)d;
            if( i == Integer.MAX_VALUE) {
                result |= 0x10;
            }
            i = (int)-d;
            if( i == Integer.MIN_VALUE) {
                result |= 0x20;
            }

            l = (long)d;
            if( l == Long.MAX_VALUE) {
                result |= 0x40;
            }
            l = (long)-d;
            if( l == Long.MIN_VALUE) {
                result |= 0x80;
            }

            return result;
        }

        @Export
        static double doubleNaN() {
            return Double.NaN;
        }

        @Export
        static float floatNaN() {
            return Float.NaN;
        }
    }
}
