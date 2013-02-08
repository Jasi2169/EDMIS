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
package com.progdan.pdf2txt.examples.fdf;

import java.io.FileInputStream;
import java.io.IOException;

import java.util.Iterator;
import java.util.List;

import com.progdan.pdf2txt.pdmodel.common.PDRectangle;

import com.progdan.pdf2txt.pdmodel.interactive.annotation.PDWidget;

import com.progdan.pdf2txt.pdmodel.interactive.form.PDAcroForm;
import com.progdan.pdf2txt.pdmodel.interactive.form.PDField;

import com.progdan.pdf2txt.exceptions.InvalidPasswordException;

import com.progdan.pdf2txt.pdmodel.PDDocument;
import com.progdan.pdf2txt.pdmodel.PDDocumentCatalog;



import com.progdan.pdf2txt.pdfparser.PDFParser;

/**
 * This example will take a PDF document and print all the fields from the file.
 *
 * @author  Ben Litchfield (ben@csh.rit.edu)
 * @version $Revision: 1.2 $
 */
public class PrintFields
{

    /**
     * This will print all the fields from the document.
     *
     * @param pdfDocument The PDF to get the fields from.
     */
    public void printFields( PDDocument pdfDocument )
    {
        PDDocumentCatalog docCatalog = pdfDocument.getDocumentCatalog();
        PDAcroForm acroForm = docCatalog.getAcroForm();
        List fields = acroForm.getFields();
        Iterator fieldsIter = fields.iterator();

        while( fieldsIter.hasNext() )
        {
            PDField field = (PDField)fieldsIter.next();
            PDWidget widget = field.getWidget();
            PDRectangle rectangle = null;
            if( widget != null )
            {
                rectangle = widget.getRectangle();
            }
            String outputString =
                field.getName() + "=" + field.toString() +
                " rect=" + rectangle +
                " type=" + field.getClass().getName();

            System.out.println( outputString );
        }
    }

    /**
     * This will read a PDF file and print out the form elements.
     * <br />
     * see usage() for commandline
     *
     * @param args command line arguments
     *
     * @throws IOException If there is an error importing the FDF document.
     */
    public static void main(String[] args) throws IOException
    {
        try
        {
            if( args.length != 1 )
            {
                usage();
            }
            else
            {
                PDDocument pdf = null;
                FileInputStream pdfStream = null;
                try
                {

                    PrintFields exporter = new PrintFields();
                    pdfStream = new FileInputStream( args[0] );
                    PDFParser pdfParser = new PDFParser( pdfStream );

                    pdfParser.parse();

                    pdf = pdfParser.getPDDocument();
                    if( pdf.isEncrypted() )
                    {
                        try
                        {
                            pdf.decrypt( "" );
                        }
                        catch( InvalidPasswordException e )
                        {
                            System.err.println( "Error: The document is encrypted." );
                            usage();
                        }
                    }
                    exporter.printFields( pdf );
                }
                finally
                {
                    if( pdfStream != null )
                    {
                        pdfStream.close();
                    }
                    if( pdf != null )
                    {
                        pdf.close();
                    }
                }
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
    private static void usage()
    {
        System.err.println( "usage: com.progdan.pdf2txt.examples.fdf.PrintFields <pdf-file>" );
    }
}
