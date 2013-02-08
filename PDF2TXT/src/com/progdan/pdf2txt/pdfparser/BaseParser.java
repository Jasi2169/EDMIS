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
package com.progdan.pdf2txt.pdfparser;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.progdan.pdf2txt.io.PushBackInputStream;

import com.progdan.pdf2txt.cos.COSArray;
import com.progdan.pdf2txt.cos.COSBase;
import com.progdan.pdf2txt.cos.COSBoolean;
import com.progdan.pdf2txt.cos.COSDictionary;
import com.progdan.pdf2txt.cos.COSInteger;
import com.progdan.pdf2txt.cos.COSName;
import com.progdan.pdf2txt.cos.COSNull;
import com.progdan.pdf2txt.cos.COSNumber;
import com.progdan.pdf2txt.cos.COSObject;
import com.progdan.pdf2txt.cos.COSStream;
import com.progdan.pdf2txt.cos.COSString;

import com.progdan.pdf2txt.persistence.util.COSObjectKey;
import com.progdan.logengine.Category;

/**
 * This class is used to contain parsing logic that will be used by both the
 * PDFParser and the COSStreamParser.
 *
 * @author Ben Litchfield (ben@csh.rit.edu)
 * @version $Revision: 1.2 $
 */
public abstract class BaseParser
{
    private static Category log = Category.getInstance(BaseParser.class.getName());

    /**
     * This is a byte array that will be used for comparisons.
     */
    public static final byte[] ENDSTREAM = "endstream".getBytes();

    /**
     * This is a byte array that will be used for comparisons.
     */
    public static final String DEF = "def";

    /**
     * This is the stream that will be read from.
     */
    //protected PushBackByteArrayStream pdfSource;
    protected PushBackInputStream pdfSource;

    /**
     * a pool of objects read/referenced so far
     * used to resolve indirect object references.
     */
    private Map objectPool = new HashMap();

    /**
     * moved xref here, is a persistence construct
     * maybe not needed anyway when not read from behind with delayed
     * access to objects.
     */
    private List xrefs = new ArrayList();

    /**
     * This is the set of valid hex digts.
     */
    private static final String HEXDIGITS = "0123456789abcdefABCDEF";

    /**
     * Constructor.
     *
     * @param input The input stream to read the data from.
     *
     * @throws IOException If there is an error reading the input stream.
     */
    public BaseParser( InputStream input ) throws IOException
    {
        //pdfSource = new PushBackByteArrayStream( input );
        pdfSource = new PushBackInputStream( new BufferedInputStream( input, 16384 ), 4096 );
    }

    private static boolean isHexDigit(char ch)
    {
        return (HEXDIGITS.indexOf(ch) != -1);
    }

    /**
     * This will parse a PDF dictionary value.
     *
     * @return The parsed Dictionary object.
     *
     * @throws IOException If there is an error parsing the dictionary object.
     */
    private COSBase parseCOSDictionaryValue() throws IOException
    {

        if( log.isDebugEnabled() )
        {
            log.debug("parseCOSDictionaryValue() " + pdfSource );
        }
        COSBase retval = null;
        COSBase number = parseDirObject();
        skipSpaces();
        char next = (char)pdfSource.peek();
        if( next >= '0' && next <= '9' )
        {
            COSBase generationNumber = parseDirObject();
            skipSpaces();
            char r = (char)pdfSource.read();
            if( r != 'R' )
            {
                throw new IOException( "expected='R' actual='" + r + "' " + pdfSource );
            }
            COSObjectKey key = new COSObjectKey(((COSInteger) number).intValue(),
                                                ((COSInteger) generationNumber).intValue());
            retval = getObjectFromPool(key);
        }
        else
        {
            retval = number;
        }
        //System.out.println( "parseCOSDictionaryValue() value=" + retval );
        return retval;
    }

