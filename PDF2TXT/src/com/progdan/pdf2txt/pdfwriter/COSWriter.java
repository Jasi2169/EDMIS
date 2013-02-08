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
package com.progdan.pdf2txt.pdfwriter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import com.progdan.pdf2txt.persistence.util.COSObjectKey;
import com.progdan.pdf2txt.persistence.util.COSHEXTable;

import com.progdan.pdf2txt.cos.COSBase;
import com.progdan.pdf2txt.cos.COSFloat;
import com.progdan.pdf2txt.cos.ICOSVisitor;
import com.progdan.pdf2txt.cos.COSName;
import com.progdan.pdf2txt.cos.COSString;
import com.progdan.pdf2txt.cos.COSBoolean;
import com.progdan.pdf2txt.cos.COSArray;
import com.progdan.pdf2txt.cos.COSDocument;
import com.progdan.pdf2txt.cos.COSStream;
import com.progdan.pdf2txt.cos.COSObject;
import com.progdan.pdf2txt.exceptions.COSVisitorException;
import com.progdan.pdf2txt.cos.COSDictionary;
import com.progdan.pdf2txt.cos.COSInteger;
import com.progdan.pdf2txt.cos.COSNull;

import com.progdan.pdf2txt.pdmodel.PDDocument;

import com.progdan.logengine.Logger;

/**
 * this class acts on a in-memory representation of a pdf document.
 *
 * todo no support for incremental updates
 * todo single xref section only
 * todo no linearization
 *
 * @author Michael Traut
 * @version $Revision: 1.2 $
 */
public class COSWriter implements ICOSVisitor
{

    private static Logger log = Logger.getLogger( COSWriter.class );

    /**
     * space character.
     */
    public static final byte[] SPACE = " ".getBytes();
    /**
     * The start to a PDF comment.
     */
    public static final byte[] COMMENT = "%".getBytes();

    /**
     * The output version of the PDF.
     */
    public static final byte[] VERSION = "PDF-1.4".getBytes();
    /**
     * Garbage bytes used to create the PDF header.
     */
    public static final byte[] GARBAGE = new byte[] {(byte)0xf6, (byte)0xe4, (byte)0xfc, (byte)0xdf};
    /**
     * The EOF constant.
     */
    public static final byte[] EOF = "%%EOF".getBytes();
    /**
     * The prefix to a PDF name.
     */
    public static final byte[] NAME_PREFIX = "/".getBytes();
    /**
     * The escape character for a name.
     */
    public static final byte[] NAME_ESCAPE = "#".getBytes();
    // pdf tokens

    /**
     * The reference token.
     */
    public static final byte[] REFERENCE = "R".getBytes();
    /**
     * The null token.
     */
    public static final byte[] NULL = "null".getBytes();
    /**
     * The true boolean token.
     */
    public static final byte[] TRUE = "true".getBytes();
    /**
     * The false boolean token.
     */
    public static final byte[] FALSE = "false".getBytes();
    /**
     * The XREF token.
     */
    public static final byte[] XREF = "xref".getBytes();
    /**
     * The xref free token.
     */
    public static final byte[] XREF_FREE = "f".getBytes();
    /**
     * The xref used token.
     */
    public static final byte[] XREF_USED = "n".getBytes();
    /**
     * The trailer token.
     */
    public static final byte[] TRAILER = "trailer".getBytes();
    /**
     * The start xref token.
     */
    public static final byte[] STARTXREF = "startxref".getBytes();
    /**
     * The starting object token.
     */
    public static final byte[] OBJ = "obj".getBytes();
    /**
     * The end object token.
     */
    public static final byte[] ENDOBJ = "endobj".getBytes();
    /**
     * One of the open string tokens.
     */
    public static final byte[] STRING_OPEN = "(".getBytes();
    /**
     * One of the close string tokens.
     */
    public static final byte[] STRING_CLOSE = ")".getBytes();
    /**
     * One of the open string tokens.
     */
    public static final byte[] HEX_STRING_OPEN = "<".getBytes();
    /**
     * One of the close string tokens.
     */
    public static final byte[] HEX_STRING_CLOSE = ">".getBytes();
    /**
     * The array open token.
     */
    public static final byte[] ARRAY_OPEN = "[".getBytes();
    /**
     * The array close token.
     */
    public static final byte[] ARRAY_CLOSE = "]".getBytes();
    /**
     * The dictionary open token.
     */
    public static final byte[] DICT_OPEN = "<<".getBytes();
    /**
     * The dictionary close token.
     */
    public static final byte[] DICT_CLOSE = ">>".getBytes();
    /**
     * The open stream token.
     */
    public static final byte[] STREAM = "stream".getBytes();
    /**
     * The close stream token.
     */
    public static final byte[] ENDSTREAM = "endstream".getBytes();

