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

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import com.progdan.pdf2txt.exceptions.COSVisitorException;

/**
 * This is the in-memory representation of the PDF document.  You need to call
 * close() on this object when you are done using it!!
 *
 * @author Ben Litchfield (ben@csh.rit.edu)
 * @version $Revision: 1.2 $
 */
public class COSDocument extends COSBase
{
    private float version;
    /**
     * A map to associate COSObjects with the embedded types.
     * This is used to better support indirect objects when manually constructing PDF's.
     */
    private Map objectMap = new HashMap();
    /**
     * added objects (actually preserving original sequence).
     */
    private List objects = new ArrayList();

    /**
     * Document trailer dictionary.
     */
    private COSDictionary trailer;

    /**
     * This file will store the streams in order to conserve memory.
     */
    private RandomAccessFile scratchFile = null;
    private File tmpFile = null;

    private String headerString = "%PDF-1.4";

    /**
     * Constructor.  Uses the java.io.tmpdir value to create a file
     * to store the streams.
     *
     *  @throws IOException If there is an error creating the tmp file.
     */
    public COSDocument() throws IOException
    {
        this( new File( System.getProperty( "java.io.tmpdir" ) ) );
    }

    /**
     * Constructor that will create a create a scratch file in the
     * following directory.
     *
     * @param scratchDir The directory to store a scratch file.
     *
     *  @throws IOException If there is an error creating the tmp file.
     */
    public COSDocument( File scratchDir ) throws IOException
    {
        tmpFile = File.createTempFile( "pdf2txt", "tmp", scratchDir );
        scratchFile = new RandomAccessFile( tmpFile, "rw" );
    }

    /**
     * Constructor that will use the following random access file for storage
     * of the PDF streams.  The client of this method is responsible for deleting
     * the storage if necessary that this file will write to.  The close method
     * will close the file though.
     *
     * @param file The random access file to use for storage.
     */
    public COSDocument( RandomAccessFile file )
    {
        scratchFile = file;
    }

    /**
     * This will get the scratch file for this document.
     *
     * @return The scratch file.
     */
    public RandomAccessFile getScratchFile()
    {
        return scratchFile;
    }

    /**
     * This will get a dictionary object by type.
     *
     * @param type The type of the object.
     *
     * @return This will return an object with the specified type.
     */
    public COSObject getObjectByType( COSName type )
    {
        COSObject retval = null;
        Iterator iter = objects.iterator();
        while( iter.hasNext() && retval == null)
        {
            COSObject object = (COSObject)iter.next();

            COSBase realObject = object.getObject();
            if( realObject instanceof COSDictionary )
            {
                COSDictionary dic = (COSDictionary)realObject;
                COSName objectType = (COSName)dic.getItem( COSName.TYPE );
                if( objectType != null && objectType.equals( type ) )
                {
                    retval = object;
                }
            }
        }
        return retval;
    }

    /**
     * This will print contents to stdout.
     */
    public void print()
    {
        Iterator iter = objects.iterator();
        while( iter.hasNext() )
        {
            COSObject object = (COSObject)iter.next();
            System.out.println( object);
        }
    }

    /**
     * This will set the version of this PDF document.
     *
     * @param versionValue The version of the PDF document.
     */
    public void setVersion( float versionValue )
    {
        version = versionValue;
    }

    /**
     * This will get the version of this PDF document.
     *
     * @return This documents version.
     */
    public float getVersion()
    {
        return version;
    }

    /**
     * This will tell if this is an encrypted document.
     *
     * @return true If this document is encrypted.
     */
    public boolean isEncrypted()
    {
        COSName encrypt = COSName.getPDFName( "Encrypt" );
        return trailer.getItem( encrypt ) != null && !trailer.getItem( encrypt ).equals( COSNull.NULL );
    }

    /**
     * This will get the encryption dictionary if the document is encrypted or null
     * if the document is not encrypted.
     *
     * @return The encryption dictionary.
     */
    public COSDictionary getEncryptionDictionary()
    {
        return (COSDictionary)trailer.getDictionaryObject( COSName.getPDFName( "Encrypt" ) );
    }

