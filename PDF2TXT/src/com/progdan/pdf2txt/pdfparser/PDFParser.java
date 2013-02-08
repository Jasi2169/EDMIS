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

import java.io.File;
import java.io.InputStream;
import java.io.IOException;

import com.progdan.pdf2txt.cos.COSBase;
import com.progdan.pdf2txt.cos.COSDictionary;
import com.progdan.pdf2txt.cos.COSDocument;
import com.progdan.pdf2txt.cos.COSInteger;
import com.progdan.pdf2txt.cos.COSObject;

import com.progdan.pdf2txt.pdmodel.PDDocument;

import com.progdan.pdf2txt.pdmodel.fdf.FDFDocument;

import com.progdan.pdf2txt.persistence.util.COSObjectKey;

import com.progdan.logengine.Logger;

/**
 * This class will handle the parsing of the PDF document.
 *
 * @author Ben Litchfield (ben@csh.rit.edu)
 * @version $Revision: 1.2 $
 */
public class PDFParser extends BaseParser
{
    private static Logger log = Logger.getLogger( PDFParser.class );

    private static final String PDF_HEADER = "%PDF-";
    private COSDocument document;

    /**
     * Temp file directory.
     */
    private File tempDirectory = new File( System.getProperty( "java.io.tmpdir" ) );

    /**
     * Constructor.
     *
     * @param input The input stream that contains the PDF document.
     *
     * @throws IOException If there is an error initializing the stream.
     */
    public PDFParser( InputStream input ) throws IOException
    {
        super( input );
    }

    /**
     * This is the directory where pdf2txt will create a temporary file
     * for storing pdf document stream in.  By default this directory will
     * be the value of the system property java.io.tmpdir.
     *
     * @param tmpDir The directory to create scratch files needed to store
     *        pdf document streams.
     */
    public void setTempDirectory( File tmpDir )
    {
        tempDirectory = tmpDir;
    }

    /**
     * This will prase the stream and create the PDF document.  This will close
     * the stream when it is done parsing.
     *
     * @throws IOException If there is an error reading from the stream.
     */
    public void parse() throws IOException
    {
        try
        {
            document = new COSDocument( tempDirectory );

            String header = readLine();
            if( log.isDebugEnabled() )
            {
                log.debug( "Header=" + header );
            }
            document.setHeaderString( header );

            if( header.length() < PDF_HEADER.length()+1 )
            {
                throw new IOException( "Error: Header is corrupt '" + header + "'" );
            }

            //sometimes there are some garbage bytes in the header before the header
            //actually starts, so lets try to find the header first.
            int headerStart = header.indexOf( PDF_HEADER );

            //greater than zero because if it is zero then
            //there is no point of trimming
            if( headerStart > 0 )
            {
                //trim off any leading characters
                header = header.substring( headerStart, header.length() );
            }

            try
            {
                float pdfVersion = Float.parseFloat( header.substring( PDF_HEADER.length(), header.length() ) );
                document.setVersion( pdfVersion );
            }
            catch( NumberFormatException e )
            {
                throw new IOException( "Error getting pdf version:" + e );
            }

            skipHeaderFillBytes();


            Object nextObject;
            boolean wasLastParsedObjectAnXref = false;
            try
            {
                while( (nextObject = parseObject()) != null )
                {
                    if( nextObject instanceof COSObject )
                    {
                        COSObject pdfObj = (COSObject)nextObject;
                        document.addObject( pdfObj );
                        wasLastParsedObjectAnXref = false;
                    }
                    else
                    {
                        PDFXref xref = (PDFXref)nextObject;
                        addXref((PDFXref)nextObject);
                        wasLastParsedObjectAnXref = true;
                    }
                    skipSpaces();
                }
            }
            catch( IOException e )
            {
                if( wasLastParsedObjectAnXref )
                {
                    log.debug( "Skipping some garbage" );
                    //Then we assume that there is just random garbage after
                    //the xref, not sure why the PDF spec allows this but it does.
                }
                else
                {
                    //some other error so just pass it along
                    throw e;
                }
            }
        }
        catch( IOException io )
        {
            //so if the PDF is corrupt then close the document and clear
            //all resources to it
            if( document != null )
            {
                document.close();
            }
            throw io;
        }
        finally
        {
            pdfSource.close();
        }
    }

