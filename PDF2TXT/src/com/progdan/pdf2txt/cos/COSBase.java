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
package com.progdan.pdf2txt.cos;

import com.progdan.pdf2txt.filter.FilterManager;

import com.progdan.pdf2txt.exceptions.COSVisitorException;

/**
 * The base object that all objects in the PDF document will extend.
 *
 * @author Ben Litchfield (ben@csh.rit.edu)
 * @version $Revision: 1.2 $
 */
public abstract class COSBase
{
    /**
     * Constructor.
     */
    public COSBase()
    {
    }

    /**
     * This will get the filter manager to use to filter streams.
     *
     * @return The filter manager.
     */
    public FilterManager getFilterManager()
    {
        /**
         * @todo move this to PDFdocument or something better
         */
        return new FilterManager();
    }



    /**
     * visitor pattern double dispatch method.
     *
     * @param visitor The object to notify when visiting this object.
     * @return any object, depending on the visitor implementation, or null
     * @throws COSVisitorException If an error occurs while visiting this object.
     */
    public abstract Object accept(ICOSVisitor visitor) throws COSVisitorException;

    /**
     * Add a COSBase to a document.
     * This method is called in the COSDocument when it
     * is advised to create a COSObject to include in the document.
     *
     * this method guarantees that every recursively contained COSObject is added to the
     * document properly. no check is done if the COSObjects are contained in only one
     * COSDocument at a time. this is left to the user to create proper clones of
     * components contained in other documents.
     *
     * @param doc the document where the object is to be inserted
     *
     */
    protected void addTo(COSDocument doc)
    {
        // do nothing
    }
}
