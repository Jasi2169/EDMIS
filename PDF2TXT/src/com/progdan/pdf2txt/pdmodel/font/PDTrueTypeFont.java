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

import com.progdan.pdf2txt.cos.COSDictionary;
import com.progdan.pdf2txt.cos.COSName;

import java.awt.Font;
import java.awt.Graphics;

import java.io.IOException;

/**
 * This is the TrueType implementation of fonts.
 *
 * @author Ben Litchfield (ben@csh.rit.edu)
 * @version $Revision: 1.2 $
 */
public class PDTrueTypeFont extends PDSimpleFont
{
    /**
     * Constructor.
     */
    public PDTrueTypeFont()
    {
        super();
        font.setItem( COSName.SUBTYPE, COSName.getPDFName( "TrueType" ) );
    }

    /**
     * Constructor.
     *
     * @param fontDictionary The font dictionary according to the PDF specification.
     */
    public PDTrueTypeFont( COSDictionary fontDictionary )
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
        Font f = new Font( "Arial", Font.PLAIN, (int)fontSize );
        g.drawString( string, (int)x, (int)y );
    }
}
