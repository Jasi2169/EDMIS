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
package com.progdan.pdf2txt.pdmodel.common;

import com.progdan.pdf2txt.cos.COSArray;
import com.progdan.pdf2txt.cos.COSFloat;
import com.progdan.pdf2txt.cos.COSNumber;

/**
 * This class will be used for matrix manipulation.
 *
 * @author Ben Litchfield (ben@csh.rit.edu)
 * @version $Revision: 1.1 $
 */
public class PDMatrix implements Cloneable
{
    private COSArray matrix;

    /**
     * Constructor.
     */
    public PDMatrix()
    {
        matrix = new COSArray();
        matrix.add( new COSFloat( 1.0f ) );
        matrix.add( new COSFloat( 0.0f ) );
        matrix.add( new COSFloat( 0.0f ) );
        matrix.add( new COSFloat( 0.0f ) );
        matrix.add( new COSFloat( 1.0f ) );
        matrix.add( new COSFloat( 0.0f ) );
        matrix.add( new COSFloat( 0.0f ) );
        matrix.add( new COSFloat( 0.0f ) );
        matrix.add( new COSFloat( 1.0f ) );
    }

    /**
     * Constructor.
     *
     * @param array The array that describes the matrix.
     */
    public PDMatrix( COSArray array )
    {
        matrix = array;
    }

    /**
     * This will get the underlying array value.
     *
     * @return The cos object that this object wraps.
     */
    public COSArray getCOSArray()
    {
        return matrix;
    }

    /**
     * This will get a matrix value at some point.
     *
     * @param row The row to get the value from.
     * @param column The column to get the value from.
     *
     * @return The value at the row/column position.
     */
    public float getValue( int row, int column )
    {
        return ((COSNumber)matrix.get( row*3 + column )).floatValue();
    }

    /**
     * This will set a value at a position.
     *
     * @param row The row to set the value at.
     * @param column the column to set the value at.
     * @param value The value to set at the position.
     */
    public void setValue( int row, int column, float value )
    {
        matrix.set( row*3+column, new COSFloat( value ) );
    }
}
