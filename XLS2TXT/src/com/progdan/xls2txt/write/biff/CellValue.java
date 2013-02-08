/*********************************************************************
*
*      Copyright (C) 2001 Andrew Khan
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
 License along with this library; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
***************************************************************************/

package com.progdan.xls2txt.write.biff;

import com.progdan.xls2txt.common.Assert;
import com.progdan.xls2txt.common.Logger;

import com.progdan.xls2txt.Cell;
import com.progdan.xls2txt.Sheet;
import com.progdan.xls2txt.biff.Type;
import com.progdan.xls2txt.biff.IntegerHelper;
import com.progdan.xls2txt.biff.WritableRecordData;
import com.progdan.xls2txt.biff.XFRecord;
import com.progdan.xls2txt.biff.FormattingRecords;
import com.progdan.xls2txt.biff.NumFormatRecordsException;
import com.progdan.xls2txt.write.WritableCell;
import com.progdan.xls2txt.write.WritableWorkbook;
import com.progdan.xls2txt.format.CellFormat;
import com.progdan.xls2txt.format.Alignment;
import com.progdan.xls2txt.format.Border;
import com.progdan.xls2txt.format.BorderLineStyle;
import com.progdan.xls2txt.format.Font;
import com.progdan.xls2txt.format.VerticalAlignment;

/**
 * Abstract class which stores the common data used for cells, such
 * as row, column and formatting information.
 * Any record which directly represents the contents of a cell, such
 * as labels and numbers, are derived from this class
 * data store
 */
