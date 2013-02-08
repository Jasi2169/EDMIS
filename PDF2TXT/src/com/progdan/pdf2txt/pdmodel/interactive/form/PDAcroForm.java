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
package com.progdan.pdf2txt.pdmodel.interactive.form;

import com.progdan.pdf2txt.cos.COSArray;
import com.progdan.pdf2txt.cos.COSDictionary;
import com.progdan.pdf2txt.cos.COSName;
import com.progdan.pdf2txt.cos.COSString;

import com.progdan.pdf2txt.pdmodel.PDDocument;
import com.progdan.pdf2txt.pdmodel.PDResources;

import com.progdan.pdf2txt.pdmodel.fdf.FDFDictionary;
import com.progdan.pdf2txt.pdmodel.fdf.FDFDocument;
import com.progdan.pdf2txt.pdmodel.fdf.FDFCatalog;
import com.progdan.pdf2txt.pdmodel.fdf.FDFField;

import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This class represents the acroform of a PDF document.
 *
 * @author Ben Litchfield (ben@csh.rit.edu)
 * @version $Revision: 1.2 $
 */
public class PDAcroForm
{
    private COSDictionary acroForm;
    private PDDocument document;

    private Map fieldCache;

    /**
     * Constructor.
     *
     * @param doc The document that this form is part of.
     */
    public PDAcroForm( PDDocument doc )
    {
        document = doc;
        acroForm = new COSDictionary();
        COSArray fields = new COSArray();
        acroForm.setItem( COSName.getPDFName( "Fields" ), fields );
    }

    /**
     * Constructor.
     *
     * @param doc The document that this form is part of.
     * @param form The existing acroForm.
     */
    public PDAcroForm( PDDocument doc, COSDictionary form )
    {
        document = doc;
        acroForm = form;
    }

    /**
     * This will get the document associated with this form.
     *
     * @return The PDF document.
     */
    public PDDocument getDocument()
    {
        return document;
    }

    /**
     * This will get the dictionary that this form wraps.
     *
     * @return The dictionary for this form.
     */
    public COSDictionary getDictionary()
    {
        return acroForm;
    }

    /**
     * This method will import an entire FDF document into the PDF document
     * that this acroform is part of.
     *
     * @param fdf The FDF document to import.
     *
     * @throws IOException If there is an error doing the import.
     */
    public void importFDF( FDFDocument fdf ) throws IOException
    {
        List fields = fdf.getCatalog().getFDF().getFields();
        if( fields != null )
        {
            for( int i=0; i<fields.size(); i++ )
            {
                FDFField fdfField = (FDFField)fields.get( i );
                PDField docField = getField( fdfField.getPartialFieldName() );
                if( docField != null )
                {
                    docField.importFDF( fdfField );
                }
            }
        }
    }

    /**
     * This will export all FDF form data.
     *
     * @return An FDF document used to export the document.
     * @throws IOException If there is an error when exporting the document.
     */
    public FDFDocument exportFDF() throws IOException
    {
        FDFDocument fdf = new FDFDocument();
        FDFCatalog catalog = fdf.getCatalog();
        FDFDictionary fdfDict = new FDFDictionary();
        catalog.setFDF( fdfDict );

        List fdfFields = new ArrayList();
        List fields = getFields();
        Iterator fieldIter = fields.iterator();
        while( fieldIter.hasNext() )
        {
            PDField docField = (PDField)fieldIter.next();
            Object fieldValue = docField.getValue();
            if( fieldValue != null )
            {
                FDFField fdfField = new FDFField();
                fdfField.setPartialFieldName( docField.getName() );
                fdfField.setValue( fieldValue );
                fdfFields.add( fdfField );
            }
        }
        fdfDict.setID( document.getDocument().getDocumentID() );
        if( fdfFields.size() > 0 )
        {
            fdfDict.setFields( fdfFields );
        }
        return fdf;
    }

    /**
     * This will return all of the fields in the document.  The type
     * will be a com.progdan.pdf2txt.pdmodel.field.PDField.
     *
     * @return A list of all the fields.
     */
    public List getFields()
    {
        COSArray fields =
            (COSArray) acroForm.getDictionaryObject(
                COSName.getPDFName("Fields"));

        List retval = new ArrayList();
        for (int i = 0; i < fields.size(); i++)
        {
            COSDictionary element = (COSDictionary) fields.getObject(i);
            if (element != null)
            {
                PDField field = PDFieldFactory.createField( this, element );
                if( field != null )
                {
                    retval.add(field);
                }
            }
        }
        return retval;
    }

    /**
     * This will tell this form to cache the fields into a Map structure
     * for fast access via the getField method.  The default is false.  You would
     * want this to be false if you were changing the COSDictionary behind the scenes,
     * otherwise setting this to true is acceptable.
     *
     * @param cache A boolean telling if we should cache the fields.
     */
    public void setCacheFields( boolean cache )
    {
        if( cache )
        {
            fieldCache = new HashMap();
            List fields = getFields();
            Iterator fieldIter = fields.iterator();
            while( fieldIter.hasNext() )
            {
                PDField next = (PDField)fieldIter.next();
                fieldCache.put( next.getName(), next );
            }
        }
        else
        {
            fieldCache = null;
        }
    }

    /**
     * This will tell if this acro form is caching the fields.
     *
     * @return true if the fields are being cached.
     */
    public boolean isCachingFields()
    {
        return fieldCache != null;
    }

    /**
     * This will get a field by name, possibly using the cache if setCache is true.
     *
     * @param name The name of the field to get.
     *
     * @return The field with that name of null if one was not found.
     */
    public PDField getField( String name )
    {
        PDField retval = null;
        if( fieldCache != null )
        {
            retval = (PDField)fieldCache.get( name );
        }
        else
        {
            COSArray fields =
                (COSArray) acroForm.getDictionaryObject(
                    COSName.getPDFName("Fields"));

            for (int i = 0; i < fields.size() && retval == null; i++)
            {
                COSDictionary element = (COSDictionary) fields.getObject(i);
                if( element != null )
                {
                    COSString fieldName =
                        (COSString)element.getDictionaryObject( COSName.getPDFName( "T" ) );
                    if( fieldName.getString().equals( name ) )
                    {
                        retval = PDFieldFactory.createField( this, element );
                    }
                }
            }
        }
        return retval;
    }

    /**
     * This will get the default resources for the acro form.
     *
     * @return The default resources.
     */
    public PDResources getDefaultResources()
    {
        PDResources retval = null;
        COSDictionary dr = (COSDictionary)acroForm.getDictionaryObject( COSName.getPDFName( "DR" ) );
        if( dr != null )
        {
            retval = new PDResources( dr );
        }
        return retval;
    }

    /**
     * This will set the default resources for the acroform.
     *
     * @param dr The new default resources.
     */
    public void setDefaultResources( PDResources dr )
    {
        COSDictionary drDict = null;
        if( dr != null )
        {
            drDict = dr.getCOSDictionary();
        }
        acroForm.setItem( COSName.getPDFName( "DR" ), drDict );
    }
}
