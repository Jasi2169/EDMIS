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
package com.progdan.pdf2txt.encryption;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;

import java.math.BigInteger;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.progdan.pdf2txt.exceptions.CryptographyException;
import com.progdan.pdf2txt.exceptions.InvalidPasswordException;

import com.progdan.pdf2txt.cos.COSArray;
import com.progdan.pdf2txt.cos.COSBase;
import com.progdan.pdf2txt.cos.COSDictionary;
import com.progdan.pdf2txt.cos.COSDocument;
import com.progdan.pdf2txt.cos.COSName;
import com.progdan.pdf2txt.cos.COSObject;
import com.progdan.pdf2txt.cos.COSStream;
import com.progdan.pdf2txt.cos.COSString;

import com.progdan.pdf2txt.pdmodel.PDDocument;

import com.progdan.pdf2txt.pdmodel.encryption.PDStandardEncryption;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * This class will deal with encrypting/decrypting a document.
 *
 * @author Ben Litchfield
 * @version $Revision: 1.2 $
 */
public class DocumentEncryption
{
    private static final COSName ENCRYPT = COSName.getPDFName( "Encrypt" );
    private PDDocument pdDocument = null;
    private COSDocument document = null;

    private byte[] encryptionKey = null;
    private PDFEncryption encryption = new PDFEncryption();

    private Set objects = new HashSet();

    /**
     * Constructor.
     *
     * @param doc The document to decrypt.
     */
    public DocumentEncryption( PDDocument doc )
    {
        pdDocument = doc;
        document = doc.getDocument();
    }

    /**
     * Constructor.
     *
     * @param doc The document to decrypt.
     */
    public DocumentEncryption( COSDocument doc )
    {
        pdDocument = new PDDocument( doc );
        document = doc;
    }

    /**
     * This will encrypt the given document, given the owner password and user password.
     * The encryption method used is the standard filter.
     *
     * @param ownerPassword The owner password for the encrypted pdf file.
     * @param userPassword The user password for the encrypted pdf file.
     *
     * @throws CryptographyException If an error occurs during encryption.
     * @throws IOException If there is an error accessing the data.
     */
    public void encryptDocument(String ownerPassword, String userPassword)
        throws CryptographyException, IOException
    {
        if( ownerPassword == null )
        {
            ownerPassword = "";
        }
        if( userPassword == null )
        {
            userPassword = "";
        }
        PDStandardEncryption encParameters = (PDStandardEncryption)pdDocument.getEncryptionDictionary();
        int permissionInt = encParameters.getPermissions();
        COSArray idArray = document.getDocumentID();

        //check if the document has an id yet.  If it does not then
        //generate one
        if( idArray == null || idArray.size() < 2 )
        {
            idArray = new COSArray();
            try
            {
                MessageDigest md = MessageDigest.getInstance( "MD5" );
                BigInteger time = BigInteger.valueOf( System.currentTimeMillis() );
                md.update( time.toByteArray() );
                md.update( ownerPassword.getBytes() );
                md.update( userPassword.getBytes() );
                md.update( document.toString().getBytes() );
                byte[] id = md.digest( this.toString().getBytes() );
                COSString idString = new COSString();
                idString.append( id );
                idArray.add( idString );
                idArray.add( idString );
                document.setDocumentID( idArray );
            }
            catch( NoSuchAlgorithmException e )
            {
                throw new CryptographyException( e );
            }

        }
        COSString id = (COSString)idArray.get( 0 );
        encryption = new PDFEncryption();

        byte[] o = encryption.computeOwnerPassword(
            ownerPassword.getBytes("ISO-8859-1"),
            userPassword.getBytes("ISO-8859-1"), 2, 5);

        byte[] u = encryption.computeUserPassword(
            userPassword.getBytes("ISO-8859-1"),
            o, permissionInt, id.getBytes(), 2, 5);

        encryptionKey = encryption.computeEncryptedKey(
            userPassword.getBytes("ISO-8859-1"), o, permissionInt, id.getBytes(), 2, 5);

        encParameters.setOwnerKey( o );
        encParameters.setUserKey( u );

        COSDictionary trailer = document.getTrailer();

        List allObjects = document.getObjects();
        Iterator objectIter = allObjects.iterator();
        while( objectIter.hasNext() )
        {
            decryptObject( (COSObject)objectIter.next() );
        }
        document.setEncryptionDictionary( encParameters.getCOSDictionary() );
    }

    /**
     * This will decrypt the document.
     *
     * @param password The password for the document.
     *
     * @throws CryptographyException If there is an error decrypting the document.
     * @throws IOException If there is an error getting the stream data.
     * @throws InvalidPasswordException If the password is not a user or owner password.
     */
    public void decryptDocument( String password )
        throws CryptographyException, IOException, InvalidPasswordException
    {
        if( password == null )
        {
            password = "";
        }
        long start = System.currentTimeMillis();
        COSDictionary trailer = document.getTrailer();

        PDStandardEncryption encParameters = (PDStandardEncryption)pdDocument.getEncryptionDictionary();


        int permissions = encParameters.getPermissions();
        int revision = encParameters.getRevision();
        int length = encParameters.getLength()/8;

        COSString id = (COSString)document.getDocumentID().get( 0 );
        byte[] u = encParameters.getUserKey();
        byte[] o = encParameters.getOwnerKey();

        boolean isUserPassword =
            encryption.isUserPassword( password.getBytes(), u,
                o, permissions, id.getBytes(), revision, length );
        boolean isOwnerPassword =
            encryption.isOwnerPassword( password.getBytes(), u,
                o, permissions, id.getBytes(), revision, length );

        if( isUserPassword )
        {
            encryptionKey =
                encryption.computeEncryptedKey(
                    password.getBytes(), o,
                    permissions, id.getBytes(), revision, length );
        }
        else if( isOwnerPassword )
        {
            byte[] computedUserPassword =
                encryption.getUserPassword(
                    password.getBytes(),
                    o,
                    revision,
                    length );
            encryptionKey =
                encryption.computeEncryptedKey(
                    computedUserPassword, o,
                    permissions, id.getBytes(), revision, length );
        }
        else
        {
            throw new InvalidPasswordException( "Error: The supplied password does not match " +
                                                "either the owner or user password in the document." );
        }

        List allObjects = document.getObjects();
        Iterator objectIter = allObjects.iterator();
        while( objectIter.hasNext() )
        {
            decryptObject( (COSObject)objectIter.next() );
        }
        document.setEncryptionDictionary( null );
    }

