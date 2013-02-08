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

import com.progdan.pdf2txt.cos.COSArray;
import com.progdan.pdf2txt.cos.COSBase;
import com.progdan.pdf2txt.cos.COSDictionary;
import com.progdan.pdf2txt.cos.COSInteger;
import com.progdan.pdf2txt.cos.COSName;
import com.progdan.pdf2txt.cos.COSNumber;
import com.progdan.pdf2txt.cos.COSStream;
import com.progdan.pdf2txt.cos.COSString;

import com.progdan.pdf2txt.util.DateConverter;

import com.progdan.pdf2txt.pdmodel.common.COSObjectable;
import com.progdan.pdf2txt.pdmodel.common.COSStreamArray;
import com.progdan.pdf2txt.pdmodel.common.PDRectangle;

import java.io.IOException;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * This represents a single page in a PDF document.
 *
 * @author  Ben Litchfield (ben@csh.rit.edu)
 * @version $Revision: 1.2 $
 */
public class PDPage implements COSObjectable
{
    private COSDictionary page;

    /**
     * Creates a new instance of PDPage.
     */
    public PDPage()
    {
        page = new COSDictionary();
        page.setItem( COSName.getPDFName( "Type" ), COSName.getPDFName( "Page" ) );
    }

    /**
     * Creates a new instance of PDPage.
     *
     * @param pageDic The existing page dictionary.
     */
    public PDPage( COSDictionary pageDic )
    {
        page = pageDic;
    }

    /**
     * Convert this standard java object to a COS object.
     *
     * @return The cos object that matches this Java object.
     */
    public COSBase getCOSObject()
    {
        return page;
    }

    /**
     * This will get the underlying dictionary that this class acts on.
     *
     * @return The underlying dictionary for this class.
     */
    public COSDictionary getCOSDictionary()
    {
        return page;
    }


    /**
     * This is the parent page node.  The parent is a required element of the
     * page.  This will be null until this page is added to the document.
     *
     * @return The parent to this page.
     */
    public PDPageNode getParent()
    {
        PDPageNode parent = null;
        COSDictionary parentDic = (COSDictionary)page.getDictionaryObject( COSName.PARENT );
        if( parentDic != null )
        {
            parent = new PDPageNode( parentDic );
        }
        return parent;
    }

    /**
     * This will set the parent of this page.
     *
     * @param parent The parent to this page node.
     */
    public void setParent( PDPageNode parent )
    {
        page.setItem( COSName.PARENT, parent.getDictionary() );
    }

    /**
     * This will update the last modified time for the page object.
     */
    public void updateLastModified()
    {
        DateConverter converter = new DateConverter();
        GregorianCalendar today = new GregorianCalendar();
        page.setItem( COSName.getPDFName( "LastModified" ), new COSString( converter.toString( today ) ) );
    }

    /**
     * This will get the date that the content stream was last modified.  This
     * may return null.
     *
     * @return The date the content stream was last modified.
     *
     * @throws IOException If there is an error accessing the date information.
     */
    public Calendar getLastModified() throws IOException
    {
        Calendar retval = null;
        COSString date = (COSString)page.getDictionaryObject( COSName.getPDFName( "LastModified" ) );
        if( date != null )
        {
            DateConverter converter = new DateConverter();
            retval = converter.toCalendar( date.getString() );
        }
        return retval;
    }

    /**
     * This will get the resources at this page and not look up the hierarchy.
     * This attribute is inheritable, and findResources() should probably used.
     * This will return null if no resources are available at this level.
     *
     * @return The resources at this level in the hierarchy.
     */
    public PDResources getResources()
    {
        PDResources retval = null;
        COSDictionary resources = (COSDictionary)page.getDictionaryObject( COSName.RESOURCES );
        if( resources != null )
        {
            retval = new PDResources( resources );
        }
        return retval;
    }

    /**
     * This will find the resources for this page by looking up the hierarchy until
     * it finds them.
     *
     * @return The resources at this level in the hierarchy.
     */
    public PDResources findResources()
    {
        PDResources retval = getResources();
        PDPageNode parent = getParent();
        if( retval == null && parent != null )
        {
            retval = parent.findResources();
        }
        return retval;
    }

    /**
     * This will set the resources for this page.
     *
     * @param resources The new resources for this page.
     */
    public void setResources( PDResources resources )
    {
        if( resources == null )
        {
            page.removeItem( COSName.RESOURCES );
        }
        else
        {
            page.setItem( COSName.RESOURCES, resources.getCOSDictionary() );
        }
    }

