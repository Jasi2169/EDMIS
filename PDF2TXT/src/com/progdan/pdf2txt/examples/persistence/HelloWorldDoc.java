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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.progdan.pdf2txt.cos.COSArray;
import com.progdan.pdf2txt.cos.COSDocument;
import com.progdan.pdf2txt.cos.COSDictionary;
import com.progdan.pdf2txt.cos.COSInteger;
import com.progdan.pdf2txt.cos.COSName;
import com.progdan.pdf2txt.cos.COSStream;





import com.progdan.pdf2txt.exceptions.COSVisitorException;
import com.progdan.pdf2txt.pdfwriter.COSWriter;

/**
 * This is an example that creates a simple document.
 *
 * The example is taken from the pdf file format specification.
 *
 * @author Michael Traut
 * @version $Revision: 1.2 $
 */
public class HelloWorldDoc
{
    /**
     * Constructor.
     */
    public HelloWorldDoc()
    {
        super();
    }

    /**
     * create the second sample document from the PDF file format specification.
     *
     * @param file The file to write the PDF to.
     * @param message The message to write in the file.
     *
     * @throws IOException If there is an error writing the data.
     * @throws COSVisitorException If there is an error generating the PDF document.
     */
    public void doIt( String file, String message) throws IOException, COSVisitorException
    {
        // the document
        COSDocument doc = null;
        OutputStream os = null;
        COSWriter writer = null;
        try
        {
            doc = new COSDocument();

            // the pages dict
            COSDictionary pages = new COSDictionary();
            pages.setItem(COSName.TYPE, COSName.PAGES);

            // the pagesarray
            COSArray pagesArray = new COSArray();

            // a page
            COSDictionary page = new COSDictionary();
            page.setItem(COSName.TYPE, COSName.PAGE);
            page.setItem(COSName.PARENT, pages);
            COSArray mediaBox = new COSArray();
            mediaBox.add( new COSInteger(0));
            mediaBox.add( new COSInteger(0));
            mediaBox.add( new COSInteger(612));
            mediaBox.add( new COSInteger(792));
            page.setItem(COSName.getPDFName("MediaBox"), mediaBox);
            byte[] bytes = ("BT /F1 24 Tf 100 100 Td (" + message + ") Tj ET").getBytes();
            COSDictionary streamDict = new COSDictionary();
            streamDict.setItem(COSName.LENGTH, new COSInteger(bytes.length));
            COSStream contents = new COSStream(streamDict,doc.getScratchFile());
            OutputStream output = contents.createUnfilteredStream();
            output.write(bytes);
            output.close();
            page.setItem(COSName.CONTENTS, contents);
            COSDictionary resources = new COSDictionary();
            // the procset
            COSArray procSet = new COSArray();
            procSet.add(COSName.getPDFName("PDF"));
            procSet.add(COSName.getPDFName("Text"));
            resources.setItem(COSName.getPDFName("ProcSet"), procSet);
            // the font
            COSDictionary font = new COSDictionary();
            font.setItem(COSName.TYPE, COSName.FONT);
            font.setItem(COSName.SUBTYPE, COSName.getPDFName("Type1"));
            font.setItem(COSName.getPDFName("Name"), COSName.getPDFName("F1"));
            font.setItem(COSName.getPDFName("BaseFont"), COSName.getPDFName("Helvetica"));
            COSDictionary fontDict = new COSDictionary();
            fontDict.setItem(COSName.getPDFName("F1"), font);
            resources.setItem(COSName.getPDFName("Font"), fontDict);
            page.setItem(COSName.RESOURCES, resources);

            // now add the page
            pagesArray.add(page);
            pages.setItem(COSName.KIDS, pagesArray);
            pages.setItem(COSName.COUNT, new COSInteger(pagesArray.size()));

            // the catalog dict
            COSDictionary catalog = new COSDictionary();
            catalog.setItem(COSName.TYPE, COSName.CATALOG);
            catalog.setItem(COSName.PAGES, pages);

            //The document trailer
            COSDictionary trailer = new COSDictionary();
            trailer.setItem( COSName.ROOT, catalog );

            doc.setTrailer( trailer );

            os = new FileOutputStream( file );
            writer = new COSWriter(os);

            writer.write(doc);
        }
        finally
        {
            doc.close();
            os.close();
            writer.close();
        }
    }

    /**
     * This will create a hello world PDF document.
     * <br />
     * see usage() for commandline
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args)
    {
        HelloWorldDoc app = new HelloWorldDoc();
        try
        {
            if( args.length != 2 )
            {
                app.usage();
            }
            else
            {
                app.doIt( args[0], args[1] );
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * This will print out a message telling how to use this example.
     */
    private void usage()
    {
        System.err.println( "usage: " + this.getClass().getName() + " <output-file> <Message>" );
    }
}