    /**
     * This will print a byte array as a hex string to standard output.
     *
     * @param data The array to print.
     */
    private static void printHexString( byte[] data )
    {
        for( int i=0; i<data.length; i++ )
        {
            int nextByte = (data[i] + 256)%256;
            String hexString = Integer.toHexString( nextByte );
            if( hexString.length() < 2 )
            {
                hexString = "0" + hexString;
            }
            System.out.print( hexString );
            if( i != 0 && (i+1) % 2 == 0 )
            {
                System.out.print( " " );
            }
            else if( i!= 0 &&i % 20 == 0 )
            {
                System.out.println();
            }
        }
        System.out.println();
    }

    /**
     * This will decrypt an object in the document.
     *
     * @param object The object to decrypt.
     *
     * @throws CryptographyException If there is an error decrypting the stream.
     * @throws IOException If there is an error getting the stream data.
     */
    private void decryptObject( COSObject object )
        throws CryptographyException, IOException
    {
        long objNum = object.getObjectNumber().intValue();
        long genNum = object.getGenerationNumber().intValue();
        COSBase base = object.getObject();
        decrypt( base, objNum, genNum );
    }

    /**
     * This will dispatch to the correct method.
     *
     * @param obj The object to decrypt.
     * @param objNum The object number.
     * @param genNum The object generation Number.
     *
     * @throws CryptographyException If there is an error decrypting the stream.
     * @throws IOException If there is an error getting the stream data.
     */
    private void decrypt( Object obj, long objNum, long genNum )
        throws CryptographyException, IOException
    {
        if( !objects.contains( obj ) )
        {
            objects.add( obj );

            if( obj instanceof COSString )
            {
                decryptString( (COSString)obj, objNum, genNum );
            }
            else if( obj instanceof COSDictionary )
            {
                decryptDictionary( (COSDictionary)obj, objNum, genNum );
            }
            else if( obj instanceof COSStream )
            {
                decryptStream( (COSStream)obj, objNum, genNum );
            }
            else if( obj instanceof COSArray )
            {
                decryptArray( (COSArray)obj, objNum, genNum );
            }
        }
    }

    /**
     * This will decrypt a stream.
     *
     * @param stream The stream to decrypt.
     * @param objNum The object number.
     * @param genNum The object generation number.
     *
     * @throws CryptographyException If there is an error getting the stream.
     * @throws IOException If there is an error getting the stream data.
     */
    private void decryptStream( COSStream stream, long objNum, long genNum )
        throws CryptographyException, IOException
    {
        decryptDictionary( stream.getDictionary(), objNum, genNum );
        InputStream encryptedStream = stream.getFilteredStream();
        encryption.encryptData( objNum,
                                genNum,
                                encryptionKey,
                                encryptedStream,
                                stream.createFilteredStream() );
    }

    /**
     * This will decrypt a dictionary.
     *
     * @param dictionary The dictionary to decrypt.
     * @param objNum The object number.
     * @param genNum The object generation number.
     *
     * @throws CryptographyException If there is an error decrypting the document.
     * @throws IOException If there is an error creating a new string.
     */
    private void decryptDictionary( COSDictionary dictionary, long objNum, long genNum )
        throws CryptographyException, IOException
    {
        Iterator values = dictionary.getValues().iterator();
        while( values.hasNext() )
        {
            decrypt( values.next(), objNum, genNum );
        }
    }

    /**
     * This will decrypt a string.
     *
     * @param string the string to decrypt.
     * @param objNum The object number.
     * @param genNum The object generation number.
     *
     * @throws CryptographyException If an error occurs during decryption.
     * @throws IOException If an error occurs writing the new string.
     */
    private void decryptString( COSString string, long objNum, long genNum )
        throws CryptographyException, IOException
    {
        ByteArrayInputStream data = new ByteArrayInputStream( string.getBytes() );
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        encryption.encryptData( objNum,
                                genNum,
                                encryptionKey,
                                data,
                                buffer );
        string.reset();
        string.append( buffer.toByteArray() );
    }

    /**
     * This will decrypt an array.
     *
     * @param array The array to decrypt.
     * @param objNum The object number.
     * @param genNum The object generation number.
     *
     * @throws CryptographyException If an error occurs during decryption.
     * @throws IOException If there is an error accessing the data.
     */
    private void decryptArray( COSArray array, long objNum, long genNum )
        throws CryptographyException, IOException
    {
        for( int i=0; i<array.size(); i++ )
        {
            decrypt( array.get( i ), objNum, genNum );
        }
    }
}
