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

import com.progdan.pdf2txt.cos.COSArray;
import com.progdan.pdf2txt.cos.COSDictionary;
import com.progdan.pdf2txt.cos.COSFloat;
import com.progdan.pdf2txt.cos.COSInteger;
import com.progdan.pdf2txt.cos.COSName;
import com.progdan.pdf2txt.cos.COSNumber;
import com.progdan.pdf2txt.cos.COSString;

import com.progdan.pdf2txt.pdmodel.common.PDRectangle;

/**
 * This class represents an implementation to the font descriptor that gets its
 * information from a COS Dictionary.
 *
 * @author Ben Litchfield
 * @version $Revision: 1.1 $
 */
public class PDFontDescriptorDictionary extends PDFontDescriptor
{
    private COSDictionary dic;

    /**
     * Constructor.
     *
     * @param desc The wrapped COS Dictionary.
     */
    public PDFontDescriptorDictionary( COSDictionary desc )
    {
        dic = desc;
    }

    /**
     * This will get the dictionary for this object.
     *
     * @return The COS dictionary.
     */
    public COSDictionary getCOSDictionary()
    {
        return dic;
    }

    /**
     * Get the font name.
     *
     * @return The name of the font.
     */
    public String getFontName()
    {
        String retval = null;
        COSName name = (COSName)dic.getDictionaryObject( COSName.getPDFName( "FontName" ) );
        if( name != null )
        {
            retval = name.getName();
        }
        return retval;
    }

    /**
     * This will set the font name.
     *
     * @param fontName The new name for the font.
     */
    public void setFontName( String fontName )
    {
        COSName name = null;
        if( fontName != null )
        {
            name = COSName.getPDFName( fontName );
        }
        dic.setItem( COSName.getPDFName( "FontName" ), name );
    }

    /**
     * A string representing the preferred font family.
     *
     * @return The font family.
     */
    public String getFontFamily()
    {
        String retval = null;
        COSString name = (COSString)dic.getDictionaryObject( COSName.getPDFName( "FontFamily" ) );
        if( name != null )
        {
            retval = name.getString();
        }
        return retval;
    }

    /**
     * This will set the font family.
     *
     * @param fontFamily The font family.
     */
    public void setFontFamily( String fontFamily )
    {
        COSString name = null;
        if( fontFamily != null )
        {
            name = new COSString( fontFamily );
        }
        dic.setItem( COSName.getPDFName( "FontFamily" ), name );
    }

    /**
     * The weight of the font.  According to the PDF spec "possible values are
     * 100, 200, 300, 400, 500, 600, 700, 800 or 900"  Where a higher number is
     * more weight and appears to be more bold.
     *
     * @return The font weight.
     */
    public float getFontWeight()
    {
        return getFloat( "FontWeight" );
    }

    /**
     * Set the weight of the font.
     *
     * @param fontWeight The new weight of the font.
     */
    public void setFontWeight( float fontWeight )
    {
        setFloat( "FontWeight", fontWeight );
    }

    /**
     * A string representing the preferred font stretch.
     * According to the PDF Spec:
     * The font stretch value; it must be one of the following (ordered from
     * narrowest to widest): UltraCondensed, ExtraCondensed, Condensed, SemiCondensed,
     * Normal, SemiExpanded, Expanded, ExtraExpanded or UltraExpanded.
     *
     * @return The stretch of the font.
     */
    public String getFontStretch()
    {
        String retval = null;
        COSName name = (COSName)dic.getDictionaryObject( COSName.getPDFName( "FontStretch" ) );
        if( name != null )
        {
            retval = name.getName();
        }
        return retval;
    }

    /**
     * This will set the font stretch.
     *
     * @param fontStretch The new stretch for the font.
     */
    public void setFontStretch( String fontStretch )
    {
        COSName name = null;
        if( fontStretch != null )
        {
            name = COSName.getPDFName( fontStretch );
        }
        dic.setItem( COSName.getPDFName( "FontStretch" ), name );
    }

    /**
     * This will get the font flags.
     *
     * @return The font flags.
     */
    public int getFlags()
    {
        int retval = 0;
        COSNumber number = (COSNumber)dic.getDictionaryObject( COSName.getPDFName( "Flags" ) );
        if( number != null )
        {
            retval = number.intValue();
        }
        return retval;
    }

    /**
     * This will set the font flags.
     *
     * @param flags The new font flags.
     */
    public void setFlags( int flags )
    {
        dic.setItem( COSName.getPDFName( "Flags" ), new COSInteger( flags ) );
    }

    /**
     * This will get the fonts bouding box.
     *
     * @return The fonts bouding box.
     */
    public PDRectangle getFontBoundingBox()
    {
        COSArray rect = (COSArray)dic.getDictionaryObject( COSName.getPDFName( "FontBBox" ) );
        PDRectangle retval = null;
        if( rect != null )
        {
            retval = new PDRectangle( rect );
        }
        return retval;
    }

    /**
     * Set the fonts bounding box.
     *
     * @param rect The new bouding box.
     */
    public void setFontBoundingBox( PDRectangle rect )
    {
        COSArray array = null;
        if( rect != null )
        {
            array = rect.getCOSArray();
        }
        dic.setItem( COSName.getPDFName( "FontBBox" ), array );
    }

