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
package com.progdan.pdf2txt.util;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.progdan.pdf2txt.cos.COSDocument;
import com.progdan.pdf2txt.cos.COSStream;

import com.progdan.pdf2txt.pdmodel.PDDocument;
import com.progdan.pdf2txt.pdmodel.PDPage;

import com.progdan.pdf2txt.pdmodel.encryption.PDEncryptionDictionary;
import com.progdan.pdf2txt.pdmodel.encryption.PDStandardEncryption;

import com.progdan.pdf2txt.exceptions.CryptographyException;
import com.progdan.pdf2txt.exceptions.InvalidPasswordException;

import com.progdan.logengine.Category;


/**
 * This class will take a pdf document and strip out all of the text and ignore the
 * formatting and such.
 *
 * @author Ben Litchfield (ben@csh.rit.edu)
 * @version $Revision: 1.2 $
 */
public class PDFTextStripper extends PDFStreamEngine
{
    private static Category log = Category.getInstance(PDFTextStripper.class.getName());
    private static final int BUFFER_SIZE = 1024;


    private int currentPage = 0;
    private int startPage = 1;
    private int endPage = Integer.MAX_VALUE;
    private PDDocument document;
    private List charactersList = new ArrayList();
    private Writer output;
    private String lineSeparator = System.getProperty("line.separator");
    private String pageSeparator = System.getProperty("line.separator");
    private String wordSeparator = " ";

    /**
     * This will return the text of a document.  See writeText. <br />
     * NOTE: The document must not be encrypted when coming into this method.
     *
     * @param doc The document to get the text from.
     *
     * @return The text of the PDF document.
     *
     * @throws IOException if the doc state is invalid or it is encrypted.
     */
    public String getText( PDDocument doc ) throws IOException
    {
        StringWriter outputStream = new StringWriter();
        writeText( doc, outputStream );
        return outputStream.toString();
    }

    /**
     * @deprecated
     * @see getText( PDDocument )
     */
    public String getText( COSDocument doc ) throws IOException
    {
        return getText( new PDDocument( doc ) );
    }

    /**
     * @deprecated
     * @see writeText( PDDocument, Writer )
     */
    public void writeText( COSDocument doc, Writer outputStream ) throws IOException
    {
        writeText( new PDDocument( doc ), outputStream );
    }

    /**
     * This will take a PDDocument and write the text of that document to the print writer.
     *
     * @param doc The document to get the data from.
     * @param outputStream The location to put the text.
     *
     * @throws IOException If the doc is in an invalid state.
     */
    public void writeText( PDDocument doc, Writer outputStream ) throws IOException
    {
        PDEncryptionDictionary encDictionary = doc.getEncryptionDictionary();

        if( encDictionary instanceof PDStandardEncryption )
        {
            PDStandardEncryption stdEncryption = (PDStandardEncryption)encDictionary;
            if( !stdEncryption.canExtractContent() )
            {
                throw new IOException( "You do not have permission to extract text" );
            }
        }
        currentPage = 0;
        document = doc;
        output = outputStream;

        if( document.isEncrypted() )
        {
            // We are expecting non-encrypted documents here, but it is common
            // for users to pass in a document that is encrypted with an empty
            // password (such a document appears to not be encrypted by
            // someone viewing the document, thus the confusion).  We will
            // attempt to decrypt with the empty password to handle this case.
            //
            log.debug("Document is encrypted, decrypting with empty password");
            try
            {
                document.decrypt("");
            }
            catch (CryptographyException e)
            {
                throw new IOException("Error decrypting document, details: " + e.getMessage());
            }
            catch (InvalidPasswordException e)
            {
                throw new IOException("Error: document is encrypted");
            }
        }

        processPages( document.getDocumentCatalog().getAllPages() );
    }

    /**
     * This will process all of the pages and the text that is in them.
     *
     * @param pages The pages object in the document.
     *
     * @throws IOException If there is an error parsing the text.
     */
    protected void processPages( List pages ) throws IOException
    {
        if( log.isDebugEnabled() )
        {
            log.debug( "processPages( " + pages + " )" );
        }

        Iterator pageIter = pages.iterator();
        while( pageIter.hasNext() )
        {
            PDPage page = (PDPage)pageIter.next();
            COSStream contents = page.getContents();
            if( contents != null )
            {
                processPage( page, page.getContents() );
            }
        }
        log.debug( "processPages() end" );
    }

    /**
     * This will process the contents of a page.
     *
     * @param page The page to process.
     * @param content The contents of the page.
     *
     * @throws IOException If there is an error processing the page.
     */
    protected void processPage( PDPage page, COSStream content ) throws IOException
    {
        long start = System.currentTimeMillis();
        if( log.isDebugEnabled() )
        {
            log.debug( "processPage( " + page + ", " + content + " )" );
        }
        currentPage++;
        if( currentPage >= startPage && currentPage <= endPage )
        {
            charactersList.clear();
            long startProcess = System.currentTimeMillis();
            processStream( content, page.findResources() );
            long stopProcess = System.currentTimeMillis();
            long startFlush = System.currentTimeMillis();
            flushText();
            long stopFlush = System.currentTimeMillis();
            if( log.isDebugEnabled() )
            {
                log.debug( "processStream time=" + (stopProcess-startProcess) );
                log.debug( "flushText time=" + (stopFlush-startFlush) );
            }
        }
        long stop = System.currentTimeMillis();
        if( log.isDebugEnabled() )
        {
            log.debug( "processPage() end time=" + (stop-start) );
        }


    }

