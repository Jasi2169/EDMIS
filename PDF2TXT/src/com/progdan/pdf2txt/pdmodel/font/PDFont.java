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

import com.progdan.pdf2txt.afmparser.AFMParser;

import com.progdan.pdf2txt.afmtypes.FontMetric;

import com.progdan.pdf2txt.cmapparser.CMapParser;

import com.progdan.pdf2txt.cmaptypes.CMap;

import com.progdan.pdf2txt.encoding.AFMEncoding;
import com.progdan.pdf2txt.encoding.DictionaryEncoding;
import com.progdan.pdf2txt.encoding.Encoding;
import com.progdan.pdf2txt.encoding.EncodingManager;

import com.progdan.pdf2txt.cos.COSBase;
import com.progdan.pdf2txt.cos.COSDictionary;
import com.progdan.pdf2txt.cos.COSName;
import com.progdan.pdf2txt.cos.COSStream;


import com.progdan.pdf2txt.pdmodel.common.COSObjectable;

import com.progdan.pdf2txt.util.ResourceLoader;

import com.progdan.logengine.Category;

import java.awt.Graphics;

import java.io.InputStream;
import java.io.IOException;

import java.util.HashMap;
import java.util.Map;

/**
 * This is the base class for all PDF fonts.
 *
 * @author Ben Litchfield (ben@csh.rit.edu)
 * @version $Revision: 1.2 $
 */
public abstract class PDFont implements COSObjectable
{
    private static Category log = Category.getInstance(PDFont.class.getName());

    /**
     * The cos dictionary for this font.
     */
    protected COSDictionary font;

    /**
     * This is only used if this is a font object and it has an encoding.
     */
    private Encoding fontEncoding = null;
    /**
     * This is only used if this is a font object and it has an encoding and it is
     * a type0 font with a cmap.
     */
    private CMap cmap = null;

    private static Map afmResources = null;
    private static Map cmapObjects = null;
    private static Map afmObjects = null;
    private static Map cmapSubstitutions = null;

    static
    {
        afmResources = new HashMap();
        cmapObjects = new HashMap();
        afmObjects = new HashMap();
        cmapSubstitutions = new HashMap();

        afmResources.put( COSName.getPDFName( "Courier-Bold" ), "Resources/afm/COB_____.AFM" );
        afmResources.put( COSName.getPDFName( "Courier-BoldOblique" ), "Resources/afm/COBO____.AFM" );
        afmResources.put( COSName.getPDFName( "Courier" ), "Resources/afm/COM_____.AFM" );
        afmResources.put( COSName.getPDFName( "Courier-Oblique" ), "Resources/afm/COO_____.AFM" );
        afmResources.put( COSName.getPDFName( "Helvetica" ), "Resources/afm/HV______.AFM" );
        afmResources.put( COSName.getPDFName( "Helvetica-Bold" ), "Resources/afm/HVB_____.AFM" );
        afmResources.put( COSName.getPDFName( "Helvetica-BoldOblique" ), "Resources/afm/HVBO____.AFM" );
        afmResources.put( COSName.getPDFName( "Helvetica-Oblique" ), "Resources/afm/HVO_____.AFM" );
        afmResources.put( COSName.getPDFName( "Anna" ), "Resources/afm/IARG____.AFM" );
        afmResources.put( COSName.getPDFName( "PopplLaudatio-Italic" ), "Resources/afm/PYI_____.AFM" );
        afmResources.put( COSName.getPDFName( "PopplLaudatio-Medium" ), "Resources/afm/PYM_____.AFM" );
        afmResources.put( COSName.getPDFName( "PopplLaudatio-MediumItalic" ), "Resources/afm/PYMI____.AFM" );
        afmResources.put( COSName.getPDFName( "PopplLaudatio-Regular" ), "Resources/afm/PYRG____.AFM" );
        afmResources.put( COSName.getPDFName( "Boulevard" ), "Resources/afm/QTRG____.AFM" );
        afmResources.put( COSName.getPDFName( "Symbol" ), "Resources/afm/SY______.AFM" );
        afmResources.put( COSName.getPDFName( "Times-Bold" ), "Resources/afm/TIB_____.AFM" );
        afmResources.put( COSName.getPDFName( "Times-BoldItalic" ), "Resources/afm/TIBI____.AFM" );
        afmResources.put( COSName.getPDFName( "Times-Italic" ), "Resources/afm/TII_____.AFM" );
        afmResources.put( COSName.getPDFName( "Times-Roman" ), "Resources/afm/TIR_____.AFM" );
        afmResources.put( COSName.getPDFName( "Boton-Italic" ), "Resources/afm/TNI_____.AFM" );
        afmResources.put( COSName.getPDFName( "Boton-Medium" ), "Resources/afm/TNM_____.AFM" );
        afmResources.put( COSName.getPDFName( "Boton-MediumItalic" ), "Resources/afm/TNMI____.AFM" );
        afmResources.put( COSName.getPDFName( "Boton-Regular" ), "Resources/afm/TNR_____.AFM" );
        afmResources.put( COSName.getPDFName( "BaskervilleBE-Italic" ), "Resources/afm/VII_____.AFM" );
        afmResources.put( COSName.getPDFName( "BaskervilleBE-Medium" ), "Resources/afm/VIM_____.AFM" );
        afmResources.put( COSName.getPDFName( "BaskervilleBE-MediumItalic" ), "Resources/afm/VIMI____.AFM" );
        afmResources.put( COSName.getPDFName( "BaskervilleBE-Regular" ), "Resources/afm/VIRG____.AFM" );
        afmResources.put( COSName.getPDFName( "Giddyup" ), "Resources/afm/WG______.AFM" );
        afmResources.put( COSName.getPDFName( "Giddyup-Thangs" ), "Resources/afm/WGTHA______.AFM" );
        afmResources.put( COSName.getPDFName( "ZapfDingbats" ), "Resources/afm/ZapfDingbats.AFM" );

        cmapSubstitutions.put( "ETenms-B5-H", "ETen-B5-H" );
        cmapSubstitutions.put( "ETenms-B5-V", "ETen-B5-V" );

    }

