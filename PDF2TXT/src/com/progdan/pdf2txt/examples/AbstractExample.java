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
 *
 */
package com.progdan.pdf2txt.examples;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.progdan.pdf2txt.cos.COSDocument;

import com.progdan.pdf2txt.pdmodel.PDDocument;

import com.progdan.pdf2txt.pdfwriter.COSWriter;

/**
 * A simple class which has some methods used by all examples.
 *
 * @author Ben Litchfield (ben@csh.rit.edu)
 * @version $Revision: 1.2 $
 */
public abstract class AbstractExample
{
    /**
     * Close the stream.
     *
     * @param stream The stream to close.
     *
     * @throws IOException If there is an error closing the stream.
     */
    public void close( InputStream stream ) throws IOException
    {
        if( stream != null )
        {
            stream.close();
        }
    }

    /**
     * Close the stream.
     *
     * @param stream The stream to close.
     *
     * @throws IOException If there is an error closing the stream.
     */
    public void close( OutputStream stream ) throws IOException
    {
        if( stream != null )
        {
            stream.close();
        }
    }

    /**
     * Close the document.
     *
     * @param doc The doc to close.
     *
     * @throws IOException If there is an error closing the document.
     */
    public void close( COSDocument doc ) throws IOException
    {
        if( doc != null )
        {
            doc.close();
        }
    }

    /**
     * Close the document.
     *
     * @param doc The doc to close.
     *
     * @throws IOException If there is an error closing the document.
     */
    public void close( PDDocument doc ) throws IOException
    {
        if( doc != null )
        {
            doc.close();
        }
    }

    /**
     * Close the writer.
     *
     * @param writer The writer to close.
     *
     * @throws IOException If there is an error closing the writer.
     */
    public static void close( COSWriter writer ) throws IOException
    {
        if( writer != null )
        {
            writer.close();
        }
    }
}
