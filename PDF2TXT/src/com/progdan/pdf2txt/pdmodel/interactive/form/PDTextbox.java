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
import com.progdan.pdf2txt.cos.COSInteger;
import com.progdan.pdf2txt.cos.COSName;
import com.progdan.pdf2txt.cos.COSNumber;
import com.progdan.pdf2txt.cos.COSString;

import java.io.IOException;

/**
 * A class for handling the PDF field as a textbox.
 *
 * @author sug
 * @version $Revision: 1.2 $
 */
public class PDTextbox extends PDField
{
    /**
     * Ff    A set of flags specifying various characteristics of the field.
     */
    private COSInteger ff;
    /**
     * AP    An apearance dictionary specifying the appearance.
     */
    private COSDictionary ap;
    /**
     * DA    Default appearance.
     */
    private COSString da;

    private PDAppearance appearance;

    private PDAcroForm acroForm;

    /**
     * A Q value.
     */
    public static final int QUADDING_LEFT = 0;

    /**
     * A Q value.
     */
    public static final int QUADDING_CENTERED = 1;

    /**
     * A Q value.
     */
    public static final int QUADDING_RIGHT = 2;

    /**
     * @see com.progdan.pdf2txt.pdmodel.field.PDField#COSField(com.progdan.pdf2txt.cos.COSDictionary)
     *
     * @param theAcroForm The acroForm for this field.
     * @param field The field's dictionary.
     */
    public PDTextbox( PDAcroForm theAcroForm, COSDictionary field)
    {
        super( theAcroForm, field);

        // Get the Ff
        ff = (COSInteger) field.getDictionaryObject( COSName.getPDFName( "Ff" ) );

        // Get the AP
        COSArray kids = (COSArray) field.getDictionaryObject(COSName.KIDS);
        if (kids != null)
        {
            COSDictionary cd = (COSDictionary) kids.getObject(0);
            ap = (COSDictionary) cd.getDictionaryObject(COSName.getPDFName("AP"));
        }
        else
        {
           ap = (COSDictionary) field.getDictionaryObject(COSName.getPDFName("AP"));
        }
        da = (COSString) field.getDictionaryObject(COSName.getPDFName("DA"));
    }

    /**
     * @see com.progdan.pdf2txt.pdmodel.field.PDField#setValue(java.lang.String)
     *
     * @param value The new value for this text field.
     *
     * @throws IOException If there is an error calculating the appearance stream.
     */
    public void setValue(String value) throws IOException
    {
        COSString fieldValue = new COSString(value);
        getDictionary().setItem( COSName.getPDFName( "V" ), fieldValue );

        //hmm, not sure what the case where the DV gets set to the field
        //value, for now leave blank until we can come up with a case
        //where it needs to be in there
        //getDictionary().setItem( COSName.getPDFName( "DV" ), fieldValue );
        if(appearance == null)
        {
            this.appearance = new PDAppearance( getAcroForm(), this );
        }
        appearance.setAppearanceValue(value);
    }

    /**
     * getValue gets the fields value to as a string.
     *
     * @return The string value of this field.
     *
     * @throws IOException If there is an error getting the value.
     */
    public String getValue() throws IOException
    {
        return getDictionary().getString( "V" );
    }

    /**
     * @see com.progdan.pdf2txt.pdmodel.field.PDField#setReadonly()
     */
    public void setReadonly()
    {
        setFfFlag(1,true);
    }

    /**
     *
     * @return true if the field is marked as readonly
     */
    public boolean isReadonly()
    {
        return getFfFlag(1);
    }

    /**
     * @return true if the field is multiline
     */
    public boolean isMultiline()
    {
        boolean ml = getFfFlag(13);
        return ml;
    }

    /**
     * @return the DA element of the dictionary object
     */
    protected COSString getDefaultAppearance()
    {
        return da;
    }

    /**
     * This will get the 'quadding' or justification of the text to be displayed.
     * 0 - Left(default)<br/>
     * 1 - Centered<br />
     * 2 - Right<br />
     * Please see the QUADDING_CONSTANTS.
     *
     * @return The justification of the text strings.
     */
    public int getQ()
    {
        int retval = 0;
        COSNumber number = (COSNumber)getDictionary().getDictionaryObject( COSName.getPDFName( "Q" ) );
        if( number != null )
        {
            retval = number.intValue();
        }
        return retval;
    }

    /**
     * This will set the quadding/justification of the text.  See QUADDING constants.
     *
     * @param q The new text justification.
     */
    public void setQ( int q )
    {
        getDictionary().setItem( COSName.getPDFName( "Q" ), new COSInteger( q ) );
    }

}
