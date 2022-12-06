/*
 * Copyright 2019 - 2022 Volker Berlin (i-net software)
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
package de.inetsoftware.jwebassembly;

import static de.inetsoftware.jwebassembly.SpiderMonkey.extractStream;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import org.junit.Assert;

/**
 * Download the wat2wasm tool from github
 * 
 * @author Volker Berlin
 */
class Wat2Wasm {

    private String      command;

    private IOException error;

    /**
     * Check if there is a new version of the script engine
     * 
     * @param target
     *            the target directory
     * @throws IOException
     *             if any error occur
     */
    private void download( File target ) throws IOException {
        //boolean is32 = "32".equals( System.getProperty( "sun.arch.data.model" ) );
        String fileName;
        final String os = System.getProperty( "os.name", "" ).toLowerCase();
        if( os.contains( "windows" ) ) {
            fileName = "windows.tar.gz";
        } else if( os.contains( "mac" ) ) {
            fileName = "macos.tar.gz";
        } else if( os.contains( "linux" ) ) {
            fileName = "ubuntu.tar.gz";
        } else {
            throw new IllegalStateException( "Unknown OS: " + os );
        }

        URL url = new URL( "https://api.github.com/repos/WebAssembly/wabt/releases/latest" );
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        InputStream input = conn.getInputStream();
        String data = WasmRule.readStream( input, true );

        Pattern pattern = Pattern.compile( "/WebAssembly/wabt/releases/download/[0-9.]*/wabt-[0-9.]*-" + fileName );
        Matcher matcher = pattern.matcher( data );
        Assert.assertTrue( data, matcher.find() );
        String downloadUrl = matcher.group();
        url = new URL( "https://github.com" + downloadUrl );
        System.out.println( "\tDownload: " + url );

        conn = (HttpURLConnection)url.openConnection();
//        if( target.exists() ) {
//            conn.setIfModifiedSince( target.lastModified() );
//        }

        input = conn.getInputStream();
        if( conn.getResponseCode() == HttpURLConnection.HTTP_NOT_MODIFIED ) {
            System.out.println( "\tUP-TO-DATE, use version from " + Instant.ofEpochMilli( target.lastModified() ) );
            return;
        }

        long lastModfied = conn.getLastModified();

        extractStream( input, fileName.endsWith( ".tar.gz" ), target );

//        target.setLastModified( lastModfied );
        System.out.println( "\tUse Version from " + Instant.ofEpochMilli( lastModfied ) );
    }

    /**
     * Search the tool in the directory recursively and set the command.
     * 
     * @param dir
     *            the directory
     */
    private void searchExecuteable( File dir ) {
        File[] list = dir.listFiles();
        if( list != null ) {
            for( File file : list ) {
                if( file.isDirectory() ) {
                    searchExecuteable( file );
                } else {
                    String name = file.getName();
                    if( name.equals( "wat2wasm" ) || name.equals( "wat2wasm.exe" ) || name.equals( "wat2wasm.bat" ) ) {
                        command = file.getAbsolutePath();
                    }
                }
                if( command != null ) {
                    return;
                }
            }
        }
    }

    /**
     * Get the executable command
     * 
     * @return the command
     * @throws IOException
     *             if any I/O error occur 
     */
    @Nonnull
    public String getCommand() throws IOException {
        if( error != null ) {
            throw error;
        }
        if( command == null ) {
            try {
                File target = new File( System.getProperty( "java.io.tmpdir" ) + "/wabt" );
                searchExecuteable( target );
                if( command == null ) {
                    download( target );
                    searchExecuteable( target );
                    if( command == null ) {
                        throw new IOException("Wabt was not download or sved to: " + target );
                    }
                }
            } catch( IOException ex ) {
                error = ex;
                throw ex;
            }
        }
        return command;
    }
}
