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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.Iterator;
import com.progdan.pdf2txt.exceptions.COSVisitorException;

import com.progdan.pdf2txt.pdmodel.common.COSObjectable;

/**
 * This class represents a dictionary where name/value pairs reside.
 *
 * @author Ben Litchfield (ben@csh.rit.edu)
 * @version $Revision: 1.2 $
 */
public class COSDictionary extends COSBase
{
    /**
     * These are all of the items in the dictionary.
     */
    private Map items = new HashMap();

    /**
     * Used to store original sequence of keys, for testing.
     */
    private List keys = new ArrayList();

    /**
     * Constructor.
     */
    public COSDictionary()
    {
    }

    /**
     * Copy Constructor.  This will make a shallow copy of this dictionary.
     *
     * @param dict The dictionary to copy.
     */
    public COSDictionary( COSDictionary dict )
    {
        items = new HashMap( dict.items );
        keys = new ArrayList( dict.keys );
    }

    /**
     * This will return the number of elements in this dictionary.
     *
     * @return The number of elements in the dictionary.
     */
    public int size()
    {
        return keys.size();
    }

    /**
     * This will clear all items in the map.
     */
    public void clear()
    {
        items.clear();
        keys.clear();
    }

    /**
     * This will get an object from this dictionary.  If the object is a reference then it will
     * dereference it and get it from the document.  If the object is COSNull then
     * null will be returned.
     *
     * @param key The key to the object that we are getting.
     *
     * @return The object that matches the key.
     */
    public COSBase getDictionaryObject( String key )
    {
        return getDictionaryObject( COSName.getPDFName( key ) );
    }

    /**
     * This will get an object from this dictionary.  If the object is a reference then it will
     * dereference it and get it from the document.  If the object is COSNull then
     * null will be returned.
     *
     * @param key The key to the object that we are getting.
     *
     * @return The object that matches the key.
     */
    public COSBase getDictionaryObject( COSName key )
    {
        COSBase retval = (COSBase)items.get( key );
        if( retval instanceof COSObject )
        {
            retval = ((COSObject)retval).getObject();
        }
        if( retval instanceof COSNull )
        {
            retval = null;
        }
        return retval;
    }

    /**
     * This will set an item in the dictionary.  If value is null then the result
     * will be the same as removeItem( key ).
     *
     * @param key The key to the dictionary object.
     * @param value The value to the dictionary object.
     */
    public void setItem( COSName key, COSBase value )
    {
        if( value == null )
        {
            removeItem( key );
        }
        else
        {
            if (!items.containsKey(key))
            {
                // insert only if not already there
                keys.add(key);
            }
            items.put( key, value );
        }
    }

    /**
     * This will set an item in the dictionary.  If value is null then the result
     * will be the same as removeItem( key ).
     *
     * @param key The key to the dictionary object.
     * @param value The value to the dictionary object.
     */
    public void setItem( COSName key, COSObjectable value )
    {
        COSBase base = null;
        if( value != null )
        {
            base = value.getCOSObject();
        }
        setItem( key, base );
    }

    /**
     * This will set an item in the dictionary.  If value is null then the result
     * will be the same as removeItem( key ).
     *
     * @param key The key to the dictionary object.
     * @param value The value to the dictionary object.
     */
    public void setItem( String key, COSObjectable value )
    {
        setItem( COSName.getPDFName( key ), value );
    }

    /**
     * This will set an item in the dictionary.
     *
     * @param key The key to the dictionary object.
     * @param value The value to the dictionary object.
     */
    public void setItem( String key, boolean value )
    {
        setItem( COSName.getPDFName( key ), COSBoolean.getBoolean( value ) );
    }

    /**
     * This will set an item in the dictionary.
     *
     * @param key The key to the dictionary object.
     * @param value The value to the dictionary object.
     */
    public void setItem( COSName key, boolean value )
    {
        setItem( key , COSBoolean.getBoolean( value ) );
    }