    /**
     * This will parse a PDF dictionary.
     *
     * @return The parsed dictionary.
     *
     * @throws IOException IF there is an error reading the stream.
     */
    protected COSDictionary parseCOSDictionary() throws IOException
    {
        if( log.isDebugEnabled() )
        {
            log.debug("parseCOSDictionary() " + pdfSource );
        }
        char c = (char)pdfSource.read();
        if( c != '<')
        {
            throw new IOException( "expected='<' actual='" + c + "'" );
        }
        c = (char)pdfSource.read();
        if( c != '<')
        {
            throw new IOException( "expected='<' actual='" + c + "' " + pdfSource );
        }
        skipSpaces();
        COSDictionary obj = new COSDictionary();
        boolean done = false;
        while( !done )
        {
            skipSpaces();
            c = (char)pdfSource.peek();
            if( c == '>')
            {
                done = true;
            }
            else
            {
                COSName key = parseCOSName();
                COSBase value = parseCOSDictionaryValue();
                skipSpaces();
                if( ((char)pdfSource.peek()) == 'd' )
                {
                    //if the next string is 'def' then we are parsing a cmap stream
                    //and want to ignore it, otherwise throw an exception.
                    String potentialDEF = readString();
                    if( !potentialDEF.equals( DEF ) )
                    {
                        pdfSource.unread( potentialDEF.getBytes() );
                    }
                    else
                    {
                        skipSpaces();
                    }
                }

                if( value == null )
                {
                    throw new IOException("Bad Dictionary Declaration " + pdfSource );
                }
                obj.setItem( key, value );
            }
        }
        if( ((char)pdfSource.peek()) != '>' )
        {
            throw new IOException( "expected='>' actual='" + (char)pdfSource.peek() + "'" );
        }
        pdfSource.read();
        if( ((char)pdfSource.peek()) != '>' )
        {
            throw new IOException( "expected='>' actual='" + (char)pdfSource.peek() + "'" );
        }
        pdfSource.read();
        if( log.isDebugEnabled() )
        {
            log.debug("parseCOSDictionary() done peek='" + pdfSource.peek() + "'" );
        }
        return obj;
    }

    /**
     * This will read a COSStream from the input stream.
     *
     * @param file The file to write the stream to when reading.
     * @param dic The dictionary that goes with this stream.
     *
     * @return The parsed pdf stream.
     *
     * @throws IOException If there is an error reading the stream.
     */
    protected COSStream parseCOSStream( COSDictionary dic, RandomAccessFile file ) throws IOException
    {
        if( log.isDebugEnabled() )
        {
            log.debug("parseCOSStream() " + pdfSource );
        }
        COSStream stream = new COSStream( dic, file );
        OutputStream out = null;
        try
        {
            char c;
            String streamString = readString();
            int streamOffset;
            //long streamLength;

            if (!streamString.equals("stream"))
            {
                throw new IOException("expected='stream' actual='" + streamString + "'");
            }

            //PDF Ref 3.2.7 A stream must be followed by either
            //a CRLF or LF but nothing else.

            int whitespace = pdfSource.read();
            if( whitespace == 0x0D )
            {
                whitespace = pdfSource.read();
                if( whitespace != 0x0A )
                {
                    pdfSource.unread( whitespace );
                    //The spec says this is invalid but it happens in the real
                    //world so we must support it.
                    //throw new IOException("expected='0x0A' actual='0x" +
                    //    Integer.toHexString(whitespace) + "' " + pdfSource);
                }
            }
            else if (whitespace == 0x0A)
            {
                //statement here just to make checkstyle happy
                String nothingToDo = "";
                //that is fine
            }
            else
            {
                //we are in an error.
                //but again we will do a lenient parsing and just assume that everything
                //is fine
                pdfSource.unread( whitespace );
                //throw new IOException("expected='0x0D or 0x0A' actual='0x" +
                //Integer.toHexString(whitespace) + "' " + pdfSource);

            }


            COSBase streamLength = dic.getDictionaryObject(COSName.LENGTH);
            long length = -1;
            if( streamLength instanceof COSNumber )
            {
                length = ((COSNumber)streamLength).intValue();
            }
            else if( streamLength instanceof COSObject &&
                     ((COSObject)streamLength).getObject() instanceof COSNumber )
            {
                length = ((COSNumber)((COSObject)streamLength).getObject()).intValue();
            }

            //length = -1;
            //streamLength = null;

            //Need to keep track of the
            out = stream.createFilteredStream( streamLength );
            String endStream = null;
            //the length is wrong in some pdf documents which means
            //that PDF2TXT must basically ignore it in order to be able to read
            //the most number of PDF documents.  This of course is a penalty hit,
            //maybe I could implement a faster parser.
            /**if( length != -1 )
            {
                byte[] buffer = new byte[1024];
                int amountRead = 0;
                int totalAmountRead = 0;
                while( amountRead != -1 && totalAmountRead < length )
                {
                    int maxAmountToRead = Math.min(buffer.length, (int)(length-totalAmountRead));
                    amountRead = pdfSource.read(buffer,0,maxAmountToRead);
                    totalAmountRead += amountRead;
                    if( amountRead != -1 )
                    {
                        out.write( buffer, 0, amountRead );
                    }
                }
            }
            else
            {**/
                readUntilEndStream( out );
            /**}*/
            c = (char) pdfSource.peek();
            //System.out.println( "BaseParser peek()=" + c );
            skipSpaces();
            endStream = readString();

            if (!endStream.equals("endstream"))
            {
                readUntilEndStream( out );
                endStream = readString();
                if( !endStream.equals( "endstream" ) )
                {
                    throw new IOException("expected='endstream' actual='" + endStream + "' " + pdfSource);
                }
            }
        }
        finally
        {
            if( out != null )
            {
                out.close();
            }
        }
        return stream;
    }

