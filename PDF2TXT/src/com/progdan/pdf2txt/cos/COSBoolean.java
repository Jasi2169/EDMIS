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

import com.progdan.pdf2txt.exceptions.COSVisitorException;

/**
 * This class represents a boolean value in the PDF document.
 *
 * @author Ben Litchfield (ben@csh.rit.edu)
 * @version $Revision: 1.2 $
 */
public class COSBoolean extends COSBase
{
    /**
     * The PDF true value.
     */
    public static final COSBoolean TRUE = new COSBoolean( true );

    /**
     * The PDF false value.
     */
    public static final COSBoolean FALSE = new COSBoolean( false );

    private boolean value;

    /**
     * Constructor.
     *
     * @param aValue The boolean value.
     */
    private COSBoolean(boolean aValue )
    {
        value = aValue;
    }

    /**
     * This will get the value that this object wraps.
     *
     * @return The boolean value of this object.
     */
    public boolean getValue()
    {
        return value;
    }

    /**
     * This will get the value that this object wraps.
     *
     * @return The boolean value of this object.
     */
    public Boolean getValueAsObject()
    {
        return (value?Boolean.TRUE:Boolean.FALSE);
    }

    /**
     * This will get the boolean value.
     *
     * @param value Parameter telling which boolean value to get.
     *
     * @return The single boolean instance that matches the parameter.
     */
    public static COSBoolean getBoolean( boolean value )
    {
        return (value?TRUE:FALSE);
    }

    /**
     * This will get the boolean value.
     *
     * @param value Parameter telling which boolean value to get.
     *
     * @return The single boolean instance that matches the parameter.
     */
    public static COSBoolean getBoolean( Boolean value )
    {
        return getBoolean( value.booleanValue() );
    }

    /**
     * visitor pattern double dispatch method.
     *
     * @param visitor The object to notify when visiting this object.
     * @return any object, depending on the visitor implementation, or null
     * @throws COSVisitorException If an error occurs while visiting this object.
     */
    public Object accept(ICOSVisitor  visitor) throws COSVisitorException
    {
        return visitor.visitFromBoolean(this);
    }

    /**
     * Return a string representation of this object.
     *
     * @return The string value of this object.
     */
    public String toString()
    {
        return String.valueOf( value );
    }
}
