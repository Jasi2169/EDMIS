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
package com.progdan.pdf2txt.pdmodel;

import java.io.IOException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.progdan.pdf2txt.cos.COSBase;
import com.progdan.pdf2txt.cos.COSDictionary;
import com.progdan.pdf2txt.cos.COSName;

import com.progdan.pdf2txt.pdmodel.common.COSDictionaryMap;

import com.progdan.pdf2txt.pdmodel.font.PDFontFactory;

import com.progdan.pdf2txt.pdmodel.graphics.PDExtendedGraphicsState;

import com.progdan.pdf2txt.pdmodel.graphics.color.PDColorSpaceFactory;

/**
 * This represents a set of resources available at the page/pages/stream level.
 *
 * @author  Ben Litchfield (ben@csh.rit.edu)
 * @version $Revision: 1.2 $
 */
public class PDResources
{
    private COSDictionary resources;

    /**
     * Default constructor.
     */
    public PDResources()
    {
        resources = new COSDictionary();
    }

    /**
     * Prepopulated resources.
     *
     * @param resourceDictionary The cos dictionary for this resource.
     */
    public PDResources( COSDictionary resourceDictionary )
    {
        resources = resourceDictionary;
    }

    /**
     * This will get the underlying dictionary.
     *
     * @return The dictionary for these resources.
     */
    public COSDictionary getCOSDictionary()
    {
        return resources;
    }

    /**
     * This will get the map of fonts.  This will never return null.  The keys are string
     * and the values are PDFont objects.
     *
     * @return The map of fonts.
     *
     * @throws IOException If there is an error getting the fonts.
     */
    public Map getFonts() throws IOException
    {
        Map retval = null;
        COSDictionary fonts = (COSDictionary)resources.getDictionaryObject( COSName.FONT );

        if( fonts == null )
        {
            fonts = new COSDictionary();
            resources.setItem( COSName.FONT, fonts );
        }

        Map actuals = new HashMap();
        retval = new COSDictionaryMap( actuals, fonts );
        Iterator fontNames = fonts.keyList().iterator();
        while( fontNames.hasNext() )
        {
            COSName fontName = (COSName)fontNames.next();
            COSDictionary fontDictionary = (COSDictionary)fonts.getDictionaryObject( fontName );
            actuals.put( fontName.getName(), PDFontFactory.createFont( fontDictionary ) );
        }
        return retval;
    }

    /**
     * This will set the map of fonts.
     *
     * @param fonts The new map of fonts.
     */
    public void setFonts( Map fonts )
    {
        resources.setItem( COSName.FONT, COSDictionaryMap.convert( fonts ) );
    }

    /**
     * This will get the map of colorspaces.  This will return null if the underlying
     * resources dictionary does not have a colorspace dictionary.  The keys are string
     * and the values are PDColorSpace objects.
     *
     * @return The map of colorspaces.
     *
     * @throws IOException If there is an error getting the colorspaces.
     */
    public Map getColorSpaces() throws IOException
    {
        Map retval = null;
        COSDictionary colorspaces = (COSDictionary)resources.getDictionaryObject( COSName.getPDFName( "ColorSpace" ) );

        if( colorspaces != null )
        {
            Map actuals = new HashMap();
            retval = new COSDictionaryMap( actuals, colorspaces );
            Iterator csNames = colorspaces.keyList().iterator();
            while( csNames.hasNext() )
            {
                COSName csName = (COSName)csNames.next();
                COSBase cs = colorspaces.getDictionaryObject( csName );
                actuals.put( csName.getName(), PDColorSpaceFactory.createColorSpace( cs ) );
            }
        }
        return retval;
    }

    /**
     * This will set the map of colorspaces.
     *
     * @param colorspaces The new map of colorspaces.
     */
    public void setColorSpaces( Map colorspaces )
    {
        resources.setItem( COSName.getPDFName( "ColorSpace" ), COSDictionaryMap.convert( colorspaces ) );
    }

    /**
     * This will get the map of graphic states.  This will return null if the underlying
     * resources dictionary does not have a graphics dictionary.  The keys are the graphic state
     * name as a String and the values are PDExtendedGraphicsState objects.
     *
     * @return The map of extended graphic state objects.
     */
    public Map getGraphicsStates()
    {
        Map retval = null;
        COSDictionary states = (COSDictionary)resources.getDictionaryObject( COSName.getPDFName( "ExtGState" ) );

        if( states != null )
        {
            Map actuals = new HashMap();
            retval = new COSDictionaryMap( actuals, states );
            Iterator names = states.keyList().iterator();
            while( names.hasNext() )
            {
                COSName name = (COSName)names.next();
                COSDictionary dictionary = (COSDictionary)states.getDictionaryObject( name );
                actuals.put( name.getName(), new PDExtendedGraphicsState( dictionary ) );
            }
        }
        return retval;
    }

    /**
     * This will set the map of graphics states.
     *
     * @param states The new map of states.
     */
    public void setGraphicsStates( Map states )
    {
        Iterator iter = states.keySet().iterator();
        COSDictionary dic = new COSDictionary();
        while( iter.hasNext() )
        {
            String name = (String)iter.next();
            PDExtendedGraphicsState state = (PDExtendedGraphicsState)states.get( name );
            dic.setItem( COSName.getPDFName( name ), state.getCOSObject() );
        }
        resources.setItem( COSName.getPDFName( "ExtGState" ), dic );
    }
}
