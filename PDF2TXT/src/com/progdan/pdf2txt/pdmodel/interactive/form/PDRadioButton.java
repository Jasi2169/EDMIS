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

import java.util.Iterator;

import com.progdan.pdf2txt.cos.COSBase;
import com.progdan.pdf2txt.cos.COSDictionary;
import com.progdan.pdf2txt.cos.COSName;

/**
 * A PDRadioButton is used as a part of PDRadioCollection.
 * The COSRatioButton represents a single radiobutton element
 * in the PDF.
 *
 * @author sug
 * @version $Revision: 1.2 $
 */
public class PDRadioButton
{
    private static final COSName KEY = COSName.getPDFName("AS");
    private static final COSName OFF_VALUE = COSName.getPDFName("Off");

    private COSDictionary obj;

    /**
     * Constructs a COSRadioButton from a child of the
     * COSRadioCollection.
     *
     * @param objValue The child of a radiobutton collection.
     */
    public PDRadioButton(COSDictionary objValue)
    {
        obj = objValue;
    }

    /**
     * This will tell if this radio button is currently checked or not.
     *
     * @return true If the radio button is checked.
     */
    public boolean isChecked()
    {
        boolean retval = false;
        String value = getOnValue();
        COSName radioValue = (COSName)obj.getDictionaryObject( KEY );
        if( radioValue != null && value != null && radioValue.getName().equals( value ) )
        {
            retval = true;
        }

        return retval;
    }

    /**
     * Checks the radiobutton.
     */
    public void check()
    {
        obj.setName( KEY, getOnValue() );
    }

    /**
     * Unchecks the radiobutton.
     */
    public void unCheck()
    {
        obj.setName( KEY, getOffValue() );
    }

    /**
     * This will get the value of the radio button.
     *
     * @return The value of the radio button.
     */
    public String getOffValue()
    {
        return OFF_VALUE.getName();
    }

    /**
     * This will get the value of the radio button.
     *
     * @return The value of the radio button.
     */
    public String getOnValue()
    {
        String value = null;
        COSDictionary ap = (COSDictionary) obj.getDictionaryObject(COSName.getPDFName("AP"));
        COSBase n = ap.getDictionaryObject(COSName.getPDFName("N"));

        //N can be a COSDictionary or a COSStream
        if( n instanceof COSDictionary )
        {
            Iterator li = ((COSDictionary)n).keyList().iterator();
            while( li.hasNext() )
            {
                Object key = li.next();
                if( !key.equals( OFF_VALUE) )
                {
                    value = ((COSName)key).getName();
                }
            }
        }
        return value;
    }

    /**
     * This method determins if the radiobutton represents
     * a given value.
     *
     * @param repValue the value to check for
     * @return the result of the determination
     */
    public boolean representsValue(COSName repValue)
    {
        return repValue.getName().equals( getOnValue() );
    }
}