    private void readUntilEndStream( OutputStream out ) throws IOException
    {
        int currentIndex = 0;
        int byteRead = 0;
        //this is the additional bytes buffered but not written
        int additionalBytes=0;
        byte[] buffer = new byte[ENDSTREAM.length+additionalBytes];
        int writeIndex = 0;
        while(!cmpCircularBuffer( buffer, currentIndex, ENDSTREAM ) && byteRead != -1 )
        {
            writeIndex = currentIndex - buffer.length;
            if( writeIndex >= 0 )
            {
                out.write( buffer[writeIndex%buffer.length] );
            }
            byteRead = pdfSource.read();
            buffer[currentIndex%buffer.length] = (byte)byteRead;
            currentIndex++;
        }

        //we want to ignore the end of the line data when reading a stream
        //so will make an attempt to ignore it.
        /*writeIndex = currentIndex - buffer.length;
        if( buffer[writeIndex%buffer.length] == 13 &&
            buffer[(writeIndex+1)%buffer.length] == 10 )
        {
            //then ignore the newline before the endstream
        }
        else if( buffer[(writeIndex+1)%buffer.length] == 10 )
        {
            //Then first byte is data, second byte is newline
            out.write( buffer[writeIndex%buffer.length] );
        }
        else
        {
            out.write( buffer[writeIndex%buffer.length] );
            out.write( buffer[(writeIndex+1)%buffer.length] );
        }*/

        /**
         * Old way of handling newlines before endstream
        for( int i=0; i<additionalBytes; i++ )
        {
            writeIndex = currentIndex - buffer.length;
            if( writeIndex >=0 &&
                //buffer[writeIndex%buffer.length] != 10 &&
                buffer[writeIndex%buffer.length] != 13 )
            {
                out.write( buffer[writeIndex%buffer.length] );
            }
            currentIndex++;
        }
        */
        //System.out.println( "currentIndex=" +currentIndex );
        //System.out.println( "BaseParser buffer=" + new String( buffer ) + " byteRead=" + byteRead +
        //    " amountRead=" + amountRead +
        //    " peek=" + pdfSource.peek());
        pdfSource.unread( ENDSTREAM );

    }

    /**
     * This basically checks to see if the next compareTo.length bytes of the
     * buffer match the compareTo byte array.
     */
    private boolean cmpCircularBuffer( byte[] buffer, int currentIndex, byte[] compareTo )
    {
        boolean match = true;
        if( currentIndex-compareTo.length < 0 )
        {
            match = false;
        }
        for( int i=0; i<compareTo.length && match; i++ )
        {
            match = buffer[((currentIndex+i-compareTo.length)%buffer.length)] == compareTo[i];
        }
        return match;
    }