    /**
     * This will print the text to the output stream.
     *
     * @throws IOException If there is an error writing the text.
     */
    protected void flushText() throws IOException
    {
        log.debug( "flushText() start" );
        float currentY = -1;
        float lastBaselineFontSize = -1;
        log.debug("<Starting text object list>");
        float endOfLastTextX = -1;
        float startOfNextWordX = -1;
        TextPosition lastProcessedCharacter = null;
        Iterator textIter = charactersList.iterator();
        while( textIter.hasNext() )
        {
            TextPosition position = (TextPosition)textIter.next();

            // RDD - We will suppress text that is very close to the current line
            // and which overwrites previously rendered text on this line.
            // This is done specifically to handle a reasonably common situation
            // where an application (MS Word, in the case of my examples) renders
            // text four times at small (1 point) offsets in order to accomplish
            // bold printing.  You would not want to do this step if you were
            // going to render the TextPosition objects graphically.
            //
            if ((endOfLastTextX != -1 && position.getX() < endOfLastTextX) &&
                (currentY != -1 && Math.abs(position.getY() - currentY) < 1))
            {
                if (log.isDebugEnabled())
                {
                    log.debug("Suppressing text overwrite" +
                              " x: " + position.getX() +
                              " endOfLastTextX: " + endOfLastTextX +
                              " string: " + position.getCharacter());
                }
                continue;
            }

            // RDD - Here we determine whether this text object is on the current
            // line.  We use the lastBaselineFontSize to handle the superscript
            // case, and the size of the current font to handle the subscript case.
            // Text must overlap with the last rendered baseline text by at least
            // a small amount in order to be considered as being on the same line.
            //
            if (currentY != -1 &&
                ((position.getY() > (currentY + (lastBaselineFontSize * 0.9f))) ||
                 (position.getY() < (currentY - (position.getFontSize() * 0.9f)))))
            {
                if (log.isDebugEnabled())
                {
                    log.debug("<newline currentY=" + currentY + ", y=" + position.getY() + ">");
                }
                output.write(lineSeparator);
                endOfLastTextX = -1;
                startOfNextWordX = -1;
                currentY = -1;
                lastBaselineFontSize = -1;
            }

            if (startOfNextWordX != -1 && startOfNextWordX < position.getX() &&
               lastProcessedCharacter != null &&
               //only bother adding a space if the last character was not a space
               lastProcessedCharacter.getCharacter() != null &&
               !lastProcessedCharacter.getCharacter().endsWith( " " ) )
            {
                if (log.isDebugEnabled())
                {
                    log.debug("<space startOfNextWordX=" + startOfNextWordX + ", x=" + position.getX() + ">");
                }
                output.write( wordSeparator );
            }

            //wordSpacing = position.getWordSpacing();
            float wordSpacing = 0;
            if( wordSpacing == 0 )
            {
                //try to get width of a space character
                wordSpacing = position.getWidthOfSpace();
                //if still zero fall back to getting the width of the current
                //character
                if( wordSpacing == 0 )
                {
                    wordSpacing = position.getWidth();
                }
            }


            if (log.isDebugEnabled())
            {
                log.debug("flushText" +
                          " x=" + position.getX() +
                          " y=" + position.getY() +
                          " width=" + position.getWidth() +
                          " currentY=" + currentY +
                          " endOfLastTextX=" + endOfLastTextX +
                          " startOfNextWordX=" + startOfNextWordX +
                          " fontSize=" + position.getFontSize() +
                          " wordSpacing=" + wordSpacing +
                          " string=\"" + position.getCharacter() + "\"");
            }

            if (currentY == -1)
            {
                currentY = position.getY();
            }

            if (currentY == position.getY())
            {
                lastBaselineFontSize = position.getFontSize();
            }

            // RDD - endX is what PDF considers to be the x coordinate of the
            // end position of the text.  We use it in computing our metrics below.
            //
            float endX = position.getX() + position.getWidth();

            // RDD - Here we compute the value that represents the end of the rendered
            // text.  This value is used to determine whether subsequent text rendered
            // on the same line overwrites the current text.
            //
            // We subtract any positive padding to handle cases where extreme amounts
            // of padding are applied, then backed off (not sure why this is done, but there
            // are cases where the padding is on the order of 10x the character width, and
            // the TJ just backs up to compensate after each character).  Also, we subtract
            // an amount to allow for kerning (a percentage of the width of the last
            // character).
            //
            boolean suppressCharacter = false;
            for( int i=0; i<charactersList.size() && position.getCharacter() != null; i++ )
            {
                TextPosition character = (TextPosition)charactersList.get( i );
                //only want to suppress
                if( character == position )
                {
                    //the characters are in order when we get to this character
                    //then stop looking for duplicates
                    break;
                }
                else
                {
                    if( character.getCharacter() != null &&
                        character.getCharacter().equals( position.getCharacter() ) &&
                        within( character.getX(), position.getX(), position.getWidth()/3.0f ) &&
                        within( character.getY(), position.getY(), position.getWidth()/3.0f ) )
                    {
                        if( log.isDebugEnabled() )
                        {
                            log.debug("suppressText" +
                                      " x=" + character.getX() +
                                      " y=" + character.getY() +
                                      " width=" + character.getWidth() +
                                      " fontSize=" + character.getFontSize() +
                                      " string=\"" + character.getCharacter() + "\"");
                        }
                        suppressCharacter = true;
                    }
                }
            }

            // RDD - We add a conservative approximation for space determination.
            // basically if there is a blank area between two characters that is
            //equal to some percentage of the word spacing then that will be the
            //start of the next word
            startOfNextWordX = endX + (wordSpacing* 0.50f);

            if (position.getCharacter() != null)
            {
                if( suppressCharacter )
                {
                    if( log.isDebugEnabled() )
                    {
                        log.debug( "Suppressing text:" + position.getCharacter() );
                    }
                }
                else
                {
                    output.write(position.getCharacter());
                }
            }
            else
            {
                log.debug( "Position.getString() is null so not writing anything" );
            }
            lastProcessedCharacter = position;
        }

        // RDD - newline at end of flush - required for end of page (so that the top
        // of the next page starts on its own line.
        //
        log.debug("<newline endOfFlush=\"true\">");
        output.write(pageSeparator);

        output.flush();
    }

