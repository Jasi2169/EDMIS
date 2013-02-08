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
import java.util.Iterator;
import java.util.List;



import com.progdan.pdf2txt.exceptions.COSVisitorException;

/**
 * An array of PDFBase objects as part of the PDF document.
 *
 * @author Ben Litchfield (ben@csh.rit.edu)
 * @version $Revision: 1.2 $
 */
public class COSArray extends COSBase
{
    private List objects = new ArrayList();

    /**
     * Constructor.
     */
    public COSArray()
    {
    }

    /**
     * This will add an object to the array.
     *
     * @param object The object to add to the array.
     */
    public void add( COSBase object )
    {
        objects.add( object );
    }

    /**
     * Add the specified object at the ith location and push the rest to the
     * right.
     *
     * @param i The index to add at.
     * @param object The object to add at that index.
     */
    public void add( int i, COSBase object)
    {
        objects.add( i, object );
    }

    /**
     * This will remove all of the objects in the collection.
     */
    public void clear()
    {
        objects.clear();
    }

    /**
     * This will remove all of the objects in the collection.
     *
     * @param objectsList The list of objects to remove from the collection.
     */
    public void removeAll( Collection objectsList )
    {
        objects.removeAll( objectsList );
    }

    /**
     * This will retain all of the objects in the collection.
     *
     * @param objectsList The list of objects to retain from the collection.
     */
    public void retainAll( Collection objectsList )
    {
        objects.retainAll( objectsList );
    }

    /**
     * This will add an object to the array.
     *
     * @param objectsList The object to add to the array.
     */
    public void addAll( Collection objectsList )
    {
        objects.addAll( objectsList );
    }

    /**
     * This will add all objects to this array.
     *
     * @param objectList The objects to add.
     */
    public void addAll( COSArray objectList )
    {
        objects.addAll( objectList.objects );
    }

    /**
     * Add the specified object at the ith location and push the rest to the
     * right.
     *
     * @param i The index to add at.
     * @param objectList The object to add at that index.
     */
    public void addAll( int i, Collection objectList )
    {
        objects.addAll( i, objectList );
    }

    /**
     * This will set an object at a specific index.
     *
     * @param index zero based index into array.
     * @param object The object to set.
     */
    public void set( int index, COSBase object )
    {
        objects.set( index, object );
    }

    /**
     * This will get an object from the array.  This will dereference the object.
     * If the object is COSNull then null will be returned.
     *
     * @param index The index into the array to get the object.
     *
     * @return The object at the requested index.
     */
    public COSBase getObject( int index )
    {
        Object obj = objects.get( index );
        if( obj instanceof COSObject )
        {
            obj = ((COSObject)obj).getObject();
        }
        if( obj instanceof COSNull )
        {
            obj = null;
        }
        return (COSBase)obj;
    }

    /**
     * This will get an object from the array.  This will NOT derefernce
     * the COS object.
     *
     * @param index The index into the array to get the object.
     *
     * @return The object at the requested index.
     */
    public COSBase get( int index )
    {
        return (COSBase)objects.get( index );
    }

    /**
     * This will get the size of this array.
     *
     * @return The number of elements in the array.
     */
    public int size()
    {
        return objects.size();
    }

    /**
     * This will remove an element from the array.
     *
     * @param i The index of the object to remove.
     *
     * @return The object that was removed.
     */
    public COSBase remove( int i )
    {
        return (COSBase)objects.remove( i );
    }

    /**
     * This will remove an element from the array.
     *
     * @param o The object to remove.
     *
     * @return The object that was removed.
     */
    public boolean remove( COSBase o )
    {
        return objects.remove( o );
    }

    /**
     * @see Object#toString()
     */
    public String toString()
    {
        return "COSArray{" + objects + "}";
    }

    /**
     * Get access to the list.
     *
     * @return an iterator over the array elements
     */
    public Iterator iterator()
    {
        return objects.iterator();
    }

    /**
     * This will return the index of the entry or -1 if it is not found.
     *
     * @param object The object to search for.
     * @return The index of the object or -1.
     */
    public int indexOf( COSBase object )
    {
        int retval = -1;
        for( int i=0; retval < 0 && i<size(); i++ )
        {
            if( get( i ).equals( object ) )
            {
                retval = i;
            }
        }
        return retval;
    }

    /**
     * visitor pattern double dispatch method.
     *
     * @param visitor The object to notify when visiting this object.
     * @return any object, depending on the visitor implementation, or null
     * @throws COSVisitorException If an error occurs while visiting this object.
     */
    public Object accept(ICOSVisitor  visitor) throws COSVisitorException
    {
        return visitor.visitFromArray(this);
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
        for (Iterator i = iterator(); i.hasNext();)
        {
            COSBase contained = (COSBase) i.next();
            contained.addTo(doc);
        }
    }
}