public abstract class CellValue extends WritableRecordData
  implements WritableCell
{
  /**
   * The logger
   */
  private static Logger logger = Logger.getLogger(CellValue.class);

  /**
   * The row in the worksheet at which this cell is located
   */
  private int row;
  /**
   * The column in the worksheet at which this cell is located
   */
  private int column;
  /**
   * The format applied to this cell
   */
  private XFRecord format;

  /**
   * A handle to the formatting records, used in case we want
   * to change the format of the cell once it has been added
   * to the spreadsheet
   */
  private FormattingRecords formattingRecords;

  /**
   * A flag to indicate that this record is already referenced within
   * a worksheet
   */
  private boolean referenced;

  /**
   * A handle to the sheet
   */
  private WritableSheetImpl sheet;

  /**
   * Constructor used when building writable cells from the Java API
   *
   * @param c the column
   * @param t the type indicator
   * @param r the row
   */
  protected CellValue(Type t, int c, int r)
  {
    this(t, c, r, WritableWorkbookImpl.NORMAL_STYLE);
  }

  /**
   * Constructor used when creating a writable cell from a read-only cell
   * (when copying a workbook)
   *
   * @param c the cell to clone
   * @param t the type of this cell
   */
  protected CellValue(Type t, Cell c)
  {
    this(t, c.getColumn(),
         c.getRow());

    format = (XFRecord) c.getCellFormat();
  }

  /**
   * Overloaded constructor used when building writable cells from the
   * Java API which also takes a format
   *
   * @param c the column
   * @param t the cell type
   * @param r the row
   * @param st the format to apply to this cell
   */
  protected CellValue(Type t, int c, int r, CellFormat st)
  {
    super(t);
    row    = r;
    column = c;
    format = (XFRecord) st;
    referenced = false;
  }

  /**
   * Copy constructor
   *
   * @param c the column
   * @param t the cell type
   * @param r the row
   * @param cv the value to copy
   */
  protected CellValue(Type t, int c, int r, CellValue cv)
  {
    super(t);
    row    = r;
    column = c;
    format = cv.format;
    referenced = false;
  }

  /**
   * An API function which sets the format to apply to this cell
   *
   * @param cf the format to apply to this cell
   */
  public void setCellFormat(CellFormat cf)
  {
    format = (XFRecord) cf;

    // If the referenced flag has not been set, this cell has not
    // been added to the spreadsheet, so we don't need to perform
    // any further logic
    if (!referenced)
    {
      return;
    }

    // The cell has already been added to the spreadsheet, so the
    // formattingRecords reference must be initialized
    Assert.verify(formattingRecords != null);

    addCellFormat();
  }

  /**
   * Returns the row number of this cell
   *
   * @return the row number of this cell
   */
  public int getRow()
  {
    return row;
  }

  /**
   * Returns the column number of this cell
   *
   * @return the column number of this cell
   */
  public int getColumn()
  {
    return column;
  }

  /**
   * Indicates whether or not this cell is hidden, by virtue of either
   * the entire row or column being collapsed
   *
   * @return TRUE if this cell is hidden, FALSE otherwise
   */
  public boolean isHidden()
  {
    ColumnInfoRecord cir = sheet.getColumnInfo(column);

    if (cir != null && cir.getWidth() == 0)
    {
      return true;
    }

    RowRecord rr = sheet.getRowInfo(row);

    if (rr != null && (rr.getRowHeight() == 0 || rr.isCollapsed()))
    {
      return true;
    }

    return false;
  }

  /**
   * Gets the data to write to the output file
   *
   * @return the binary data
   */
  public byte[] getData()
  {
    byte[] mydata = new byte[6];
    IntegerHelper.getTwoBytes(row, mydata, 0);
    IntegerHelper.getTwoBytes(column, mydata, 2);
    IntegerHelper.getTwoBytes(format.getXFIndex(), mydata, 4);
    return mydata;
  }

  /**
   * Called when the cell is added to the worksheet in order to indicate
   * that this object is already added to the worksheet
   * This method also verifies that the associated formats and formats
   * have been initialized correctly
   *
   * @param fr the formatting records
   * @param ss the shared strings used within the workbook
   * @param s the sheet this is being added to
   */
  void setCellDetails(FormattingRecords fr, SharedStrings ss,
                      WritableSheetImpl s)
  {
    referenced = true;
    sheet = s;
    formattingRecords = fr;

    addCellFormat();
  }

  /**
   * Internal method to see if this cell is referenced within the workbook.
   * Once this has been placed in the workbook, it becomes immutable
   *
   * @return TRUE if this cell has been added to a sheet, FALSE otherwise
   */
  final boolean isReferenced()
  {
    return referenced;
  }

  /**
   * Gets the internal index of the formatting record
   *
   * @return the index of the format record
   */
  final int getXFIndex()
  {
    return format.getXFIndex();
  }

  /**
   * API method which gets the format applied to this cell
   *
   * @return the format for this cell
   */
  public CellFormat getCellFormat()
  {
    return format;
  }

  /**
   * Increments the row of this cell by one.  Invoked by the sheet when
   * inserting rows
   */
  void incrementRow()
  {
    row++;
  }

  /**
   * Decrements the row of this cell by one.  Invoked by the sheet when
   * removing rows
   */
  void decrementRow()
  {
    row--;
  }

  /**
   * Increments the column of this cell by one.  Invoked by the sheet when
   * inserting columns
   */
  void incrementColumn()
  {
    column++;
  }

  /**
   * Decrements the column of this cell by one.  Invoked by the sheet when
   * removing columns
   */
  void decrementColumn()
  {
    column--;
  }

  /**
   * Called when a column is inserted on the specified sheet.  Notifies all
   * RCIR cells of this change. The default implementation here does nothing
   *
   * @param s the sheet on which the column was inserted
   * @param sheetIndex the sheet index on which the column was inserted
   * @param col the column number which was inserted
   */
  void columnInserted(Sheet s, int sheetIndex, int col)
  {
  }

  /**
   * Called when a column is removed on the specified sheet.  Notifies all
   * RCIR cells of this change. The default implementation here does nothing
   *
   * @param s the sheet on which the column was inserted
   * @param sheetIndex the sheet index on which the column was inserted
   * @param col the column number which was inserted
   */
  void columnRemoved(Sheet s, int sheetIndex, int col)
  {
  }

  /**
   * Called when a row is inserted on the specified sheet.  Notifies all
   * RCIR cells of this change. The default implementation here does nothing
   *
   * @param s the sheet on which the column was inserted
   * @param sheetIndex the sheet index on which the column was inserted
   * @param row the column number which was inserted
   */
  void rowInserted(Sheet s, int sheetIndex, int row)
  {
  }

  /**
   * Called when a row is inserted on the specified sheet.  Notifies all
   * RCIR cells of this change. The default implementation here does nothing
   *
   * @param s the sheet on which the row was removed
   * @param sheetIndex the sheet index on which the column was removed
   * @param row the column number which was removed
   */
  void rowRemoved(Sheet s, int sheetIndex, int row)
  {
  }

  /**
   * Accessor for the sheet containing this cell
   *
   * @return the sheet containing this cell
   */
  protected WritableSheetImpl getSheet()
  {
    return sheet;
  }

  /**
   * Adds the format information to the shared records.  Performs the necessary
   * checks (and clones) to ensure that the formats are not shared.
   * Called from setCellDetails and setCellFormat
   */
  private void addCellFormat()
  {
    // Check to see if the format is one of the shared Workbook defaults.  If
    // so, then get hold of the Workbook's specific instance
    if (format == WritableWorkbook.NORMAL_STYLE)
    {
      Styles styles = sheet.getWorkbook().getStyles();
      format = styles.getNormalStyle();
    }
    else if (format == WritableWorkbook.HYPERLINK_STYLE)
    {
      Styles styles = sheet.getWorkbook().getStyles();
      format = styles.getHyperlinkStyle();
    }
    else if (format == WritableWorkbook.HIDDEN_STYLE)
    {
      Styles styles = sheet.getWorkbook().getStyles();
      format = styles.getHiddenStyle();
    }

    // Do the same with the statically shared fonts
    if (format.getFont() == WritableWorkbook.ARIAL_10_PT)
    {
      Styles styles = sheet.getWorkbook().getStyles();
      format.setFont(styles.getArial10Pt());
    }
    else if (format.getFont() == WritableWorkbook.HYPERLINK_FONT)
    {
      Styles styles = sheet.getWorkbook().getStyles();
      format.setFont(styles.getHyperlinkFont());
    }

    try
    {
      if (!format.isInitialized())
      {
        formattingRecords.addStyle(format);
      }
    }
    catch (NumFormatRecordsException e)
    {
      logger.warn("Maximum number of format records exceeded.  Using default format.");
      Styles styles = sheet.getWorkbook().getStyles();
      format = styles.getNormalStyle();
    }

  }

}