    /**
     * This will clear AFM resources that are stored statically.
     * This is usually not a problem unless you want to reclaim
     * resources for a long running process.
     *
     * SPECIAL NOTE: The font calculations are currently in COSObject, which
     * is where they will reside until PDFont is mature enough to take them over.
     * PDFont is the appropriate place for them and not in COSObject but we need font
     * calculations for text extractaion.  THIS METHOD WILL BE MOVED OR REMOVED
     * TO ANOTHER LOCATION IN A FUTURE VERSION OF PDF2TXT.
     */
    public static void clearResources()
    {
        afmObjects.clear();
        cmapObjects.clear();
    }

    /**
     * Constructor.
     */
    public PDFont()
    {
        font = new COSDictionary();
        font.setItem( COSName.TYPE, COSName.FONT );
    }

    /**
     * Constructor.
     *
     * @param fontDictionary The font dictionary according to the PDF specification.
     */
    public PDFont( COSDictionary fontDictionary )
    {
        font = fontDictionary;
    }

    /**
     * @see COSObjectable#getCOSObject()
     */
    public COSBase getCOSObject()
    {
        return font;
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
    public abstract float getFontWidth( byte[] c, int offset, int length ) throws IOException;

    /**
     * This will get the width of this string for this font.
     *
     * @param string The string to get the width of.
     *
     * @return The width of the string in 1000 units of text space, ie 333 567...
     *
     * @throws IOException If there is an error getting the width information.
     */
    public float getStringWidth( String string ) throws IOException
    {
        byte[] data = string.getBytes();
        float totalWidth = 0;
        for( int i=0; i<data.length; i++ )
        {
            totalWidth+=getFontWidth( data, i, 1 );
        }
        return totalWidth;
    }

    /**
     * This will get the average font width for all characters.
     *
     * @return The width is in 1000 unit of text space, ie 333 or 777
     *
     * @throws IOException If an error occurs while parsing.
     */
    public abstract float getAverageFontWidth() throws IOException;

    /**
     * This will draw a string on a canvas using the font.
     *
     * @param string The string to draw.
     * @param g The graphics to draw onto.
     * @param fontSize The size of the font to draw.
     * @param x The x coordinate to draw at.
     * @param y The y coordinate to draw at.
     *
     * @throws IOException If there is an error drawing the specific string.
     */
    public abstract void drawString( String string, Graphics g, float fontSize, float x, float y ) throws IOException;

    /**
     * Used for multibyte encodings.
     *
     * @param data The array of data.
     * @param offset The offset into the array.
     * @param length The number of bytes to use.
     *
     * @return The int value of data from the array.
     */
    protected int getCodeFromArray( byte[] data, int offset, int length )
    {
        int code = 0;
        for( int i=0; i<length; i++ )
        {
            code <<= 8;
            code = (data[offset+i]+256)%256;
        }
        return code;
    }

    /**
     * This will attempt to get the font width from an AFM file.
     *
     * @param code The character code we are trying to get.
     *
     * @return The font width from the AFM file.
     *
     * @throws IOException if we cannot find the width.
     */
    protected float getFontWidthFromAFMFile( int code ) throws IOException
    {
        float retval = 0;
        FontMetric metric = getAFM();
        if( metric != null )
        {
            Encoding encoding = getEncoding();
            COSName characterName = encoding.getName( code );
            retval = metric.getCharacterWidth( characterName.getName() );
        }
        return retval;
    }

    /**
     * This will attempt to get the average font width from an AFM file.
     *
     * @return The average font width from the AFM file.
     *
     * @throws IOException if we cannot find the width.
     */
    protected float getAverageFontWidthFromAFMFile() throws IOException
    {
        float retval = 0;
        FontMetric metric = getAFM();
        if( metric != null )
        {
            retval = metric.getAverageCharacterWidth();
        }
        return retval;
    }

    /**
     * This will get an AFM object if one exists.
     *
     * @return The afm object from the name.
     *
     * @throws IOException If there is an error getting the AFM object.
     */
    protected FontMetric getAFM() throws IOException
    {
        COSName name = (COSName)font.getDictionaryObject( COSName.BASE_FONT );
        FontMetric result = null;
        if( name != null )
        {
            result = (FontMetric)afmObjects.get( name );
            if( result == null )
            {
                String resource = (String)afmResources.get( name );
                if( log.isDebugEnabled() )
                {
                    log.debug("resource: " + resource + ", name: " + name.getName());
                }
                if( resource == null )
                {
                    log.debug( "resource is null" );
                    //ok for now
                    //throw new IOException( "Unknown AFM font '" + name.getName() + "'" );
                }
                else
                {
                    InputStream afmStream = ResourceLoader.loadResource( resource );
                    if( afmStream == null )
                    {
                        throw new IOException( "Can't handle font width:" + resource );
                    }
                    AFMParser parser = new AFMParser( afmStream );
                    parser.parse();
                    result = parser.getResult();
                    afmObjects.put( name, result );
                }
            }
        }
        return result;
    }

    /**
     * This will perform the encoding of a character if needed.
     *
     * @param c The character to encode.
     * @param offset The offset into the array to get the data
     * @param length The number of bytes to read.
     *
     * @return The value of the encoded character.
     *
     * @throws IOException If there is an error during the encoding.
     */
    public String encode( byte[] c, int offset, int length ) throws IOException
    {
        String retval = null;
        COSName fontSubtype = (COSName)font.getDictionaryObject( COSName.getPDFName( "Subtype" ) );
        if( fontSubtype.getName().equals( "Type0" ) ||
            fontSubtype.getName().equals( "TrueType" ) )
        {
            if( cmap == null )
            {
                if( font.getDictionaryObject( COSName.getPDFName( "ToUnicode" ) ) != null )
                {
                    COSStream toUnicode = (COSStream)font.getDictionaryObject( COSName.getPDFName( "ToUnicode" ) );
                    if( toUnicode != null )
                    {
                        parseCmap( toUnicode.getUnfilteredStream(), null );
                        if( log.isDebugEnabled() )
                        {
                            log.debug( "Getting embedded CMAP Stream from ToUnicode" );
                        }
                    }
                }
                else
                {
                    COSBase encoding = font.getDictionaryObject( COSName.ENCODING );
                    if( encoding instanceof COSStream )
                    {
                        COSStream encodingStream = (COSStream)encoding;
                        parseCmap( encodingStream.getUnfilteredStream(), null );
                        if( log.isDebugEnabled() )
                        {
                            log.debug( "Getting embedded CMAP Stream from encoding" );
                        }
                    }
                    else if( fontSubtype.getName().equals( "Type0" ) &&
                             encoding instanceof COSName )
                    {
                        COSName encodingName = (COSName)encoding;
                        if( cmapObjects.get( encodingName ) != null )
                        {
                            cmap = (CMap)cmapObjects.get( encodingName );
                        }
                        else
                        {
                            if( log.isDebugEnabled() )
                            {
                                log.debug( "Getting CMAP Stream from resource" );
                            }
                            String cmapName = encodingName.getName();
                            cmapName = performCMAPSubstitution( cmapName );
                            String resourceName = "Resources/cmap/" + cmapName;
                            parseCmap( ResourceLoader.loadResource( resourceName ), encodingName );
                            if( cmap == null && !encodingName.getName().equals( COSName.IDENTITY_H.getName() ) )
                            {
                                throw new IOException( "Error: Could not find predefined " +
                                "CMAP file for '" + encodingName.getName() + "'" );
                            }
                        }
                    }
                    else if( encoding instanceof COSName ||
                             encoding instanceof COSDictionary )
                    {
                        Encoding currentFontEncoding = getEncoding();
                        if( currentFontEncoding != null )
                        {
                            retval = currentFontEncoding.getCharacter( getCodeFromArray( c, offset, length ) );
                        }
                    }
                    else
                    {
                        COSDictionary fontDescriptor =
                            (COSDictionary)font.getDictionaryObject( COSName.getPDFName( "FontDescriptor" ) );
                        if( fontSubtype.getName().equals( "TrueType" ) &&
                            fontDescriptor != null &&
                            (fontDescriptor.getDictionaryObject( COSName.getPDFName( "FontFile" ) )!= null ||
                             fontDescriptor.getDictionaryObject( COSName.getPDFName( "FontFile2" ) ) != null ||
                             fontDescriptor.getDictionaryObject( COSName.getPDFName( "FontFile3" ) ) != null ) )
                        {
                            //If we are using an embedded font then there is not much we can do besides
                            //return the same character codes.
                            retval = new String( c, offset, length );
                        }
                        else
                        {
                            // throw new IOException( "Error: No 'ToUnicode' and no 'Encoding' for Font" );
                            //If we don't known the encoding there is not much we can do besides
                            //return the same character codes.
                            if( log.isDebugEnabled() )
                            {
                                log.debug( "Warning: No 'ToUnicode' and no 'Encoding' for Font:" + font );
                            }
                            retval = new String( c, offset, length );
                        }
                    }
                }


            }
        }
        else
        {
            Encoding encoding = getEncoding();
            if( encoding != null )
            {
                retval = encoding.getCharacter( getCodeFromArray( c, offset, length ) );
            }
        }
        if( retval == null )
        {
            if( cmap != null )
            {
                retval = cmap.lookup( c, offset, length );
                if( log.isDebugEnabled() )
                {
                    log.debug( "cmap.lookup(" +c + ")='" +retval + "'" );
                }
            }
            else
            {
                retval = new String( c,offset, length );
            }
        }
        return retval;
    }

    /**
     * Some cmap names are synonyms for other CMAPs.  If that is the case
     * then this method will perform that substitution.
     *
     * @param cmapName The name of the cmap to attempt to look up.
     *
     * @return Either the original name or the substituted name.
     */
    private String performCMAPSubstitution( String cmapName )
    {
        String retval = (String)cmapSubstitutions.get( cmapName );
        if( retval == null )
        {
            //if there is no substitution then just return the same value.
            retval = cmapName;
        }
        return retval;
    }

    private void parseCmap( InputStream cmapStream, COSName encodingName ) throws IOException
    {
        if( log.isDebugEnabled() )
        {
            log.debug( "Parsing a new CMAP for font:" + font );
        }
        if( cmapStream != null )
        {
            CMapParser parser = new CMapParser( cmapStream, null );
            parser.parse();
            cmap = parser.getResult();
            if( encodingName != null )
            {
                cmapObjects.put( encodingName, cmap );
            }
        }
    }

    /**
     * This will get or create the encoder.
     *
     * @return The encoding to use.
     *
     * @throws IOException If there is an error getting the encoding.
     */
    private Encoding getEncoding() throws IOException
    {
        EncodingManager manager = new EncodingManager();
        if( fontEncoding == null )
        {
            COSBase encoding = font.getDictionaryObject( COSName.ENCODING );
            if( encoding == null )
            {
                FontMetric metric = getAFM();
                if( metric != null )
                {
                    fontEncoding = new AFMEncoding( metric );
                }
                if( fontEncoding == null )
                {
                    fontEncoding = manager.getStandardEncoding();
                }
            }
            else if( encoding instanceof COSDictionary )
            {
                COSDictionary encodingDic = (COSDictionary)font.getDictionaryObject( COSName.ENCODING );
                fontEncoding = new DictionaryEncoding( encodingDic );
            }
            else if( encoding instanceof COSName )
            {
                if( !encoding.equals( COSName.IDENTITY_H ) )
                {
                    fontEncoding = manager.getEncoding( (COSName)encoding );
                }
            }
            else
            {
                throw new IOException( "Unexpected encoding type:" + encoding.getClass().getName() );
            }
        }
        return fontEncoding;
    }
}
