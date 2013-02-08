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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.progdan.pdf2txt.cos.COSArray;
import com.progdan.pdf2txt.cos.COSDictionary;
import com.progdan.pdf2txt.cos.COSDocument;
import com.progdan.pdf2txt.cos.COSInteger;
import com.progdan.pdf2txt.cos.COSName;
import com.progdan.pdf2txt.cos.COSStream;
import com.progdan.pdf2txt.cos.COSString;
import com.progdan.pdf2txt.cos.COSObject;

import com.progdan.pdf2txt.encryption.PDFEncryption;
import com.progdan.pdf2txt.encryption.DocumentEncryption;

import com.progdan.pdf2txt.exceptions.COSVisitorException;
import com.progdan.pdf2txt.exceptions.CryptographyException;
import com.progdan.pdf2txt.exceptions.InvalidPasswordException;

import com.progdan.pdf2txt.pdfparser.PDFParser;

import com.progdan.pdf2txt.pdfwriter.COSWriter;

import com.progdan.pdf2txt.pdmodel.encryption.PDEncryptionDictionary;
import com.progdan.pdf2txt.pdmodel.encryption.PDEncryptionManager;
import com.progdan.pdf2txt.pdmodel.encryption.PDStandardEncryption;

/**
 * This is the in-memory representation of the PDF document.  You need to call
 * close() on this object when you are done using it!!
 *
 * @author Ben Litchfield (ben@csh.rit.edu)
 * @version $Revision: 1.2 $
 */
public class PDDocument
{
    private COSDocument document;

    //cached values
    private PDDocumentInformation documentInformation;
    private PDDocumentCatalog documentCatalog;

    //The encParameters will be cached here.  When the document is decrypted then
    //the COSDocument will not have an "Encrypt" dictionary anymore and this object
    //must be used.
    private PDEncryptionDictionary encParameters = null;

    /**
     * Constructor, creates a new PDF Document with no pages.  You need to add
     * at least one page for the document to be valid.
     *
     * @throws IOException If there is an error creating this document.
     */
    public PDDocument() throws IOException
    {
        document = new COSDocument();

        //First we need a trailer
        COSDictionary trailer = new COSDictionary();
        document.setTrailer( trailer );

        //Next we need the root dictionary.
        COSDictionary rootDictionary = new COSDictionary();
        COSObject root = new COSObject( rootDictionary );
        document.addObject( root );
        trailer.setItem( COSName.getPDFName( "Root" ), root );
        rootDictionary.setItem( COSName.getPDFName( "Type" ), COSName.getPDFName( "Catalog" ) );
        rootDictionary.setItem( COSName.getPDFName( "Version" ), COSName.getPDFName( "1.4" ) );

        //next we need the pages tree structure
        COSDictionary pages = new COSDictionary();
        COSObject pagesObject = new COSObject( pages );
        document.addObject( pagesObject );
        rootDictionary.setItem( COSName.getPDFName( "Pages" ), pagesObject );
        pages.setItem( COSName.getPDFName( "Type" ), COSName.getPDFName( "Pages" ) );
        COSArray kidsArray = new COSArray();
        pages.setItem( COSName.getPDFName( "Kids" ), kidsArray );
        pages.setItem( COSName.getPDFName( "Count" ), new COSInteger( 0 ) );
    }

    /**
     * This will add a page to the document.  This is a convenience method, that
     * will add the page to the root of the hierarchy and set the parent of the
     * page to the root.
     *
     * @param page The page to add to the document.
     */
    public void addPage( PDPage page )
    {
        PDPageNode rootPages = getDocumentCatalog().getPages();
        rootPages.getKids().add( page );
        page.setParent( rootPages );
        rootPages.updateCount();
    }

    /**
     * This will import and copy the contents from another location.  Currently
     * the content stream is stored in a scratch file.  The scratch file is
     * associated with the document.  If you are adding a page to this document
     * from another document and want to copy the contents to this document's
     * scratch file then use this method otherwise just use the addPage method.
     *
     * @param page The page to import.
     * @return The page that was imported.
     *
     * @throws IOException If there is an error copying the page.
     */
    public PDPage importPage( PDPage page ) throws IOException
    {
        PDPage importedPage = new PDPage( new COSDictionary( page.getCOSDictionary() ) );
        InputStream is = null;
        OutputStream os = null;
        try
        {
            COSStream src = page.getContents();
            COSStream dest = new COSStream( src.getDictionary(), document.getScratchFile() );
            importedPage.setContents( dest );
            os = dest.createUnfilteredStream();

            byte[] buf = new byte[10240];
            int amountRead = 0;
            is = src.getUnfilteredStream();
            while((amountRead = is.read(buf,0,10240)) > -1)
            {
                os.write(buf, 0, amountRead);
            }
            addPage( importedPage );
        }
        finally
        {
            if( is != null )
            {
                is.close();
            }
            if( os != null )
            {
                os.close();
            }
        }
        return importedPage;

    }