    private NumberFormat formatXrefOffset = new DecimalFormat("0000000000");
    /**
     * The decimal format for the xref object generation number data.
     */
    private NumberFormat formatXrefGeneration = new DecimalFormat("00000");

    private NumberFormat formatDecimal = NumberFormat.getNumberInstance();

    // the stream where we create the pdf output
    private OutputStream output;
    // the stream used to write standard cos data
    private COSStandardOutputStream standardOutput;
    // the stream used to write escaped cos data
    private COSEscapedOutputStream escapedOutput;

    // the start position of the x ref section
    private long startxref = 0;

    // the current object number
    private long number = 0;

    // maps the object to the keys generated in the writer
    // these are used for indirect refrences in other objects
    private Map objectKeys = new HashMap();

    // the list of x ref entries to be made so far
    private List xRefEntries = new ArrayList();

    //A list of objects to write.
    private List objectsToWrite = new ArrayList();

    //a list of objects already written
    private Set writtenObjects = new HashSet();

    /**
     * COSWriter constructor comment.
     *
     * @param os The wrapped output stream.
     */
    public COSWriter(OutputStream os)
    {
        super();
        setOutput(os);
        setStandardOutput(new COSStandardOutputStream(getOutput()));
        setEscapedOutput(new COSEscapedOutputStream(getStandardOutput()));
        formatDecimal.setMaximumFractionDigits( 10 );
    }
    /**
     * add an entry in the x ref table for later dump.
     *
     * @param entry The new entry to add.
     */
    protected void addXRefEntry(COSWriterXRefEntry entry)
    {
        getXRefEntries().add(entry);
    }

    /**
     * This will close the stream.
     *
     * @throws IOException If the underlying stream throws an exception.
     */
    public void close() throws IOException
    {
        if (getEscapedOutput() != null)
        {
            getEscapedOutput().close();
        }
        if (getStandardOutput() != null)
        {
            getStandardOutput().close();
        }
        if (getOutput() != null)
        {
            getOutput().close();
        }
    }

    /**
     * This will get the escaped output stream.
     *
     * @return The escaped output stream.
     */
    protected COSEscapedOutputStream getEscapedOutput()
    {
        return escapedOutput;
    }

    /**
     * This will get the current object number.
     *
     * @return The current object number.
     */
    protected long getNumber()
    {
        return number;
    }

    /**
     * This will get all available object keys.
     *
     * @return A map of all object keys.
     */
    public java.util.Map getObjectKeys()
    {
        return objectKeys;
    }

    /**
     * This will get the output stream.
     *
     * @return The output stream.
     */
    protected java.io.OutputStream getOutput()
    {
        return output;
    }

    /**
     * This will get the standard output stream.
     *
     * @return The standard output stream.
     */
    protected COSStandardOutputStream getStandardOutput()
    {
        return standardOutput;
    }

    /**
     * This will get the current start xref.
     *
     * @return The current start xref.
     */
    protected long getStartxref()
    {
        return startxref;
    }
    /**
     * This will get the xref entries.
     *
     * @return All available xref entries.
     */
    protected java.util.List getXRefEntries()
    {
        return xRefEntries;
    }

