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
package com.progdan.pdf2txt.pdmodel.graphics.image;

import java.awt.Image;
import java.awt.Point;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

import com.progdan.pdf2txt.util.ImageParameters;

/**
 * This class represents an inlined image.
 *
 * @author Ben Litchfield (ben@csh.rit.edu)
 * @version $Revision: 1.2 $
 */
public class PDInlinedImage
{
    private ImageParameters params;
    private byte[] imageData;

    /**
     * This will get the image parameters.
     *
     * @return The image parameters.
     */
    public ImageParameters getImageParameters()
    {
        return params;
    }

    /**
     * This will set the image parameters for this image.
     *
     * @param imageParams The imageParams.
     */
    public void setImageParameters( ImageParameters imageParams )
    {
        params = imageParams;
    }

    /**
     * Get the bytes for the image.
     *
     * @return The image data.
     */
    public byte[] getImageData()
    {
        return imageData;
    }

    /**
     * Set the bytes that make up the image.
     *
     * @param value The image data.
     */
    public void setImageData(byte[] value)
    {
        imageData = value;
    }

    /**
     * This will take the inlined image information and create a java.awt.Image from
     * it.
     *
     * @return The image that this object represents.
     */
    public Image getImage()
    {
        byte[] transparentColors = new byte[]{(byte)0xFF,(byte)0xFF};
        byte[] colors=new byte[]{0, (byte)0xFF};
        IndexColorModel colorModel = new IndexColorModel( 1, 2, colors, colors, colors, transparentColors );
        BufferedImage image = new BufferedImage(
            params.getWidth(),
            params.getHeight(),
            BufferedImage.TYPE_BYTE_BINARY,
            colorModel );
        DataBufferByte buffer = new DataBufferByte( getImageData(), 1 );
        WritableRaster raster =
            Raster.createPackedRaster(
                buffer,
                params.getWidth(),
                params.getHeight(),
                params.getBitsPerComponent(),
                new Point(0,0) );
        image.setData( raster );
        return image;
    }
}