    /**
     * This will set an item in the dictionary.  If value is null then the result
     * will be the same as removeItem( key ).
     *
     * @param key The key to the dictionary object.
     * @param value The value to the dictionary object.
     */
    public void setItem( String key, COSBase value )
    {
        setItem( COSName.getPDFName( key ), value );
    }

    /**
     * This is a convenience method that will convert the value to a COSName
     * object.  If it is null then the object will be removed.
     *
     * @param key The key to the object,
     * @param value The string value for the name.
     */
    public void setName( String key, String value )
    {
        setName( COSName.getPDFName( key ), value );
    }

    /**
     * This is a convenience method that will convert the value to a COSName
     * object.  If it is null then the object will be removed.
     *
     * @param key The key to the object,
     * @param value The string value for the name.
     */
    public void setName( COSName key, String value )
    {
        COSName name = null;
        if( value != null )
        {
            name = COSName.getPDFName( value );
        }
        setItem( key, name );
    }

    /**
     * This is a convenience method that will convert the value to a COSString
     * object.  If it is null then the object will be removed.
     *
     * @param key The key to the object,
     * @param value The string value for the name.
     */
    public void setString( String key, String value )
    {
        setString( COSName.getPDFName( key ), value );
    }

    /**
     * This is a convenience method that will convert the value to a COSString
     * object.  If it is null then the object will be removed.
     *
     * @param key The key to the object,
     * @param value The string value for the name.
     */
    public void setString( COSName key, String value )
    {
        COSString name = null;
        if( value != null )
        {
            name = new COSString( value );
        }
        setItem( key, name );
    }

    /**
     * This is a convenience method that will get the dictionary object that
     * is expected to be a name and convert it to a string.  Null is returned
     * if the entry does not exist in the dictionary.
     *
     * @param key The key to the item in the dictionary.
     * @return The name converted to a string.
     */
    public String getNameAsString( String key )
    {
        return getNameAsString( COSName.getPDFName( key ) );
    }

    /**
     * This is a convenience method that will get the dictionary object that
     * is expected to be a name and convert it to a string.  Null is returned
     * if the entry does not exist in the dictionary.
     *
     * @param key The key to the item in the dictionary.
     * @return The name converted to a string.
     */
    public String getNameAsString( COSName key )
    {
        String retval = null;
        COSName name = (COSName)getDictionaryObject( key );
        if( name != null )
        {
            retval = name.getName();
        }
        return retval;
    }

    /**
     * This is a convenience method that will get the dictionary object that
     * is expected to be a name and convert it to a string.  Null is returned
     * if the entry does not exist in the dictionary.
     *
     * @param key The key to the item in the dictionary.
     * @param defaultValue The value to return if the dictionary item is null.
     * @return The name converted to a string.
     */
    public String getNameAsString( String key, String defaultValue )
    {
        return getNameAsString( COSName.getPDFName( key ), defaultValue );
    }

    /**
     * This is a convenience method that will get the dictionary object that
     * is expected to be a name and convert it to a string.  Null is returned
     * if the entry does not exist in the dictionary.
     *
     * @param key The key to the item in the dictionary.
     * @param defaultValue The value to return if the dictionary item is null.
     * @return The name converted to a string.
     */
    public String getNameAsString( COSName key, String defaultValue )
    {
        String retval = getNameAsString( key );
        if( retval == null )
        {
            retval = defaultValue;
        }
        return retval;
    }

    /**
     * This is a convenience method that will get the dictionary object that
     * is expected to be a name and convert it to a string.  Null is returned
     * if the entry does not exist in the dictionary.
     *
     * @param key The key to the item in the dictionary.
     * @return The name converted to a string.
     */
    public String getString( String key )
    {
        return getString( COSName.getPDFName( key ) );
    }

    /**
     * This is a convenience method that will get the dictionary object that
     * is expected to be a name and convert it to a string.  Null is returned
     * if the entry does not exist in the dictionary.
     *
     * @param key The key to the item in the dictionary.
     * @return The name converted to a string.
     */
    public String getString( COSName key )
    {
        String retval = null;
        COSString name = (COSString)getDictionaryObject( key );
        if( name != null )
        {
            retval = name.getString();
        }
        return retval;
    }

