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

import java.io.IOException;

import java.util.Calendar;

import com.progdan.pdf2txt.cos.COSBase;
import com.progdan.pdf2txt.cos.COSDictionary;
import com.progdan.pdf2txt.cos.COSName;
import com.progdan.pdf2txt.cos.COSString;

import com.progdan.pdf2txt.pdmodel.common.COSObjectable;

import com.progdan.pdf2txt.util.DateConverter;

/**
 * This is the document metadata.  Each getXXX method will return the entry if
 * it exists or null if it does not exist.  If you pass in null for the setXXX
 * method then it will clear the value.
 *
 * @author Ben Litchfield (ben@csh.rit.edu)
 * @version $Revision: 1.2 $
 */
public class PDDocumentInformation implements COSObjectable
{
    private static final COSName TITLE = COSName.getPDFName( "Title" );
    private static final COSName AUTHOR = COSName.getPDFName( "Author" );
    private static final COSName SUBJECT = COSName.getPDFName( "Subject" );
    private static final COSName KEYWORDS = COSName.getPDFName( "Keywords" );
    private static final COSName CREATOR = COSName.getPDFName( "Creator" );
    private static final COSName PRODUCER = COSName.getPDFName( "Producer" );
    private static final COSName CREATION_DATE = COSName.getPDFName( "CreationDate" );
    private static final COSName MODIFICATION_DATE = COSName.getPDFName( "ModDate" );
    private static final COSName TRAPPED = COSName.getPDFName( "Trapped" );
    private COSDictionary info;

    /**
     * Constructor that is used for a preexisting dictionary.
     *
     * @param dic The underlying dictionary.
     */
    public PDDocumentInformation( COSDictionary dic )
    {
        info = dic;
    }

    /**
     * This will get the underlying dictionary that this object wraps.
     *
     * @return The underlying info dictionary.
     */
    public COSDictionary getDictionary()
    {
        return info;
    }

    /**
     * Convert this standard java object to a COS object.
     *
     * @return The cos object that matches this Java object.
     */
    public COSBase getCOSObject()
    {
        return info;
    }

    /**
     * This will get the title of the document.  This will return null if no title exists.
     *
     * @return The title of the document.
     */
    public String getTitle()
    {
        return getStringItem( TITLE );
    }

    /**
     * This will set the title of the document.
     *
     * @param title The new title for the document.
     */
    public void setTitle( String title )
    {
        setStringItem( TITLE, title );
    }

    /**
     * This will get the author of the document.  This will return null if no author exists.
     *
     * @return The author of the document.
     */
    public String getAuthor()
    {
        return getStringItem( AUTHOR );
    }

    /**
     * This will set the author of the document.
     *
     * @param author The new author for the document.
     */
    public void setAuthor( String author )
    {
        setStringItem( AUTHOR, author );
    }

    /**
     * This will get the subject of the document.  This will return null if no subject exists.
     *
     * @return The subject of the document.
     */
    public String getSubject()
    {
        return getStringItem( SUBJECT );
    }

    /**
     * This will set the subject of the document.
     *
     * @param subject The new subject for the document.
     */
    public void setSubject( String subject )
    {
        setStringItem( SUBJECT, subject );
    }

    /**
     * This will get the keywords of the document.  This will return null if no keywords exists.
     *
     * @return The keywords of the document.
     */
    public String getKeywords()
    {
        return getStringItem( KEYWORDS );
    }

    /**
     * This will set the keywords of the document.
     *
     * @param keywords The new keywords for the document.
     */
    public void setKeywords( String keywords )
    {
        setStringItem( KEYWORDS, keywords );
    }

    /**
     * This will get the creator of the document.  This will return null if no creator exists.
     *
     * @return The creator of the document.
     */
    public String getCreator()
    {
        return getStringItem( CREATOR );
    }

    /**
     * This will set the creator of the document.
     *
     * @param creator The new creator for the document.
     */
    public void setCreator( String creator )
    {
        setStringItem( CREATOR, creator );
    }

    /**
     * This will get the producer of the document.  This will return null if no producer exists.
     *
     * @return The producer of the document.
     */
    public String getProducer()
    {
        return getStringItem( PRODUCER );
    }

    /**
     * This will set the producer of the document.
     *
     * @param producer The new producer for the document.
     */
    public void setProducer( String producer )
    {
        setStringItem( PRODUCER, producer );
    }

