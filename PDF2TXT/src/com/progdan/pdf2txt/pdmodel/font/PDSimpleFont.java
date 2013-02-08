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
package com.progdan.pdf2txt.pdmodel.font;

import java.awt.Graphics;

import java.io.IOException;

import com.progdan.pdf2txt.afmtypes.FontMetric;

import com.progdan.pdf2txt.cos.COSArray;
import com.progdan.pdf2txt.cos.COSDictionary;
import com.progdan.pdf2txt.cos.COSName;
import com.progdan.pdf2txt.cos.COSNumber;
import com.progdan.pdf2txt.cos.COSInteger;

/**
 * This class contains implementation details of the simple pdf fonts.
 *
 * @author Ben Litchfield (ben@csh.rit.edu)
 * @version $Revision: 1.2 $
 */
public abstract class PDSimpleFont extends PDFont
{
    /**
     * Constructor.
     */
    public PDSimpleFont()
    {
        super();
    }

    /**
     * Constructor.
     *
     * @param fontDictionary The font dictionary according to the PDF specification.
     */
    public PDSimpleFont( COSDictionary fontDictionary )
    {
        super( fontDictionary );
    }

    /**
     * This will draw a string on a canvas using the font.
     *
     * @param string The string to draw.
     * @param g The graphics to draw onto.
     * @param fontSize The size of the font to draw.
     * @param x The x coordinate to draw at.
     * @param y The y coordinate to draw at.
     *
     * @throws IOException If there is an error drawing the specified font.
     */
    public void drawString( String string, Graphics g, float fontSize, float x, float y ) throws IOException
    {
        System.err.println( "Not yet implemented:" + getClass().getName() );
    }

    /**
     * This will get the font width for a character.
     *
     * @param c The character code to get the width for.
     * @param offset The offset into the array.
     * @param length The length of the data.
     *
     * @return The width is in 1000 unit of text space, ie 333 or 777
     *
     * @throws IOException If an error occurs while parsing.
     */
    public float getFontWidth( byte[] c, int offset, int length ) throws IOException
    {
        float fontWidth = 0;
        int code = getCodeFromArray( c, offset, length );

        //hmm should this be in a subclass??
        COSInteger firstChar = (COSInteger)font.getDictionaryObject( COSName.FIRSTCHAR );
        COSInteger lastChar = (COSInteger)font.getDictionaryObject( COSName.LASTCHAR );
        if( firstChar != null && lastChar != null )
        {
            long first = firstChar.intValue();
            long last = lastChar.intValue();
            if( code >= first && code <= last && font.getDictionaryObject( COSName.WIDTHS ) != null )
            {
                COSArray widthArray = (COSArray)font.getDictionaryObject( COSName.WIDTHS );
                COSNumber fontWidthObject = (COSNumber)widthArray.get( (int)(code - first) );
                fontWidth = fontWidthObject.floatValue();
            }
            else
            {
                fontWidth = getFontWidthFromAFMFile( code );
            }
        }
        else
        {
            fontWidth = getFontWidthFromAFMFile( code );
        }
        return fontWidth;
    }

    /**
     * This will get the average font width for all characters.
     *
     * @return The width is in 1000 unit of text space, ie 333 or 777
     *
     * @throws IOException If an error occurs while parsing.
     */
    public float getAverageFontWidth() throws IOException
    {
        float average = 0.0f;
        float totalWidth = 0.0f;
        float characterCount = 0.0f;
        COSArray widths = (COSArray)font.getDictionaryObject( COSName.WIDTHS );
        if( widths != null )
        {
            for( int i=0; i<widths.size(); i++ )
            {
                COSNumber fontWidth = (COSNumber)widths.get( i );
                if( fontWidth.floatValue() > 0 )
                {
                    totalWidth += fontWidth.floatValue();
                    characterCount += 1;
                }
            }
        }

        if( totalWidth > 0 )
        {
            average = totalWidth / characterCount;
        }
        else
        {
            average = getAverageFontWidthFromAFMFile();
        }
        return average;
    }

    /**
     * This will get the font descriptor for this font.
     *
     * @return The font descriptor for this font.
     *
     * @throws IOException If there is an error parsing an AFM file, or unable to
     *      create a PDFontDescriptor object.
     */
    public PDFontDescriptor getFontDescriptor() throws IOException
    {
        PDFontDescriptor retval = null;
        COSDictionary fd = (COSDictionary)font.getDictionaryObject( COSName.getPDFName( "FontDescriptor" ) );
        if( fd == null )
        {
            FontMetric afm = getAFM();
            if( afm == null )
            {
                throw new IOException( "Error: Can't create font descriptor file" );
            }
            retval = new PDFontDescriptorAFM( afm );
        }
        else
        {
            retval = new PDFontDescriptorDictionary( fd );
        }

        return retval;
    }

    /**
     * This will set the font descriptor.
     *
     * @param fontDescriptor The font descriptor.
     */
    public void setFontDescriptor( PDFontDescriptorDictionary fontDescriptor )
    {
        COSDictionary dic = null;
        if( fontDescriptor != null )
        {
            dic = fontDescriptor.getCOSDictionary();
        }
        font.setItem( COSName.getPDFName( "FontDescriptor" ), dic );
    }
}