    /**
     * This will set the escaped output stream.
     *
     * @param newEscapedOutput The new escaped output stream.
     */
    private void setEscapedOutput(COSEscapedOutputStream newEscapedOutput)
    {
        escapedOutput = newEscapedOutput;
    }

    /**
     * This will set the current object number.
     *
     * @param newNumber The new object number.
     */
    protected void setNumber(long newNumber)
    {
        number = newNumber;
    }

    /**
     * This will set the object keys.
     *
     * @param newObjectKeys the new object keys.
     */
    private void setObjectKeys( Map newObjectKeys )
    {
        objectKeys = newObjectKeys;
    }

    /**
     * This will set the output stream.
     *
     * @param newOutput The new output stream.
     */
    private void setOutput( OutputStream newOutput )
    {
        output = newOutput;
    }

    /**
     * This will set the standard output stream.
     *
     * @param newStandardOutput The new standard output stream.
     */
    private void setStandardOutput(COSStandardOutputStream newStandardOutput)
    {
        standardOutput = newStandardOutput;
    }

    /**
     * This will set the start xref.
     *
     * @param newStartxref The new start xref attribute.
     */
    protected void setStartxref(long newStartxref)
    {
        startxref = newStartxref;
    }

    /**
     * This will set the list of xref entries.
     *
     * @param newXrefEntries The new list of all xref entries.
     */
    private void setXRefEntries( List newXRefEntries )
    {
        xRefEntries = newXRefEntries;
    }

    /**
     * This will write the body of the document.
     *
     * @param doc The document to write the body for.
     *
     * @throws IOException If there is an error writing the data.
     * @throws COSVisitorException If there is an error generating the data.
     */
    protected void doWriteBody(COSDocument doc) throws IOException, COSVisitorException
    {
        COSDictionary trailer = doc.getTrailer();
        COSDictionary root = (COSDictionary)trailer.getDictionaryObject( COSName.ROOT );
        COSDictionary info = (COSDictionary)trailer.getDictionaryObject( COSName.getPDFName( "Info" ) );
        COSDictionary encrypt = (COSDictionary)trailer.getDictionaryObject( COSName.getPDFName( "Encrypt" ) );
        if( root != null )
        {
            addObjectToWrite( root );
        }
        if( info != null )
        {
            addObjectToWrite( info );
        }
        if( encrypt != null )
        {
            addObjectToWrite( encrypt );
        }

        while( objectsToWrite.size() > 0 )
        {
            COSBase nextObject = (COSBase)objectsToWrite.remove( 0 );
            doWriteObject( nextObject );
        }

        // write all objects
        /**
        for (Iterator i = doc.getObjects().iterator(); i.hasNext();)
        {
            COSObject obj = (COSObject) i.next();
            doWriteObject(obj);
        }**/
    }

    private void addObjectToWrite( COSBase object )
    {
        if( !writtenObjects.contains( object ) &&
            !objectsToWrite.contains( object ) )
        {
            objectsToWrite.add( object );
        }
    }

    /**
     * This will write a COS object.
     *
     * @param obj The object to write.
     *
     * @throws COSVisitorException If there is an error visiting objects.
     */
    public void doWriteObject( COSBase obj ) throws COSVisitorException
    {
        try
        {
            writtenObjects.add( obj );
            // find the physical reference
            COSObjectKey key = getObjectKey( obj );
            // add a x ref entry
            addXRefEntry( new COSWriterXRefEntry(getStandardOutput().getPos(), obj, key));
            // write the object
            getStandardOutput().write(String.valueOf(key.getNumber()).getBytes());
            getStandardOutput().write(SPACE);
            getStandardOutput().write(String.valueOf(key.getGeneration()).getBytes());
            getStandardOutput().write(SPACE);
            getStandardOutput().write(OBJ);
            getStandardOutput().writeEOL();
            obj.accept( this );
            getStandardOutput().writeEOL();
            getStandardOutput().write(ENDOBJ);
            getStandardOutput().writeEOL();
        }
        catch (IOException e)
        {
            throw new COSVisitorException(e);
        }
    }

