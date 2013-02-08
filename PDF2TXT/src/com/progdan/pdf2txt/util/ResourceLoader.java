/**
 * Copyright (c) 2004, ProgDan® Software
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of pdf2txt; nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * http://progdan.no-ip.org:25000
 */
package com.progdan.pdf2txt.util;

import java.io.InputStream;

import com.progdan.logengine.Category;

/**
 * This class will handle loading resource files(AFM/CMAP).
 *
 * @author  Ben Litchfield (ben@csh.rit.edu)
 * @version $Revision: 1.2 $
 */
public class ResourceLoader
{

    /**
     * private constructor for utility class.
     */
    private ResourceLoader()
    {
    }

    private static Category cat = Category.getInstance( ResourceLoader.class );
    /**
     * This will attempt to load the resource given the resource name.
     *
     * @param resourceName The resource to try and load.
     *
     * @return The resource as a stream or null if it could not be found.
     */
    public static InputStream loadResource( String resourceName )
    {
        if( cat.isDebugEnabled() )
        {
            cat.debug( "loadResource( " + resourceName + ")" );
        }

        ClassLoader loader = ResourceLoader.class.getClassLoader();

        //see sourceforge bug 863053, this is a fix for a user that
        //needed to have PDF2TXT loaded by the bootstrap classloader
        if( loader == null )
        {
            loader = ClassLoader.getSystemClassLoader();
        }

        return loader.getResourceAsStream( resourceName );
    }
}
