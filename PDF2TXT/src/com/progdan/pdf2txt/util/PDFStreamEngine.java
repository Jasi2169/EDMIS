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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.progdan.pdf2txt.cos.COSArray;
import com.progdan.pdf2txt.cos.COSBase;
import com.progdan.pdf2txt.cos.COSNumber;
import com.progdan.pdf2txt.cos.COSName;
import com.progdan.pdf2txt.cos.COSObject;
import com.progdan.pdf2txt.cos.COSStream;
import com.progdan.pdf2txt.cos.COSString;

import com.progdan.pdf2txt.pdmodel.PDResources;

import com.progdan.pdf2txt.pdmodel.font.PDFont;

import com.progdan.pdf2txt.pdmodel.graphics.PDExtendedGraphicsState;
import com.progdan.pdf2txt.pdmodel.graphics.PDGraphicsState;

import com.progdan.pdf2txt.pdmodel.graphics.color.PDColorSpace;
import com.progdan.pdf2txt.pdmodel.graphics.color.PDColorSpaceFactory;
import com.progdan.pdf2txt.pdmodel.graphics.color.PDDeviceCMYK;

import com.progdan.logengine.Category;

/**
 * This class will run through a PDF content stream and execute certain operations
 * and provide a callback interface for clients that want to do things with the stream.
 * See the PDFTextStripper class for an example of how to use this class.
 *
 * @author Ben Litchfield (ben@csh.rit.edu)
 * @version $Revision: 1.2 $
 */
public class PDFStreamEngine
{
    private static Category log = Category.getInstance(PDFStreamEngine.class.getName());

    private static final float[] EMPTY_FLOAT_ARRAY = new float[0];

    private static final byte[] SPACE_BYTES = { (byte)32 };

    private COSStream stream;
    private PDGraphicsState graphicsState = null;

    private Matrix textMatrix = null;
    private Matrix textLineMatrix = null;
    private Stack graphicsStack = new Stack();
    private PDResources resources = null;
    private Map fonts;
    private Map colorSpaces;
    private Map graphicsStates;

    /**
     * Constructor.
     */
    public PDFStreamEngine()
    {
    }

    /**
     * This will process the contents of the stream.
     *
     * @param cosStream the Stream to execute.
     * @param pageResources The page resources.
     *
     * @throws IOException if there is an error accessing the stream.
     */
    public void processStream( COSStream cosStream, PDResources pageResources ) throws IOException
    {
        graphicsState = new PDGraphicsState();
        stream = cosStream;
        textMatrix = null;
        textLineMatrix = null;
        graphicsStack = new Stack();
        resources = pageResources;
        if( resources != null )
        {
            fonts = resources.getFonts();
            colorSpaces = resources.getColorSpaces();
            graphicsStates = resources.getGraphicsStates();
        }

        List arguments = new ArrayList();
        long startTokens = System.currentTimeMillis();
        List tokens = stream.getStreamTokens();
        long stopTokens = System.currentTimeMillis();
        if( log.isDebugEnabled() )
        {
            log.debug( "Getting tokens time=" + (stopTokens-startTokens) );
        }
        if( tokens != null )
        {
            Iterator iter = tokens.iterator();
            while( iter.hasNext() )
            {
                Object next = iter.next();
                if( next instanceof COSObject )
                {
                    arguments.add( ((COSObject)next).getObject() );
                }
                else if( next instanceof PDFOperator )
                {
                    processOperator( (PDFOperator)next, arguments );
                    arguments = new ArrayList();
                }
                else
                {
                    arguments.add( next );
                }
            }
        }
    }

    /**
     * A method provided as an event interface to allow a subclass to perform
     * some specific functionality when a character needs to be displayed.
     *
     * @param text The character to be displayed.
     */
    protected void showCharacter( TextPosition text )
    {
    }