    /**
     * This will parse a PDF string.
     *
     * @return The parsed PDF string.
     *
     * @throws IOException If there is an error reading from the stream.
     */
    protected COSString parseCOSString() throws IOException
    {
        if( log.isDebugEnabled() )
        {
            log.debug("parseCOSString() " + pdfSource );
        }
        char nextChar = (char)pdfSource.read();
        //System.out.println( "parseCOSString() nextChar=" + nextChar );
        COSString retval = new COSString();
        char openBrace;
        char closeBrace;
        if( nextChar == '(' )
        {
            openBrace = '(';
            closeBrace = ')';
        }
        else if( nextChar == '<' )
        {
            openBrace = '<';
            closeBrace = '>';
        }
        else
        {
            throw new IOException( "parseCOSString string should start with '(' or '<' and not '" +
                                   nextChar + "' " + pdfSource );
        }

        //This is the number of braces read
        //
        int braces = 1;
        while( braces > 0 && !pdfSource.isEOF())
        {
            char c = (char)pdfSource.read();
            //if( log.isDebugEnabled() )
            //{
            //    log.debug( "Parsing COSString character '" + c + "' code=" + (int)c );
            //}

            if(c == closeBrace)
            {
                braces--;
                if( braces != 0 )
                {
                    retval.append( c );
                }
            }
            else if( c == openBrace )
            {
                braces++;
                retval.append( c );
            }
            else if( c == '\\' )
            {
                 //patched by ram
                char next = (char)pdfSource.read();
                switch(next)
                {
                    case 'n':
                        retval.append( '\n' );
                        break;
                    case 'r':
                        retval.append( '\r' );
                        break;
                    case 't':
                        retval.append( '\t' );
                        break;
                    case 'b':
                        retval.append( '\b' );
                        break;
                    case 'f':
                        retval.append( '\f' );
                        break;
                    case '(':
                    case ')':
                    case '\\':
                        retval.append( next );
                        break;
                    case 10:
                    case 13:
                        //this is a break in the line so ignore it and the newline and continue
                        while( isEOL() && !pdfSource.isEOF())
                        {
                            pdfSource.read();
                        }
                        break;
                    case '0':
                    case '1':
                    case '2':
                    case '3':
                    case '4':
                    case '5':
                    case '6':
                    case '7':
                    {
                        StringBuffer octal = new StringBuffer();
                        octal.append( next );
                        if( (char)pdfSource.peek() >= '0' && (char)pdfSource.peek() <= '7' )
                        {
                            octal.append( (char)pdfSource.read() );
                        }
                        if( (char)pdfSource.peek() >= '0' && (char)pdfSource.peek() <= '7' )
                        {
                            octal.append( (char)pdfSource.read() );
                        }
                        int character = 0;
                        try
                        {
                            character = Integer.parseInt( octal.toString(), 8 );
                        }
                        catch( NumberFormatException e )
                        {
                            throw new IOException( "Error: Expected octal character, actual='" + octal + "'" );
                        }
                        retval.append( character );
                        break;
                    }
                    default:
                    {
                        retval.append( '\\' );
                        retval.append( next );
                        //another ficken problem with PDF's, sometimes the \ doesn't really
                        //mean escape like the PDF spec says it does, sometimes is should be literal
                        //which is what we will assume here.
                        //throw new IOException( "Unexpected break sequence '" + next + "' " + pdfSource );
                    }
                }
            }
            else
            {
                if( openBrace == '<' )
                {
                    if( isHexDigit(c) )
                    {
                        retval.append( c );
                    }
                }
                else
                {
                    retval.append( c );
                }
            }
        }
        if( openBrace == '<' )
        {
            retval = COSString.createFromHexString( retval.getString() );
        }
        if( log.isDebugEnabled() )
        {
            log.debug("parseCOSString() done parsed=" + retval );
        }
        return retval;
    }

    /**
     * This will parse a PDF array object.
     *
     * @return The parsed PDF array.
     *
     * @throws IOException If there is an error parsing the stream.
     */
    protected COSArray parseCOSArray() throws IOException
    {
        if( log.isDebugEnabled() )
        {
            log.debug("parseCOSArray() " + pdfSource );
        }
        char c = (char)pdfSource.peek();
        if( c != '[')
        {
            throw new IOException( "expected='[' actual='" + c + "'" );
        }
        pdfSource.read(); //read '[' character
        COSArray po = new COSArray();
        COSBase pbo = null;
        skipSpaces();
        int i = 0;
        while( ((i = pdfSource.peek()) > 0) && ((char)i != ']') )
        {
            pbo = parseDirObject();
            if( pbo instanceof COSObject )
            {
                COSInteger genNumber = (COSInteger)po.remove( po.size() -1 );
                COSInteger number = (COSInteger)po.remove( po.size() -1 );
                COSObjectKey key = new COSObjectKey(number.intValue(), genNumber.intValue());
                pbo = getObjectFromPool(key);
            }
            //System.out.println( "parseCOSArray() adding " + pbo );
            if( pbo != null )
            {
                po.add( pbo );
            }
            else
            {
                //it could be a bad object in the array which is just skipped
            }
            skipSpaces();
        }
        pdfSource.read(); //read ']'
        skipSpaces();
        if( log.isDebugEnabled() )
        {
            log.debug("parseCOSArray() done peek='" + (char)pdfSource.peek() + "'" );
        }
        return po;
    }

