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

import com.progdan.pdf2txt.exceptions.COSVisitorException;

import java.io.IOException;

import com.progdan.logengine.Category;

/**
 * This class represents a PDF object.
 *
 * @author Ben Litchfield (ben@csh.rit.edu)
 * @version $Revision: 1.2 $
 */
public class COSObject extends COSBase
{
    private static Category log = Category.getInstance(COSObject.class.getName());

    private COSBase baseObject;
    private COSInteger objectNumber;
    private COSInteger generationNumber;

    /**
     * Constructor.
     *
     * @param object The object that this encapsulates.
     *
     * @throws IOException If there is an error with the object passed in.
     */
    public COSObject( COSBase object ) throws IOException
    {
        setObject( object );
    }

    /**
     * This will get the dictionary object in this object that has the name key and
     * if it is a pdfobjref then it will dereference that and return it.
     *
     * @param key The key to the value that we are searching for.
     *
     * @return The pdf object that matches the key.
     */
    public COSBase getDictionaryObject( COSName key )
    {
        COSBase retval =null;
        if( baseObject instanceof COSDictionary )
        {
            retval = ((COSDictionary)baseObject).getDictionaryObject( key );
        }
        return retval;
    }

    /**
     * This will get the dictionary object in this object that has the name key.
     *
     * @param key The key to the value that we are searching for.
     *
     * @return The pdf object that matches the key.
     */
    public COSBase getItem( COSName key )
    {
        COSBase retval =null;
        if( baseObject instanceof COSDictionary )
        {
            retval = ((COSDictionary)baseObject).getItem( key );
        }
        return retval;
    }

    /**
     * This will get the object that this object encapsulates.
     *
     * @return The encapsulated object.
     */
    public COSBase getObject()
    {
        return baseObject;
    }

    /**
     * This will set the object that this object encapsulates.
     *
     * @param object The new object to encapsulate.
     *
     * @throws IOException If there is an error setting the updated object.
     */
    public void setObject( COSBase object ) throws IOException
    {
        if( baseObject == null )
        {
            baseObject = object;
        }
        else
        {
            //This is for when an object appears twice in the
            //pdf file we really want to replace it such that
            //object references still work correctly.
            //see owcp-as-received.pdf for an example
            if( baseObject instanceof COSDictionary )
            {
                COSDictionary dic = (COSDictionary)baseObject;
                COSDictionary dicObject = (COSDictionary)object;
                dic.clear();
                dic.addAll( dicObject );
            }
            else if( baseObject instanceof COSArray )
            {
                COSArray array = (COSArray)baseObject;
                COSArray arrObject = (COSArray)object;
                array.clear();
                for( int i=0; i<arrObject.size(); i++ )
                {
                    array.add( arrObject.get( i ) );
                }
            }
            else if( baseObject instanceof COSStream )
            {
                COSStream oldStream = (COSStream)baseObject;
                COSStream newStream = (COSStream)object;
                oldStream.replaceWithStream( newStream );
            }
            else
            {
                throw new IOException( "Unknown object substitution type:" + baseObject );
            }
        }

    }

    /**
     * @see Object#toString()
     */
    public String toString()
    {
        return "COSObject{" +
            (objectNumber == null ? "unknown" : "" + objectNumber.intValue() ) + ", " +
            (generationNumber == null ? "unknown" : "" + generationNumber.intValue() ) +
            "}";
    }

    /** Getter for property objectNumber.
     * @return Value of property objectNumber.
     */
    public COSInteger getObjectNumber()
    {
        return objectNumber;
    }

    /** Setter for property objectNumber.
     * @param objectNum New value of property objectNumber.
     */
    public void setObjectNumber(COSInteger objectNum)
    {
        objectNumber = objectNum;
    }

    /** Getter for property generationNumber.
     * @return Value of property generationNumber.
     */
    public COSInteger getGenerationNumber()
    {
        return generationNumber;
    }

    /** Setter for property generationNumber.
     * @param generationNumberValue New value of property generationNumber.
     */
    public void setGenerationNumber(COSInteger generationNumberValue)
    {
        generationNumber = generationNumberValue;
    }

    /**
     * visitor pattern double dispatch method.
     *
     * @param visitor The object to notify when visiting this object.
     * @return any object, depending on the visitor implementation, or null
     * @throws COSVisitorException If an error occurs while visiting this object.
     */
    public Object accept( ICOSVisitor visitor ) throws COSVisitorException
    {
        return null;
    }

    /**
     * @see COSBase#addTo( COSDocument )
     *
     * @param doc the document where the object is to be inserted
     *
     */
    protected void addTo(COSDocument doc)
    {
        super.addTo(doc);
        doc.addObject(this);
    }
}