    /**
     * This will write the header to the PDF document.
     *
     * @param doc The document to get the data from.
     *
     * @throws IOException If there is an error writing to the stream.
     */
    protected void doWriteHeader(COSDocument doc) throws IOException
    {
        getStandardOutput().write( doc.getHeaderString().getBytes() );
        getStandardOutput().writeEOL();
        getStandardOutput().write(COMMENT);
        getStandardOutput().write(GARBAGE);
        getStandardOutput().writeEOL();
    }


    /**
     * This will write the trailer to the PDF document.
     *
     * @param doc The document to create the trailer for.
     *
     * @throws IOException If there is an IOError while writing the document.
     * @throws COSVisitorException If there is an error while generating the data.
     */
    protected void doWriteTrailer(COSDocument doc) throws IOException, COSVisitorException
    {
        getStandardOutput().write(TRAILER);
        getStandardOutput().writeEOL();

        COSDictionary trailer = doc.getTrailer();
        trailer.setItem(COSName.getPDFName("Size"), new COSInteger( getXRefEntries().size()+1));
        trailer.removeItem( COSName.PREV );
        /**
        COSObject catalog = doc.getCatalog();
        if (catalog != null)
        {
            trailer.setItem(COSName.getPDFName("Root"), catalog);
        }
        */
        trailer.accept(this);

        getStandardOutput().write(STARTXREF);
        getStandardOutput().writeEOL();
        getStandardOutput().write(String.valueOf(getStartxref()).getBytes());
        getStandardOutput().writeEOL();
        getStandardOutput().write(EOF);
    }

    /**
     * write the x ref section for the pdf file
     *
     * currently, the pdf is reconstructed from the scratch, so we write a single section
     *
     * todo support for incremental writing?
     *
     * @param doc The document to write the xref from.
     *
     * @throws IOException If there is an error writing the data to the stream.
     */
    protected void doWriteXRef(COSDocument doc) throws IOException
    {
        String offset;
        String generation;

        // sort xref, needed only if object keys not regenerated
        Collections.sort(getXRefEntries());
        COSWriterXRefEntry lastEntry = (COSWriterXRefEntry)getXRefEntries().get( getXRefEntries().size()-1 );

        // remember the position where x ref is written
        setStartxref(getStandardOutput().getPos());
        //
        getStandardOutput().write(XREF);
        getStandardOutput().writeEOL();
        // write start object number and object count for this x ref section
        // we assume starting from scratch
        getStandardOutput().write(String.valueOf(0).getBytes());
        getStandardOutput().write(SPACE);
        getStandardOutput().write(String.valueOf(lastEntry.getKey().getNumber() + 1).getBytes());
        getStandardOutput().writeEOL();
        // write initial start object with ref to first deleted object and magic generation number
        offset = formatXrefOffset.format(0);
        generation = formatXrefGeneration.format(65535);
        getStandardOutput().write(offset.getBytes());
        getStandardOutput().write(SPACE);
        getStandardOutput().write(generation.getBytes());
        getStandardOutput().write(SPACE);
        getStandardOutput().write(XREF_FREE);
        getStandardOutput().writeCRLF();
        // write entry for every object
        long lastObjectNumber = 0;
        for (Iterator i = getXRefEntries().iterator(); i.hasNext();)
        {
            COSWriterXRefEntry entry = (COSWriterXRefEntry) i.next();
            while( lastObjectNumber<entry.getKey().getNumber()-1 )
            {
                offset = formatXrefOffset.format(0);
                generation = formatXrefGeneration.format(65535);
                getStandardOutput().write(offset.getBytes());
                getStandardOutput().write(SPACE);
                getStandardOutput().write(generation.getBytes());
                getStandardOutput().write(SPACE);
                getStandardOutput().write(XREF_FREE);
                getStandardOutput().writeCRLF();
                lastObjectNumber++;
            }
            lastObjectNumber = entry.getKey().getNumber();
            offset = formatXrefOffset.format(entry.getOffset());
            generation = formatXrefGeneration.format(entry.getKey().getGeneration());
            getStandardOutput().write(offset.getBytes());
            getStandardOutput().write(SPACE);
            getStandardOutput().write(generation.getBytes());
            getStandardOutput().write(SPACE);
            getStandardOutput().write(entry.isFree() ? XREF_FREE : XREF_USED);
            getStandardOutput().writeCRLF();
        }
    }