    /**
     * Determine if a character terminates a PDF name.
     *
     * @param ch The character
     * @return <code>true</code> if the character terminates a PDF name, otherwise <code>false</code>.
     */
    private boolean isEndOfName(char ch)
    {
        return (ch == ' ' || ch == 13 || ch == 10 || ch == 9 || ch == '>' || ch == '<'
            || ch == '[' || ch =='/' || ch ==']' || ch ==')' || ch =='(' ||
            ch == -1 //EOF
            );
    }

    /**
     * This will parse a PDF name from the stream.
     *
     * @return The parsed PDF name.
     *
     * @throws IOException If there is an error reading from the stream.
     */
    protected COSName parseCOSName() throws IOException
    {
        if( log.isDebugEnabled() )
        {
            log.debug("parseCOSName() " + pdfSource );
        }
        COSName retval = null;
        if( ((char)pdfSource.peek()) != '/')
        {
            throw new IOException("expected='/' actual='" + (char)pdfSource.peek() + "' " + pdfSource );
        }
        // costruisce il nome
        pdfSource.read();
        StringBuffer buffer = new StringBuffer();
        while( !pdfSource.isEOF() )
        {
            char c = (char)pdfSource.peek();
            if(c == '#')
            {
                pdfSource.read(); //read '#' symbol
                char ch1 = (char)pdfSource.read();
                char ch2 = (char)pdfSource.read();

                // Prior to PDF v1.2, the # was not a special character.  Also,
                // it has been observed that various PDF tools do not follow the
                // spec with respect to the # escape, even though they report
                // PDF versions of 1.2 or later.  The solution here is that we
                // interpret the # as an escape only when it is followed by two
                // valid hex digits.
                //
                if (isHexDigit(ch1) && isHexDigit(ch2))
                {
                    String hex = "" + ch1 + ch2;
                    try
                    {
                        buffer.append( (char) Integer.parseInt(hex, 16));
                    }
                    catch (NumberFormatException e)
                    {
                        throw new IOException("Error: expected hex number, actual='" + hex + "'");
                    }
                }
                else
                {
                    pdfSource.unread(ch2);
                    pdfSource.unread(ch1);
                    buffer.append( c );
                }
            }
            else if (isEndOfName(c))
            {
                break;
            }
            else
            {
                buffer.append( c );
                pdfSource.read();
            }
        }
        retval = COSName.getPDFName( buffer.toString() );
        //System.out.println( "parseCOSName() parsed:'" + buffer + "'" );
        return retval;
    }

    /**
     * This will parse a boolean object from the stream.
     *
     * @return The parsed boolean object.
     *
     * @throws IOException If an IO error occurs during parsing.
     */
    protected COSBoolean parseBoolean() throws IOException
    {
        COSBoolean retval = null;
        char c = (char)pdfSource.peek();
        if( c == 't' )
        {
            byte[] trueArray = new byte[ 4 ];
            int amountRead = pdfSource.read( trueArray, 0, 4 );
            String trueString = new String( trueArray, 0, amountRead );
            if( !trueString.equals( "true" ) )
            {
                throw new IOException( "Error parsing boolean: expected='true' actual='" + trueString + "'" );
            }
            else
            {
                retval = COSBoolean.TRUE;
            }
        }
        else if( c == 'f' )
        {
            byte[] falseArray = new byte[ 5 ];
            int amountRead = pdfSource.read( falseArray, 0, 5 );
            String falseString = new String( falseArray, 0, amountRead );
            if( !falseString.equals( "false" ) )
            {
                throw new IOException( "Error parsing boolean: expected='true' actual='" + falseString + "'" );
            }
            else
            {
                retval = COSBoolean.FALSE;
            }
        }
        else
        {
            throw new IOException( "Error parsing boolean expected='t or f' actual='" + c + "'" );
        }
        return retval;
    }