    /**
     * This will get the italic angle for the font.
     *
     * @return The italic angle.
     */
    public float getItalicAngle()
    {
        return getFloat( "ItalicAngle" );
    }

    /**
     * This will set the italic angle for the font.
     *
     * @param angle The new italic angle for the font.
     */
    public void setItalicAngle( float angle )
    {
        setFloat( "ItalicAngle", angle );
    }

    /**
     * This will get the ascent for the font.
     *
     * @return The ascent.
     */
    public float getAscent()
    {
        return getFloat( "Ascent" );
    }

    /**
     * This will set the ascent for the font.
     *
     * @param ascent The new ascent for the font.
     */
    public void setAscent( float ascent )
    {
        setFloat( "Ascent", ascent );
    }

    /**
     * This will get the descent for the font.
     *
     * @return The descent.
     */
    public float getDescent()
    {
        return getFloat( "Descent" );
    }

    /**
     * This will set the descent for the font.
     *
     * @param descent The new descent for the font.
     */
    public void setDescent( float descent )
    {
        setFloat( "Descent", descent );
    }

    /**
     * This will get the leading for the font.
     *
     * @return The leading.
     */
    public float getLeading()
    {
        return getFloat( "Leading" );
    }

    /**
     * This will set the leading for the font.
     *
     * @param leading The new leading for the font.
     */
    public void setLeading( float leading )
    {
        setFloat( "Leading", leading );
    }

    /**
     * This will get the CapHeight for the font.
     *
     * @return The cap height.
     */
    public float getCapHeight()
    {
        return getFloat( "CapHeight" );
    }

    /**
     * This will set the cap height for the font.
     *
     * @param capHeight The new cap height for the font.
     */
    public void setCapHeight( float capHeight )
    {
        setFloat( "CapHeight", capHeight );
    }

    /**
     * This will get the x height for the font.
     *
     * @return The x height.
     */
    public float getXHeight()
    {
        return getFloat( "XHeight" );
    }

    /**
     * This will set the x height for the font.
     *
     * @param xHeight The new x height for the font.
     */
    public void setXHeight( float xHeight )
    {
        setFloat( "XHeight", xHeight );
    }

    /**
     * This will get the stemV for the font.
     *
     * @return The stem v value.
     */
    public float getStemV()
    {
        return getFloat( "StemV" );
    }

    /**
     * This will set the stem V for the font.
     *
     * @param stemV The new stem v for the font.
     */
    public void setStemV( float stemV )
    {
        setFloat( "StemV", stemV );
    }

    /**
     * This will get the stemH for the font.
     *
     * @return The stem h value.
     */
    public float getStemH()
    {
        return getFloat( "StemH" );
    }

    /**
     * This will set the stem H for the font.
     *
     * @param stemH The new stem h for the font.
     */
    public void setStemH( float stemH )
    {
        setFloat( "StemH", stemH );
    }

    /**
     * This will get the average width for the font.
     *
     * @return The average width value.
     */
    public float getAverageWidth()
    {
        return getFloat( "AvgWidth" );
    }

    /**
     * This will set the average width for the font.
     *
     * @param averageWidth The new average width for the font.
     */
    public void setAverageWidth( float averageWidth )
    {
        setFloat( "AvgWidth", averageWidth );
    }

    /**
     * This will get the max width for the font.
     *
     * @return The max width value.
     */
    public float getMaxWidth()
    {
        return getFloat( "MaxWidth" );
    }

    /**
     * This will set the max width for the font.
     *
     * @param maxWidth The new max width for the font.
     */
    public void setMaxWidth( float maxWidth )
    {
        setFloat( "MaxWidth", maxWidth );
    }

    /**
     * This will get the missing width for the font.
     *
     * @return The missing width value.
     */
    public float getMissingWidth()
    {
        return getFloat( "MissingWidth" );
    }

    /**
     * This will set the missing width for the font.
     *
     * @param missingWidth The new missing width for the font.
     */
    public void setMissingWidth( float missingWidth )
    {
        setFloat( "MissingWidth", missingWidth );
    }

    /**
     * This will get the character set for the font.
     *
     * @return The character set value.
     */
    public String getCharSet()
    {
        String retval = null;
        COSString name = (COSString)dic.getDictionaryObject( COSName.getPDFName( "CharSet" ) );
        if( name != null )
        {
            retval = name.getString();
        }
        return retval;
    }

    /**
     * This will set the character set for the font.
     *
     * @param charSet The new character set for the font.
     */
    public void setCharacterSet( String charSet )
    {
        COSString name = null;
        if( charSet != null )
        {
            name = new COSString( charSet );
        }
        dic.setItem( COSName.getPDFName( "CharSet" ), name );
    }

    private float getFloat( String name )
    {
        float retval = 0;
        COSNumber number = (COSNumber)dic.getDictionaryObject( COSName.getPDFName( name ) );
        if( number != null )
        {
            retval = number.floatValue();
        }
        return retval;
    }

    private void setFloat( String name, float value )
    {
        dic.setItem( COSName.getPDFName( name ), new COSFloat( value ) );
    }
}