    /**
     * This will get the object key for the object.
     *
     * @param obj The object to get the key for.
     *
     * @return The object key for the object.
     */
    public COSObjectKey getObjectKey( COSBase obj )
    {
        COSObjectKey  key = (COSObjectKey )objectKeys.get(obj);
        if (key == null)
        {
            setNumber(getNumber()+1);
            key = new COSObjectKey(getNumber(),0);
            objectKeys.put(obj, key);
        }
        return key;
    }

    /**
     * visitFromArray method comment.
     *
     * @param obj The object that is being visited.
     *
     * @throws COSVisitorException If there is an exception while visiting this object.
     *
     * @return null
     */
    public Object visitFromArray( COSArray obj ) throws COSVisitorException
    {
        try
        {
            int count = 0;
            getStandardOutput().write(ARRAY_OPEN);
            for (Iterator i = obj.iterator(); i.hasNext();)
            {
                COSBase current = (COSBase) i.next();
                //a quick fix until parsing is fixed
                if( current instanceof com.progdan.pdf2txt.cos.COSObject )
                {
                    current = ((com.progdan.pdf2txt.cos.COSObject)current).getObject();
                }
                if( current instanceof COSDictionary ||
                    current instanceof COSStream )
                {
                    addObjectToWrite( current );
                    writeReference( current );
                }
                else
                {
                    current.accept(this);
                }
                count++;
                if (i.hasNext())
                {
                    if (count % 10 == 0)
                    {
                        getStandardOutput().writeEOL();
                    }
                    else
                    {
                        getStandardOutput().write(SPACE);
                    }
                }
            }
            getStandardOutput().write(ARRAY_CLOSE);
            getStandardOutput().writeEOL();
            return null;
        }
        catch (IOException e)
        {
            throw new COSVisitorException(e);
        }
    }

    /**
     * visitFromBoolean method comment.
     *
     * @param obj The object that is being visited.
     *
     * @throws COSVisitorException If there is an exception while visiting this object.
     *
     * @return null
     */
    public Object visitFromBoolean(COSBoolean obj) throws COSVisitorException
    {
        try
        {
            if (obj.getValue())
            {
                getStandardOutput().write(TRUE);
            }
            else
            {
                getStandardOutput().write(FALSE);
            }
            return null;
        }
        catch (IOException e)
        {
            throw new COSVisitorException(e);
        }
    }

    /**
     * visitFromDictionary method comment.
     *
     * @param obj The object that is being visited.
     *
     * @throws COSVisitorException If there is an exception while visiting this object.
     *
     * @return null
     */
    public Object visitFromDictionary(COSDictionary obj) throws COSVisitorException
    {
        try
        {
            getStandardOutput().write(DICT_OPEN);
            getStandardOutput().writeEOL();
            for (Iterator i = obj.keyList().iterator(); i.hasNext();)
            {
                COSName name = (COSName) i.next();
                COSBase value = obj.getItem(name);
                if (value != null)
                {
                    // this is purely defensive, if entry is set to null instead of removed

                    //a quick fix until parsing is fixed
                    if( value instanceof com.progdan.pdf2txt.cos.COSObject )
                    {
                        value = ((com.progdan.pdf2txt.cos.COSObject)value).getObject();
                    }
                    if( value != null )
                    {
                        name.accept(this);
                        getStandardOutput().write(SPACE);
                    }


                    if( value == null )
                    {
                        log.debug( "Value is null" );
                        //then we won't write anything, there are a couple cases
                        //were the value of an entry in the COSDictionary will
                        //be a dangling reference that points to nothing
                        //so we will just not write out the entry if that is the case
                    }
                    else if( value instanceof COSDictionary ||
                        value instanceof COSStream )
                    {
                        addObjectToWrite( value );
                        writeReference( value );
                    }
                    else
                    {
                        value.accept(this);

                    }
                    getStandardOutput().writeEOL();

                }
            }
            getStandardOutput().write(DICT_CLOSE);
            getStandardOutput().writeEOL();
            return null;
        }
        catch( IOException e )
        {
            throw new COSVisitorException(e);
        }
    }