    /**
     * This will parse a directory object from the stream.
     *
     * @return The parsed object.
     *
     * @throws IOException If there is an error during parsing.
     */
    protected COSBase parseDirObject() throws IOException
    {
        if( log.isDebugEnabled() )
        {
            log.debug("parseDirObject() " + pdfSource );
        }
        COSBase retval = null;

        skipSpaces();
        int nextByte = pdfSource.peek();
        char c = (char)nextByte;
        //System.out.println( "parseDirObject() switching on='" + c + "'" );
        switch(c)
        {
            case '<':
            {
                int leftBracket = pdfSource.read();//pull off first left bracket
                c = (char)pdfSource.peek(); //check for second left bracket
                pdfSource.unread( leftBracket );
                if(c == '<')
                {

                    retval = parseCOSDictionary();
                    skipSpaces();
                }
                else
                {
                    retval = parseCOSString();
                }
                break;
            }
            case '[': // array
            {
                retval = parseCOSArray();
                break;
            }
            case '(':
                retval = parseCOSString();
                break;
            case '/':   // name
                retval = parseCOSName();
                break;
            case 'n':   // null
            {
                String nullString = readString();
                if( !nullString.equals( "null") )
                {
                    throw new IOException("Expected='null' actual='" + nullString + "'");
                }
                retval = COSNull.NULL;
                break;
            }
            case 't':
            {
                byte[] trueBytes = new byte[4];
                int amountRead = pdfSource.read( trueBytes, 0, 4 );
                String trueString = new String( trueBytes, 0, amountRead );
                if( trueString.equals( "true" ) )
                {
                    retval = COSBoolean.TRUE;
                }
                else
                {
                    throw new IOException( "expected true actual='" + trueString + "' " + pdfSource );
                }
                break;
            }
            case 'f':
            {
                byte[] falseBytes = new byte[5];
                int amountRead = pdfSource.read( falseBytes, 0, 5 );
                String falseString = new String( falseBytes, 0, amountRead );
                if( falseString.equals( "false" ) )
                {
                    retval = COSBoolean.FALSE;
                }
                else
                {
                    throw new IOException( "expected false actual='" + falseString + "' " + pdfSource );
                }
                break;
            }
            case 'R':
                pdfSource.read();
                retval = new COSObject(null);
                break;
            default:
            {
                if( Character.isDigit(c) || c == '-' || c == '+' || c == '.')
                {
                    StringBuffer buf = new StringBuffer();
                    while( Character.isDigit(( c = (char)pdfSource.peek()) )||
                           c == '-' ||
                           c == '+' ||
                           c == '.' ||
                           c == 'E' ||
                           c == 'e' )
                    {
                        buf.append( c );
                        pdfSource.read();
                    }
                    retval = COSNumber.get( buf.toString() );
                }
                else
                {
                    //This is not suppose to happen, but we will allow for it
                    //so we are more compatible with POS writers that don't
                    //follow the spec
                    String badString = readString();
                    //throw new IOException( "Unknown dir object c='" + c +
                    //"' peek='" + (char)pdfSource.peek() + "' " + pdfSource );
                }
            }
        }
        if( log.isDebugEnabled() )
        {
            log.debug("parseDirObject() done retval=" +retval );
        }
        return retval;
    }

    /**
     * This will read the next byte from the stream and verify that it is actually a space.
     *
     * @throws IOException If the next byte is not 32.
     */
    private void readSingleSpace() throws IOException
    {
        int nextByte = pdfSource.read();
        if( nextByte != 32 )
        {
            throw new IOException( "expected=(0x20) actual=0x" + Integer.toHexString( nextByte ) );
        }
    }

    /**
     * This will read the next string from the stream.
     *
     * @return The string that was read from the stream.
     *
     * @throws IOException If there is an error reading from the stream.
     */
    protected String readString() throws IOException
    {
        skipSpaces();

        //average string size is around 2 and the normal string buffer size is
        //about 16 so lets save some space.
        StringBuffer buffer = new StringBuffer(4);
        while( !isEndOfName((char)pdfSource.peek()) && !isClosing() && !pdfSource.isEOF() )
        {
            buffer.append( (char)pdfSource.read() );
        }
        return buffer.toString();
    }