    /**
     * This will get the creation date of the document.  This will return null if no creation date exists.
     *
     * @return The creation date of the document.
     */
    public Calendar getCreationDate()
    {
        return getDateItem( CREATION_DATE );
    }

    /**
     * This will set the creation date of the document.
     *
     * @param date The new creation date for the document.
     */
    public void setCreationDate( Calendar date )
    {
        setDateItem( CREATION_DATE, date );
    }

    /**
     * This will get the modification date of the document.  This will return null if no modification date exists.
     *
     * @return The modification date of the document.
     */
    public Calendar getModificationDate()
    {
        return getDateItem( MODIFICATION_DATE );
    }

    /**
     * This will set the modification date of the document.
     *
     * @param date The new modification date for the document.
     */
    public void setModificationDate( Calendar date )
    {
        setDateItem( MODIFICATION_DATE, date );
    }

    /**
     * This will get the trapped value for the document.
     * This will return null if one is not found.
     *
     * @return The trapped value for the document.
     */
    public String getTrapped()
    {
        return getNameItem( TRAPPED );
    }

    /**
     * This will set the trapped of the document.  This will be
     * 'True', 'False', or 'Unknown'.
     *
     * @param value The new trapped value for the document.
     */
    public void setTrapped( String value )
    {
        if( value != null &&
            !value.equals( "True" ) &&
            !value.equals( "False" ) &&
            !value.equals( "Unknown" ) )
        {
            throw new RuntimeException( "Valid values for trapped are " +
                                        "'True', 'False', or 'Unknown'" );
        }

        setNameItem( TRAPPED, value );
    }

    /**
     * This will get a string item from the dictionary.
     *
     * @param key The key to the string item.
     *
     * @return The value if it exists or null.
     */
    private String getStringItem( COSName key )
    {
        String retval = null;
        COSString value = (COSString)info.getDictionaryObject( key );
        if( value != null )
        {
            retval = value.getString();
        }
        return retval;
    }

    /**
     * This will set a string item in the info dictionary.
     *
     * @param key The key that matches the value.
     * @param value The new string value.
     */
    private void setStringItem( COSName key, String value )
    {
        if( value == null )
        {
            info.removeItem( key );
        }
        else
        {
            info.setItem( key, new COSString( value ) );
        }
    }

    /**
     * This will get a date item from the dictionary.
     *
     * @param key The key to the date item.
     *
     * @return The value if it exists or null.
     */
    private Calendar getDateItem( COSName key )
    {
        Calendar retval = null;
        COSString value = (COSString)info.getDictionaryObject( key );
        if( value != null )
        {
            //lets first verify that the string is valid.
            String strValue = value.getString();
            int index = 0;
            if( strValue.startsWith( "D:" ) )
            {
                index = 2;
            }
            boolean validDate = true;
            for( int i=index; i<strValue.length(); i++ )
            {
                validDate = validDate && Character.isDigit( strValue.charAt( i ) );
            }
            if( validDate )
            {
                DateConverter converter = new DateConverter();
                try
                {
                    retval = converter.toCalendar( value.getString() );
                }
                catch( IOException e )
                {
                    retval = null;
                }
            }
        }
        return retval;
    }

    /**
     * This will set a date item in the info dictionary.
     *
     * @param key The key that matches the value.
     * @param value The new date value.
     */
    private void setDateItem( COSName key, Calendar value )
    {
        if( value == null )
        {
            info.removeItem( key );
        }
        else
        {
            DateConverter converter = new DateConverter();
            info.setItem( key, new COSString( converter.toString( value ) ) );
        }
    }

    /**
     * This will get a COSName item from the dictionary.
     *
     * @param key The key to the COSName item.
     *
     * @return The value if it exists or null.
     */
    private String getNameItem( COSName key )
    {
        String retval = null;
        COSName value = (COSName)info.getDictionaryObject( key );
        if( value != null )
        {
            retval = value.getName();
        }
        return retval;
    }

    /**
     * This will set a COSName item in the info dictionary.
     *
     * @param key The key that matches the value.
     * @param value The new name value.
     */
    private void setNameItem( COSName key, String value )
    {
        if( value == null )
        {
            info.removeItem( key );
        }
        else
        {
            info.setItem( key, COSName.getPDFName( value ) );
        }
    }
}
