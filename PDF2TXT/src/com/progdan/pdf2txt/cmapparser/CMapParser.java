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
package com.progdan.pdf2txt.cmapparser;

import java.io.InputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import java.util.List;

import com.progdan.pdf2txt.cmaptypes.CMap;
import com.progdan.pdf2txt.cmaptypes.CodespaceRange;

import com.progdan.pdf2txt.cos.COSName;
import com.progdan.pdf2txt.cos.COSNumber;
import com.progdan.pdf2txt.cos.COSString;

import com.progdan.pdf2txt.pdfparser.PDFStreamParser;

import com.progdan.pdf2txt.util.PDFOperator;

/**
 * This will parser a CMap stream.
 *
 * @author  Ben Litchfield (ben@csh.rit.edu)
 * @version $Revision: 1.2 $
 */
public class CMapParser
{
    private static final String BEGIN_CODESPACE_RANGE = "begincodespacerange";
    private static final String BEGIN_BASE_FONT_CHAR = "beginbfchar";
    private static final String BEGIN_BASE_FONT_RANGE = "beginbfrange";

    private InputStream input;
    private CMap result;
    private RandomAccessFile file;

    /**
     * Creates a new instance of CMapParser.
     *
     * @param in The input stream to read data from.
     * @param raf The random access file from the document
     */
    public CMapParser( InputStream in, RandomAccessFile raf )
    {
        input = in;
        file = raf;
    }

    /**
     * This will get the results of the parsing.  parse() must be called first.
     *
     * @return The parsed CMap file.
     */
    public CMap getResult()
    {
        return result;
    }

    /**
     * This will parse the stream and create a cmap object.
     *
     * @throws IOException If there is an error parsing the stream.
     */
    public void parse() throws IOException
    {
        result = new CMap();
        PDFStreamParser parser = new PDFStreamParser( input, file );
        parser.parse();
        List tokens = parser.getTokens();
        for( int i=0; i<tokens.size(); i++ )
        {
            Object token = tokens.get( i );
            if( token instanceof PDFOperator )
            {
                PDFOperator op = (PDFOperator)token;
                if( op.getOperation().equals( BEGIN_CODESPACE_RANGE ) )
                {
                    COSNumber cosCount = (COSNumber)tokens.get( i-1 );
                    for( int j=0; j<cosCount.intValue(); j++ )
                    {
                        i++;
                        COSString startRange = (COSString)tokens.get( i );
                        i++;
                        COSString endRange = (COSString)tokens.get( i );
                        CodespaceRange range = new CodespaceRange();
                        range.setStart( startRange.getBytes() );
                        range.setEnd( endRange.getBytes() );
                        result.addCodespaceRange( range );
                    }
                }
                else if( op.getOperation().equals( BEGIN_BASE_FONT_CHAR ) )
                {
                    COSNumber cosCount = (COSNumber)tokens.get( i-1 );
                    for( int j=0; j<cosCount.intValue(); j++ )
                    {
                        i++;
                        COSString inputCode = (COSString)tokens.get( i );
                        i++;
                        Object nextToken = tokens.get( i );
                        if( nextToken instanceof COSString )
                        {
                            String value = new String( ((COSString)nextToken).getBytes(), "UTF-16BE" );
                            result.addMapping( inputCode.getBytes(), value );
                        }
                        else if( nextToken instanceof COSName )
                        {
                            result.addMapping( inputCode.getBytes(), ((COSName)nextToken).getName() );
                        }
                        else
                        {
                            throw new IOException( "Error parsing CMap beginbfchar, expected{COSString " +
                                                   "or COSName} and not " + nextToken );
                        }
                    }
                }
               else if( op.getOperation().equals( BEGIN_BASE_FONT_RANGE ) )
                {
                    COSNumber cosCount = (COSNumber)tokens.get( i-1 );
                    for( int j=0; j<cosCount.intValue(); j++ )
                    {
                        i++;
                        COSString startCode = (COSString)tokens.get( i );
                        i++;
                        COSString endCode = (COSString)tokens.get( i );
                        i++;
                        Object nextToken = tokens.get( i );

                        byte[] startBytes = startCode.getBytes();
                        byte[] endBytes = endCode.getBytes();
                        byte[] tokenBytes = ((COSString)nextToken).getBytes();

                        String value = null;

                        while( !equals( startBytes, endBytes ) )
                        {
                            value = new String( tokenBytes, "UTF-16BE" );
                            result.addMapping( startBytes, value );
                            increment( startBytes );
                            increment( tokenBytes );
                        }
                        //now add it when they are equal and that is all
                        value = new String( tokenBytes, "UTF-16BE" );
                        result.addMapping( startBytes, value );
                    }
                }
            }
        }
    }

    private String getHexString( byte[] data )
    {
        String finalResult = "";
        for( int i=0; i<data.length; i++ )
        {
            int intValue = (data[i]+256)%256;
            if( intValue < 10 )
            {
                finalResult += "0";
            }
            finalResult += Integer.toHexString( intValue );
        }
        return finalResult;

    }

    private void increment( byte[] data )
    {
        increment( data, data.length-1 );
    }

    private void increment( byte[] data, int position )
    {
        if( position > 0 && (data[position]+256)%256 == 255 )
        {
            data[position]=0;
            increment( data, position-1);
        }
        else
        {
            data[position] = (byte)(data[position]+1);
        }
    }

    private boolean equals( byte[] first, byte[] second )
    {
        boolean equals = true;
        for( int i=0; i<first.length; i++ )
        {
            if( first[i] != second[i] )
            {
                equals = false;
            }
        }
        return equals;
    }
}