    /**
     * This will read bytes until the end of line marker occurs.
     *
     * @param theString The next expected string in the stream.
     *
     * @return The characters between the current position and the end of the line.
     *
     * @throws IOException If there is an error reading from the stream or theString does not match what was read.
     */
    protected String readExpectedString( String theString ) throws IOException
    {
        while( isWhitespace() && !pdfSource.isEOF())
        {
            pdfSource.read();
        }
        StringBuffer buffer = new StringBuffer( theString.length() );
        int charsRead = 0;
        while( !isEOL() && !pdfSource.isEOF() && charsRead < theString.length() )
        {
            char next = (char)pdfSource.read();
            buffer.append( next );
            if( theString.charAt( charsRead ) == next )
            {
                charsRead++;
            }
            else
            {
                throw new IOException( "Error: Expected to read '" + theString +
                    "' instead started reading '" +buffer.toString() + "'" );
            }
        }
        while( isEOL() && !pdfSource.isEOF() )
        {
            pdfSource.read();
        }
        return buffer.toString();
    }

    /**
     * This will read the next string from the stream up to a certain length.
     *
     * @param length The length to stop reading at.
     *
     * @return The string that was read from the stream of length 0 to length.
     *
     * @throws IOException If there is an error reading from the stream.
     */
    protected String readString( int length ) throws IOException
    {
        skipSpaces();

        //average string size is around 2 and the normal string buffer size is
        //about 16 so lets save some space.
        StringBuffer buffer = new StringBuffer(length);
        while( !isWhitespace() && !isClosing() && !pdfSource.isEOF() && buffer.length() < length &&
            pdfSource.peek() != (int)'[' &&
            pdfSource.peek() != (int)'<' &&
            pdfSource.peek() != (int)'(' &&
            pdfSource.peek() != (int)'/' )
        {
            buffer.append( (char)pdfSource.read() );
        }
        return buffer.toString();
    }

    /**
     * This will tell if the next character is a closing brace( close of PDF array ).
     *
     * @return true if the next byte is ']', false otherwise.
     *
     * @throws IOException If an IO error occurs.
     */
    protected boolean isClosing() throws IOException
    {
        char next = (char)pdfSource.peek();
        return next == ']';
    }

    /**
     * This will read bytes until the end of line marker occurs.
     *
     * @return The characters between the current position and the end of the line.
     *
     * @throws IOException If there is an error reading from the stream.
     */
    protected String readLine() throws IOException
    {
        while( isWhitespace() && !pdfSource.isEOF())
        {
            pdfSource.read();
        }
        StringBuffer buffer = new StringBuffer( 11 );
        while( !isEOL() && !pdfSource.isEOF() )
        {
            char next = (char)pdfSource.read();
            buffer.append( next );
        }
        while( isEOL() && !pdfSource.isEOF() )
        {
            pdfSource.read();
        }
        return buffer.toString();
    }

    /**
     * This will tell if the next byte to be read is an end of line byte.
     *
     * @return true if the next byte is 0x0A or 0x0D.
     *
     * @throws IOException If there is an error reading from the stream.
     */
    protected boolean isEOL() throws IOException
    {
        int nextByte = pdfSource.peek();
        return nextByte == 10 || nextByte == 13;
    }

    /**
     * This will tell if the next byte is whitespace or not.
     *
     * @return true if the next byte in the stream is a whitespace character.
     *
     * @throws IOException If there is an error reading from the stream.
     */
    protected boolean isWhitespace() throws IOException
    {
        return isWhitespace( pdfSource.peek() );
    }

    /**
     * This will tell if the next byte is whitespace or not.
     *
     * @param c The character to check against whitespace
     *
     * @return true if the next byte in the stream is a whitespace character.
     *
     * @throws IOException If there is an error reading from the stream.
     */
    protected boolean isWhitespace( int c ) throws IOException
    {
        return c == 0 || c == 9 || c == 12  || c == 10
        || c == 13 || c == 32;
    }

