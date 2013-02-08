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

/**
 * This is the Factory for creating and returning the correct
 * field elements.
 *
 * @author sug
 * @version $Revision: 1.2 $
 */
public class PDFieldFactory
{
    private static final int RADIO_BITMASK = 32768;
    private static final int PUSHBUTTON_BITMASK = 65536;
    private static final int RADIOS_IN_UNISON_BITMASK = 33554432;

    private static final COSName BTN = COSName.getPDFName("Btn");
    private static final COSName TX = COSName.getPDFName("Tx");
    private static final COSName CH = COSName.getPDFName("Ch");
    private static final COSName SIG = COSName.getPDFName("Sig");

    /**
     * Utility class so no constructor.
     */
    private PDFieldFactory()
    {
    }

    /**
     * This method creates a COSField subclass from the given field.
     * The field is a PDF Dictionary object that must represent a
     * field element. - othewise null is returned
     *
     * @param acroForm The form that the field will be part of.
     * @param field The dictionary representing a field element
     *
     * @return a subclass to COSField according to the kind of field passed to createField
     */
    public static PDField createField( PDAcroForm acroForm, COSDictionary field)
    {
        if( isButton(field) )
        {
            int flags = getFieldFlags( field );
            //BJL, I have found that the radio flag bit is not always set
            //and that sometimes there is just a kids dictionary.
            //so, if there is a kids dictionary then it must be a radio button
            //group.
            COSArray kids = (COSArray)field.getDictionaryObject( COSName.getPDFName( "Kids" ) );
            if( kids != null || isRadio(flags) )
            {
                return new PDRadioCollection( acroForm, field );
            }
            else if( isPushButton( flags ) )
            {
                return new PDPushButton( acroForm, field );
            }
            else
            {
                return new PDCheckbox( acroForm, field );
            }

        }
        else if (isChoiceField(field))
        {
            return new PDChoiceField( acroForm, field );
        }
        else if (isTextbox(field))
        {
            return new PDTextbox( acroForm, field );
        }
        else if( isSignature( field ) )
        {
            return new PDSignature( acroForm, field );
        }
        else
        {
            System.err.println( "Error finding field type for " + field );
            return null;
        }
    }

    /**
     * This will get the field flags for a field.
     *
     * @return The fields flags.
     */
    private static int getFieldFlags( COSDictionary field )
    {
        //0 is default
        int retval = 0;
        COSInteger ff = (COSInteger)field.getDictionaryObject( COSName.getPDFName( "Ff" ) );
        if( ff != null )
        {
            retval = (int)ff.intValue();
        }
        return retval;
    }

    /**
     * This method determines if the given
     * field is a radiobutton collection.
     *
     * @param flags The field flags.
     *
     * @return the result of the determination
     */
    private static boolean isRadio( int flags )
    {
        return (flags & RADIO_BITMASK) > 0;
    }

    /**
     * This method determines if the given
     * field is a pushbutton.
     *
     * @param flags The field flags.
     *
     * @return the result of the determination
     */
    private static boolean isPushButton( int flags )
    {
        return (flags & PUSHBUTTON_BITMASK) > 0;
    }

    /**
     * This method determines if the given field is a choicefield
     * Choicefields are either listboxes or comboboxes.
     *
     * @param field the field to determine
     * @return the result of the determination
     */
    private static boolean isChoiceField(COSDictionary field)
    {
        return CH.equals(field.getDictionaryObject(COSName.getPDFName("FT")));
    }

    /**
     * This method determines if the given field is a button.
     *
     * @param field the field to determine
     * @return the result of the determination
     */
    private static boolean isButton(COSDictionary field)
    {
        COSName ft = (COSName)field.getDictionaryObject(COSName.getPDFName("FT"));
        boolean retval = BTN.equals( ft );
        COSArray kids = (COSArray)field.getDictionaryObject( COSName.KIDS );
        if( ft == null && kids != null && kids.size() > 0)
        {
            //sometimes if it is a button the type is only defined by one
            //of the kids entries
            COSDictionary entry = (COSDictionary)kids.getObject( 0 );
            retval = isButton( entry );
        }
        return retval;
    }

   /**
     * This method determines if the given field is a signature.
     *
     * @param field the field to determine
     * @return the result of the determination
     */
    private static boolean isSignature(COSDictionary field)
    {
        return SIG.equals(field.getDictionaryObject(COSName.getPDFName("FT")));
    }

    /**
     * This method determines if the given field is a Textbox.
     *
     * @param field the field to determine
     * @return the result of the determination
     */
    private static boolean isTextbox(COSDictionary field)
    {
        return TX.equals(field.getDictionaryObject(COSName.getPDFName("FT")));
    }
}
