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
package com.progdan.pdf2txt.pdmodel.fdf;

import java.util.ArrayList;
import java.util.List;

import com.progdan.pdf2txt.cos.COSArray;
import com.progdan.pdf2txt.cos.COSBase;
import com.progdan.pdf2txt.cos.COSDictionary;
import com.progdan.pdf2txt.cos.COSName;

import com.progdan.pdf2txt.pdmodel.common.COSObjectable;
import com.progdan.pdf2txt.pdmodel.common.COSArrayList;
import com.progdan.pdf2txt.pdmodel.common.PDTextStream;
import com.progdan.pdf2txt.pdmodel.common.PDNamedTextStream;

/**
 * This represents an FDF JavaScript dictionary that is part of the FDF document.
 *
 * @author Ben Litchfield (ben@csh.rit.edu)
 * @version $Revision: 1.2 $
 */
public class FDFJavaScript implements COSObjectable
{
    private COSDictionary js;

    /**
     * Default constructor.
     */
    public FDFJavaScript()
    {
        js = new COSDictionary();
    }

    /**
     * Constructor.
     *
     * @param javaScript The FDF java script.
     */
    public FDFJavaScript( COSDictionary javaScript )
    {
        js = javaScript;
    }

    /**
     * Convert this standard java object to a COS object.
     *
     * @return The cos object that matches this Java object.
     */
    public COSBase getCOSObject()
    {
        return js;
    }

    /**
     * Convert this standard java object to a COS object.
     *
     * @return The cos object that matches this Java object.
     */
    public COSDictionary getCOSDictionary()
    {
        return js;
    }

    /**
     * This will get the javascript that is executed before the import.
     *
     * @return Some javascript code.
     */
    public PDTextStream getBefore()
    {
        return PDTextStream.createTextStream( js.getDictionaryObject( "Before" ) );
    }

    /**
     * This will set the javascript code the will get execute before the import.
     *
     * @param before A reference to some javascript code.
     */
    public void setBefore( PDTextStream before )
    {
        js.setItem( "Before", before );
    }

    /**
     * This will get the javascript that is executed after the import.
     *
     * @return Some javascript code.
     */
    public PDTextStream getAfter()
    {
        return PDTextStream.createTextStream( js.getDictionaryObject( "After" ) );
    }

    /**
     * This will set the javascript code the will get execute after the import.
     *
     * @param after A reference to some javascript code.
     */
    public void setAfter( PDTextStream after )
    {
        js.setItem( "After", after );
    }

    /**
     * This will return a list of PDNamedTextStream objects.  This is the "Doc"
     * entry of the pdf document.  These will be added to the PDF documents
     * javascript name tree.  This will not return null.
     *
     * @return A list of all named javascript entries.
     */
    public List getNamedJavaScripts()
    {
        List retval = null;
        COSArray array = (COSArray)js.getDictionaryObject( "Doc" );
        List namedStreams = new ArrayList();
        if( array == null )
        {
            array = new COSArray();
            js.setItem( "Doc", array );
        }
        for( int i=0; i<array.size(); i++ )
        {
            COSName name = (COSName)array.get( i );
            i++;
            COSBase stream = array.get( i );
            PDNamedTextStream namedStream = new PDNamedTextStream( name, stream );
            namedStreams.add( namedStream );
        }
        return new COSArrayList( namedStreams, array );
    }

    /**
     * This should be a list of PDNamedTextStream objects.
     *
     * @param namedStreams The named streams.
     */
    public void setNamedJavaScripts( List namedStreams )
    {
        COSArray array = COSArrayList.converterToCOSArray( namedStreams );
        js.setItem( "Doc", array );
    }
}
