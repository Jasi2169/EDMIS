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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.progdan.pdf2txt.persistence.util.COSHEXTable;

import com.progdan.pdf2txt.exceptions.COSVisitorException;

/**
 * This represents a string object in a PDF document.
 *
 * @author Ben Litchfield (ben@csh.rit.edu)
 * @version $Revision: 1.2 $
 */
public class COSString extends COSBase
{
    private ByteArrayOutputStream out = new ByteArrayOutputStream();

    /**
     * Constructor.
     */
    public COSString()
    {
    }

    /**
     * Explicit constructor for ease of manual PDF construction.
     *
     * @param value The string value of the object.
     */
    public COSString( String value )
    {
        try
        {
            out.write(value.getBytes());
        }
        catch (IOException ignore)
        {
            ignore.printStackTrace();
            //should never happen
        }
    }

    /**
     * Explicit constructor for ease of manual PDF construction.
     *
     * @param value The string value of the object.
     */
    public COSString( byte[] value )
    {
        try
        {
            out.write( value );
        }
        catch (IOException ignore)
        {
            ignore.printStackTrace();
            //should never happen
        }
    }

    /**
     * This will create a COS string from a string of hex characters.
     *
     * @param hex A hex string.
     * @return A cos string with the hex characters converted to their actual bytes.
     * @throws IOException If there is an error with the hex string.
     */
    public static COSString createFromHexString( String hex ) throws IOException
    {
        COSString retval = new COSString();
        StringBuffer hexBuffer = new StringBuffer( hex );
        //if odd number then the last hex digit is assumed to be 0
        if( hexBuffer.length() % 2 == 1 )
        {
            hexBuffer.append( "0" );
        }
        for( int i=0; i<hexBuffer.length();)
        {
            String hexChars = "" + hexBuffer.charAt( i++ ) + hexBuffer.charAt( i++ );
            try
            {
                retval.append( Integer.parseInt( hexChars, 16 ) );
            }
            catch( NumberFormatException e )
            {
                throw new IOException( "Error: Expected hex number, actual='" + hexChars + "'" );
            }
        }
        return retval;
    }

    /**
     * This will take this string and create a hex representation of the bytes that make the string.
     *
     * @return A hex string representing the bytes in this string.
     */
    public String getHexString()
    {
        StringBuffer retval = new StringBuffer( out.size() * 2 );
        byte[] data = getBytes();
        for( int i=0; i<data.length; i++ )
        {
            retval.append( COSHEXTable.HEX_TABLE[ (data[i]+256)%256 ] );
        }

        return retval.toString();
    }

    /**
     * This will get the string that this object wraps.
     *
     * @return The wrapped string.
     */
    public String getString()
    {
        String retval;
        String encoding = null;
        byte[] data = getBytes();
        int start = 0;
        if( data.length > 2 )
        {
            if( data[0] == (byte)0xFF && data[1] == (byte)0xFE )
            {
                encoding = "UTF-16LE";
                start=2;
            }
            else if( data[0] == (byte)0xFE && data[1] == (byte)0xFF )
            {
                encoding = "UTF-16BE";
                start=2;
            }
        }
        try
        {
            if( encoding != null )
            {
                retval = new String( getBytes(), start, data.length-start, encoding );
            }
            else
            {
                retval = new String( getBytes() );
            }
        }
        catch( UnsupportedEncodingException e )
        {
            //should never happen
            e.printStackTrace();
            retval = new String( getBytes() );
        }
        return retval;
    }

    /**
     * This will append a byte[] to the string.
     *
     * @param data The byte[] to add to this string.
     *
     * @throws IOException If an IO error occurs while writing the byte.
     */
    public void append( byte[] data ) throws IOException
    {
        out.write( data );
    }

    /**
     * This will append a byte to the string.
     *
     * @param in The byte to add to this string.
     *
     * @throws IOException If an IO error occurs while writing the byte.
     */
    public void append( int in ) throws IOException
    {
        out.write( in );
    }

    /**
     * This will reset the internal buffer.
     */
    public void reset()
    {
        out.reset();
    }

    /**
     * This will get the bytes of the string.
     *
     * @return A byte array that represents the string.
     */
    public byte[] getBytes()
    {
        return out.toByteArray();
    }

    /**
     * @see Object#toString()
     */
    public String toString()
    {
        return "COSString{" + new String( getBytes() ) + "}";
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
        return visitor.visitFromString( this );
    }

    /**
     * @see Object#equals( Object )
     */
    public boolean equals(Object obj)
    {
        return (obj instanceof COSString) && java.util.Arrays.equals(((COSString) obj).getBytes(), getBytes());
    }

    /**
     * @see Object#hashCode()
     */
    public int hashCode()
    {
        return getBytes().hashCode();
    }
}
