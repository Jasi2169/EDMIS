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
package com.progdan.pdf2txt.searchengine.lucene;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

import java.net.URL;
import java.net.URLConnection;

import java.util.Date;

import org.apache.lucene.document.DateField;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import com.progdan.pdf2txt.pdmodel.PDDocument;
import com.progdan.pdf2txt.pdmodel.PDDocumentInformation;

import com.progdan.pdf2txt.exceptions.CryptographyException;
import com.progdan.pdf2txt.exceptions.InvalidPasswordException;

import com.progdan.pdf2txt.util.PDFTextStripper;

/**
 * This class is used to create a document for the lucene search engine.
 * This should easily plug into the IndexHTML or IndexFiles that comes with
 * the lucene project.
 *
 * @author  Ben Litchfield
 * @version $Revision: 1.2 $
 */
public final class LucenePDFDocument
{
    private static final char FILE_SEPARATOR = System.getProperty("file.separator").charAt(0);


    /**
     * private constructor because there are only static methods.
     */
    private LucenePDFDocument()
    {
    }

    /**
     * This will get a lucene document from a PDF file.
     *
     * @param file The file to get the document for.
     *
     * @return The lucene document.
     *
     * @throws IOException If there is an error parsing or indexing the document.
     */
    public static Document getDocument( File file ) throws IOException
    {
        Document document = new Document();

        // Add the url as a field named "url".  Use an UnIndexed field, so
        // that the url is just stored with the document, but is not searchable.
        document.add( Field.UnIndexed("path", file.getPath() ) );
        document.add(Field.UnIndexed("url", file.getPath().replace(FILE_SEPARATOR, '/')));

        // Add the last modified date of the file a field named "modified".  Use a
        // Keyword field, so that it's searchable, but so that no attempt is made
        // to tokenize the field into words.
        document.add(Field.Keyword("modified", DateField.timeToString( file.lastModified() )));

        String uid = file.getPath().replace(FILE_SEPARATOR, '\u0000') + "\u0000" +
               DateField.timeToString(file.lastModified() );

        // Add the uid as a field, so that index can be incrementally maintained.
        // This field is not stored with document, it is indexed, but it is not
        // tokenized prior to indexing.
        document.add(new Field("uid", uid, false, true, false));

        FileInputStream input = null;
        try
        {
            input = new FileInputStream( file );
            addContent( document, input, file.getPath() );
        }
        finally
        {
            if( input != null )
            {
                input.close();
            }
        }


        // return the document

        return document;
    }

    /**
     * This will get a lucene document from a PDF file.
     *
     * @param url The file to get the document for.
     *
     * @return The lucene document.
     *
     * @throws IOException If there is an error parsing or indexing the document.
     */
    public static Document getDocument( URL url ) throws IOException
    {
        Document document = new Document();
        URLConnection connection = url.openConnection();
        connection.connect();
        // Add the url as a field named "url".  Use an UnIndexed field, so
        // that the url is just stored with the document, but is not searchable.
        document.add( Field.UnIndexed("url", url.toExternalForm() ) );

        // Add the last modified date of the file a field named "modified".  Use a
        // Keyword field, so that it's searchable, but so that no attempt is made
        // to tokenize the field into words.
        document.add(Field.Keyword("modified", DateField.timeToString( connection.getLastModified())));

        String uid = url.toExternalForm().replace(FILE_SEPARATOR, '\u0000') + "\u0000" +
               DateField.timeToString( connection.getLastModified() );

        // Add the uid as a field, so that index can be incrementally maintained.
        // This field is not stored with document, it is indexed, but it is not
        // tokenized prior to indexing.
        document.add(new Field("uid", uid, false, true, false));

        InputStream input = null;
        try
        {
            input = connection.getInputStream();
            addContent( document, input,url.toExternalForm() );
        }
        finally
        {
            if( input != null )
            {
                input.close();
            }
        }

        // return the document
        return document;
    }