    /**
     * The visit from document method.
     *
     * @param doc The object that is being visited.
     *
     * @throws COSVisitorException If there is an exception while visiting this object.
     *
     * @return null
     */
    public Object visitFromDocument(COSDocument doc) throws COSVisitorException
    {
        try
        {
            doWriteHeader(doc);
            doWriteBody(doc);
            doWriteXRef(doc);
            doWriteTrailer(doc);
            return null;
        }
        catch (IOException e)
        {
            throw new COSVisitorException(e);
        }
    }

    /**
     * visitFromFloat method comment.
     *
     * @param obj The object that is being visited.
     *
     * @throws COSVisitorException If there is an exception while visiting this object.
     *
     * @return null
     */
    public Object visitFromFloat(COSFloat obj) throws COSVisitorException
    {
        try
        {
            getStandardOutput().write(formatDecimal.format( obj.floatValue() ).getBytes());
            return null;
        }
        catch (IOException e)
        {
            throw new COSVisitorException(e);
        }
    }

    /**
     * visitFromFloat method comment.
     *
     * @param obj The object that is being visited.
     *
     * @throws COSVisitorException If there is an exception while visiting this object.
     *
     * @return null
     */
    public Object visitFromInt(COSInteger obj) throws COSVisitorException
    {
        try
        {
            getStandardOutput().write(String.valueOf(obj.intValue()).getBytes());
            return null;
        }
        catch (IOException e)
        {
            throw new COSVisitorException(e);
        }
    }

    /**
     * visitFromName method comment.
     *
     * @param obj The object that is being visited.
     *
     * @throws COSVisitorException If there is an exception while visiting this object.
     *
     * @return null
     */
    public Object visitFromName(COSName obj) throws COSVisitorException
    {
        try
        {
            getStandardOutput().write(NAME_PREFIX);
            byte[] bytes = obj.getName().getBytes();
            for (int i = 0; i < bytes.length;i++)
            {
                int current = ((bytes[i]+256)%256);

                if(current <= 32 || current >= 127 ||
                   current == '(' ||
                   current == ')' ||
                   current == '[' ||
                   current == ']' ||
                   current == '/' ||
                   current == '%' ||
                   current == NAME_ESCAPE[0] )
                {
                    getStandardOutput().write(NAME_ESCAPE);
                    getStandardOutput().write(COSHEXTable.TABLE[current]);
                }
                else
                {
                    getStandardOutput().write(current);
                }
            }
            return null;
        }
        catch (IOException e)
        {
            throw new COSVisitorException(e);
        }
    }

    /**
     * visitFromNull method comment.
     *
     * @param obj The object that is being visited.
     *
     * @throws COSVisitorException If there is an exception while visiting this object.
     *
     * @return null
     */
    public Object visitFromNull(COSNull obj) throws COSVisitorException
    {
        try
        {
            getStandardOutput().write(NULL);
            return null;
        }
        catch (IOException e)
        {
            throw new COSVisitorException(e);
        }
    }

