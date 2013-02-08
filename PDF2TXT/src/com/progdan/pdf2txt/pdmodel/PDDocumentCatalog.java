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
package com.progdan.pdf2txt.pdmodel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.progdan.pdf2txt.cos.COSDictionary;
import com.progdan.pdf2txt.cos.COSName;
import com.progdan.pdf2txt.cos.COSString;

import com.progdan.pdf2txt.pdmodel.interactive.form.PDAcroForm;

import com.progdan.pdf2txt.pdmodel.interactive.viewerpreferences.PDViewerPreferences;

/**
 * This class represents the acroform of a PDF document.
 *
 * @author Ben Litchfield (ben@csh.rit.edu)
 * @version $Revision: 1.2 $
 */
public class PDDocumentCatalog
{
    private COSDictionary root;
    private PDDocument document;

    private PDAcroForm acroForm = null;

    /**
     * Constructor.
     *
     * @param doc The document that this catalog is part of.
     */
    public PDDocumentCatalog( PDDocument doc )
    {
        document = doc;
        root = new COSDictionary();
        root.setItem( COSName.TYPE, new COSString( "Catalog" ) );
        document.getDocument().getTrailer().setItem( COSName.ROOT, root );
    }

    /**
     * Constructor.
     *
     * @param doc The document that this catalog is part of.
     * @param rootDictionary The root dictionary that this object wraps.
     */
    public PDDocumentCatalog( PDDocument doc, COSDictionary rootDictionary )
    {
        document = doc;
        root = rootDictionary;
    }

    /**
     * This will get the documents acroform.  This will return null if
     * no acroform is part of the document.
     *
     * @return The documents acroform.
     */
    public PDAcroForm getAcroForm()
    {
        if( acroForm == null )
        {
            COSDictionary acroFormDic =
                (COSDictionary)root.getDictionaryObject( COSName.ACRO_FORM );
            if( acroFormDic == null )
            {
                acroForm = new PDAcroForm( document );
                root.setItem( COSName.ACRO_FORM, acroForm.getDictionary() );
            }
            else
            {
                acroForm = new PDAcroForm( document, acroFormDic );
            }
        }
        return acroForm;
    }

    /**
     * This will get the root node for the pages.
     *
     * @return The parent page node.
     */
    public PDPageNode getPages()
    {
        return new PDPageNode( (COSDictionary)root.getDictionaryObject( COSName.PAGES ) );
    }

    /**
     * The PDF document contains a hierarchical structure of PDPageNode and PDPages, which
     * is mostly just a way to store this information.  This method will return a flat list
     * of all PDPage objects in this document.
     *
     * @return A list of PDPage objects.
     */
    public List getAllPages()
    {
        List retval = new ArrayList();
        PDPageNode rootNode = getPages();
        getPageObjects( rootNode, retval );
        return retval;
    }

    /**
     * Get the viewer preferences associated with this document or null if they
     * do not exist.
     *
     * @return The document's viewer preferences.
     */
    public PDViewerPreferences getViewerPreferences()
    {
        PDViewerPreferences retval = null;
        COSDictionary dict = (COSDictionary)root.getDictionaryObject( "ViewerPreferences" );
        if( dict != null )
        {
            retval = new PDViewerPreferences( dict );
        }

        return retval;
    }

    /**
     * Set the viewer preferences.
     *
     * @param prefs The new viewer preferences.
     */
    public void setViewerPreferences( PDViewerPreferences prefs )
    {
        COSDictionary dict = null;
        if( prefs != null )
        {
            dict = prefs.getDictionary();
        }
        root.setItem( "ViewerPreferences", dict );
    }

    private void getPageObjects( PDPageNode node, List list )
    {
        List kids = node.getKids();
        Iterator kidIter = kids.iterator();
        while(kidIter.hasNext())
        {
            Object next = kidIter.next();
            if( next instanceof PDPage )
            {
                list.add( next );
            }
            else
            {
                PDPageNode nextNode = (PDPageNode)next;
                getPageObjects( nextNode, list );
            }
        }
    }
}
