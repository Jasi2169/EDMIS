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
package com.progdan.pdf2txt.pdmodel.annotation;

import com.progdan.pdf2txt.cos.COSArray;
import com.progdan.pdf2txt.cos.COSDictionary;
import com.progdan.pdf2txt.cos.COSInteger;
import com.progdan.pdf2txt.cos.COSName;

import com.progdan.pdf2txt.pdmodel.common.COSObjectable;
import com.progdan.pdf2txt.pdmodel.common.PDRectangle;
import com.progdan.pdf2txt.cos.COSBase;

/**
 * This class represents a PDF annotation.
 *
 * @author Ben Litchfield (ben@csh.rit.edu)
 * @version $Revision: 1.2 $
 */
public abstract class PDAnnotation implements COSObjectable
{
    private COSDictionary dictionary;

    /**
     * Constructor.
     *
     * @param dict The annotations dictionary.
     */
    public PDAnnotation( COSDictionary dict )
    {
        dictionary = dict;
    }

    /**
     * returns the dictionary.
     * @return the dictionary
     */
    public COSDictionary getDictionary()
    {
        return dictionary;
    }

    /**
     * The annotation rectangle, defining the location of the annotation
     * on the page in default user space units.  This is usually required and should
     * not return null on valid PDF documents.  But where this is a parent form field
     * with children, such as radio button collections then the rectangle will be null.
     *
     * @return The Rect value of this annotation.
     */
    public PDRectangle getRectangle()
    {
        COSArray rectArray = (COSArray)dictionary.getDictionaryObject( COSName.getPDFName( "Rect" ) );
        PDRectangle rectangle = null;
        if( rectArray != null )
        {
            rectangle = new PDRectangle( rectArray );
        }
        return rectangle;
    }

    /**
     * This will set the rectangle for this annotation.
     *
     * @param rectangle The new rectangle values.
     */
    public void setRectangle( PDRectangle rectangle )
    {
        dictionary.setItem( COSName.getPDFName( "Rect" ), rectangle.getCOSArray() );
    }

   /**
     * This will get the flags for this field.
     *
     * @return flags The set of flags.
     */
    public int getAnnotationFlags()
    {
        int retval = 0;
        COSInteger f = (COSInteger)getDictionary().getDictionaryObject( COSName.getPDFName( "F" ) );
        if( f != null )
        {
            retval = (int)f.intValue();
        }
        return retval;
    }

    /**
     * This will set the flags for this field.
     *
     * @param flags The new flags.
     */
    public void setAnnotationFlags( int flags )
    {
        COSInteger f = new COSInteger( flags );
        getDictionary().setItem( COSName.getPDFName( "F" ), f );
    }

    /**
     * Interface method for COSObjectable.
     *
     * @return This object as a standard COS object.
     */
    public COSBase getCOSObject()
    {
        return getDictionary();
    }
}