    /**
     * You should override this method if you want to perform an action when a
     * string is being shown.
     *
     * @param string The string to display.
     * @param adjustment Defined by TJ operator, 0 otherwise.
     *
     * @return A description of the text being shown.
     *
     * @throws IOException If there is an error showing the string
     */
    private void showString( byte[] string ) throws IOException
    {
        float xPos = 0;
        float yPos = 0;
        float characterDisplacement = 0;
        float spaceDisplacement = 0;
        float fontSize = graphicsState.getTextState().getFontSize();
        float horizontalScaling = graphicsState.getTextState().getHorizontalScalingPercent()/100;
        float rise = graphicsState.getTextState().getRise();
        float wordSpacing = graphicsState.getTextState().getWordSpacing();

        Matrix initialMatrix = new Matrix();
        initialMatrix.setValue(0,0,fontSize*horizontalScaling);
        initialMatrix.setValue(0,1,0);
        initialMatrix.setValue(0,2,0);
        initialMatrix.setValue(1,0,0);
        initialMatrix.setValue(1,1,fontSize);
        initialMatrix.setValue(1,2,0);
        initialMatrix.setValue(2,0,0);
        initialMatrix.setValue(2,1,rise);
        initialMatrix.setValue(2,2,1);


        //this
        int codeLength = 1;
        Matrix ctm = graphicsState.getCurrentTransformationMatrix();
        for( int i=0; i<string.length; i+=codeLength )
        {
            Matrix trm = initialMatrix.multiply( textMatrix ).multiply( ctm );
            codeLength = 1;

            String c = graphicsState.getTextState().getFont().encode( string, i, codeLength );

            if( log.isDebugEnabled() )
            {
                log.debug( "Character Code=" + string[i] + "='" + c + "'" );
            }
            if( c == null && i+1<string.length)
            {
                //maybe a multibyte encoding
                codeLength++;
                if( log.isDebugEnabled() )
                {
                    log.debug( "Multibyte Character Code=" + string[i] + string[i+1] );
                }
                c = graphicsState.getTextState().getFont().encode( string, i, codeLength );
            }

            //todo, handle horizontal displacement
            characterDisplacement = (graphicsState.getTextState().getFont().getFontWidth( string, i, codeLength )/1000);
            spaceDisplacement = (graphicsState.getTextState().getFont().getFontWidth( SPACE_BYTES, 0, 1 )/1000);
            if( spaceDisplacement == 0 )
            {
                spaceDisplacement = (graphicsState.getTextState().getFont().getAverageFontWidth()/1000);
                //The average space width appears to be higher than necessary
                //so lets make it a little bit smaller.
                spaceDisplacement *= .80f;
                if( log.isDebugEnabled() )
                {
                    log.debug( "Font: Space From Average=" + spaceDisplacement );
                }
            }


            // PDF Spec - 5.5.2 Word Spacing
            //
            // Word spacing works the same was as character spacing, but applies
            // only to the space character, code 32.
            //
            // Note: Word spacing is applied to every occurrence of the single-byte
            // character code 32 in a string.  This can occur when using a simple
            // font or a composite font that defines code 32 as a single-byte code.
            // It does not apply to occurrences of the byte value 32 in multiple-byte
            // codes.
            //
            // RDD - My interpretation of this is that only character code 32's that
            // encode to spaces should have word spacing applied.  Cases have been
            // observed where a font has a space character with a character code
            // other than 32, and where word spacing (Tw) was used.  In these cases,
            // applying word spacing to either the non-32 space or to the character
            // code 32 non-space resulted in errors consistent with this interpretation.
            //
            float spacing = 0;
            if ((string[i] == 0x20) && c.equals( " " ) )
            {
                spacing = graphicsState.getTextState().getWordSpacing() +
                          graphicsState.getTextState().getCharacterSpacing();
            }
            else
            {
                spacing = graphicsState.getTextState().getCharacterSpacing();
            }
            if( log.isDebugEnabled() )
            {
                log.debug( "Checking code '" + c + "' font=" + graphicsState.getTextState().getFont() +
                     " Tc=" + graphicsState.getTextState().getCharacterSpacing() +
                     " Tw=" + graphicsState.getTextState().getWordSpacing() +
                     " fontSize=" + fontSize +
                     " horizontalScaling=" + horizontalScaling +
                     " characterDisplacement=" + characterDisplacement +
                     " spacing=" + spacing +
                     " spaceDisplacement=" + spaceDisplacement );
            }

            //The adjustment will always be zero.  The adjustment as shown in the
            //TJ operator will be handled separately.
            float adjustment=0;
            float tx = (characterDisplacement-adjustment/1000)*fontSize + spacing;
            if( log.isDebugEnabled() )
            {
                log.debug( "disp=" + characterDisplacement + " adj=" + adjustment +
                           " fSize=" + fontSize + " tx=" + tx );
            }
            //todo, need to compute the horizontal displacement
            float ty = 0;

            float xScale = trm.getValue(0,0);
            float yScale = trm.getValue(1,1);

            showCharacter(
                new TextPosition(
                    trm.getValue(2,0),
                    trm.getValue(2,1),
                    characterDisplacement * xScale,
                    spaceDisplacement * xScale,
                    c,
                    graphicsState.getTextState().getFont(),
                    graphicsState.getTextState().getFontSize(),
                    wordSpacing*xScale  ));
            // We want to update the textMatrix using the width, in text space units.
            //
            Matrix td = new Matrix();
            td.setValue( 2, 0, tx );
            td.setValue( 2, 1, ty );
            if( log.isDebugEnabled() )
            {
                log.debug( "TRM=" + trm );
                log.debug( "TextMatrix before " + textMatrix );
            }
            textMatrix = td.multiply( textMatrix );
            if( log.isDebugEnabled() )
            {
                log.debug( "TextMatrix after " + textMatrix );
            }
        }
    }

