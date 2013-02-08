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
package com.progdan.pdf2txt.util;

/**
 * This class will be used for matrix manipulation.
 *
 * @author Ben Litchfield (ben@csh.rit.edu)
 * @version $Revision: 1.2 $
 */
public class Matrix implements Cloneable
{

    private static final int MATRIX_SIZE = 3;
    private float[][] matrix =
        {
            { 1, 0, 0 },
            { 0, 1, 0 },
            { 0, 0, 1 }
        };

    /**
     * Constructor.
     */
    public Matrix()
    {
    }

    /**
     * This is used for debugging purposes only.  It will print the contents
     * of the matrix to stdout.
     */
    public void print()
    {
        for( int i=0; i<MATRIX_SIZE; i++ )
        {
            System.out.print( "[" );
            for( int j=0; j<MATRIX_SIZE; j++ )
            {
                System.out.print( matrix[i][j] );
                if( j < MATRIX_SIZE-1 )
                {
                    System.out.print( "," );
                }
            }
            System.out.println( "]" );
        }
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
        return matrix[row][column];
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
        matrix[row][column] = value;
    }

    /**
     * This will take the current matrix and multipy it with a matrix that is passed in.
     *
     * @param b The matrix to multiply by.
     *
     * @return The result of the two multiplied matrices.
     */
    public Matrix multiply( Matrix b )
    {
        Matrix result = new Matrix();

        float[][] bMatrix = b.matrix;
        float[][] resultMatrix = result.matrix;
        resultMatrix[0][0] = matrix[0][0] * bMatrix[0][0] + matrix[0][1] * bMatrix[1][0] + matrix[0][2] * bMatrix[2][0];
        resultMatrix[0][1] = matrix[0][0] * bMatrix[0][1] + matrix[0][1] * bMatrix[1][1] + matrix[0][2] * bMatrix[2][1];
        resultMatrix[0][2] = matrix[0][0] * bMatrix[0][2] + matrix[0][1] * bMatrix[1][2] + matrix[0][2] * bMatrix[2][2];
        resultMatrix[1][0] = matrix[1][0] * bMatrix[0][0] + matrix[1][1] * bMatrix[1][0] + matrix[1][2] * bMatrix[2][0];
        resultMatrix[1][1] = matrix[1][0] * bMatrix[0][1] + matrix[1][1] * bMatrix[1][1] + matrix[1][2] * bMatrix[2][1];
        resultMatrix[1][2] = matrix[1][0] * bMatrix[0][2] + matrix[1][1] * bMatrix[1][2] + matrix[1][2] * bMatrix[2][2];
        resultMatrix[2][0] = matrix[2][0] * bMatrix[0][0] + matrix[2][1] * bMatrix[1][0] + matrix[2][2] * bMatrix[2][0];
        resultMatrix[2][1] = matrix[2][0] * bMatrix[0][1] + matrix[2][1] * bMatrix[1][1] + matrix[2][2] * bMatrix[2][1];
        resultMatrix[2][2] = matrix[2][0] * bMatrix[0][2] + matrix[2][1] * bMatrix[1][2] + matrix[2][2] * bMatrix[2][2];

        return result;
    }

    /**
     * Clones this object.
     * @return cloned matrix as an object.
     */
    public Object clone()
    {
        Matrix clone = new Matrix();
        clone.matrix[0][0] = this.matrix[0][0];
        clone.matrix[0][1] = this.matrix[0][1];
        clone.matrix[0][2] = this.matrix[0][2];
        clone.matrix[1][0] = this.matrix[1][0];
        clone.matrix[1][1] = this.matrix[1][1];
        clone.matrix[1][2] = this.matrix[1][2];
        clone.matrix[2][0] = this.matrix[2][0];
        clone.matrix[2][1] = this.matrix[2][1];
        clone.matrix[2][2] = this.matrix[2][2];

        return clone;
    }

    /**
     * This will copy the text matrix data.
     *
     * @return a matrix that matches this one.
     */
    public Matrix copy()
    {
        return (Matrix) clone();
    }

    /**
     * This will return a string representation of the matrix.
     *
     * @return The matrix as a string.
     */
    public String toString()
    {
        StringBuffer result = new StringBuffer( "" );
        for( int i=0; i<MATRIX_SIZE; i++ )
        {
            result.append( "[" );
            for( int j=0; j<MATRIX_SIZE; j++ )
            {
                result.append( "" + matrix[i][j] + " " );
            }
            result.append( "] " );
        }
        return result.toString();
    }

}
