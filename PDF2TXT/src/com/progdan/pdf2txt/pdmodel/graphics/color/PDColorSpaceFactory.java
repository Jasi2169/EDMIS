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

import java.io.IOException;

import com.progdan.pdf2txt.cos.COSArray;
import com.progdan.pdf2txt.cos.COSBase;
import com.progdan.pdf2txt.cos.COSName;

/**
 * This class represents a color space in a pdf document.
 *
 * @author Ben Litchfield (ben@csh.rit.edu)
 * @version $Revision: 1.2 $
 */
public final class PDColorSpaceFactory
{
    /**
     * Private constructor for utility classes.
     */
    private PDColorSpaceFactory()
    {
    }

    /**
     * This will create the correct color space given the name.
     *
     * @param colorSpace The color space object.
     *
     * @return The color space.
     *
     * @throws IOException If the color space name is unknown.
     */
    public static PDColorSpace createColorSpace( COSBase colorSpace ) throws IOException
    {
        PDColorSpace retval = null;
        if( colorSpace instanceof COSName )
        {
            retval = createColorSpace( ((COSName)colorSpace).getName() );
        }
        else if( colorSpace instanceof COSArray )
        {
            COSArray array = (COSArray)colorSpace;
            COSName type = (COSName)array.getObject( 0 );
            if( type.getName().equals( PDCalGray.NAME ) )
            {
                retval = new PDCalGray( array );
            }
            else if( type.getName().equals( PDCalRGB.NAME ) )
            {
                retval = new PDCalRGB( array );
            }
            else if( type.getName().equals( PDDeviceN.NAME ) )
            {
                retval = new PDDeviceN( array );
            }
            else if( type.getName().equals( PDIndexed.NAME ) ||
                   type.getName().equals( PDIndexed.ABBREVIATED_NAME ))
            {
                retval = new PDIndexed( array );
            }
            else if( type.getName().equals( PDLab.NAME ) )
            {
                retval = new PDLab( array );
            }
            else if( type.getName().equals( PDSeparation.NAME ) )
            {
                retval = new PDSeparation( array );
            }
            else if( type.getName().equals( PDICCBased.NAME ) )
            {
                retval = new PDICCBased( array );
            }
            else if( type.getName().equals( PDPattern.NAME ) )
            {
                retval = new PDPattern( array );
            }
            else
            {
                throw new IOException( "Unknown colorspace array type:" + type );
            }
        }
        else
        {
            throw new IOException( "Unknown colorspace type:" + colorSpace );
        }
        return retval;
    }

    /**
     * This will create the correct color space given the name.
     *
     * @param colorSpaceName The name of the colorspace.
     *
     * @return The color space.
     *
     * @throws IOException If the color space name is unknown.
     */
    public static PDColorSpace createColorSpace( String colorSpaceName ) throws IOException
    {
        PDColorSpace cs = null;
        if( colorSpaceName.equals( PDDeviceCMYK.NAME ) ||
                 colorSpaceName.equals( PDDeviceCMYK.ABBREVIATED_NAME ) )
        {
            cs = new PDDeviceCMYK();
        }
        else if( colorSpaceName.equals( PDDeviceRGB.NAME ) ||
                 colorSpaceName.equals( PDDeviceRGB.ABBREVIATED_NAME ) )
        {
            cs = new PDDeviceRGB();
        }
        else if( colorSpaceName.equals( PDDeviceGray.NAME ) ||
                 colorSpaceName.equals( PDDeviceGray.ABBREVIATED_NAME ))
        {
            cs = new PDDeviceGray();
        }
        else if( colorSpaceName.equals( PDLab.NAME ) )
        {
            cs = new PDLab();
        }
        else if( colorSpaceName.equals( PDPattern.NAME ) )
        {
            cs = new PDPattern();
        }
        else
        {
            throw new IOException( "Error: Unknown colorspace '" + colorSpaceName + "'" );
        }
        return cs;
    }
}
