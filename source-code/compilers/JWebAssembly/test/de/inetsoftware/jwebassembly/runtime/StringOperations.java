/*
 * Copyright 2019 - 2020 Volker Berlin (i-net software)
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

import org.junit.Assume;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;

import de.inetsoftware.jwebassembly.ScriptEngine;
import de.inetsoftware.jwebassembly.WasmRule;
import de.inetsoftware.jwebassembly.api.annotation.Export;
import de.inetsoftware.jwebassembly.web.DOMString;
import de.inetsoftware.jwebassembly.web.JSObject;

public class StringOperations extends AbstractBaseTest {

    @ClassRule
    public static WasmRule rule = new WasmRule( TestClass.class );

    public StringOperations( ScriptEngine script, String method, Object[] params ) {
        super( rule, script, method, params );
    }

    @Parameters( name = "{0}-{1}" )
    public static Collection<Object[]> data() {
        ArrayList<Object[]> list = new ArrayList<>();
        for( ScriptEngine script : ScriptEngine.testEngines() ) {
            addParam( list, script, "newFromChars" );
            addParam( list, script, "newFromBytes" );
            addParam( list, script, "constant" );
            addParam( list, script, "objToString" );
        }
        rule.setTestParameters( list );
        return list;
    }

    static class TestClass {

        @Export
        static DOMString newFromChars() {
            char[] chars = {'ä','ö','ü'};
            return JSObject.domString( new String( chars ) );
        }

        @Export
        static DOMString newFromBytes() {
            byte[] bytes = {(byte)0xC3,(byte)0xA4};
            return JSObject.domString( new String( bytes ) );
        }

        @Export
        static DOMString constant() {
            // string larger as 128 bytes
            String constant  = "1234567890 äöüäöüß " // umlaute
                            + "𝟘𝟙𝟚𝟛𝟜𝟝𝟞𝟟𝟠𝟡 𝒥𝒶𝓋𝒶𝓈𝒸𝓇𝒾𝓅𝓉 " // surrogate chars
                            + "abcdefghijklmnopqrstuvwxyz";
            return JSObject.domString( constant );
        }

        @Export
        static DOMString objToString() {
            Object obj = new Object();
            return JSObject.domString( obj.toString().substring( 0, 17 ) );
        }

    }
}