    /**
     * A rectangle, expressed
     * in default user space units, defining the boundaries of the physical
     * medium on which the page is intended to be displayed or printed
     *
     * This will get the MediaBox at this page and not look up the hierarchy.
     * This attribute is inheritable, and findMediaBox() should probably used.
     * This will return null if no MediaBox are available at this level.
     *
     * @return The MediaBox at this level in the hierarchy.
     */
    public PDRectangle getMediaBox()
    {
        PDRectangle retval = null;
        COSArray array = (COSArray)page.getDictionaryObject( COSName.getPDFName( "MediaBox" ) );
        if( array != null )
        {
            retval = new PDRectangle( array );
        }
        return retval;
    }

    /**
     * This will find the MediaBox for this page by looking up the hierarchy until
     * it finds them.
     *
     * @return The MediaBox at this level in the hierarchy.
     */
    public PDRectangle findMediaBox()
    {
        PDRectangle retval = getMediaBox();
        PDPageNode parent = getParent();
        if( retval == null && parent != null )
        {
            retval = parent.findMediaBox();
        }
        return retval;
    }

    /**
     * This will set the mediaBox for this page.
     *
     * @param mediaBox The new mediaBox for this page.
     */
    public void setMediaBox( PDRectangle mediaBox )
    {
        if( mediaBox == null )
        {
            page.removeItem( COSName.getPDFName( "MediaBox" ) );
        }
        else
        {
            page.setItem( COSName.getPDFName( "MediaBox" ), mediaBox.getCOSArray() );
        }
    }

    /**
     * A rectangle, expressed in default user space units,
     * defining the visible region of default user space. When the page is displayed
     * or printed, its contents are to be clipped (cropped) to this rectangle
     * and then imposed on the output medium in some implementationdefined
     * manner
     *
     * This will get the CropBox at this page and not look up the hierarchy.
     * This attribute is inheritable, and findCropBox() should probably used.
     * This will return null if no CropBox is available at this level.
     *
     * @return The CropBox at this level in the hierarchy.
     */
    public PDRectangle getCropBox()
    {
        PDRectangle retval = null;
        COSArray array = (COSArray)page.getDictionaryObject( COSName.getPDFName( "CropBox" ) );
        if( array != null )
        {
            retval = new PDRectangle( array );
        }
        return retval;
    }

    /**
     * This will find the CropBox for this page by looking up the hierarchy until
     * it finds them.
     *
     * @return The CropBox at this level in the hierarchy.
     */
    public PDRectangle findCropBox()
    {
        PDRectangle retval = getCropBox();
        PDPageNode parent = getParent();
        if( retval == null && parent != null )
        {
            retval = findParentCropBox( parent );
        }

        //default value for cropbox is the media box
        if( retval == null )
        {
            retval = findMediaBox();
        }
        return retval;
    }

    /**
     * This will search for a crop box in the parent and return null if it is not
     * found.  It will NOT default to the media box if it cannot be found.
     *
     * @param node The node
     */
    private PDRectangle findParentCropBox( PDPageNode node )
    {
        PDRectangle rect = node.getCropBox();
        PDPageNode parent = node.getParent();
        if( rect == null && parent != null )
        {
            rect = findParentCropBox( parent );
        }
        return rect;
    }

    /**
     * This will set the CropBox for this page.
     *
     * @param cropBox The new CropBox for this page.
     */
    public void setCropBox( PDRectangle cropBox )
    {
        if( cropBox == null )
        {
            page.removeItem( COSName.getPDFName( "CropBox" ) );
        }
        else
        {
            page.setItem( COSName.getPDFName( "CropBox" ), cropBox.getCOSArray() );
        }
    }

    /**
     * A rectangle, expressed in default user space units, defining
     * the region to which the contents of the page should be clipped
     * when output in a production environment.  The default is the CropBox.
     *
     * @return The BleedBox attribute.
     */
    public PDRectangle getBleedBox()
    {
        PDRectangle retval = null;
        COSArray array = (COSArray)page.getDictionaryObject( COSName.getPDFName( "BleedBox" ) );
        if( array != null )
        {
            retval = new PDRectangle( array );
        }
        else
        {
            retval = findCropBox();
        }
        return retval;
    }

    /**
     * This will set the BleedBox for this page.
     *
     * @param bleedBox The new BleedBox for this page.
     */
    public void setBleedBox( PDRectangle bleedBox )
    {
        if( bleedBox == null )
        {
            page.removeItem( COSName.getPDFName( "BleedBox" ) );
        }
        else
        {
            page.setItem( COSName.getPDFName( "BleedBox" ), bleedBox.getCOSArray() );
        }
    }

