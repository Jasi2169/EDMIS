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
package com.progdan.pdf2txt.pdmodel.graphics;

import com.progdan.pdf2txt.cos.COSArray;
import com.progdan.pdf2txt.cos.COSBase;
import com.progdan.pdf2txt.cos.COSInteger;
import com.progdan.pdf2txt.cos.COSNumber;

import com.progdan.pdf2txt.pdmodel.common.COSArrayList;
import com.progdan.pdf2txt.pdmodel.common.COSObjectable;

import java.util.List;

/**
 * This class represents the line dash pattern for a graphics state.  See PDF
 * Reference 1.5 section 4.3.2
 *
 * @author Ben Litchfield (ben@csh.rit.edu)
 * @version $Revision: 1.2 $
 */
public class PDLineDashPattern implements COSObjectable
{
    private COSArray lineDashPattern = null;

    /**
     * Creates a blank line dash pattern.  With no dashes and a phase of 0.
     */
    public PDLineDashPattern()
    {
        lineDashPattern = new COSArray();
        lineDashPattern.add( new COSArray() );
        lineDashPattern.add( new COSInteger( 0 ) );
    }

    /**
     * Constructs a line dash pattern from an existing array.
     *
     * @param ldp The existing line dash pattern.
     */
    public PDLineDashPattern( COSArray ldp )
    {
        lineDashPattern = ldp;
    }

    /**
     * @see COSObjectable#getCOSObject()
     */
    public COSBase getCOSObject()
    {
        return lineDashPattern;
    }

    /**
     * This will get the line dash pattern phase.  The dash phase specifies the
     * distance into the dash pattern at which to start the dash.
     *
     * @return The line dash pattern phase.
     */
    public int getPhaseStart()
    {
        COSNumber phase = (COSNumber)lineDashPattern.get( 1 );
        return phase.intValue();
    }

    /**
     * This will set the line dash pattern phase.
     *
     * @param phase The new line dash patter phase.
     */
    public void setPhaseStart( int phase )
    {
        lineDashPattern.set( 1, new COSInteger( phase ) );
    }

    /**
     * This will return a list of java.lang.Integer objects that represent the line
     * dash pattern appearance.
     *
     * @return The line dash pattern.
     */
    public List getDashPattern()
    {
        COSArray dashPatterns = (COSArray)lineDashPattern.get( 0 );
        return COSArrayList.convertIntegerCOSArrayToList( dashPatterns );
    }

    /**
     * This will replace the existing line dash pattern.
     *
     * @param dashPattern A list of java.lang.Integer objects.
     */
    public void setDashPattern( List dashPattern )
    {
        lineDashPattern.set( 0, COSArrayList.converterToCOSArray( dashPattern ) );
    }
}