    /**
     * This will skip all spaces and comments that are present.
     *
     * @throws IOException If there is an error reading from the stream.
     */
    protected void skipSpaces() throws IOException
    {
        //log( "skipSpaces() " + pdfSource );
        int c;
        while((c = pdfSource.peek()) == 0 || c == 9 || c == 10 || c == 12
        || c == 13 || c== 32 || c== 37 )//37 is the % character, a comment
        {
            if( c == 37 )
            {
                while( !isEOL() && !pdfSource.isEOF()  )
                {
                    //skip past the comment section
                    pdfSource.read();
                }
            }
            else
            {
                int readChar = pdfSource.read();
            }
        }
        //log( "skipSpaces() done peek='" + (char)pdfSource.peek() + "'" );
    }

    /**
     * this will compare two byte arrays.
     *
     * @param first The first byte array to compare.
     * @param second The second byte array to compare.
     *
     * @return true if both arrays are the same AND forall i : first[i] = second[i]
     */
    private boolean cmpArray( byte[] first, byte[] second )
    {
        return cmpArray( first, 0, second );
    }

    /**
     * This will tell if a character is a hex digit or not.
     *
     * @param c The character to test.
     *
     * @return true if c between 0 and 9 and c between a and f
     */
    protected boolean isxdigit( char c )
    {
        return c == '0' ||
        c == '1' ||
        c == '2' ||
        c == '3' ||
        c == '4' ||
        c == '5' ||
        c == '6' ||
        c == '7' ||
        c == '8' ||
        c == '9' ||
        c == 'A' ||
        c == 'B' ||
        c == 'C' ||
        c == 'D' ||
        c == 'E' ||
        c == 'F' ||
        c == 'a' ||
        c == 'b' ||
        c == 'c' ||
        c == 'd' ||
        c == 'e' ||
        c == 'f';
    }

    /**
     * This will compare two arrays for equality.
     *
     * @param first The first array to compare.
     * @param firstOffset The first byte to start comparing.
     * @param second The second array to compare.
     */
    private boolean cmpArray( byte[] first, int firstOffset, byte[] second )
    {
        boolean show = false;
        boolean retval = true;
        if( first.length-firstOffset >= second.length )
        {
            int arrayLength = second.length;
            for( int i =0; i<arrayLength && retval; i++ )
            {
                retval = retval && first[ firstOffset + i ] == second[ i ];
            }
        }
        else
        {
            retval = false;
        }
        return retval;
    }

    /**
     * This will read an integer from the stream.
     *
     * @return The integer that was read from the stream.
     *
     * @throws IOException If there is an error reading from the stream.
     */
    protected int readInt() throws IOException
    {
        int retval = 0;

        int lastByte = 0;
        StringBuffer intBuffer = new StringBuffer();
        while( (lastByte = pdfSource.read() ) != 32 &&
        lastByte != 10 &&
        lastByte != 13 &&
        lastByte != 0 && //See sourceforge bug 853328
        lastByte != -1 )
        {
            intBuffer.append( (char)lastByte );
        }
        try
        {
            retval = Integer.parseInt( intBuffer.toString() );
        }
        catch( NumberFormatException e )
        {
            throw new IOException( "Error: Expected an integer type, actual='" + intBuffer + "'" );
        }
        return retval;
    }

    /**
     * This will add an xref.
     *
     * @param xref The xref to add.
     */
    public void addXref( PDFXref xref )
    {
        xrefs.add(xref);
    }

    /**
     * This will get an object from the pool.
     *
     * @param key The object key.
     *
     * @return The object in the pool or a new one if it has not been parsed yet.
     *
     * @throws IOException If there is an error getting the proxy object.
     */
    public COSObject getObjectFromPool(COSObjectKey key) throws IOException
    {
        COSObject obj = (COSObject) objectPool.get(key);
        if (obj == null)
        {
            // this was a forward reference, make "proxy" object
            obj = new COSObject(null);
            objectPool.put(key, obj);
        }
        return obj;
    }

    /**
     * This will get all of the xrefs.
     *
     * @return A list of all xrefs.
     */
    public List getXrefs()
    {
        return xrefs;
    }

    /**
     * This will set the xrefs for this parser.
     *
     * @param newXrefs The xrefs for this parser.
     */
    private void setXrefs( List newXrefs )
    {
        xrefs = newXrefs;
    }
}
