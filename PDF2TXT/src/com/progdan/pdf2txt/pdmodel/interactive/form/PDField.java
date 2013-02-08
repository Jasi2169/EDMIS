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

import com.progdan.pdf2txt.pdmodel.interactive.annotation.PDWidget;

import com.progdan.pdf2txt.pdmodel.common.COSArrayList;
import com.progdan.pdf2txt.pdmodel.common.COSObjectable;

import com.progdan.pdf2txt.cos.COSArray;
import com.progdan.pdf2txt.cos.COSBase;
import com.progdan.pdf2txt.cos.COSDictionary;
import com.progdan.pdf2txt.cos.COSInteger;
import com.progdan.pdf2txt.cos.COSName;
import com.progdan.pdf2txt.cos.COSString;

import com.progdan.pdf2txt.pdmodel.common.PDTextStream;

import com.progdan.pdf2txt.pdmodel.fdf.FDFField;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the superclass for a Field element in a PDF.
 * Based on the COS object model from PDF2TXT.
 *
 * @author sug
 * @version $Revision: 1.2 $
 */
public abstract class PDField implements COSObjectable
{
    private PDAcroForm acroForm;

    private COSDictionary dictionary;

    /**
     * Constructor.
     *
     * @param theAcroForm The form that this field is part of.
     */
    public PDField( PDAcroForm theAcroForm )
    {
        acroForm = theAcroForm;
        dictionary = new COSDictionary();
        //no required fields in base field class
    }


    /**
     * Creates a COSField from a COSDictionary, expected to be
     * a correct object definition for a field in PDF.
     *
     * @param theAcroForm The form that this field is part of.
     * @param field the PDF objet to represent as a field.
     */
    public PDField(PDAcroForm theAcroForm, COSDictionary field)
    {
        acroForm = theAcroForm;
        dictionary = field;
    }

    /**
     * Returns the name of the field.
     *
     * @return the name of the field
     */
    public String getName()
    {
        return ((COSString)getDictionary().getDictionaryObject(COSName.getPDFName("T"))).getString();
    }

    /**
     * This will set the name of the field.
     *
     * @param name The new name for the field.
     */
    public void setName( String name )
    {
        getDictionary().setItem( COSName.getPDFName( "T" ), new COSString( name ) );
    }


    /**
     * setValue sets the fields value to a given string.
     *
     * @param value the string value
     *
     * @throws IOException If there is an error creating the appearance stream.
     */
    public abstract void setValue(String value) throws IOException;

    /**
     * getValue gets the fields value to as a string.
     *
     * @return The string value of this field.
     *
     * @throws IOException If there is an error getting the value.
     */
    public abstract String getValue() throws IOException;

    /**
     * sets the field to be read-only.
     *
     * @param readonly The new flag for readonly.
     */
    public void setReadonly(boolean readonly)
    {
        setFfFlag(1, readonly);
    }

    /**
     *
     * @return true if the field is readonly
     */
    public boolean isReadonly()
    {
        return getFfFlag(1);
    }

    /**
     * This will get the flags for this field.
     *
     * @return flags The set of flags.
     */
    public int getFieldFlags()
    {
        int retval = 0;
        COSInteger ff = (COSInteger)getDictionary().getDictionaryObject( COSName.getPDFName( "Ff" ) );
        if( ff != null )
        {
            retval = (int)ff.intValue();
        }
        return retval;
    }

    /**
     * This will set the flags for this field.
     *
     * @param flags The new flags.
     */
    public void setFieldFlags( int flags )
    {
        COSInteger ff = new COSInteger( flags );
        getDictionary().setItem( COSName.getPDFName( "Ff" ), ff );
    }

    /**
     * Sets the given boolean value at bitPos in the Ff flags.
     *
     * @param bitPos the bit position to set the value in
     * @param value the value the bit position should have
     */
    protected void setFfFlag(int bitPos, boolean value)
    {
        long ffval = getFieldFlags();
        char[] bits = Long.toBinaryString(ffval).toCharArray();
        bits[bits.length-bitPos] = value ? '1' : '0';
        String s = new String(bits);
        COSInteger ff = new COSInteger(Integer.parseInt(s,2));
        getDictionary().setItem( COSName.getPDFName( "Ff" ), ff );
    }

    /**
     * Gets the boolean value from the Ff flags at the given bit
     * position.
     * @param bitPos the bitPosition to get the value from
     * @return true if the number at bitPos is '1'
     */
    protected boolean getFfFlag(int bitPos)
    {
        COSInteger ff = (COSInteger)getDictionary().getDictionaryObject( COSName.getPDFName( "Ff" ) );
        if (ff != null)
        {
            int bitMask = (int) Math.pow(2,bitPos-1);
            return (( ff.intValue() & bitMask) == bitMask);
        }
        else
        {
            return false;
        }
    }

