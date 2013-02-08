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
package com.progdan.pdf2txt.pdmodel.graphics.color;

import com.progdan.pdf2txt.cos.COSArray;
import com.progdan.pdf2txt.cos.COSBase;
import com.progdan.pdf2txt.cos.COSInteger;
import com.progdan.pdf2txt.cos.COSFloat;
import com.progdan.pdf2txt.cos.COSName;
import com.progdan.pdf2txt.cos.COSNumber;
import com.progdan.pdf2txt.cos.COSStream;

import com.progdan.pdf2txt.pdmodel.common.COSArrayList;
import com.progdan.pdf2txt.pdmodel.common.PDRange;

import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;

import java.io.InputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a ICC profile color space.
 *
 * @author Ben Litchfield (ben@csh.rit.edu)
 * @version $Revision: 1.2 $
 */
public class PDICCBased extends PDColorSpace
{
    /**
     * The name of this color space.
     */
    public static final String NAME = "ICCBased";

    private COSArray array;
    private COSStream stream;

    /**
     * Constructor.
     *
     * @param iccArray The ICC stream object.
     */
    public PDICCBased( COSArray iccArray )
    {
        array = iccArray;
        stream = (COSStream)iccArray.getObject( 1 );
    }

    /**
     * This will return the name of the color space.
     *
     * @return The name of the color space.
     */
    public String getName()
    {
        return NAME;
    }

    /**
     * Convert this standard java object to a COS object.
     *
     * @return The cos object that matches this Java object.
     */
    public COSBase getCOSObject()
    {
        return stream;
    }

    /**
     * Create a Java colorspace for this colorspace.
     *
     * @return A color space that can be used for Java AWT operations.
     *
     * @throws IOException If there is an error creating the color space.
     */
    public ColorSpace createColorSpace() throws IOException
    {
        InputStream profile = null;
        ColorSpace cSpace = null;
        try
        {
            profile = stream.getUnfilteredStream();
            ICC_Profile iccProfile = ICC_Profile.getInstance( profile );
            cSpace = new ICC_ColorSpace( iccProfile );
        }
        finally
        {
            if( profile != null )
            {
                profile.close();
            }
        }
        return cSpace;
    }

    /**
     * This will return the number of color components.  As of PDF 1.4 this will
     * be 1,3,4.
     *
     * @return The number of components in this color space.
     *
     * @throws IOException If there is an error getting the number of color components.
     */
    public int getNumberOfComponents() throws IOException
    {
        COSNumber n = (COSNumber)stream.getDictionaryObject( COSName.getPDFName( "N" ) );
        return n.intValue();
    }

    /**
     * This will set the number of color components.
     *
     * @param n The number of color components.
     */
    public void setNumberOfComponents( int n )
    {
        stream.setItem( COSName.getPDFName( "N" ), new COSInteger( n ) );
    }

    /**
     * This will return a list of alternate color spaces(PDColorSpace) if the display application
     * does not support this icc stream.
     *
     * @return A list of alternate color spaces.
     *
     * @throws IOException If there is an error getting the alternate color spaces.
     */
    public List getAlternateColorSpaces() throws IOException
    {
        COSBase alternate = stream.getDictionaryObject( COSName.getPDFName( "Alternate" ) );
        COSArray alternateArray = null;
        if( alternate == null )
        {
            alternateArray = new COSArray();
            int numComponents = getNumberOfComponents();
            String csName = null;
            if( numComponents == 1 )
            {
                csName = PDDeviceGray.NAME;
            }
            else if( numComponents == 3 )
            {
                csName = PDDeviceRGB.NAME;
            }
            else if( numComponents == 4 )
            {
                csName = PDDeviceCMYK.NAME;
            }
            else
            {
                throw new IOException( "Unknown colorspace number of components:" + numComponents );
            }
            alternateArray.add( COSName.getPDFName( csName ) );
        }
        else
        {
            if( alternate instanceof COSArray )
            {
                alternateArray = (COSArray)alternate;
            }
            else if( alternate instanceof COSName )
            {
                alternateArray = new COSArray();
                alternateArray.add( alternate );
            }
            else
            {
                throw new IOException( "Error: expected COSArray or COSName and not " +
                    alternate.getClass().getName() );
            }
        }
        List retval = new ArrayList();
        for( int i=0; i<alternateArray.size(); i++ )
        {
            retval.add( PDColorSpaceFactory.createColorSpace( (COSName)alternateArray.get( i ) ) );
        }
        return new COSArrayList( retval, alternateArray );
    }

    /**
     * This will set the list of alternate color spaces.  This should be a list
     * of PDColorSpace objects.
     *
     * @param list The list of colorspace objects.
     */
    public void setAlternateColorSpaces( List list )
    {
        COSArray altArray = null;
        if( list != null )
        {
            altArray = COSArrayList.converterToCOSArray( list );
        }
        stream.setItem( COSName.getPDFName( "Alternate" ), altArray );
    }

    private COSArray getRangeArray( int n )
    {
        COSArray rangeArray = (COSArray)stream.getDictionaryObject( COSName.getPDFName( "Range" ) );
        if( rangeArray == null )
        {
            rangeArray = new COSArray();
            stream.setItem( COSName.getPDFName( "Range" ), rangeArray );
            while( rangeArray.size() < n*2 )
            {
                rangeArray.add( new COSFloat( -100 ) );
                rangeArray.add( new COSFloat( 100 ) );
            }
        }
        return rangeArray;
    }

    /**
     * This will get the range for a certain component number.  This is will never
     * return null.  If it is not present then the range -100 to 100 will
     * be returned.
     *
     * @param n The component number to get the range for.
     *
     * @return The range for this component.
     */
    public PDRange getRangeForComponent( int n )
    {
        COSArray rangeArray = getRangeArray( n );
        return new PDRange( rangeArray, n );
    }

    /**
     * This will set the a range for this color space.
     *
     * @param range The new range for the a component.
     * @param n The component to set the range for.
     */
    public void setRangeForComponent( PDRange range, int n )
    {
        COSArray rangeArray = getRangeArray( n );
        rangeArray.set( n*2, new COSFloat( range.getMin() ) );
        rangeArray.set( n*2+1, new COSFloat( range.getMax() ) );
    }

    /**
     * This will get the metadata stream for this object.  Null if there is no
     * metadata stream.
     *
     * @return The metadata stream, if it exists.
     */
    public COSStream getMetadata()
    {
        return (COSStream)stream.getDictionaryObject( COSName.getPDFName( "Metadata" ) );
    }

    /**
     * This will set the metadata stream that is associated with this color space.
     *
     * @param metadata The new metadata stream.
     */
    public void setMetadata( COSStream metadata )
    {
        stream.setItem( COSName.getPDFName( "Metadata" ), metadata );
    }
}