    /**
     * This will skip a header's binary fill bytes.  This is in accordance to
     * PDF Specification 1.5 pg 68 section 3.4.1 "Syntax.File Structure.File Header"
     *
     * @throws IOException If there is an error reading from the stream.
    */
    protected void skipHeaderFillBytes() throws IOException
    {
        skipSpaces();
        int c = pdfSource.peek();
        if ( c >= 128 )
        {
            // Fill bytes conform with PDF reference (but without comment sign)
            // => skip until EOL
            readLine();
        }
        // else: no fill bytes
    }

    /**
     * This will get the document that was parsed.  parse() must be called before this is called.
     * When you are done with this document you must call close() on it to release
     * resources.
     *
     * @return The document that was parsed.
     *
     * @throws IOException If there is an error getting the document.
     */
    public COSDocument getDocument() throws IOException
    {
        if( document == null )
        {
            throw new IOException( "You must call parse() before calling getDocument()" );
        }
        return document;
    }

    /**
     * This will get the PD document that was parsed.  When you are done with
     * this document you must call close() on it to release resources.
     *
     * @return The document at the PD layer.
     *
     * @throws IOException If there is an error getting the document.
     */
    public PDDocument getPDDocument() throws IOException
    {
        return new PDDocument( getDocument() );
    }

    /**
     * This will get the FDF document that was parsed.  When you are done with
     * this document you must call close() on it to release resources.
     *
     * @return The document at the PD layer.
     *
     * @throws IOException If there is an error getting the document.
     */
    public FDFDocument getFDFDocument() throws IOException
    {
        return new FDFDocument( getDocument() );
    }