    /**
     * This is a convenience method that will get the dictionary object that
     * is expected to be a name and convert it to a string.  Null is returned
     * if the entry does not exist in the dictionary.
     *
     * @param key The key to the item in the dictionary.
     * @param defaultValue The default value to return.
     * @return The name converted to a string.
     */
    public String getString( String key, String defaultValue )
    {
        return getString( COSName.getPDFName( key ), defaultValue );
    }

    /**
     * This is a convenience method that will get the dictionary object that
     * is expected to be a name and convert it to a string.  Null is returned
     * if the entry does not exist in the dictionary.
     *
     * @param key The key to the item in the dictionary.
     * @param defaultValue The default value to return.
     * @return The name converted to a string.
     */
    public String getString( COSName key, String defaultValue )
    {
        String retval = getString( key );
        if( retval == null )
        {
            retval = defaultValue;
        }
        return retval;
    }

    /**
     * This is a convenience method that will get the dictionary object that
     * is expected to be a cos boolean and convert it to a primitive boolean.
     *
     * @param key The key to the item in the dictionary.
     * @param defaultValue The value returned if the entry is null.
     *
     * @return The value converted to a boolean.
     */
    public boolean getBoolean( String key, boolean defaultValue )
    {
        return getBoolean( COSName.getPDFName( key ), defaultValue );
    }

    /**
     * This is a convenience method that will get the dictionary object that
     * is expected to be a COSBoolean and convert it to a primitive boolean.
     *
     * @param key The key to the item in the dictionary.
     * @param defaultValue The value returned if the entry is null.
     *
     * @return The entry converted to a boolean.
     */
    public boolean getBoolean( COSName key, boolean defaultValue )
    {
        boolean retval = defaultValue;
        COSBoolean bool = (COSBoolean)getDictionaryObject( key );
        if( bool != null )
        {
            retval = bool.getValue();
        }
        return retval;
    }

    /**
     * This will remove an item for the dictionary.  This
     * will do nothing of the object does not exist.
     *
     * @param key The key to the item to remove from the dictionary.
     */
    public void removeItem( COSName key )
    {
        keys.remove( key );
        items.remove( key );
    }

    /**
     * This will do a lookup into the dictionary.
     *
     * @param key The key to the object.
     *
     * @return The item that matches the key.
     */
    public COSBase getItem( COSName key )
    {
        return (COSBase)items.get( key );
    }



    /**
     * This will get the keys for all objects in the dictionary in the sequence that
     * they were added.
     *
     * @return a list of the keys in the sequence of insertion
     *
     */
    public List keyList()
    {
        return keys;
    }

    /**
     * This will get all of the values for the dictionary.
     *
     * @return All the values for the dictionary.
     */
    public Collection getValues()
    {
        return items.values();
    }


    /**
     * @see Object#toString()
     */
    public String toString()
    {
        return "COSDictionary" + items;
    }

    /**
     * visitor pattern double dispatch method.
     *
     * @param visitor The object to notify when visiting this object.
     * @return The object that the visitor returns.
     *
     * @throws COSVisitorException If there is an error visiting this object.
     */
    public Object accept(ICOSVisitor  visitor) throws COSVisitorException
    {
        return visitor.visitFromDictionary(this);
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
        for (Iterator i = items.values().iterator(); i.hasNext();)
        {
            COSBase contained = (COSBase) i.next();
            contained.addTo(doc);
        }
    }

    /**
     * This will add all of the dictionarys keys/values to this dictionary.
     *
     * @param dic The dic to get the keys from.
     */
    public void addAll( COSDictionary dic )
    {
        Iterator dicKeys = dic.keyList().iterator();
        while( dicKeys.hasNext() )
        {
            COSName key = (COSName)dicKeys.next();
            COSBase value = dic.getItem( key );
            setItem( key, value );
        }
    }
}
