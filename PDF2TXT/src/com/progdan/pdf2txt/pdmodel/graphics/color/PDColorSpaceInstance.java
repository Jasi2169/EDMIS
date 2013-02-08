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

/**
 * This class represents a color space and the color value for that colorspace.
 *
 * @author Ben Litchfield (ben@csh.rit.edu)
 * @version $Revision: 1.1 $
 */
public class PDColorSpaceInstance
{
    private PDColorSpace colorSpace = new PDDeviceGray();
    private float[] colorSpaceValue = new float[] { 0f };


    /**
     * This will get the current colorspace.
     *
     * @return The current colorspace.
     */
    public PDColorSpace getColorSpace()
    {
        return colorSpace;
    }

    /**
     * This will set the current colorspace.
     *
     * @param value The new colorspace.
     */
    public void setColorSpace(PDColorSpace value)
    {
        colorSpace = value;
    }

    /**
     * This will get the color space values.  Either 1 for gray or 3 for RGB.
     *
     * @return The colorspace values.
     */
    public float[] getColorSpaceValue()
    {
        return colorSpaceValue;
    }

    /**
     * This will update the colorspace values.
     *
     * @param value The new colorspace values.
     */
    public void setColorSpaceValue(float[] value)
    {
        colorSpaceValue = value;
    }
}
