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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.progdan.pdf2txt.cos.COSArray;
import com.progdan.pdf2txt.cos.COSDictionary;
import com.progdan.pdf2txt.cos.COSName;

import com.progdan.pdf2txt.pdmodel.common.COSArrayList;

/**
 * A class for handling the PDF field as a Radio Collection.
 * This class automatically keeps track of the child radio buttons
 * in the collection.
 *
 * @see com.progdan.pdf2txt.pdmodel.field.PDRadioButton
 * @author sug
 * @version $Revision: 1.2 $
 */
public class PDRadioCollection extends PDChoiceButton
{
    /**
     * @see com.progdan.pdf2txt.pdmodel.field.PDField#PDField(com.progdan.pdf2txt.cos.COSDictionary)
     *
     * @param theAcroForm The acroForm for this field.
     * @param field The field that makes up the radio collection.
     */
    public PDRadioCollection( PDAcroForm theAcroForm, COSDictionary field)
    {
        super(theAcroForm,field);
    }

    /**
     * This setValue method iterates the collection of radiobuttons
     * and checks or unchecks each radiobutton according to the
     * given value.
     * If the value is not represented by any of the radiobuttons,
     * then none will be checked.
     *
     * @see com.progdan.pdf2txt.pdmodel.field.PDField#setValue(java.lang.String)
     */
    public void setValue(String value)
    {
        COSName val = COSName.getPDFName(value);
        List kids = getKids();
        for (int i = 0; i < kids.size(); i++)
        {
            PDRadioButton btn = (PDRadioButton)kids.get(i);
            if( btn.representsValue(val) )
            {
                btn.check();
            }
            else
            {
                btn.unCheck();
            }
        }
    }

    /**
     * getValue gets the fields value to as a string.
     *
     * @return The string value of this field.
     *
     * @throws IOException If there is an error getting the value.
     */
    public String getValue()throws IOException
    {
        String retval = null;
        List kids = getKids();
        for (int i = 0; i < kids.size(); i++)
        {
            PDRadioButton btn = (PDRadioButton)kids.get(i);
            //if( btn.representsValue(val) )
            if( btn.isChecked() )
            {
                retval = btn.getOnValue();
            }
            else
            {
                retval = btn.getOffValue();
            }
        }
        return retval;
    }


    /**
     * This will get all the widgets in this collection.
     *
     * @see getWidget
     * @return A list of PDWidget objects.
     */
    public List getKids()
    {
        List retval = null;
        COSArray kids = (COSArray)getDictionary().getDictionaryObject(COSName.KIDS);
        if( kids != null )
        {
            List kidsList = new ArrayList();
            for (int i = 0; i < kids.size(); i++)
            {
                kidsList.add( new PDRadioButton( (COSDictionary)kids.getObject(i) ) );
            }
            retval = new COSArrayList( kidsList, kids );
        }
        return retval;
    }
}