    /**
     * This is used to handle an operation.
     *
     * @param operator The operation to perform.
     * @param arguments The list of arguments.
     *
     * @throws IOException If there is an error processing the operation.
     */
    protected void processOperator( PDFOperator operator, List arguments ) throws IOException
    {
        String operation = operator.getOperation();
        if( log.isDebugEnabled() )
        {
            log.debug( "processOperator( '" + operation + "' )" );
        }
        /**
        if( operation.equals( "b" ) )
        {
            //Close, fill, and stroke path using nonzero winding number rule
        }
        else if( operation.equals( "B" ) )
        {
            //Fill and stroke path using nonzero winding number rule
        }
        else if( operation.equals( "b*" ) )
        {
            //Close, fill, and stroke path using even-odd rule
        }
        else if( operation.equals( "B*" ) )
        {
            //Fill and stroke path using even-odd rule
        }
        else if( operation.equals( "BDC" ) )
        {
            //(PDF 1.2) Begin marked-content sequence with property list
        }
        else if( operation.equals( "BI" ) )
        {
            //begin inline image object
        }
        else if( operation.equals( "BMC" ) )
        {
            //(PDF 1.2) Begin marked-content sequence
        }
        else */if( operation.equals( "BT" ) )
        {
            log.debug( "<BT>" );
            textMatrix = new Matrix();
            textLineMatrix = new Matrix();
        }/*
        else if( operation.equals( "BX" ) )
        {
            //(PDF 1.1) Begin compatibility section
        }
        else if( operation.equals( "c" ) )
        {
            //Append curved segment to path (three control points)
        }*/
        else if( operation.equals( "cm" ) )
        {
            //concatenate matrix to current transformation matrix
            COSNumber a = (COSNumber)arguments.get( 0 );
            COSNumber b = (COSNumber)arguments.get( 1 );
            COSNumber c = (COSNumber)arguments.get( 2 );
            COSNumber d = (COSNumber)arguments.get( 3 );
            COSNumber e = (COSNumber)arguments.get( 4 );
            COSNumber f = (COSNumber)arguments.get( 5 );

            if (log.isDebugEnabled())
            {
                log.debug("<cm " +
                          "a=\"" + a.floatValue() + "\" " +
                          "b=\"" + b.floatValue() + "\" " +
                          "c=\"" + c.floatValue() + "\" " +
                          "d=\"" + d.floatValue() + "\" " +
                          "e=\"" + e.floatValue() + "\" " +
                          "f=\"" + f.floatValue() + "\" >");
            }

            Matrix newMatrix = new Matrix();
            newMatrix.setValue( 0, 0, a.floatValue() );
            newMatrix.setValue( 0, 1, b.floatValue() );
            newMatrix.setValue( 1, 0, c.floatValue() );
            newMatrix.setValue( 1, 1, d.floatValue() );
            newMatrix.setValue( 2, 0, e.floatValue() );
            newMatrix.setValue( 2, 1, f.floatValue() );

            graphicsState.setCurrentTransformationMatrix(
                graphicsState.getCurrentTransformationMatrix().multiply(newMatrix) );
        }/*
        else if( operation.equals( "cs" ) )
        {
            //set colorspace for non stroking operations
            COSName name = (COSName)parameterStack.get( parameterStack.size() -2 );
            //System.out.println( "<cs name=\"" + name.getName() + "\" >" );
        }*/
        else if( operation.equals( "CS" ) )
        {
            //(PDF 1.1) Set color space for stroking operations
            COSName name = (COSName)arguments.get( 0 );
            PDColorSpace cs = (PDColorSpace)colorSpaces.get( name.getName() );
            if( cs == null )
            {
                cs = PDColorSpaceFactory.createColorSpace( name );
            }
            graphicsState.getStrokingColorSpace().setColorSpace( cs );
            int numComponents = cs.getNumberOfComponents();
            float[] values = EMPTY_FLOAT_ARRAY;
            if( numComponents >= 0 )
            {
                values = new float[numComponents];
                for( int i=0; i<numComponents; i++ )
                {
                    values[i] = 0f;
                }
                if( cs instanceof PDDeviceCMYK )
                {
                    values[3] = 1f;
                }
            }
            graphicsState.getStrokingColorSpace().setColorSpaceValue( values );
        }/*
        else if( operation.equals( "d" ) )
        {
            //Set the line dash pattern in the graphics state
            //System.out.println( "<d>" );
        }
        else if( operation.equals( "d0" ) )
        {
            COSNumber x = (COSNumber)parameterStack.get( parameterStack.size() -3 );
            COSNumber y = (COSNumber)parameterStack.get( parameterStack.size() -2 );
            //System.out.println( "<d0 x=\"" + x.getValue() + "\" y=\"" + y.getValue() + "\">" );
        }
        else if( operation.equals( "d1" ) )
        {
            //System.out.println( "<d1>" );
        }
        else if( operation.equals( "Do" ) )
        {
            //invoke named object.
        }
        else if( operation.equals( "DP" ) )
        {
            //(PDF 1.2) Define marked-content point with property list
        }
        else if( operation.equals( "EI" ) )
        {
            //end inline image object
        }
        else if( operation.equals( "EMC" ) )
        {
            //End inline image object
        }*/
        else if( operation.equals( "ET" ) )
        {
            log.debug( "<ET>" );
            textMatrix = null;
            textLineMatrix = null;
        }/*
        else if( operation.equals( "EX" ) )
        {
            //(PDF 1.1) End compatibility section
        }
        else if( operation.equals( "f" ) )
        {
            //Fill the path, using the nonzero winding number rule to determine the region to .ll
            //System.out.println( "<f>" );
        }
        else if( operation.equals( "F" ) )
        {
            //Equivalent to f; included only for compatibility. Although applications that read
            //PDF files must be able to accept this operator, those that generate PDF files should
            //use f instead.
            //System.out.println( "<F>" );
        }
        else if( operation.equals( "f*" ) )
        {
            //Fill path using even-odd rule
        }
        else if( operation.equals( "g" ) )
        {
            //set gray level for nonstroking operations
            COSNumber grayLevel = (COSNumber)parameterStack.get( parameterStack.size() -2 );
            //System.out.println( "<g gray=\"" + grayLevel.getValue() + "\" />" );
        }
        else if( operation.equals( "G" ) )
        {
            //set gray level for stroking operations
            COSNumber grayLevel = (COSNumber)parameterStack.get( parameterStack.size() -2 );
            //System.out.println( "<G gray=\"" + grayLevel.getValue() + "\" />" );
        }**/
        else if( operation.equals( "gs" ) )
        {
            //set parameters from graphics state parameter dictionary
            COSName graphicsName = (COSName)arguments.get( 0 );

            if (log.isDebugEnabled())
            {
                log.debug("<gs name=\"" + graphicsName.getName() + "\" />" );
            }
            PDExtendedGraphicsState gs = (PDExtendedGraphicsState)graphicsStates.get( graphicsName.getName() );
            gs.copyIntoGraphicsState( graphicsState );
        }
        /**
        else if( operation.equals( "h" ) )
        {
            //close subpath
        }
        else if( operation.equals( "i" ) )
        {
            //set flatness tolerance, not sure what this does
        }
        else if( operation.equals( "ID" ) )
        {
            //begin inline image data
        }
        else if( operation.equals( "j" ) )
        {
            //Set the line join style in the graphics state
            //System.out.println( "<j>" );
        }
        else if( operation.equals( "J" ) )
        {
            //Set the line cap style in the graphics state
            //System.out.println( "<J>" );
        }
        else if( operation.equals( "k" ) )
        {
            //Set CMYK color for nonstroking operations
        }
        else if( operation.equals( "K" ) )
        {
            //Set CMYK color for stroking operations
        }
        else if( operation.equals( "l" ) )
        {
            //append straight line segment from the current point to the point.
            COSNumber x = (COSNumber)parameterStack.get( parameterStack.size() -3 );
            COSNumber y = (COSNumber)parameterStack.get( parameterStack.size() -2 );
            //System.out.println( "<l x=\"" + x.getValue() + "\" y=\"" + y.getValue() + "\" >" );
        }
        else if( operation.equals( "m" ) )
        {
            COSNumber x = (COSNumber)parameterStack.get( parameterStack.size() -3 );
            COSNumber y = (COSNumber)parameterStack.get( parameterStack.size() -2 );
            //System.out.println( "<m x=\"" + x.getValue() + "\" y=\"" + y.getValue() + "\" >" );
        }
        else if( operation.equals( "M" ) )
        {
            //System.out.println( "<M>" );
        }
        else if( operation.equals( "MP" ) )
        {
            //(PDF 1.2) Define marked-content point
        }
        else if( operation.equals( "n" ) )
        {
            //End path without .lling or stroking
            //System.out.println( "<n>" );
        }*/
        else if( operation.equals( "q" ) )
        {
            //save graphics state
            if( log.isDebugEnabled() )
            {
                log.debug( "<" + operation + "> - save state" );
            }
            graphicsStack.push(graphicsState.clone());
        }
        else if( operation.equals( "Q" ) )
        {
            //restore graphics state
            if( log.isDebugEnabled() )
            {
                log.debug( "<" + operation + "> - restore state" );
            }
            graphicsState = (PDGraphicsState)graphicsStack.pop();
        }/*
        else if( operation.equals( "re" ) )
        {
            COSNumber x = (COSNumber)parameterStack.get( parameterStack.size() -5 );
            COSNumber y = (COSNumber)parameterStack.get( parameterStack.size() -4 );
            COSNumber width = (COSNumber)parameterStack.get( parameterStack.size() -3 );
            COSNumber height = (COSNumber)parameterStack.get( parameterStack.size() -2 );
            //System.out.println( "<re x=\"" + x.getValue() + "\" y=\"" + y.getValue() + "\" width=\"" +
            //                                 width.getValue() + "\" height=\"" + height.getValue() + "\" >" );
        }
        else if( operation.equals( "rg" ) )
        {
            //Set RGB color for nonstroking operations
        }
        else if( operation.equals( "RG" ) )
        {
            //Set RGB color for stroking operations
        }
        else if( operation.equals( "ri" ) )
        {
            //Set color rendering intent
        }
        else if( operation.equals( "s" ) )
        {
            //Close and stroke path
        }
        else if( operation.equals( "S" ) )
        {
            //System.out.println( "<S>" );
        }
        else if( operation.equals( "sc" ) )
        {
            //set color for nonstroking operations
            //System.out.println( "<sc>" );
        }
        else if( operation.equals( "SC" ) )
        {
            //set color for stroking operations
            //System.out.println( "<SC>" );
        }
        else if( operation.equals( "scn" ) )
        {
            //set color for nonstroking operations special
        }
        else if( operation.equals( "SCN" ) )
        {
            //set color for stroking operations special
        }
        else if( operation.equals( "sh" ) )
        {
            //(PDF 1.3) Paint area defined by shading pattern
        }*/
        else if( operation.equals( "T*" ) )
        {
            if (log.isDebugEnabled())
            {
                log.debug("<T* graphicsState.getTextState().getLeading()=\"" +
                          graphicsState.getTextState().getLeading() + "\">");
            }
            //move to start of next text line
            if( graphicsState.getTextState().getLeading() == 0 )
            {
                graphicsState.getTextState().setLeading( -.01f );
            }
            Matrix td = new Matrix();
            td.setValue( 2, 1, -1 * graphicsState.getTextState().getLeading() * textMatrix.getValue(1,1));
            textLineMatrix = textLineMatrix.multiply( td );
            textMatrix = textLineMatrix.copy();
        }
        else if( operation.equals( "Tc" ) )
        {
            //set character spacing
            COSNumber characterSpacing = (COSNumber)arguments.get( 0 );
            if (log.isDebugEnabled())
            {
                log.debug("<Tc characterSpacing=\"" + characterSpacing.floatValue() + "\" />");
            }
            graphicsState.getTextState().setCharacterSpacing( characterSpacing.floatValue() );
        }
        else if( operation.equals( "Td" ) )
        {
            COSNumber x = (COSNumber)arguments.get( 0 );
            COSNumber y = (COSNumber)arguments.get( 1 );
            if (log.isDebugEnabled())
            {
                log.debug("<Td x=\"" + x.floatValue() + "\" y=\"" + y.floatValue() + "\">");
            }
            Matrix td = new Matrix();
            td.setValue( 2, 0, x.floatValue() );//.* textMatrix.getValue(0,0) );
            td.setValue( 2, 1, y.floatValue() );//* textMatrix.getValue(1,1) );
            //log.debug( "textLineMatrix before " + textLineMatrix );
            textLineMatrix = td.multiply( textLineMatrix ); //textLineMatrix.multiply( td );
            //log.debug( "textLineMatrix after " + textLineMatrix );
            textMatrix = textLineMatrix.copy();
        }
        else if( operation.equals( "TD" ) )
        {
            //move text position and set leading
            COSNumber x = (COSNumber)arguments.get( 0 );
            COSNumber y = (COSNumber)arguments.get( 1 );
            if (log.isDebugEnabled())
            {
                log.debug("<TD x=\"" + x.floatValue() + "\" y=\"" + y.floatValue() + "\">");
            }
            graphicsState.getTextState().setLeading( -1 * y.floatValue() );
            Matrix td = new Matrix();
            td.setValue( 2, 0, x.floatValue() );//* textMatrix.getValue(0,0) );
            td.setValue( 2, 1, y.floatValue() );//* textMatrix.getValue(1,1) );
            //log.debug( "textLineMatrix before " + textLineMatrix );
            textLineMatrix = td.multiply( textLineMatrix ); //textLineMatrix.multiply( td );
            //log.debug( "textLineMatrix after " + textLineMatrix );
            textMatrix = textLineMatrix.copy();
        }
        else if( operation.equals( "Tf" ) )
        {
            //set font and size
            COSName fontName = (COSName)arguments.get( 0 );
            graphicsState.getTextState().setFontSize( ((COSNumber)arguments.get( 1 ) ).floatValue() );

            if (log.isDebugEnabled())
            {
                log.debug("<Tf font=\"" + fontName.getName() + "\" size=\"" +
                          graphicsState.getTextState().getFontSize() + "\">");
            }

            //old way
            //graphicsState.getTextState().getFont() = (COSObject)stream.getDictionaryObject( fontName );
            //if( graphicsState.getTextState().getFont() == null )
            //{
            //    graphicsState.getTextState().getFont() = (COSObject)graphicsState.getTextState().getFont()
            //                                           Dictionary.getItem( fontName );
            //}
            graphicsState.getTextState().setFont( (PDFont)fonts.get( fontName.getName() ) );
            if( graphicsState.getTextState().getFont() == null )
            {
                throw new IOException( "Error: Could not find font(" + fontName + ") in map=" + fonts );
            }
            //log.debug( "Font Resource=" + fontResource );
            //log.debug( "Current Font=" + graphicsState.getTextState().getFont() );
            //log.debug( "graphicsState.getTextState().getFontSize()=" + graphicsState.getTextState().getFontSize() );
        }
        else if( operation.equals( "Tj" ) )
        {
            COSString string = (COSString)arguments.get( 0 );
            showString( string.getBytes() );
            if (log.isDebugEnabled())
            {
                log.debug("<Tj string=\"" + string.getString() + "\">");
            }
        }
        else if( operation.equals( "TJ" ) )
        {
            Matrix td = new Matrix();

            COSArray array = (COSArray)arguments.get( 0 );
            float adjustment=0;
            for( int i=0; i<array.size(); i++ )
            {
                COSBase next = array.get( i );
                if( next instanceof COSNumber )
                {
                    adjustment = ((COSNumber)next).floatValue();

                    Matrix adjMatrix = new Matrix();
                    adjustment=(-adjustment/1000)*graphicsState.getTextState().getFontSize();
                    adjMatrix.setValue( 2, 0, adjustment );
                    if( log.isDebugEnabled() )
                    {
                        log.debug( "TJ adjustment=" + adjustment + " textMatrix=" + textMatrix );
                    }
                    textMatrix = adjMatrix.multiply( textMatrix );
                    if( log.isDebugEnabled() )
                    {
                        log.debug( "textMatrix after=" + textMatrix );
                    }
                }
                else if( next instanceof COSString )
                {
                    showString( ((COSString)next).getBytes() );
                }
                else
                {
                    throw new IOException( "Unknown type in array for TJ operation:" + next );
                }
            }
        }
        else if( operation.equals( "TL" ) )
        {
            COSNumber leading = (COSNumber)arguments.get( 0 );
            graphicsState.getTextState().setLeading( leading.floatValue() );
            if (log.isDebugEnabled())
            {
                log.debug("<TL leading=\"" + leading.floatValue() + "\" >");
            }
        }
        else if( operation.equals( "Tm" ) )
        {
            //Set text matrix and text line matrix
            COSNumber a = (COSNumber)arguments.get( 0 );
            COSNumber b = (COSNumber)arguments.get( 1 );
            COSNumber c = (COSNumber)arguments.get( 2 );
            COSNumber d = (COSNumber)arguments.get( 3 );
            COSNumber e = (COSNumber)arguments.get( 4 );
            COSNumber f = (COSNumber)arguments.get( 5 );

            if (log.isDebugEnabled())
            {
                log.debug("<Tm " +
                          "a=\"" + a.floatValue() + "\" " +
                          "b=\"" + b.floatValue() + "\" " +
                          "c=\"" + c.floatValue() + "\" " +
                          "d=\"" + d.floatValue() + "\" " +
                          "e=\"" + e.floatValue() + "\" " +
                          "f=\"" + f.floatValue() + "\" >");
            }

            textMatrix = new Matrix();
            textMatrix.setValue( 0, 0, a.floatValue() );
            textMatrix.setValue( 0, 1, b.floatValue() );
            textMatrix.setValue( 1, 0, c.floatValue() );
            textMatrix.setValue( 1, 1, d.floatValue() );
            textMatrix.setValue( 2, 0, e.floatValue() );
            textMatrix.setValue( 2, 1, f.floatValue() );
            textLineMatrix = textMatrix.copy();
        }
        else if( operation.equals( "Tr" ) )
        {
            COSNumber mode = (COSNumber)arguments.get( 0 );
            graphicsState.getTextState().setRenderingMode( mode.intValue() );
        }
        else if( operation.equals( "Ts" ) )
        {
            COSNumber rise = (COSNumber)arguments.get(0);
            graphicsState.getTextState().setRise( rise.floatValue() );
        }
        else if( operation.equals( "Tw" ) )
        {
            //set word spacing
            COSNumber wordSpacing = (COSNumber)arguments.get( 0 );
            if (log.isDebugEnabled())
            {
                log.debug("<Tw wordSpacing=\"" + wordSpacing.floatValue() + "\" />");
            }
            graphicsState.getTextState().setWordSpacing( wordSpacing.floatValue() );
        }
        else if( operation.equals( "Tz" ) )
        {
            COSNumber scaling = (COSNumber)arguments.get(0);
            graphicsState.getTextState().setHorizontalScalingPercent( scaling.floatValue() );
        }/**
        else if( operation.equals( "v" ) )
        {
            //Append curved segment to path (initial point replicated)
        }*/
        else if( operation.equals( "w" ) )
        {
            COSNumber width = (COSNumber)arguments.get( 0 );
            graphicsState.setLineWidth( width.doubleValue() );
        }/**
        else if( operation.equals( "W" ) )
        {
            //Set clipping path using nonzero winding number rule
            //System.out.println( "<W>" );
        }
        else if( operation.equals( "W*" ) )
        {
            //Set clipping path using even-odd rule
        }
        else if( operation.equals( "y" ) )
        {
            //Append curved segment to path (final point replicated)
        }*/
        else if( operation.equals( "'" ) )
        {
            // Move to start of next text line, and show text
            //
            COSString string = (COSString)arguments.get( 0 );
            if (log.isDebugEnabled())
            {
                log.debug("<' string=\"" + string.getString() + "\">");
            }

            Matrix td = new Matrix();
            td.setValue( 2, 1, -1 * graphicsState.getTextState().getLeading() * textMatrix.getValue(1,1));
            textLineMatrix = textLineMatrix.multiply( td );
            textMatrix = textLineMatrix.copy();

            showString( string.getBytes() );
        }
        else if( operation.equals( "\"" ) )
        {
            //Set word and character spacing, move to next line, and show text
            //
            COSNumber wordSpacing = (COSNumber)arguments.get( 0 );
            COSNumber characterSpacing = (COSNumber)arguments.get( 1 );
            COSString string = (COSString)arguments.get( 2 );

            if (log.isDebugEnabled())
            {
                log.debug("<\" wordSpacing=\"" + wordSpacing +
                          "\", characterSpacing=\"" + characterSpacing +
                          "\", string=\"" + string.getString() + "\">");
            }

            graphicsState.getTextState().setCharacterSpacing( characterSpacing.floatValue() );
            graphicsState.getTextState().setWordSpacing( wordSpacing.floatValue() );

            Matrix td = new Matrix();
            td.setValue( 2, 1, -1 * graphicsState.getTextState().getLeading() * textMatrix.getValue(1,1));
            textLineMatrix = textLineMatrix.multiply( td );
            textMatrix = textLineMatrix.copy();

            showString( string.getBytes() );
        }
    }
}
