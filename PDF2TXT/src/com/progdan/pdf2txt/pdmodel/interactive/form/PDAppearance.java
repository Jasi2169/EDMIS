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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.progdan.pdf2txt.cos.COSName;
import com.progdan.pdf2txt.cos.COSStream;
import com.progdan.pdf2txt.cos.COSString;

import com.progdan.pdf2txt.pdfparser.PDFStreamParser;

import com.progdan.pdf2txt.pdmodel.PDResources;

import com.progdan.pdf2txt.pdmodel.common.PDRectangle;

import com.progdan.pdf2txt.pdmodel.font.PDFont;
import com.progdan.pdf2txt.pdmodel.font.PDFontDescriptor;
import com.progdan.pdf2txt.pdmodel.font.PDSimpleFont;

import com.progdan.pdf2txt.pdmodel.interactive.annotation.PDAppearanceDictionary;
import com.progdan.pdf2txt.pdmodel.interactive.annotation.PDAppearanceStream;
import com.progdan.pdf2txt.pdmodel.interactive.annotation.PDWidget;

import com.progdan.pdf2txt.util.PDFOperator;

/**
 * This one took me a while, but i'm proud to say that it handles
 * the appearance of a textbox. This allows you to apply a value to
 * a field in the document and handle the appearance so that the
 * value is actually visible too.
 * The problem was described by Ben Litchfield, the author of the
 * example: com.progdan.pdf2txt.examlpes.fdf.ImportFDF. So Ben, here is the
 * solution.
 *
 * @author sug
 * @version $Revision: 1.2 $
 */
public class PDAppearance
{
    private PDTextbox parent;

    private String value;
    private COSString defaultAppearance;

    private PDAcroForm acroForm;
    private List widgets = new ArrayList();


    /**
     * Constructs a COSAppearnce from the given field.
     *
     * @param theAcroForm the acro form that this field is part of.
     * @param field the field which you wish to control the appearance of
     */
    public PDAppearance( PDAcroForm theAcroForm, PDTextbox field )
    {
        acroForm = theAcroForm;
        parent = field;

        PDWidget fieldWidget = field.getWidget();
        if( fieldWidget == null )
        {
            widgets = field.getKids();
        }
        else
        {
            widgets.add( fieldWidget );
        }

        defaultAppearance = getDefaultAppearance();


    }

    /**
     * Returns the default apperance of a textbox. If the textbox
     * does not have one, then it will be taken from the AcroForm.
     * @return The DA element
     */
    private COSString getDefaultAppearance()
    {
        COSString dap = parent.getDefaultAppearance();
        if (dap == null)
        {
            dap = (COSString) acroForm.getDictionary().getDictionaryObject(COSName.getPDFName("DA"));
        }
        return dap;
    }