    /**
     * This will determine of two floating point numbers are within a specified variance.
     *
     * @param first The first number to compare to.
     * @param second The second number to compare to.
     * @param variance The allowed variance.
     */
    private boolean within( float first, float second, float variance )
    {
        float firstMin = first - variance;
        float firstMax = first + variance;
        return second > firstMin && second < firstMax;
    }

    /**
     * This will determine of two floating point numbers are equal within a small amount of variance.
     *
     * @param first The first number to compare to.
     * @param second The second number to compare to.
     */
    private boolean equals( float first, float second )
    {
        return within( first, second, 0.001f);
    }

    /**
     * This will show add a character to the list of characters to be printed to
     * the text file.
     *
     * @param text The description of the character to display.
     */
    protected void showCharacter( TextPosition text )
    {
        charactersList.add( text );
    }

    /**
     * This is the page that the text extraction will start on.  The pages start
     * at page 1.  For example in a 5 page PDF document, if the start page is 1
     * then all pages will be extracted.  If the start page is 4 then pages 4 and 5
     * will be extracted.  The default value is 1.
     *
     * @return Value of property startPage.
     */
    public int getStartPage()
    {
        return startPage;
    }

    /**
     * This will set the first page to be extracted by this class.
     *
     * @param startPageValue New value of property startPage.
     */
    public void setStartPage(int startPageValue)
    {
        startPage = startPageValue;
    }

    /**
     * This will get the last page that will be extracted.  This is inclusive,
     * for example if a 5 page PDF an endPage value of 5 would extract the
     * entire document, an end page of 2 would extract pages 1 and 2.  This defaults
     * to Integer.MAX_VALUE such that all pages of the pdf will be extracted.
     *
     * @return Value of property endPage.
     */
    public int getEndPage()
    {
        return endPage;
    }

    /**
     * This will set the last page to be extracted by this class.
     *
     * @param endPageValue New value of property endPage.
     */
    public void setEndPage(int endPageValue)
    {
        endPage = endPageValue;
    }

    /**
     * Set the desired line separator for output text.  The line.separator
     * system property is used if the line separator preference is not set
     * explicitly using this method.
     *
     * @param separator The desired line separator string.
     */
    public void setLineSeparator(String separator)
    {
        lineSeparator = separator;
    }

    /**
     * This will get the line separator.
     *
     * @return The desired line separator string.
     */
    public String getLineSeparator()
    {
        return lineSeparator;
    }

    /**
     * Set the desired page separator for output text.  The line.separator
     * system property is used if the page separator preference is not set
     * explicitly using this method.
     *
     * @param separator The desired page separator string.
     */
    public void setPageSeparator(String separator)
    {
        pageSeparator = separator;
    }

    /**
     * This will get the word separator.
     *
     * @return The desired word separator string.
     */
    public String getWordSeparator()
    {
        return wordSeparator;
    }

    /**
     * Set the desired word separator for output text.  The PDF2TXT text extraction
     * algorithm will output a space character if there is enough space between
     * two words.  By default a space character is used.  If you need and accurate
     * count of characters that are found in a PDF document then you might want to
     * set the word separator to the empty string.
     *
     * @param separator The desired page separator string.
     */
    public void setWordSeparator(String separator)
    {
        wordSeparator = separator;
    }

    /**
     * This will get the page separator.
     *
     * @return The page separator string.
     */
    public String getPageSeparator()
    {
        return pageSeparator;
    }
}
