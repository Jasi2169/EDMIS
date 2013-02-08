/*********************************************************************
*
*      Copyright (C) 2004 Andrew Khan
*
* This library is free software; you can redistribute it and/or
* modify it under the terms of the GNU Lesser General Public
* License as published by the Free Software Foundation; either
* version 2.1 of the License, or (at your option) any later version.
*
* This library is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
* Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with this library; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
***************************************************************************/

package com.progdan.xls2txt.biff.drawing;

import java.io.IOException;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;

import com.progdan.xls2txt.common.Assert;
import com.progdan.xls2txt.common.Logger;

import com.progdan.xls2txt.read.biff.Record;
import com.progdan.xls2txt.write.biff.File;

/**
 * This class contains the Excel picture data in Escher format for the
 * entire workbook
 */
public class DrawingGroup implements EscherStream
{
  /**
   * The logger
   */
  private static Logger logger = Logger.getLogger(DrawingGroup.class);

  /**
   * The escher data read in from file
   */
  private byte[] drawingData;

  /**
   * The top level escher container
   */
  private EscherContainer escherData;

  /**
   * The Bstore container, which contains all the drawing data
   */
  private BStoreContainer bstoreContainer;

  /**
   * The initialized flag
   */
  private boolean initialized;

  /**
   * The list of user added drawings
   */
  private ArrayList drawings;

  /**
   * The number of blips
   */
  private int numBlips;

  /**
   * The number of charts
   */
  private int numCharts;

  /**
   * The number of shape ids used on the second Dgg cluster
   */
  private int drawingGroupId;

  /**
   * Flag which indicates that at least one of the drawings has been omitted
   * from the worksheet
   */
  private boolean drawingsOmitted;

  /**
   * The origin of this drawing group
   */
  private Origin origin;

  /**
   * A hash map of images keyed on the file path, containing the
   * reference count
   */
  private HashMap imageFiles;

  private static class Origin {};
  public static final Origin READ = new Origin();
  public static final Origin WRITE = new Origin();
  public static final Origin READ_WRITE = new Origin();

  /**
   * Constructor
   *
   * @param o the origin of this drawing group
   */
  public DrawingGroup(Origin o)
  {
    origin = o;
    initialized = o == WRITE ? true : false;
    drawings = new ArrayList();
    imageFiles = new HashMap();
    drawingsOmitted = false;
  }

  /**
   * Adds in a drawing group record to this drawing group.  The binary
   * data is extracted from the drawing group and added to a single
   * byte array
   *
   * @param mso the drawing group record to add
   */
  public void add(MsoDrawingGroupRecord mso)
  {
    addData(mso.getData());
  }

  public void add(Record cont)
  {
    addData(cont.getData());
  }

  private void addData(byte[] msodata)
  {
    if (drawingData == null)
    {
      drawingData = new byte[msodata.length];
      System.arraycopy(msodata, 0, drawingData, 0, msodata.length);
      return;
    }

    // Grow the array
    byte[] newdata = new byte[drawingData.length + msodata.length];
    System.arraycopy(drawingData, 0, newdata, 0, drawingData.length);
    System.arraycopy(msodata, 0, newdata, drawingData.length, msodata.length);
    drawingData = newdata;
  }

  /**
   * Adds a drawing to the drawing group
   *
   * @param d the drawing to add
   */
  final void addDrawing(Drawing d)
  {
    drawings.add(d);
  }

  /**
   * Adds a  chart to the darwing group
   *
   * @param c
   */
  public void add(Chart c)
  {
    numCharts++;
  }

  /**
   * Adds a drawing from the public, writable interface
   *
   * @param d the drawing to add
   */
  public void add(Drawing d)
  {
    if (origin == READ)
    {
      origin = READ_WRITE;
      numBlips = getBStoreContainer().getNumBlips();

      Dgg dgg = (Dgg) escherData.getChildren()[0];
      drawingGroupId = dgg.getCluster(1).drawingGroupId - numBlips - 1;
    }

    // See if this is referenced elsewhere
    Drawing refImage = (Drawing) imageFiles.get(d.getImageFilePath());

    if (refImage == null)
    {
      // There are no other references to this drawing, so assign
      // a new object id and put it on the hash map
      drawings.add(d);
      d.setDrawingGroup(this);
      d.setObjectId(numBlips+1, numBlips+1);
      numBlips++;
      imageFiles.put(d.getImageFilePath(), d);
    }
    else
    {
      // This drawing is used elsewhere in the workbook.  Increment the
      // reference count on the drawing, and set the object id of the drawing
      // passed in
      refImage.setReferenceCount(refImage.getReferenceCount() + 1);
      d.setDrawingGroup(this);
      d.setObjectId(refImage.getObjectId(),refImage.getBlipId());
    }
  }

  /**
   * Interface method to remove a drawing from the group
   *
   * @param d the drawing to remove
   */
  public void remove(Drawing d)
  {
    if (origin == READ)
    {
      origin = READ_WRITE;
      numBlips = getBStoreContainer().getNumBlips();
      Dgg dgg = (Dgg) escherData.getChildren()[0];
      drawingGroupId = dgg.getCluster(1).drawingGroupId - numBlips - 1 ;
    }

    // Get the blip
    EscherRecord[] children = getBStoreContainer().getChildren();
    BlipStoreEntry bse = (BlipStoreEntry) children[d.getBlipId()-1];

    bse.dereference();

    if (bse.getReferenceCount() == 0)
    {
      // Remove the blip
      getBStoreContainer().remove(bse);

      // Adjust blipId on the other blips
      for (Iterator i = drawings.iterator() ; i.hasNext() ; )
      {
        Drawing drawing = (Drawing) i.next();

        if (drawing.getBlipId() > d.getBlipId())
        {
          drawing.setObjectId(drawing.getObjectId(), drawing.getBlipId() - 1);
        }
      }

      numBlips--;
    }
  }


