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

import com.progdan.pdf2txt.cos.COSArray;
import com.progdan.pdf2txt.cos.COSDictionary;
import com.progdan.pdf2txt.cos.COSName;

import java.io.IOException;

/**
 * This is implementation of the Type0 Font.
 *
 * @author Ben Litchfield (ben@csh.rit.edu)
 * @version $Revision: 1.2 $
 */
public class PDType0Font extends PDFont
{
    /**
     * Constructor.
     */
    public PDType0Font()
    {
        super();
        font.setItem( COSName.SUBTYPE, COSName.getPDFName( "Type0" ) );
    }

    /**
     * Constructor.
     *
     * @param fontDictionary The font dictionary according to the PDF specification.
     */
    public PDType0Font( COSDictionary fontDictionary )
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
        COSArray descendantFontArray =
            (COSArray)font.getDictionaryObject( COSName.getPDFName( "DescendantFonts" ) );

        COSDictionary descendantFontDictionary = (COSDictionary)descendantFontArray.getObject( 0 );
        PDFont descendentFont = PDFontFactory.createFont( descendantFontDictionary );

        return descendentFont.getFontWidth( c, offset, length );
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
        COSArray descendantFontArray =
            (COSArray)font.getDictionaryObject( COSName.getPDFName( "DescendantFonts" ) );

        COSDictionary descendantFontDictionary = (COSDictionary)descendantFontArray.getObject( 0 );
        PDFont descendentFont = PDFontFactory.createFont( descendantFontDictionary );

        return descendentFont.getAverageFontWidth();
    }
}
