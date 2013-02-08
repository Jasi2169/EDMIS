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
package com.progdan.pdf2txt.examples.persistence;

import java.io.IOException;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.FileInputStream;

import java.util.Iterator;
import java.util.List;

import com.progdan.pdf2txt.pdfparser.PDFParser;

import com.progdan.pdf2txt.pdfwriter.COSWriter;

import com.progdan.pdf2txt.cos.COSBase;
import com.progdan.pdf2txt.cos.COSDictionary;
import com.progdan.pdf2txt.cos.COSName;
import com.progdan.pdf2txt.cos.COSArray;
import com.progdan.pdf2txt.cos.COSNumber;
import com.progdan.pdf2txt.cos.COSInteger;

import com.progdan.pdf2txt.pdmodel.PDDocument;
import com.progdan.pdf2txt.pdmodel.PDPage;

import com.progdan.pdf2txt.exceptions.COSVisitorException;

/**
 * This example concatenates two documents and writes the result.
 *
 * @author Michael Traut
 * @version $Revision: 1.2 $
 */
public class AppendDoc
{
    /**
     * Constructor.
     */
    public AppendDoc()
    {
        super();
    }
    /**
     * append all pages from source to destination.
     *
     * todo: this method will go to the pdfmodel package one day
     *
     * @param destination the document to receive the pages
     * @param source the document originating the new pages
     *
     * @throws IOException If there is an error accessing data from either document.
     */
    public void appendDocument(PDDocument destination, PDDocument source) throws IOException
    {

        COSDictionary destTrailer = destination.getDocument().getTrailer();
        COSDictionary destRoot = (COSDictionary)destTrailer.getDictionaryObject( COSName.ROOT );

        COSDictionary srcTrailer = source.getDocument().getTrailer();
        COSDictionary srcRoot = (COSDictionary)srcTrailer.getDictionaryObject( COSName.ROOT );

        COSName openAction = COSName.getPDFName( "OpenAction" );
        if( destRoot.getDictionaryObject( openAction ) == null )
        {
            COSBase open = (COSBase)srcRoot.getDictionaryObject( openAction );
            if( open != null )
            {
                destRoot.setItem( openAction, open );
            }
        }

        COSName acroForm = COSName.getPDFName( "AcroForm" );
        if( destRoot.getDictionaryObject( acroForm ) == null )
        {
            COSBase form = (COSBase)srcRoot.getDictionaryObject( acroForm );
            if( form != null )
            {
                destRoot.setItem( acroForm, form );
            }
        }

        COSName threads = COSName.getPDFName( "Threads" );
        COSArray destThreads = (COSArray)destRoot.getDictionaryObject( threads );
        COSArray srcThreads = (COSArray)srcRoot.getDictionaryObject( threads );
        if( srcThreads != null )
        {
            if( destThreads == null )
            {
                destRoot.setItem( threads, srcThreads );
            }
            else
            {
                destThreads.addAll( srcThreads );
            }
        }

        COSName names = COSName.getPDFName( "Names" );
        COSDictionary destNames = (COSDictionary)destRoot.getDictionaryObject( names );
        COSDictionary srcNames = (COSDictionary)srcRoot.getDictionaryObject( names );
        if( srcNames != null )
        {
            if( destNames == null )
            {
                destRoot.setItem( names, srcNames );
            }
            else
            {
                //warning, potential for collision here!!
                destNames.addAll( srcNames );
            }
        }

        COSName pageLabels = COSName.getPDFName( "PageLabels" );
        COSDictionary destLabels = (COSDictionary)destRoot.getDictionaryObject( pageLabels );
        COSDictionary srcLabels = (COSDictionary)srcRoot.getDictionaryObject( pageLabels );
        if( srcLabels != null )
        {
            int destPageCount = destination.getPageCount();
            COSArray destNums = null;
            if( destLabels == null )
            {
                destLabels = new COSDictionary();
                destNums = new COSArray();
                destLabels.setItem( COSName.getPDFName( "Nums" ), destNums );
                destRoot.setItem( pageLabels, destLabels );
            }
            else
            {
                destNums = (COSArray)destLabels.getDictionaryObject( COSName.getPDFName( "Nums" ) );
            }
            COSArray srcNums = (COSArray)srcLabels.getDictionaryObject( COSName.getPDFName( "Nums" ) );
            for( int i=0; i<srcNums.size(); i+=2 )
            {
                COSNumber labelIndex = (COSNumber)srcNums.get( i );
                long labelIndexValue = labelIndex.intValue();
                destNums.add( new COSInteger( labelIndexValue + destPageCount ) );
                destNums.add( srcNums.get( i+1 ) );
            }

        }

        //finally append the pages
        List pages = source.getDocumentCatalog().getAllPages();
        Iterator pageIter = pages.iterator();
        while( pageIter.hasNext() )
        {
            PDPage page = (PDPage)pageIter.next();
            COSDictionary pageDictionary = page.getCOSDictionary();
            destination.addPage( page );
        }

    }

    /**
     * concat two pdf documents.
     *
     * @param in1 The first template file
     * @param in2 The second template file
     * @param out The created fiel with all pages from document one and document two
     *
     * @throws IOException If there is an error writing the data.
     * @throws COSVisitorException If there is an error generating the PDF document.
     */
    public void doIt(String in1, String in2, String out) throws IOException, COSVisitorException
    {
        InputStream is1 = null;
        PDDocument doc1 = null;

        InputStream is2 = null;
        PDDocument doc2 = null;
        OutputStream os = null;
        COSWriter writer = null;
        try
        {
            is1 = new FileInputStream(in1);
            PDFParser parser1 = new PDFParser(is1);
            parser1.parse();
            doc1 = parser1.getPDDocument();

            is2 = new FileInputStream(in2);
            PDFParser parser2 = new PDFParser(is2);
            parser2.parse();
            doc2 = parser2.getPDDocument();

            appendDocument(doc1, doc2);

            os = new FileOutputStream(out);
            writer = new COSWriter(os);
            writer.write(doc1);
        }
        finally
        {
            close( is1 );
            close( doc1 );

            close( is2 );
            close( doc2 );

            close( writer );
            close( os );
        }
    }

    private void close( InputStream is ) throws IOException
    {
        if( is != null )
        {
            is.close();
        }
    }

    private void close( OutputStream os ) throws IOException
    {
        if( os != null )
        {
            os.close();
        }
    }

    private void close( COSWriter writer ) throws IOException
    {
        if( writer != null )
        {
            writer.close();
        }
    }

    private void close( PDDocument doc ) throws IOException
    {
        if( doc != null )
        {
            doc.close();
        }
    }

    /**
     * This will concat two pdf documents.
     * <br />
     * see usage() for commandline
     *
     * @param args command line arguments
     */
    public static void main(String[] args)
    {
        AppendDoc app = new AppendDoc();
        try
        {
            if( args.length != 3 )
            {
                app.usage();
            }
            else
            {
                app.doIt( args[0], args[1], args[2]);
            }
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
    }
    /**
     * This will print out a message telling how to use this example.
     */
    private void usage()
    {
        System.err.println( "usage: " + this.getClass().getName() + " <input-file1> <input-file2> <output-file>" );
    }
}