    /**
     * visitFromObjRef method comment.
     *
     * @param obj The object that is being visited.
     *
     * @throws COSVisitorException If there is an exception while visiting this object.
     */
    public void writeReference(COSBase obj) throws COSVisitorException
    {
        try
        {
            COSObjectKey  key = getObjectKey(obj);
            getStandardOutput().write(String.valueOf(key.getNumber()).getBytes());
            getStandardOutput().write(SPACE);
            getStandardOutput().write(String.valueOf(key.getGeneration()).getBytes());
            getStandardOutput().write(SPACE);
            getStandardOutput().write(REFERENCE);
        }
        catch (IOException e)
        {
            throw new COSVisitorException(e);
        }
    }

    /**
     * visitFromStream method comment.
     *
     * @param obj The object that is being visited.
     *
     * @throws COSVisitorException If there is an exception while visiting this object.
     *
     * @return null
     */
    public Object visitFromStream(COSStream obj) throws COSVisitorException
    {
        try
        {
            InputStream input = obj.getFilteredStream();
            // set the length of the stream and write stream dictionary
            COSInteger length = new COSInteger( input.available() );
            obj.getDictionary().setItem(COSName.LENGTH, length);
            obj.getDictionary().accept(this);
            // write the stream content
            getStandardOutput().write(STREAM);
            getStandardOutput().writeCRLF();
            byte[] buffer = new byte[1024];
            int amountRead = 0;
            while( (amountRead = input.read(buffer,0,1024)) != -1 )
            {
                getStandardOutput().write( buffer, 0, amountRead );
            }
            getStandardOutput().writeCRLF();
            getStandardOutput().write(ENDSTREAM);
            getStandardOutput().writeEOL();
            return null;
        }
        catch (IOException e)
        {
            throw new COSVisitorException(e);
        }
    }

    /**
     * visitFromString method comment.
     *
     * @param obj The object that is being visited.
     *
     * @return null
     *
     * @throws COSVisitorException If there is an exception while visiting this object.
     */
    public Object visitFromString(COSString obj) throws COSVisitorException
    {
        try
        {
            boolean outsideASCII = false;
            //Lets first check if we need to escape this string.
            byte[] bytes = obj.getBytes();
            for( int i=0; i<bytes.length && !outsideASCII; i++ )
            {
                //if the byte is negative then it is an eight bit byte and is
                //outside the ASCII range.
                outsideASCII = bytes[i] <0;
            }
            if( !outsideASCII )
            {
                getStandardOutput().write(STRING_OPEN);
                getEscapedOutput().write(obj.getString().getBytes());
                getStandardOutput().write(STRING_CLOSE);
            }
            else
            {
                getStandardOutput().write(HEX_STRING_OPEN);
                for(int i=0; i<bytes.length; i++ )
                {
                    getEscapedOutput().write( COSHEXTable.TABLE[ (bytes[i]+256)%256 ] );
                }
                getStandardOutput().write(HEX_STRING_CLOSE);
            }

            return null;
        }
        catch (IOException e)
        {
            throw new COSVisitorException(e);
        }
    }

    /**
     * This will write the pdf document.
     *
     * @param doc The document to write.
     *
     * @throws COSVisitorException If an error occurs while generating the data.
     */
    public void write(COSDocument doc) throws COSVisitorException
    {
        List objects = doc.getObjects();
        Iterator iter = objects.iterator();
        long maxNumber = 0;
        while( iter.hasNext() )
        {
            COSObject object = (COSObject)iter.next();
            if( object.getObjectNumber() != null &&
                object.getGenerationNumber() != null )
            {
                COSObjectKey key = new COSObjectKey( object.getObjectNumber().longValue(),
                                                     object.getGenerationNumber().longValue() );
                objectKeys.put( object.getObject(), key );
                maxNumber = Math.max( key.getNumber(), maxNumber );
                setNumber( maxNumber );
            }
        }
        doc.accept(this);
    }

    /**
     * This will write the pdf document.
     *
     * @param doc The document to write.
     *
     * @throws COSVisitorException If an error occurs while generating the data.
     */
    public void write(PDDocument doc) throws COSVisitorException
    {
        write( doc.getDocument() );
    }
}
