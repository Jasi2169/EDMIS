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
package com.progdan.pdf2txt;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import com.progdan.pdf2txt.exceptions.InvalidPasswordException;

import com.progdan.pdf2txt.pdmodel.PDDocument;

import com.progdan.pdf2txt.util.PDFTextStripper;

import com.progdan.logengine.Logger;

/**
 * This is the main program that simply parses the pdf document and transforms it
 * into text.
 *
 * @author Ben Litchfield (ben@csh.rit.edu)
 * @version $Revision: 1.2 $
 */
public class ExtractText
{
    private static final Logger LOG = Logger.getLogger( ExtractText.class );

    /**
     * This is the default encoding of the text to be output.
     */
    public static final String DEFAULT_ENCODING =
        null;
        //"ISO-8859-1";
        //"ISO-8859-6"; //arabic
        //"US-ASCII";
        //"UTF-8";
        //"UTF-16";
        //"UTF-16BE";
        //"UTF-16LE";


    private static final String PASSWORD = "-password";
    private static final String ENCODING = "-encoding";
    private static final String CONSOLE = "-console";
    private static final String START_PAGE = "-startPage";
    private static final String END_PAGE = "-endPage";

    /**
     * private constructor.
    */
    private ExtractText()
    {
    }

    /**
     * Infamous main method.
     *
     * @param args Command line arguments, should be one and a reference to a file.
     *
     * @throws Exception If there is an error parsing the document.
     */
    public static void main( String[] args ) throws Exception
    {
        boolean toConsole = false;
        int currentArgumentIndex = 0;
        String password = "";
        String encoding = DEFAULT_ENCODING;
        PDFTextStripper stripper = new PDFTextStripper();
        String pdfFile = null;
        String textFile = null;
        int startPage = 1;
        int endPage = Integer.MAX_VALUE;
        for( int i=0; i<args.length; i++ )
        {
            if( args[i].equals( PASSWORD ) )
            {
                i++;
                if( i >= args.length )
                {
                    usage();
                }
                password = args[i];
            }
            else if( args[i].equals( ENCODING ) )
            {
                i++;
                if( i >= args.length )
                {
                    usage();
                }
                encoding = args[i];
            }
            else if( args[i].equals( START_PAGE ) )
            {
                i++;
                if( i >= args.length )
                {
                    usage();
                }
                startPage = Integer.parseInt( args[i] );
            }
            else if( args[i].equals( END_PAGE ) )
            {
                i++;
                if( i >= args.length )
                {
                    usage();
                }
                endPage = Integer.parseInt( args[i] );
            }
            else if( args[i].equals( CONSOLE ) )
            {
                toConsole = true;
            }
            else
            {
                if( pdfFile == null )
                {
                    pdfFile = args[i];
                }
                else
                {
                    textFile = args[i];
                }
            }
        }

        if( pdfFile == null )
        {
            usage();
        }

        if( textFile == null && pdfFile.length() >4 )
        {
            textFile = pdfFile.substring( 0, pdfFile.length() -4 ) + ".txt";
        }

        Writer output = null;
        PDDocument document = null;
        try
        {
            long start = System.currentTimeMillis();
            document = PDDocument.load( pdfFile );
            long stop = System.currentTimeMillis();
            LOG.info( "Time to parse time=" + (stop-start) );


            //document.print();
            if( document.isEncrypted() )
            {
                try
                {
                    document.decrypt( password );
                }
                catch( InvalidPasswordException e )
                {
                    if( args.length == 4 )//they supplied the wrong password
                    {
                        System.err.println( "Error: The supplied password is incorrect." );
                        System.exit( 2 );
                    }
                    else
                    {
                        //they didn't suppply a password and the default of "" was wrong.
                        System.err.println( "Error: The document is encrypted." );
                        usage();
                    }
                }
            }
            if( toConsole )
            {
                output = new OutputStreamWriter( System.out );
            }
            else
            {
                if( encoding != null )
                {
                    output = new OutputStreamWriter(
                        new FileOutputStream( textFile ), encoding );
                }
                else
                {
                    //use default encoding
                    output = new OutputStreamWriter(
                        new FileOutputStream( textFile ) );
                }
            }

            start = System.currentTimeMillis();
            stripper.setStartPage( startPage );
            stripper.setEndPage( endPage );
            stripper.writeText( document, output );
            stop = System.currentTimeMillis();
            LOG.info( "Time to extract text time=" +(stop-start) );
        }
        finally
        {
            if( output != null )
            {
                output.close();
            }
            if( document != null )
            {
                document.close();
            }
        }
    }

    /**
     * This will print the usage requirements and exit.
     */
    private static void usage()
    {
        System.err.println( "Usage: java com.progdan.pdf2txt.ExtractText [OPTIONS] <PDF file> [Text File]\n" +
            "  -password  <password>        Password to decrypt document\n" +
            "  -encoding  <output encoding> (ISO-8859-1,UTF-16BE,UTF-16LE,...)\n" +
            "  -console                     Send text to console instead of file\n" +
            "  -startPage <number>          The first page to start extraction(1 based)\n" +
            "  -endPage <number>            The last page to extract(inclusive)\n" +
            "  <PDF file>                   The PDF document to use\n" +
            "  [Text File]                  The file to write the text to\n"
            );
        System.exit( 1 );
    }
}
