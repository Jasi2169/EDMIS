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

import com.progdan.xls2txt.common.Assert;

import com.progdan.xls2txt.biff.Type;
import com.progdan.xls2txt.biff.IntegerHelper;
import com.progdan.xls2txt.biff.StringHelper;
import com.progdan.xls2txt.biff.WritableRecordData;

/**
 * Stores the supporting workbook information.  For files written by
 * JExcelApi this will only reference internal sheets
 */
class SupbookRecord extends WritableRecordData
{
  /**
   * The type of this supbook record
   */
  private SupbookType type;

  /**
   * The data to be written to the binary file
   */
  private byte[] data;

  /**
   * The number of sheets - internal & external supbooks only
   */
  private int numSheets;

  /**
   * The name of the external file
   */
  private String fileName;

  /**
   * The names of the external sheets
   */
  private String[] sheetNames;

  /**
   * The type of supbook this refers to
   */
  private static class SupbookType {};

  public final static SupbookType INTERNAL = new SupbookType();
  public final static SupbookType EXTERNAL = new SupbookType();
  public final static SupbookType ADDIN    = new SupbookType();
  public final static SupbookType LINK     = new SupbookType();
  public final static SupbookType UNKNOWN  = new SupbookType();

  /**
   * Constructor for internal sheets
   */
  public SupbookRecord(int sheets)
  {
    super(Type.SUPBOOK);

    numSheets = sheets;
    data = new byte[4];

    IntegerHelper.getTwoBytes(sheets, data, 0);
    data[2] = 0x1;
    data[3] = 0x4;

    type = INTERNAL;
  }

  /**
   * Constructor used when copying from an external workbook
   */
  public SupbookRecord(com.progdan.xls2txt.read.biff.SupbookRecord sr)
  {
    super(Type.SUPBOOK);

    if (sr.getType() == sr.INTERNAL)
    {
      initInternal(sr);
    }
    else if (sr.getType() == sr.EXTERNAL)
    {
      initExternal(sr);
    }
  }

  /**
   * Initializes an internal supbook record
   *
   * @param sr the read supbook record to copy from
   */
  private void initInternal(com.progdan.xls2txt.read.biff.SupbookRecord sr)
  {
    initInternal(sr.getNumberOfSheets());
  }

  /**
   * Initializes an internal supbook record
   *
   * @param sheets the number of sheets
   */
  private void initInternal(int sheets)
  {
    numSheets = sheets;

    data = new byte[4];

    IntegerHelper.getTwoBytes(numSheets, data, 0);
    data[2] = 0x1;
    data[3] = 0x4;
    type = INTERNAL;
  }

  /**
   * Adjust the number of internal sheets.  Called by WritableSheet when
   * a sheet is added or or removed to the workbook
   *
   * @param sheets the new number of sheets
   */
  void adjustInternal(int sheets)
  {
    Assert.verify(type == INTERNAL);
    initInternal(sheets);
  }

  /**
   * Initializes an external supbook record
   *
   * @param sr the read supbook record to copy from
   */
  private void initExternal(com.progdan.xls2txt.read.biff.SupbookRecord sr)
  {
    numSheets = sr.getNumberOfSheets();
    fileName = sr.getFileName();
    sheetNames = new String[numSheets];

    int totalSheetNameLength = 0;
    for (int i = 0; i < numSheets; i++)
    {
      sheetNames[i] = sr.getSheetName(i);
      totalSheetNameLength += sheetNames[i].length();
    }

    int dataLength = 2 + // numsheets
                     4 + fileName.length() +
                     numSheets * 3 + totalSheetNameLength * 2;

    data = new byte[dataLength];

    IntegerHelper.getTwoBytes(numSheets, data, 0);

    // Add in the file name.  Precede with a byte denoting that is a file name
    int pos = 2;
    IntegerHelper.getTwoBytes(fileName.length()+1, data, pos);
    data[pos+2] = 0; // ascii indicator
    data[pos+3] = 1; // file name indicator
    StringHelper.getBytes(fileName, data, pos+4);

    pos += 4 + fileName.length();

    // Get the sheet names
    for (int i = 0; i < sheetNames.length; i++)
    {
      IntegerHelper.getTwoBytes(sheetNames[i].length(), data, pos);
      data[pos+2] = 1; // unicode indicator
      StringHelper.getUnicodeBytes(sheetNames[i], data, pos+3);
      pos += 3 + sheetNames[i].length() * 2;
    }

    type = EXTERNAL;
  }

  /**
   * The binary data to be written out
   *
   * @return the binary data
   */
  public byte[] getData()
  {
    return data;
  }

  /**
   * Gets the type of this supbook record
   *
   * @return the type of this supbook
   */
  public SupbookType getType()
  {
    return type;
  }

  /**
   * Gets the number of sheets.  This will only be non-zero for internal
   * and external supbooks
   *
   * @return the number of sheets
   */
  public int getNumberOfSheets()
  {
    return numSheets;
  }
}
