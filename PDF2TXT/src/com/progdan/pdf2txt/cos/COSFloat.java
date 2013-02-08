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
package com.progdan.pdf2txt.cos;

import java.io.IOException;

import com.progdan.pdf2txt.exceptions.COSVisitorException;

/**
 * This class represents a floating point number in a PDF document.
 *
 * @author Ben Litchfield (ben@csh.rit.edu)
 * @version $Revision: 1.2 $
 */
public class COSFloat extends COSNumber
{
    private float value;

    /**
     * Constructor.
     *
     * @param aFloat The primitive float object that this object wraps.
     */
    public COSFloat( float aFloat )
    {
        value = aFloat;
    }

    /**
     * Constructor.
     *
     * @param aFloat The primitive float object that this object wraps.
     *
     * @throws IOException If aFloat is not a float.
     */
    public COSFloat( String aFloat ) throws IOException
    {
        try
        {
            value = Float.parseFloat( aFloat );
        }
        catch( NumberFormatException e )
        {
            throw new IOException( "Error expected floating point number actual='" +aFloat + "'" );
        }
    }

    /**
     * The value of the float object that this one wraps.
     *
     * @return The value of this object.
     */
    public float floatValue()
    {
        return value;
    }

    /**
     * The value of the double object that this one wraps.
     *
     * @return The double of this object.
     */
    public double doubleValue()
    {
        return (double)value;
    }

    /**
     * This will get the integer value of this object.
     *
     * @return The int value of this object,
     */
    public long longValue()
    {
        return (long)value;
    }

    /**
     * This will get the integer value of this object.
     *
     * @return The int value of this object,
     */
    public int intValue()
    {
        return (int)value;
    }

    /**
     * @see Object#equals( Object )
     */
    public boolean equals( Object o )
    {
        return o instanceof COSFloat && Float.floatToIntBits(((COSFloat)o).value) == Float.floatToIntBits(value);
    }

    /**
     * @see Object#hashCode()
     */
    public int hashCode()
    {
        return Float.floatToIntBits(value);
    }

    /**
     * @see Object#toString()
     */
    public String toString()
    {
        return "COSFloat{" + value + "}";
    }

    /**
     * visitor pattern double dispatch method.
     *
     * @param visitor The object to notify when visiting this object.
     * @return any object, depending on the visitor implementation, or null
     * @throws COSVisitorException If an error occurs while visiting this object.
     */
    public Object accept(ICOSVisitor visitor) throws COSVisitorException
    {
        return visitor.visitFromFloat(this);
    }
}