    /**
     * A rectangle, expressed in default user space units, defining
     * the intended dimensions of the finished page after trimming.
     * The default is the CropBox.
     *
     * @return The TrimBox attribute.
     */
    public PDRectangle getTrimBox()
    {
        PDRectangle retval = null;
        COSArray array = (COSArray)page.getDictionaryObject( COSName.getPDFName( "TrimBox" ) );
        if( array != null )
        {
            retval = new PDRectangle( array );
        }
        else
        {
            retval = findCropBox();
        }
        return retval;
    }

    /**
     * This will set the TrimBox for this page.
     *
     * @param trimBox The new TrimBox for this page.
     */
    public void setTrimBox( PDRectangle trimBox )
    {
        if( trimBox == null )
        {
            page.removeItem( COSName.getPDFName( "TrimBox" ) );
        }
        else
        {
            page.setItem( COSName.getPDFName( "TrimBox" ), trimBox.getCOSArray() );
        }
    }

    /**
     * A rectangle, expressed in default user space units, defining
     * the extent of the page's meaningful content (including potential
     * white space) as intended by the page's creator  The default isthe CropBox.
     *
     * @return The ArtBox attribute.
     */
    public PDRectangle getArtBox()
    {
        PDRectangle retval = null;
        COSArray array = (COSArray)page.getDictionaryObject( COSName.getPDFName( "ArtBox" ) );
        if( array != null )
        {
            retval = new PDRectangle( array );
        }
        else
        {
            retval = findCropBox();
        }
        return retval;
    }

    /**
     * This will set the ArtBox for this page.
     *
     * @param artBox The new ArtBox for this page.
     */
    public void setArtBox( PDRectangle artBox )
    {
        if( artBox == null )
        {
            page.removeItem( COSName.getPDFName( "ArtBox" ) );
        }
        else
        {
            page.setItem( COSName.getPDFName( "ArtBox" ), artBox.getCOSArray() );
        }
    }


    //todo BoxColorInfo
    //todo Contents

    /**
     * A value representing the rotation.  This will be null if not set at this level
     * The number of degrees by which the page should
     * be rotated clockwise when displayed or printed. The value must be a multiple
     * of 90.
     *
     * This will get the rotation at this page and not look up the hierarchy.
     * This attribute is inheritable, and findRotation() should probably used.
     * This will return null if no rotation is available at this level.
     *
     * @return The rotation at this level in the hierarchy.
     */
    public Integer getRotation()
    {
        Integer retval = null;
        COSNumber value = (COSNumber)page.getDictionaryObject( COSName.getPDFName( "Rotate" ) );
        if( value != null )
        {
            retval = new Integer( (int)value.intValue() );
        }
        return retval;
    }

    /**
     * This will find the rotation for this page by looking up the hierarchy until
     * it finds them.
     *
     * @return The rotation at this level in the hierarchy.
     */
    public int findRotation()
    {
        int retval = 0;
        Integer rotation = getRotation();
        if( rotation != null )
        {
            retval = rotation.intValue();
        }
        else
        {
            PDPageNode parent = getParent();
            if( parent != null )
            {
                retval = parent.findRotation();
            }
        }

        return retval;
    }

    /**
     * This will set the rotation for this page.
     *
     * @param rotation The new rotation for this page.
     */
    public void setRotation( int rotation )
    {
        page.setItem( COSName.getPDFName( "Rotate" ), new COSInteger( rotation ) );
    }

    /**
     * This will get the contents of the PDF Page, in the case that the contents
     * of the page is an array then then the entire array of streams will be
     * be wrapped and appear as a single stream.
     *
     * @return The page content stream.
     *
     * @throws IOException If there is an error obtaining the stream.
     */
    public COSStream getContents() throws IOException
    {
        COSStream retval = null;
        COSBase contents = page.getDictionaryObject( COSName.CONTENTS );
        if( contents != null )
        {
            if( contents instanceof COSStream )
            {
                retval = (COSStream)contents;
            }
            else if( contents instanceof COSArray )
            {
                retval = new COSStreamArray( (COSArray)contents );
            }
            else
            {
                throw new IOException( "Contents are unknown type:" + contents.getClass().getName() );
            }
        }
        return retval;
    }

    /**
     * This will set the contents of this page.
     *
     * @param contents The new contents of the page.
     */
    public void setContents( COSStream contents )
    {
        page.setItem( COSName.CONTENTS, contents );
    }
}
