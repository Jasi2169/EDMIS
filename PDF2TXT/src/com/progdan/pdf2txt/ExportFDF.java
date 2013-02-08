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

import com.progdan.pdf2txt.pdmodel.interactive.form.PDAcroForm;

import com.progdan.pdf2txt.pdmodel.fdf.FDFDocument;

/**
 * This example will take a PDF document and fill the fields with data from the
 * FDF fields.
 *
 * @author  Ben Litchfield (ben@csh.rit.edu)
 * @version $Revision: 1.1 $
 */
public class ExportFDF
{
    /**
     * Creates a new instance of ImportFDF.
     */
    public ExportFDF()
    {
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
        ExportFDF exporter = new ExportFDF();
        exporter.exportFDF( args );
    }

    private void exportFDF( String[] args ) throws Exception
    {
        PDDocument pdf = null;
        FDFDocument fdf = null;

        try
        {
            if( args.length != 2 )
            {
                usage();
            }
            else
            {
                pdf = PDDocument.load( args[0] );
                PDAcroForm form = pdf.getDocumentCatalog().getAcroForm();
                if( form == null )
                {
                    System.err.println( "Error: This PDF does not contain a form." );
                }
                else
                {
                    fdf = form.exportFDF();
                    fdf.save( args[1] );
                }
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
        System.err.println( "usage: com.progdan.pdf2txt.ExortFDF <pdf-file> <output-fdf-file>" );
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
