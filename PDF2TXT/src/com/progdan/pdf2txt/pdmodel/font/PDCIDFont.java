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

import java.util.HashMap;
import java.util.Map;

import com.progdan.pdf2txt.cos.COSArray;
import com.progdan.pdf2txt.cos.COSBase;
import com.progdan.pdf2txt.cos.COSDictionary;
import com.progdan.pdf2txt.cos.COSInteger;
import com.progdan.pdf2txt.cos.COSName;
import com.progdan.pdf2txt.cos.COSNumber;

import com.progdan.logengine.Category;

/**
 * This is implementation for the CIDFontType0/CIDFontType2 Fonts.
 *
 * @author Ben Litchfield (ben@csh.rit.edu)
 * @version $Revision: 1.2 $
 */
public abstract class PDCIDFont extends PDFont
{
    private static Category log = Category.getInstance(PDCIDFont.class.getName());


    private Map widthCache = new HashMap();

    /**
     * Constructor.
     */
    public PDCIDFont()
    {
        super();
    }

    /**
     * Constructor.
     *
     * @param fontDictionary The font dictionary according to the PDF specification.
     */
    public PDCIDFont( COSDictionary fontDictionary )
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
     */
    public void drawString( String string, Graphics g, float fontSize, float x, float y )
    {
        throw new RuntimeException( "Not yet implemented" );
    }

    /**
     * This will get the default width.  The default value for the default width is 1000.
     *
     * @return The default width for the glyphs in this font.
     */
    public long getDefaultWidth()
    {
        long dw = 1000;
        COSNumber number = (COSNumber)font.getDictionaryObject( COSName.getPDFName( "DW" ) );
        if( number != null )
        {
            dw = number.intValue();
        }
        return dw;
    }

    /**
     * This will set the default width for the glyphs of this font.
     *
     * @param dw The default width.
     */
    public void setDefaultWidth( long dw )
    {
        font.setItem( COSName.getPDFName( "DW" ), new COSInteger( dw ) );
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

        float retval = 0.0f;
        int code = getCodeFromArray( c, offset, length );

        Float widthFloat = (Float)widthCache.get( new Integer( code ) );
        if( widthFloat == null )
        {
            float width = getDefaultWidth();
            COSArray widths = (COSArray)font.getDictionaryObject( COSName.getPDFName( "W" ) );

            if( widths != null )
            {
                boolean foundWidth = false;
                for( int i=0; !foundWidth && i<widths.size(); i++ )
                {
                    COSNumber firstCode = (COSNumber)widths.getObject( i++ );
                    COSBase next = widths.getObject( i );
                    if( next instanceof COSArray )
                    {
                        COSArray array = (COSArray)next;
                        if( code >= firstCode.intValue() &&
                            code < firstCode.intValue() + array.size() )
                        {
                            COSNumber rangeWidth =
                                (COSNumber)array.get( code - (int)firstCode.intValue() );
                            retval = rangeWidth.floatValue();
                            foundWidth = true;
                        }
                    }
                    else
                    {
                        COSNumber secondCode = (COSNumber)next;
                        i++;
                        COSNumber rangeWidth = (COSNumber)widths.getObject( i );
                        if( code >= firstCode.intValue() &&
                            code <= secondCode.intValue() )
                        {
                            retval = rangeWidth.floatValue();
                            foundWidth = true;
                        }
                    }
                }
                widthCache.put( new Integer( code ), new Float( retval ) );
            }
        }
        else
        {
            retval = widthFloat.floatValue();
        }

        if(log.isDebugEnabled() )
        {
            log.debug( "PDCIDFontType0Font.getFontWidth( code=" + code +" ) retval=" +retval );
        }
        return retval;
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
        float totalWidths = 0.0f;
        float characterCount = 0.0f;
        float defaultWidth = getDefaultWidth();
        COSArray widths = (COSArray)font.getDictionaryObject( COSName.getPDFName( "W" ) );

        if( widths != null )
        {
            for( int i=0; i<widths.size(); i++ )
            {
                COSNumber firstCode = (COSNumber)widths.getObject( i++ );
                COSBase next = widths.getObject( i );
                float nextWidth=0.0f;
                if( next instanceof COSArray )
                {
                    COSArray array = (COSArray)next;
                    for( int j=0; j<array.size(); j++ )
                    {
                        COSNumber width = (COSNumber)array.get( j );
                        totalWidths+=width.floatValue();
                        characterCount += 1;
                    }
                }
                else
                {
                    COSNumber secondCode = (COSNumber)next;
                    i++;
                    COSNumber rangeWidth = (COSNumber)widths.getObject( i );
                    if( rangeWidth.floatValue() > 0 )
                    {
                        totalWidths += rangeWidth.floatValue();
                        characterCount += 1;
                    }
                }
            }
        }
        float average = totalWidths / characterCount;
        if( average <= 0 )
        {
            average = defaultWidth;
        }
        return average;
    }
}