    /**
     * extracts the original appearance stream into a string.
     *
     * @return the original appearance stream
     */
    private String getOriginalStream( PDAppearanceStream appearanceStream )
    {
        String retval = null;
        if( appearanceStream != null )
        {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            byte[] buffer = new byte[ 1024 ];
            int amountRead = -1;
            try
            {
                InputStream is = appearanceStream.getStream().getUnfilteredStream();
                if( is != null )
                {
                    while( (amountRead = is.read( buffer )) != -1 )
                    {
                        b.write( buffer, 0, amountRead );
                    }
                    retval = new String(b.toByteArray());
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return retval;
    }

    /**
     * Tests if the apperance stream already contains content.
     *
     * @return true if it contains any content
     */
    private boolean containsMarkedContent( String stream )
    {
        return stream != null && (stream.indexOf("BMC") > -1);
    }

    /**
     * This is the public method for setting the appearance stream.
     *
     * @param apValue the String value which the apperance shoud represent
     *
     * @throws IOException If there is an error creating the stream.
     */
    public void setAppearanceValue(String apValue) throws IOException
    {
        // MulitLine check and set
        if ( parent.isMultiline() && apValue.indexOf('\n') != -1 )
        {
            apValue = convertToMultiLine( apValue );
        }

        value = apValue;
        Iterator widgetIter = widgets.iterator();
        while( widgetIter.hasNext() )
        {
            PDWidget widget = (PDWidget)widgetIter.next();

            PDAppearanceDictionary appearance = widget.getAppearance();
            if( appearance == null )
            {
                appearance = new PDAppearanceDictionary();
                widget.setAppearance( appearance );
            }

            Map normalAppearance = appearance.getNormalAppearance();
            PDAppearanceStream appearanceStream = (PDAppearanceStream)normalAppearance.get( "default" );
            if( appearanceStream == null )
            {
                COSStream cosStream = new COSStream( acroForm.getDocument().getDocument().getScratchFile() );
                appearanceStream = new PDAppearanceStream( cosStream );
                appearanceStream.setBoundingBox( widget.getRectangle().createRetranslatedRectangle() );
                appearance.setNormalAppearance( appearanceStream );
            }

            String stream = getOriginalStream( appearanceStream );
            PDFont pdFont = getFontAndUpdateResources( stream, appearanceStream );

            if (!containsMarkedContent( stream ))
            {
                StringBuffer sb = new StringBuffer( 100 );

                //BJL 9/25/2004 Must prepend existing stream
                //because it might have operators to draw things like
                //rectangles and such
                sb.append( stream );

                sb.append(" /Tx BMC\n");
                insertGeneratedAppearance( widget, sb, pdFont, stream, appearanceStream );
                sb.append( " EMC");
                stream = sb.toString();
                writeToStream( stream, appearanceStream );
            }
            else
            {
                if( stream != null )
                {
                    int i1 = stream.indexOf("(");
                    int i2 = stream.indexOf(")");
                    if (i1 > -1 && i2 > -1)
                    {
                        stream =
                            stream.substring(0, i1 + 1)
                                + apValue
                                + stream.substring(i2);
                        writeToStream( stream, appearanceStream );
                    }
                    else
                    {
                        int bmcIndex = stream.indexOf("BMC");
                        int emcIndex = stream.indexOf("EMC");
                        StringBuffer sb = new StringBuffer( 100 );
                        if( bmcIndex != -1 )
                        {
                            sb.append( stream.substring( 0, bmcIndex+3 ) );
                        }
                        else
                        {
                            sb.append( stream );
                        }
                        sb.append( "\n" );
                        insertGeneratedAppearance( widget, sb, pdFont, stream, appearanceStream );
                        if( emcIndex != -1 )
                        {
                            sb.append( stream.substring( emcIndex, stream.length() ) );
                        }
                        stream = sb.toString();
                        writeToStream( stream, appearanceStream );
                    }
                }
                else
                {
                    //hmm?
                }
            }
        }
    }

    private void insertGeneratedAppearance( PDWidget fieldWidget, StringBuffer sb,
        PDFont pdFont, String stream, PDAppearanceStream appearanceStream ) throws IOException
    {
        float fontSize = 0.0f;
        PDRectangle boundingBox = null;
        boundingBox = appearanceStream.getBoundingBox();
        if( boundingBox == null )
        {
            boundingBox = fieldWidget.getRectangle().createRetranslatedRectangle();
        }
        sb.append( "q\n" );
        sb.append( "BT\n" );
        if( defaultAppearance != null )
        {
            //daString looks like   "BMC /Helv 3.4 Tf EMC"
            String daString = defaultAppearance.getString();
            String preFont = daString;
            String afterFont = "";
            String fontName ="/Helv";
            int fontIndex = daString.indexOf( "Tf" );
            if(fontIndex != -1 )
            {
                //upToEndOfFont="BMC /Helv 3.4"
                String upToEndOfFont = daString.substring(0, fontIndex).trim();
                afterFont = daString.substring( fontIndex+2 );

                String fontSizeString = upToEndOfFont.substring( upToEndOfFont.lastIndexOf(" ")+1);
                try
                {
                    fontSize = Float.parseFloat( fontSizeString );
                }
                catch( NumberFormatException e )
                {
                    throw new IOException( e.getMessage() );
                }
                //upToFontName="BMC /Helv"
                String upToFontName = upToEndOfFont.substring( 0,
                    upToEndOfFont.length() - fontSizeString.length() ).trim();
                fontName = upToFontName.substring( upToFontName.lastIndexOf(" ")+1);
                //preFont="BMC"
                preFont = upToFontName.substring( 0, upToFontName.length() - fontName.length() ).trim();


            }

            if( fontSize == 0 )
            {
                fontSize = calculateFontSize( pdFont, boundingBox, stream );
            }
            sb.append( preFont + " " );
            sb.append( fontName + " " );
            sb.append( "" + fontSize + " Tf\n" );
            sb.append( afterFont );

            sb.append( "\n" );
        }
        sb.append( getTextPosition( boundingBox, pdFont, fontSize, stream ) );
        sb.append( "\n" );
        int q = parent.getQ();
        if( q == PDTextbox.QUADDING_LEFT )
        {
            //do nothing because left is default
        }
        else if( q == PDTextbox.QUADDING_CENTERED ||
                 q == PDTextbox.QUADDING_RIGHT )
        {
            float fieldWidth = boundingBox.getWidth();
            float stringWidth = (pdFont.getStringWidth( value )/1000)*fontSize;
            float adjustAmount = fieldWidth - stringWidth - 4;

            if( q == PDTextbox.QUADDING_CENTERED )
            {
                adjustAmount = adjustAmount/2f;
            }

            sb.append( adjustAmount + " 0 Td\n" );
        }
        else
        {
            throw new IOException( "Error: Unknown justification value:" + q );
        }
        sb.append("(" + value + ") Tj\n");
        sb.append("ET Q\n" );
    }

    private PDFont getFontAndUpdateResources( String stream, PDAppearanceStream appearanceStream ) throws IOException
    {

        PDFont retval = null;
        PDResources streamResources = appearanceStream.getResources();
        PDResources formResources = acroForm.getDefaultResources();
        if( formResources != null )
        {
            if( streamResources == null )
            {
                streamResources = new PDResources();
                appearanceStream.setResources( streamResources );
            }
            String data = stream;
            COSString da = getDefaultAppearance();
            if( da != null )
            {
                data = da.getString();
            }

            PDFStreamParser streamParser = new PDFStreamParser(
                new ByteArrayInputStream( data.getBytes() ), null );
            streamParser.parse();
            List tokens = streamParser.getTokens();
            Iterator tokenIter = tokens.iterator();
            Object oneObjectAgo = null;
            Object twoObjectsAgo = null;
            while( tokenIter.hasNext() )
            {
                Object next = tokenIter.next();
                if( next instanceof PDFOperator )
                {
                    PDFOperator op = (PDFOperator)next;
                    if( op.getOperation().equals( "Tf" ) )
                    {
                        COSName cosFontName = (COSName)twoObjectsAgo;
                        String fontName = cosFontName.getName();
                        if( streamResources.getFonts().get( fontName ) == null )
                        {
                            retval = (PDFont)formResources.getFonts().get( fontName );
                            streamResources.getFonts().put( fontName, retval );
                        }
                    }
                }
                twoObjectsAgo = oneObjectAgo;
                oneObjectAgo = next;
            }
        }
        return retval;
    }

    private String convertToMultiLine( String line )
    {
        int currIdx = 0;
        int lastIdx = 0;
        StringBuffer result = new StringBuffer(line.length() + 64);
        while( (currIdx = line.indexOf('\n',lastIdx )) > -1 )
        {
            result.append(value.substring(lastIdx,currIdx));
            result.append(" ) Tj\n0 -13 Td\n(");
            lastIdx = currIdx + 1;
        }
        result.append(line.substring(lastIdx));
        return result.toString();
    }

    /**
     * Writes the stream to the actual stream in the COSStream.
     *
     * @throws IOException If there is an error writing to the stream
     */
    private void writeToStream( String stream, PDAppearanceStream appearanceStream ) throws IOException
    {
        OutputStream out = appearanceStream.getStream().createUnfilteredStream();
        out.write(stream.getBytes());
        out.flush();
    }


    /**
     * w in an appearance stream represents the lineWidth.
     * @return the linewidth
     */
    private float getLineWidth( String stream )
    {
        float retval = 1;
        if( stream != null )
        {
            int w = stream.indexOf("w");
            String fl = "";
            for(int i = w-2; i > 0 &&
                             i < stream.length() &&
                             (Character.isDigit(stream.charAt(i)) || stream.charAt(i) == '.'); i--)
            {
                fl = stream.charAt(i) + fl;
            }

            try
            {
                retval = Float.parseFloat(fl);
            }
            catch( NumberFormatException e )
            {
                //default to zero for line width.
            }
        }
        return retval;
    }

    /**
     * My "not so great" method for calculating the fontsize.
     * It does not work superb, but it handles ok.
     * @return the calculated font-size
     *
     * @throws IOException If there is an error getting the font height.
     */
    private float calculateFontSize( PDFont pdFont, PDRectangle boundingBox, String stream ) throws IOException
    {
        float lineWidth = getLineWidth( stream );
        float stringWidth = pdFont.getStringWidth( value );
        float height = 0;
        if( pdFont instanceof PDSimpleFont )
        {
            height = ((PDSimpleFont)pdFont).getFontDescriptor().getFontBoundingBox().getHeight();
        }
        else
        {
            //now much we can do, so lets assume font is square and use width
            //as the height
            height = pdFont.getAverageFontWidth();
        }
        height = height/1000f;

        float availHeight = getAvailableHeight( boundingBox, lineWidth );
        return (availHeight/height);
    }

    /**
     * Calculates where to start putting the text in the box.
     * The positioning is not quite as accurate as when Acrobat
     * places the elements, but it works though.
     *
     * @return the sting for representing the start position of the text
     *
     * @throws IOException If there is an error calculating the text position.
     */
    private String getTextPosition( PDRectangle boundingBox, PDFont pdFont, float fontSize, String stream )
        throws IOException
    {
        float lineWidth = getLineWidth( stream );
        float pos = 0.0f;
        if(parent.isMultiline())
        {
            int rows = (int) (getAvailableHeight( boundingBox, lineWidth ) / ((int) fontSize));
            pos = ((rows)*fontSize)-fontSize;
        }
        else
        {
            if( pdFont instanceof PDSimpleFont )
            {
                //BJL 9/25/2004
                //This algorithm is a little bit of black magic.  It does
                //not appear to be documented anywhere.  Through examining a few
                //PDF documents and the value that Acrobat places in there I
                //have determined that the below method of computing the position
                //is correct for certain documents, but maybe not all.  It does
                //work f1040ez.pdf and Form_1.pdf
                PDFontDescriptor fd = ((PDSimpleFont)pdFont).getFontDescriptor();
                float bBoxHeight = boundingBox.getHeight();
                float fontHeight = fd.getFontBoundingBox().getHeight() + 2 * fd.getDescent();
                fontHeight = (fontHeight/1000) * fontSize;
                pos = (bBoxHeight - fontHeight)/2;
            }
            else
            {
                throw new IOException( "Error: Don't know how to calculate the position for non-simple fonts" );
            }
        }
        return "2 "+ pos + " Td";
    }

    /**
     * calculates the available width of the box.
     * @return the calculated available width of the box
     */
    private float getAvailableWidth( PDRectangle boundingBox, float lineWidth )
    {
        return boundingBox.getWidth() - 2 * lineWidth;
    }

    /**
     * calculates the available height of the box.
     * @return the calculated available height of the box
     */
    private float getAvailableHeight( PDRectangle boundingBox, float lineWidth )
    {
        return boundingBox.getHeight() - 2 * lineWidth;
    }
}