    /**
     * This will add the contents to the lucene document.
     *
     * @param document The document to add the contents to.
     * @param is The stream to get the contents from.
     * @param documentLocation The location of the document, used just for debug messages.
     *
     * @throws IOException If there is an error parsing the document.
     */
    private static void addContent( Document document, InputStream is, String documentLocation ) throws IOException
    {
        PDDocument pdfDocument = null;
        try
        {
            pdfDocument = PDDocument.load( is );


            if( pdfDocument.isEncrypted() )
            {
                //Just try using the default password and move on
                pdfDocument.decrypt( "" );
            }

            //create a tmp output stream with the size of the content.
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            OutputStreamWriter writer = new OutputStreamWriter( out );
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.writeText( pdfDocument, writer );
            writer.close();

            byte[] contents = out.toByteArray();
            InputStreamReader input = new InputStreamReader( new ByteArrayInputStream( contents ) );
            // Add the tag-stripped contents as a Reader-valued Text field so it will
            // get tokenized and indexed.
            document.add(Field.Text("contents", input ));

            PDDocumentInformation info = pdfDocument.getDocumentInformation();
            if( info.getAuthor() != null )
            {
                document.add(Field.Text( "Author", info.getAuthor() ) );
            }
            if( info.getCreationDate() != null )
            {
                Date date = info.getCreationDate().getTime();
                //for some reason lucene cannot handle dates before the epoch
                //and throws a nasty RuntimeException, so we will check and
                //verify that this does not happen
                if( date.getTime() >= 0 )
                {
                    document.add(Field.Text("CreationDate", DateField.dateToString( date ) ) );
                }
            }
            if( info.getCreator() != null )
            {
                document.add( Field.Text( "Creator", info.getCreator() ) );
            }
            if( info.getKeywords() != null )
            {
                document.add( Field.Text( "Keywords", info.getKeywords() ) );
            }
            if( info.getModificationDate() != null )
            {
                Date date = info.getModificationDate().getTime();
                //for some reason lucene cannot handle dates before the epoch
                //and throws a nasty RuntimeException, so we will check and
                //verify that this does not happen
                if( date.getTime() >= 0 )
                {
                    document.add(Field.Text("ModificationDate", DateField.dateToString( date ) ) );
                }
            }
            if( info.getProducer() != null )
            {
                document.add( Field.Text( "Producer", info.getProducer() ) );
            }
            if( info.getSubject() != null )
            {
                document.add( Field.Text( "Subject", info.getSubject() ) );
            }
            if( info.getTitle() != null )
            {
                document.add( Field.Text( "Title", info.getTitle() ) );
            }
            if( info.getTrapped() != null )
            {
                document.add( Field.Text( "Trapped", info.getTrapped() ) );
            }

            int summarySize = Math.min( contents.length, 500 );
            // Add the summary as an UnIndexed field, so that it is stored and returned
            // with hit documents for display.
            document.add(Field.UnIndexed("summary", new String( contents, 0, summarySize ) ) );
        }
        catch( CryptographyException e )
        {
            throw new IOException( "Error decrypting document(" + documentLocation + "): " + e );
        }
        catch( InvalidPasswordException e )
        {
            //they didn't suppply a password and the default of "" was wrong.
            throw new IOException( "Error: The document(" + documentLocation +
                                    ") is encrypted and will not be indexed." );
        }
        finally
        {
            if( pdfDocument != null )
            {
                pdfDocument.close();
            }
        }
    }

    /**
     * This will test creating a document.
     *
     * usage: java pdfparser.searchengine.lucene.LucenePDFDocument &lt;pdf-document&gt;
     *
     * @param args command line arguments.
     *
     * @throws IOException If there is an error.
     */
    public static void main( String[] args ) throws IOException
    {
        if( args.length != 1 )
        {
            System.err.println( "usage: java com.progdan.pdf2txt.searchengine.lucene.LucenePDFDocument <pdf-document>" );
            System.exit( 1 );
        }
        System.out.println( "Document=" + getDocument( new File( args[0] ) ) );
    }
}
