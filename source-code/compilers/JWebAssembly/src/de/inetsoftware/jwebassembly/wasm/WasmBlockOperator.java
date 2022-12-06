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
package de.inetsoftware.jwebassembly.wasm;

/**
 * Block operators in the WASM byte code.
 * 
 * @author Volker Berlin
 *
 */
public enum WasmBlockOperator {
    RETURN,
    IF,
    ELSE,
    END,
    DROP,
    BLOCK,
    BR,
    BR_IF,
    BR_TABLE,
    BR_ON_NULL,
    LOOP,
    UNREACHABLE,
    TRY,
    CATCH,
    THROW,
    RETHROW,
    BR_ON_EXN,
    MONITOR_ENTER,
    MONITOR_EXIT,
}