    /**
     * This will import a fdf field from a fdf document.
     *
     * @param fdfField The fdf field to import.
     *
     * @throws IOException If there is an error importing the data for this field.
     */
    public void importFDF( FDFField fdfField ) throws IOException
    {
        Object fieldValue = fdfField.getValue();
        int fieldFlags = getFieldFlags();

        if( fieldValue != null )
        {
            if( fieldValue instanceof String )
            {
                setValue( (String)fieldValue );
            }
            else if( fieldValue instanceof PDTextStream )
            {
                setValue( ((PDTextStream)fieldValue).getAsString() );
            }
            else
            {
                throw new IOException( "Uknown field type:" + fieldValue.getClass().getName() );
            }
        }
        Integer ff = fdfField.getFieldFlags();
        if( ff != null )
        {
            setFieldFlags( (int)ff.intValue() );
        }
        else
        {
            //these are suppose to be ignored if the Ff is set.
            Integer setFf = fdfField.getSetFieldFlags();

            if( setFf != null )
            {
                int setFfInt = setFf.intValue();
                fieldFlags = fieldFlags | setFfInt;
                setFieldFlags( fieldFlags );
            }

            Integer clrFf = fdfField.getClearFieldFlags();
            if( clrFf != null )
            {
                //we have to clear the bits of the document fields for every bit that is
                //set in this field.
                //
                //Example:
                //docFf = 1011
                //clrFf = 1101
                //clrFfValue = 0010;
                //newValue = 1011 & 0010 which is 0010
                int clrFfValue = clrFf.intValue();
                clrFfValue ^= 0xFFFFFFFF;
                fieldFlags = fieldFlags & clrFfValue;
                setFieldFlags( fieldFlags );
            }
        }

        PDWidget widget = getWidget();
        if( widget != null )
        {
            int annotFlags = widget.getAnnotationFlags();
            Integer f = fdfField.getWidgetFieldFlags();
            if( f != null && widget != null )
            {
                widget.setAnnotationFlags( f.intValue() );
            }
            else
            {
                //these are suppose to be ignored if the F is set.
                Integer setF = fdfField.getSetWidgetFieldFlags();
                if( setF != null )
                {
                    annotFlags = annotFlags | setF.intValue();
                    widget.setAnnotationFlags( annotFlags );
                }

                Integer clrF = fdfField.getClearWidgetFieldFlags();
                if( clrF != null )
                {
                    //we have to clear the bits of the document fields for every bit that is
                    //set in this field.
                    //
                    //Example:
                    //docF = 1011
                    //clrF = 1101
                    //clrFValue = 0010;
                    //newValue = 1011 & 0010 which is 0010
                    int clrFValue = clrF.intValue();
                    clrFValue ^= 0xFFFFFFFFL;
                    annotFlags = annotFlags & clrFValue;
                    widget.setAnnotationFlags( annotFlags );
                }
            }
        }
    }

    /**
     * This will get the single associated widget that is part of this field.  This
     * occurs when the Widget is embedded in the fields dictionary.  Sometimes there
     * are multiple sub widgets associated with this field, in which case you want to
     * use getKids().  Either getKids() or getWidget() will return null.
     *
     * @return The widget that is associated with this field.
     */
    public PDWidget getWidget()
    {
        PDWidget retval = null;
        List kids = getKids();
        if( kids == null )
        {
            retval = new PDWidget( getDictionary() );
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
                kidsList.add( new PDWidget( (COSDictionary)kids.getObject(i) ) );
            }
            retval = new COSArrayList( kidsList, kids );
        }
        return retval;
    }

    /**
     * This will set the list of kids.
     *
     * @param kids The list of child widgets.
     */
    public void setKids( List kids )
    {
        COSArray kidsArray = COSArrayList.converterToCOSArray( kids );
        getDictionary().setItem( COSName.KIDS, kidsArray );
    }

    /**
     * This will return a string representation of this field.
     *
     * @return A string representation of this field.
     */
    public String toString()
    {
        return "" + getDictionary().getDictionaryObject( COSName.getPDFName( "V" ) );
    }

    /**
     * This will get the acroform that this field is part of.
     *
     * @return The form this field is on.
     */
    public PDAcroForm getAcroForm()
    {
        return acroForm;
    }

    /**
     * This will set the form this field is on.
     *
     * @param value The new form to use.
     */
    public void setAcroForm(PDAcroForm value)
    {
        acroForm = value;
    }

    /**
     * This will get the dictionary associated with this field.
     *
     * @return The dictionary that this class wraps.
     */
    public COSDictionary getDictionary()
    {
        return dictionary;
    }

    /**
     * Convert this standard java object to a COS object.
     *
     * @return The cos object that matches this Java object.
     */
    public COSBase getCOSObject()
    {
        return dictionary;
    }
}
