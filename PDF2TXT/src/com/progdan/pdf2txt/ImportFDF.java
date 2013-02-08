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

import java.io.IOException;

import com.progdan.pdf2txt.pdmodel.PDDocument;
import com.progdan.pdf2txt.pdmodel.PDDocumentCatalog;

import com.progdan.pdf2txt.pdmodel.fdf.FDFDocument;

import com.progdan.pdf2txt.pdmodel.interactive.form.PDAcroForm;

/**
 * This example will take a PDF document and fill the fields with data from the
 * FDF fields.
 *
 * @author  Ben Litchfield (ben@csh.rit.edu)
 * @version $Revision: 1.1 $
 */
public class ImportFDF
{
    /**
     * Creates a new instance of ImportFDF.
     */
    public ImportFDF()
    {
    }

    /**
     * This will takes the values from the fdf document and import them into the
     * PDF document.
     *
     * @param pdfDocument The document to put the fdf data into.
     * @param fdfDocument The FDF document to get the data from.
     *
     * @throws IOException If there is an error setting the data in the field.
     */
    public void importFDF( PDDocument pdfDocument, FDFDocument fdfDocument ) throws IOException
    {
        PDDocumentCatalog docCatalog = pdfDocument.getDocumentCatalog();
        PDAcroForm acroForm = docCatalog.getAcroForm();
        acroForm.setCacheFields( true );
        acroForm.importFDF( fdfDocument );
    }

    /**
     * This will import an fdf document and write out another pdf.
     * <br />
     * see usage() for commandline
     *
     * @param args command line arguments
     *
     * @throws Exception If there is an error importing the FDF document.
     */
    public static void main(String[] args) throws Exception
    {
        ImportFDF importer = new ImportFDF();
        importer.importFDF( args );
    }

    private void importFDF( String[] args ) throws Exception
    {
        PDDocument pdf = null;
        FDFDocument fdf = null;

        try
        {
            if( args.length != 3 )
            {
                usage();
            }
            else
            {
                ImportFDF importer = new ImportFDF();

                pdf = PDDocument.load( args[0] );
                fdf = FDFDocument.load( args[1] );
                importer.importFDF( pdf, fdf );

                pdf.save( args[2] );
            }
        }
        finally
        {
            close( fdf );
            close( pdf );
        }
    }

    /**
     * This will print out a message telling how to use this example.
     */
    private static void usage()
    {
        System.err.println( "usage: com.progdan.pdf2txt.ImportFDF <pdf-file> <fdf-file> <output-file>" );
    }

    /**
     * Close the document.
     *
     * @param doc The doc to close.
     *
     * @throws IOException If there is an error closing the document.
     */
    public void close( FDFDocument doc ) throws IOException
    {
        if( doc != null )
        {
            doc.close();
        }
    }

    /**
     * Close the document.
     *
     * @param doc The doc to close.
     *
     * @throws IOException If there is an error closing the document.
     */
    public void close( PDDocument doc ) throws IOException
    {
        if( doc != null )
        {
            doc.close();
        }
    }
}
