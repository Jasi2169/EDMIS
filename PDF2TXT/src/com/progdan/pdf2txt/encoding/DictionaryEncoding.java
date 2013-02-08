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
package com.progdan.pdf2txt.encoding;

import java.io.IOException;

import com.progdan.pdf2txt.cos.COSArray;
import com.progdan.pdf2txt.cos.COSBase;
import com.progdan.pdf2txt.cos.COSDictionary;
import com.progdan.pdf2txt.cos.COSName;
import com.progdan.pdf2txt.cos.COSNumber;

/**
 * This will perform the encoding from a dictionary.
 *
 * @author Ben Litchfield (ben@csh.rit.edu)
 * @version $Revision: 1.2 $
 */
public class DictionaryEncoding extends Encoding
{
    private COSDictionary encoding = null;

    /**
     * Constructor.
     *
     * @param fontEncoding The encoding dictionary.
     *
     * @throws IOException If there is a problem getting the base font.
     */
    public DictionaryEncoding( COSDictionary fontEncoding ) throws IOException
    {
        encoding = fontEncoding;

        //first set up the base encoding
        Encoding baseEncoding = new WinAnsiEncoding();
        COSName baseEncodingName = (COSName)encoding.getDictionaryObject( COSName.BASE_ENCODING );
        if( baseEncodingName != null )
        {
            EncodingManager manager = new EncodingManager();
            baseEncoding = manager.getEncoding( baseEncodingName );
        }
        nameToCode.putAll( baseEncoding.nameToCode );
        codeToName.putAll( baseEncoding.codeToName );


        //now replace with the differences.
        COSArray differences = (COSArray)encoding.getDictionaryObject( COSName.DIFFERENCES );
        int currentIndex = -1;
        for( int i=0; differences != null && i<differences.size(); i++ )
        {
            COSBase next = differences.get( i );
            if( next instanceof COSNumber )
            {
                currentIndex = (int)((COSNumber)next).intValue();
            }
            else if( next instanceof COSName )
            {
                COSName name = (COSName)next;
                addCharacterEncoding( currentIndex++, name );
            }
        }
    }
}