    /**
     * This will set the encryption dictionary, this should only be called when
     * encypting the document.
     *
     * @param encDictionary The encryption dictionary.
     */
    public void setEncryptionDictionary( COSDictionary encDictionary )
    {
        trailer.setItem( COSName.getPDFName( "Encrypt" ), encDictionary );
    }

    /**
     * This will get the document ID.
     *
     * @return The document id.
     */
    public COSArray getDocumentID()
    {
        return (COSArray) getTrailer().getItem(COSName.getPDFName("ID"));
    }

    /**
     * This will set the document ID.
     *
     * @param id The document id.
     */
    public void setDocumentID( COSArray id )
    {
        getTrailer().setItem(COSName.getPDFName("ID"), id);
    }

    /**
     * This will add an object to this document.
     * the method checks if obj is already present as there may be cyclic dependencies
     *
     * @param obj The object to add to the document.
     */
    public void addObject(COSObject obj)
    {
        // the wrapped object may be null when not completely parsed...
        if (obj.getObject()== null || hasObject(obj))
        {
            // break cycle if already contained
            return;
        }
        objects.add(obj);
        objectMap.put(obj.getObject(), obj);
        if (obj.getObject() != null)
        {
            // call recursive for contained objects
            obj.getObject().addTo(this);
        }
    }

    /**
     * This will create an object for this document.
     *
     * Create an indirect object out of the direct type and include in the document
     * for later lookup via document a map from direct object to indirect object
     * is maintained. this provides better support for manual PDF construction.
     *
     * @param base the base object to wrap in an indirect object.
     *
     * @return The pdf object that wraps the base, or creates a new one.
     */
    /**
    public COSObject createObject( COSBase base )
    {
        COSObject obj = (COSObject)objectMap.get(base);
        if (obj == null)
        {
            obj = new COSObject( base );
            obj.addTo(this);
        }
        return obj;
    }**/

    /**
     * This will get the document catalog.
     *
     * Maybe this should move to an object at PDFEdit level
     *
     * @return catalog is the root of all document activities
     *
     * @throws IOException If no catalog can be found.
     */
    public COSObject getCatalog() throws IOException
    {
        COSObject catalog = (COSObject)getObjectByType( COSName.CATALOG );
        if( catalog == null )
        {
            throw new IOException( "Catalog cannot be found" );
        }
        return catalog;
    }

    /**
     * This will get a list of all available objects.
     *
     * @return A list of all objects.
     */
    public List getObjects()
    {
        return new ArrayList(objects);
    }

    /**
     * This will get the document trailer.
     *
     * @return the document trailer dict
     */
    public COSDictionary getTrailer()
    {
        return trailer;
    }

    /**
     * // MIT added, maybe this should not be supported as trailer is a persistence construct.
     * This will set the document trailer.
     *
     * @param newTrailer the document trailer dictionary
     */
    public void setTrailer(COSDictionary newTrailer)
    {
        trailer = newTrailer;
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
        return visitor.visitFromDocument( this );
    }

    /**
     * return true if the object is already contained in this document.
     *
     * @param obj The object to check
     *
     * @return true If this document contains the object.
     */
    public boolean hasObject(COSObject obj)
    {
        return objectMap.get(obj.getObject()) != null;
    }

    /**
     * This will close all storage and delete the tmp files.
     *
     *  @throws IOException If there is an error close resources.
     */
    public void close() throws IOException
    {
        if( scratchFile != null )
        {
            scratchFile.close();
            scratchFile = null;
        }
        if( tmpFile != null )
        {
            tmpFile.delete();
            tmpFile = null;
        }
    }

    /**
     * The sole purpose of this is to inform a client of PDF2TXT that they
     * did not close the document.
     */
    protected void finalize()
    {
        if( tmpFile != null || scratchFile != null )
        {
            Throwable t = new Throwable( "Warning: You did not close the PDF Document" );
            t.printStackTrace();
        }
    }
    /**
     * @return Returns the headerString.
     */
    public String getHeaderString()
    {
        return headerString;
    }
    /**
     * @param header The headerString to set.
     */
    public void setHeaderString(String header)
    {
        headerString = header;
    }
}
