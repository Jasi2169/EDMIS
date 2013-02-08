/*********************************************************************
*
*      Copyright (C) 2002 Andrew Khan
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

package com.progdan.xls2txt.write.biff;

import com.progdan.xls2txt.biff.Type;
import com.progdan.xls2txt.biff.IntegerHelper;
import com.progdan.xls2txt.biff.StringHelper;
import com.progdan.xls2txt.biff.DoubleHelper;
import com.progdan.xls2txt.biff.WritableRecordData;

/**
 * A name record.  Simply takes the binary data from the name
 * record read in
 */
class NameRecord extends WritableRecordData
{
  /**
   * The binary data for output to file
   */
  private byte[] data;

  /**
   * The name
   */
  private String name;

  /**
   * The index into the name table
   */
  private int index;

  /**
   * A nested class to hold range information
   */
  class NameRange
  {
    private int columnFirst;
    private int rowFirst;
    private int columnLast;
    private int rowLast;
    private int externalSheet;

    NameRange(com.progdan.xls2txt.read.biff.NameRecord.NameRange nr)
    {
      columnFirst = nr.getFirstColumn();
      rowFirst = nr.getFirstRow();
      columnLast = nr.getLastColumn();
      rowLast = nr.getLastRow();
      externalSheet = nr.getExternalSheet();
    }

    int getFirstColumn() {return columnFirst;}
    int getFirstRow() {return rowFirst;}
    int getLastColumn() {return columnLast;}
    int getLastRow() {return rowLast;}
    int getExternalSheet() { return externalSheet;}
  }

  /**
   * The ranges covered by this name
   */
  private NameRange[] ranges;

  /**
   * Constructor - used when copying sheets
   *
   * @param index the index into the name table
   */
  public NameRecord(com.progdan.xls2txt.read.biff.NameRecord sr, int ind)
  {
    super(Type.NAME);

    data = sr.getData();
    name = sr.getName();
    index = ind;

    // Copy the ranges
    com.progdan.xls2txt.read.biff.NameRecord.NameRange[] r = sr.getRanges();
    ranges = new NameRange[r.length];
    for (int i = 0 ; i < ranges.length ; i++)
    {
      ranges[i] = new NameRange(r[i]);
    }
  }

  /**
   * Gets the binary data for output to file
   *
   * @return the binary data
   */
  public byte[] getData()
  {
    return data;
  }

  /**
   * Accessor for the name
   *
   * @return the name
   */
  public String getName()
  {
    return name;
  }

  /**
   * Accessor for the index of this name in the name table
   *
   * @return the index of this name in the name table
   */
  public int getIndex()
  {
    return index;
  }

  /**
   * Gets the array of ranges for this name
   * @return the ranges
   */
  public NameRange[] getRanges()
  {
    return ranges;
  }
}