    /**
     * Constructor that uses an existing document.  The COSDocument that
     * is passed in must be valid.
     *
     * @param doc The COSDocument that this document wraps.
     */
    public PDDocument( COSDocument doc )
    {
        document = doc;
    }

    /**
     * This will get the low level document.
     *
     * @return The document that this layer sits on top of.
     */
    public COSDocument getDocument()
    {
        return document;
    }

    /**
     * This will get the document info dictionary.  This is guaranteed to not return null.
     *
     * @return The documents /Info dictionary
     */
    public PDDocumentInformation getDocumentInformation()
    {
        if( documentInformation == null )
        {
            COSName infoName = COSName.getPDFName( "Info" );
            COSDictionary trailer = document.getTrailer();
            COSDictionary infoDic = (COSDictionary)trailer.getDictionaryObject( infoName );
            if( infoDic == null )
            {
                infoDic = new COSDictionary();
                trailer.setItem( infoName, infoDic );
            }
            documentInformation = new PDDocumentInformation( infoDic );
        }
        return documentInformation;
    }

    /**
     * This will set the document information for this document.
     *
     * @param info The updated document information.
     */
    public void setDocumentInformation( PDDocumentInformation info )
    {
        documentInformation = info;
        document.getTrailer().setItem( COSName.getPDFName( "Info" ), info.getDictionary() );
    }

    /**
     * This will get the document CATALOG.  This is guaranteed to not return null.
     *
     * @return The documents /Root dictionary
     */
    public PDDocumentCatalog getDocumentCatalog()
    {
        if( documentCatalog == null )
        {
            COSDictionary trailer = document.getTrailer();
            COSDictionary infoDic = (COSDictionary)trailer.getDictionaryObject( COSName.ROOT );
            if( infoDic == null )
            {
                documentCatalog = new PDDocumentCatalog( this );
            }
            else
            {
                documentCatalog = new PDDocumentCatalog( this, infoDic );
            }

        }
        return documentCatalog;
    }

    /**
     * This will tell if this document is encrypted or not.
     *
     * @return true If this document is encrypted.
     */
    public boolean isEncrypted()
    {
        return document.isEncrypted();
    }

    /**
     * This will get the encryption dictionary for this document.  This will still
     * return the parameters if the document was decrypted.  If the document was
     * never encrypted then this will return null.  As the encryption architecture
     * in PDF documents is plugable this returns an abstract class, but the only
     * supported subclass at this time is a PDStandardEncryption object.
     *
     * @return The encryption dictionary(most likely a PDStandardEncryption object)
     *
     * @throws IOException If there is an error determining which security handler to use.
     */
    public PDEncryptionDictionary getEncryptionDictionary() throws IOException
    {
        if( encParameters == null )
        {
            encParameters = PDEncryptionManager.getEncryptionDictionary( document.getEncryptionDictionary() );
        }
        return encParameters;
    }

    /**
     * This will set the encryption dictionary for this document.
     *
     * @param encDictionary The encryption dictionary(most likely a PDStandardEncryption object)
     *
     * @throws IOException If there is an error determining which security handler to use.
     */
    public void setEncryptionDictionary( PDEncryptionDictionary encDictionary ) throws IOException
    {
        encParameters = encDictionary;
    }

    /**
     * This will determine if this is the user password.  This only applies when
     * the document is encrypted and uses standard encryption.
     *
     * @param password The plain text user password.
     *
     * @return true If the password passed in matches the user password used to encrypt the document.
     *
     * @throws IOException If there is an error determining if it is the user password.
     * @throws CryptographyException If there is an error in the encryption algorithms.
     */
    public boolean isUserPassword( String password ) throws IOException, CryptographyException
    {
        boolean retval = false;
        PDFEncryption encryptor = new PDFEncryption();
        PDEncryptionDictionary encryptionDictionary = getEncryptionDictionary();
        if( encryptionDictionary == null )
        {
            throw new IOException( "Error: Document is not encrypted" );
        }
        else
        {
            if( encryptionDictionary instanceof PDStandardEncryption )
            {
                COSString documentID = (COSString)document.getDocumentID().get(0);
                PDStandardEncryption standard = (PDStandardEncryption)encryptionDictionary;
                retval = encryptor.isUserPassword(
                    password.getBytes(),
                    standard.getUserKey(),
                    standard.getOwnerKey(),
                    standard.getPermissions(),
                    documentID.getBytes(),
                    standard.getRevision(),
                    standard.getLength()/8 );
            }
            else
            {
                throw new IOException( "Error: Encyption dictionary is not 'Standard'" +
                    encryptionDictionary.getClass().getName() );
            }
        }
        return retval;
    }