  /**
   * Initializes the drawing data from the escher record read in
   */
  private void initialize()
  {
    EscherRecordData er = new EscherRecordData(this, 0);

    Assert.verify(er.isContainer());

    escherData = new EscherContainer(er);

    Assert.verify(escherData.getLength() == drawingData.length);
    Assert.verify(escherData.getType() == EscherRecordType.DGG_CONTAINER);

    initialized = true;
  }

  /**
   * Gets hold of the BStore container from the Escher data
   *
   * @return the BStore container
   */
  private BStoreContainer getBStoreContainer()
  {
    if (bstoreContainer == null)
    {
      if (!initialized)
      {
        initialize();
      }

      EscherRecord[] children = escherData.getChildren();
      Assert.verify(children[1].getType() ==
                    EscherRecordType.BSTORE_CONTAINER);
      bstoreContainer = (BStoreContainer) children[1];
    }

    return bstoreContainer;
  }

  /**
   * Gets hold of the binary data
   *
   * @return the data
   */
  public byte[] getData()
  {
    return drawingData;
  }

  /**
   * Writes the drawing group to the output file
   *
   * @param outputFile the file to write to
   * @exception IOException
   */
  public void write(File outputFile) throws IOException
  {
    if (origin == WRITE)
    {
      DggContainer dggContainer = new DggContainer();

      Dgg dgg = new Dgg(numBlips+numCharts+1, numBlips);

      dgg.addCluster(1,0);
      dgg.addCluster(numBlips+1,0);

      dggContainer.add(dgg);

      BStoreContainer bstoreCont = new BStoreContainer(drawings.size());

      // Create a blip entry for each drawing
      for (Iterator i = drawings.iterator(); i.hasNext();)
      {
        Drawing d = (Drawing) i.next();
        BlipStoreEntry bse = new BlipStoreEntry(d);

       bstoreCont.add(bse);
      }
      dggContainer.add(bstoreCont);

      Opt opt = new Opt();

      /*
      opt.addProperty(191, false, false, 524296);
      opt.addProperty(385, false, false, 134217737);
      opt.addProperty(448, false, false, 134217792);
      */

      dggContainer.add(opt);

      SplitMenuColors splitMenuColors = new SplitMenuColors();
      dggContainer.add(splitMenuColors);

      drawingData = dggContainer.getData();
    }
    else if (origin == READ_WRITE)
    {
      DggContainer dggContainer = new DggContainer();

      Dgg dgg = new Dgg(numBlips+numCharts+1, numBlips);

      dgg.addCluster(1,0);
      dgg.addCluster(drawingGroupId+numBlips+1,0);

      dggContainer.add(dgg);

      BStoreContainer bstoreCont = new BStoreContainer(numBlips);

      // Create a blip entry for each drawing that was read in
      BStoreContainer readBStoreContainer = getBStoreContainer();
      EscherRecord[] children = readBStoreContainer.getChildren();
      for (int i = 0; i < children.length ; i++)
      {
        BlipStoreEntry bse = (BlipStoreEntry) children[i];
        bstoreCont.add(bse);
      }

      // Create a blip entry for each drawing that has been added
      for (Iterator i = drawings.iterator(); i.hasNext();)
      {
        Drawing d = (Drawing) i.next();
        if (d.getOrigin() != Drawing.READ)
        {
          BlipStoreEntry bse = new BlipStoreEntry(d);
          bstoreCont.add(bse);
        }
      }
      dggContainer.add(bstoreCont);

      Opt opt = new Opt();

      opt.addProperty(191, false, false, 524296);
      opt.addProperty(385, false, false, 134217737);
      opt.addProperty(448, false, false, 134217792);


      dggContainer.add(opt);

      SplitMenuColors splitMenuColors = new SplitMenuColors();
      dggContainer.add(splitMenuColors);

      drawingData = dggContainer.getData();
    }

    MsoDrawingGroupRecord msodg = new MsoDrawingGroupRecord(drawingData);
    outputFile.write(msodg);
  }

  /**
   * Accessor for the number of blips in the drawing group
   *
   * @return the number of blips
   */
  final int getNumberOfBlips()
  {
    return numBlips;
  }

  /**
   * Gets the drawing data for the given blip id.  Called by the Drawing
   * object
   *
   * @param blipId the blipId
   * @return the drawing data
   */
  byte[] getImageData(int blipId)
  {
    numBlips = getBStoreContainer().getNumBlips();

    Assert.verify(blipId <= numBlips);
    Assert.verify(origin == READ || origin == READ_WRITE);

    // Get the blip
    EscherRecord[] children = getBStoreContainer().getChildren();
    BlipStoreEntry bse = (BlipStoreEntry) children[blipId-1];

    return bse.getImageData();
  }

  /**
   * Indicates that at least one of the drawings has been omitted from
   * the worksheet
   */
  public void setDrawingsOmitted()
  {
    drawingsOmitted = true;
  }

  /**
   * Accessor for the drawingsOmitted flag
   *
   * @return TRUE if a drawing has been omitted, FALSE otherwise
   */
  public boolean hasDrawingsOmitted()
  {
    return drawingsOmitted;
  }
}