    /**
     * This will parse a document object from the stream.
     *
     * @param file The raf used for parsing.
     *
     * @return The parsed object.
     *
     * @throws IOException If an IO error occurs.
     */
    private Object parseObject() throws IOException
    {
        Object object = null;
        char peekedChar = (char)pdfSource.peek();
        if( log.isDebugEnabled() )
        {
            log.debug( "PDFParser.parseObject() peek='" + peekedChar + "'" );
        }
        if( pdfSource.isEOF() )
        {
            log.debug( "Skipping because of EOF" );
            //end of file we will return a null object and call it a day.
        }
        else if( peekedChar == 'x' ||
                 peekedChar == 't' )
        {
            //System.out.println( "parseObject() parsing xref" );
            String xref = null;
            int number = 0;
            int genNumber = 0;

            //FDF documents do not always have the xref
            if( peekedChar == 'x' || peekedChar == 't' )
            {
                object = parseXrefSection();
            }

            if( peekedChar == 'x' )
            {
                skipSpaces();
                while( pdfSource.peek() == 'x' )
                {
                    parseXrefSection();
                }
                String startxref = readString();
                if( !startxref.equals( "startxref" ) )
                {
                    throw new IOException( "expected='startxref' actual='" + startxref + "' " + pdfSource );
                }
                skipSpaces();
                int someInt = readInt();
            }

            //This MUST be readLine because readString strips out comments
            //and it will think that %% is a comment in from of the EOF
            String eof = readExpectedString( "%%EOF" );
            if( eof.indexOf( "%%EOF" )== -1 && !pdfSource.isEOF() )
            {
                throw new IOException( "expected='%%EOF' actual='" + eof + "' next=" + readString() +
                                       " next=" +readString() );
            }
            else if( !pdfSource.isEOF() )
            {
                //we might really be at the end of the file, there might just be some crap at the
                //end of the file.
                if( pdfSource.available() < 1000 )
                {
                    //We need to determine if we are at the end of the file.
                    byte[] data = new byte[ 1000 ];

                    int amountRead = pdfSource.read( data );
                    if( amountRead != -1 )
                    {
                        pdfSource.unread( data, 0, amountRead );
                    }
                    boolean atEndOfFile = true;//we assume yes unless we find another.
                    for( int i=0; i<amountRead-3 && atEndOfFile; i++ )
                    {
                        atEndOfFile = !(data[i] == 'E' &&
                                        data[i+1] == 'O' &&
                                        data[i+2] == 'F' );
                    }
                    if( atEndOfFile )
                    {
                        while( pdfSource.read( data, 0, data.length ) != -1 )
                        {
                            //single statement to make checkstyle happy
                            String readUntilDone = "";
                            //read until done.
                        }
                    }
                }
            }
        }
        else
        {
            int number;
            int genNum;
            String objectKey = null;
            try
            {
                number = readInt();
            }
            catch( IOException e )
            {
                //ok for some reason "GNU Ghostscript 5.10" puts two endobj
                //statements after an object, of course this is nonsense
                //but because we want to support as many PDFs as possible
                //we will simply try again
                number = readInt();
            }
            skipSpaces();
            genNum = readInt();
            if( log.isDebugEnabled() )
            {
                log.debug( "Parsing object (" + number + "," + genNum + ")" );
            }

            objectKey = readString( 3 );
            //System.out.println( "parseObject() num=" + number + " genNumber=" + genNum + " key='" + objectKey + "'" );
            if( !objectKey.equals( "obj" ) )
            {
                throw new IOException("expected='obj' actual='" + objectKey + "' " + pdfSource );
            }

            skipSpaces();
            COSBase pb = parseDirObject();
            String endObjectKey = readString();
            if( endObjectKey.equals( "stream" ) )
            {
                pdfSource.unread( endObjectKey.getBytes() );
                pdfSource.unread( ' ' );
                if( pb instanceof COSDictionary )
                {
                    pb = parseCOSStream( (COSDictionary)pb, getDocument().getScratchFile() );
                }
                else
                {
                    // this is not legal
                    // the combination of a dict and the stream/endstream forms a complete stream object
                    throw new IOException("stream not preceded by dictionary");
                }
                endObjectKey = readString();
            }
            object = getObjectFromPool(new COSObjectKey(number, genNum));
            COSObject pdfObject = (COSObject)object;
            pdfObject.setObject(pb);
            pdfObject.setObjectNumber( new COSInteger( number ) );
            pdfObject.setGenerationNumber( new COSInteger( genNum ) );

            if( !endObjectKey.equals( "endobj" ) )
            {
                if( !pdfSource.isEOF() )
                {
                    try
                    {
                        //It is possible that the endobj  is missing, there
                        //are several PDFs out there that do that so skip it and move on.
                        Float.parseFloat( endObjectKey );
                        pdfSource.unread( " ".getBytes() );
                        pdfSource.unread( endObjectKey.getBytes() );
                        if( log.isDebugEnabled() )
                        {
                            log.debug( "Missing endobj, found '" + endObjectKey +
                                "' instead, assuming that endobj is not present and will continue parsing." );
                        }
                    }
                    catch( NumberFormatException e )
                    {
                        //we will try again incase there was some garbage which
                        //some writers will leave behind.
                        String secondEndObjectKey = readString();
                        if( !secondEndObjectKey.equals( "endobj" ) )
                        {
                            throw new IOException("expected='endobj' firstReadAttempt='" + endObjectKey + "' " +
                                "secondReadAttempt='" + secondEndObjectKey + " " + pdfSource);
                        }
                    }
                }
            }
            skipSpaces();

        }
        //System.out.println( "parsed=" + object );
        return object;
    }

    private PDFXref parseXrefSection() throws IOException
    {
        int start = 0;
        int count = 0;
        String nextLine = null;

        nextLine = readLine();
        if( nextLine.equals( "xref" ) )
        {
            start = readInt();
            count = readInt();
            nextLine = readString();
        }
        while( !nextLine.equals( "trailer" ) && !pdfSource.isEOF() )
        {
            //skip past all the xref entries.
            nextLine = readString();
        }
        skipSpaces();
        COSDictionary parsedTrailer = parseCOSDictionary();
        COSDictionary docTrailer = document.getTrailer();
        if( log.isDebugEnabled() )
        {
            log.debug( "parsedTrailer=" + parsedTrailer );
            log.debug( "docTrailer=" + docTrailer );
        }
        if( docTrailer == null )
        {
            document.setTrailer( parsedTrailer );
        }
        else
        {
            docTrailer.addAll( parsedTrailer );
        }
        return new PDFXref( start, count );
    }
}