    /**
     * This will determine if this is the owner password.  This only applies when
     * the document is encrypted and uses standard encryption.
     *
     * @param password The plain text owner password.
     *
     * @return true If the password passed in matches the owner password used to encrypt the document.
     *
     * @throws IOException If there is an error determining if it is the user password.
     * @throws CryptographyException If there is an error in the encryption algorithms.
     */
    public boolean isOwnerPassword( String password ) throws IOException, CryptographyException
    {
        boolean retval = false;
        PDFEncryption encryptor = new PDFEncryption();
        PDEncryptionDictionary encryptionDictionary = getEncryptionDictionary();
        if( encryptionDictionary == null )
        {
            throw new IOException( "Error: Document is not encrypted" );
        }
        else
        {
            if( encryptionDictionary instanceof PDStandardEncryption )
            {
                COSString documentID = (COSString)document.getDocumentID().get( 0 );
                PDStandardEncryption standard = (PDStandardEncryption)encryptionDictionary;
                retval = encryptor.isOwnerPassword(
                    password.getBytes(),
                    standard.getUserKey(),
                    standard.getOwnerKey(),
                    standard.getPermissions(),
                    documentID.getBytes(),
                    standard.getRevision(),
                    standard.getLength()/8 );
            }
            else
            {
                throw new IOException( "Error: Encyption dictionary is not 'Standard'" +
                    encryptionDictionary.getClass().getName() );
            }
        }
        return retval;
    }

    /**
     * This will decrypt a document.
     *
     * @param password Either the user or owner password.
     *
     * @throws CryptographyException If there is an error decrypting the document.
     * @throws IOException If there is an error getting the stream data.
     * @throws InvalidPasswordException If the password is not a user or owner password.
     */
    public void decrypt( String password ) throws CryptographyException, IOException, InvalidPasswordException
    {
        DocumentEncryption decryptor = new DocumentEncryption( this );
        decryptor.decryptDocument( password );
    }

    /**
     * This will encrypt a document.
     *
     * @param ownerPassword The owner password to encrypt the document.
     * @param userPassword The user password to encrypt the document.
     *
     * @throws CryptographyException If an error occurs during encryption.
     * @throws IOException If there is an error accessing the data.
     */
    public void encrypt( String ownerPassword, String userPassword )
        throws CryptographyException, IOException
    {
        DocumentEncryption decryptor = new DocumentEncryption( this );
        decryptor.encryptDocument( ownerPassword, userPassword );
    }

    /**
     * This will load a document from a file.
     *
     * @param filename The name of the file to load.
     *
     * @return The document that was loaded.
     *
     * @throws IOException If there is an error reading from the stream.
     */
    public static PDDocument load( String filename ) throws IOException
    {
        return load( new BufferedInputStream( new FileInputStream( filename ) ) );
    }

    /**
     * This will load a document from a file.
     *
     * @param file The name of the file to load.
     *
     * @return The document that was loaded.
     *
     * @throws IOException If there is an error reading from the stream.
     */
    public static PDDocument load( File file ) throws IOException
    {
        return load( new BufferedInputStream( new FileInputStream( file ) ) );
    }

    /**
     * This will load a document from an input stream.
     *
     * @param input The stream that contains the document.
     *
     * @return The document that was loaded.
     *
     * @throws IOException If there is an error reading from the stream.
     */
    public static PDDocument load( InputStream input ) throws IOException
    {
        PDFParser parser = new PDFParser( input );
        parser.parse();
        return parser.getPDDocument();
    }

    /**
     * This will save this document to the filesystem.
     *
     * @param fileName The file to save as.
     *
     * @throws IOException If there is an error saving the document.
     * @throws COSVisitorException If an error occurs while generating the data.
     */
    public void save( String fileName ) throws IOException, COSVisitorException
    {
        save( new FileOutputStream( fileName ) );
    }

    /**
     * This will save the document to an output stream.
     *
     * @param output The stream to write to.
     *
     * @throws IOException If there is an error writing the document.
     * @throws COSVisitorException If an error occurs while generating the data.
     */
    public void save( OutputStream output ) throws IOException, COSVisitorException
    {
        COSWriter writer = null;
        try
        {
            writer = new COSWriter( output );
            writer.write( document );
            writer.close();
        }
        finally
        {
            if( writer != null )
            {
                writer.close();
            }
        }

    }

    /**
     * This will return the total page count of the PDF document.
     *
     * @return The total number of pages in the PDF document.
     *
     * @throws IOException If there is an error getting the page count
     */
    public int getPageCount() throws IOException
    {
        COSObject catalog = document.getCatalog();
        COSObject pages = (COSObject)catalog.getItem( COSName.PAGES );
        return getPageCount(pages);
    }

    /**
     * Helper method for getPageCount method.
     */
    private int getPageCount( COSObject pages )
    {
        COSArray pagesArray = (COSArray)pages.getDictionaryObject( COSName.KIDS );
        int pagecount = 0;
        if( pagesArray != null )
        {
            for( int i=0; i<pagesArray.size(); i++ )
            {
                COSObject pageChild = (COSObject)pagesArray.get( i );
                pagecount = pagecount + getPageCount(pageChild );
            }
        }
        else
        {
            // If its null then its a page otherwise its an array
            pagecount++;
        }

        return pagecount;
    }

    /**
     * This will close the underlying COSDocument object.
     *
     * @throws IOException If there is an error releasing resources.
     */
    public void close() throws IOException
    {
        document.close();
    }
}
