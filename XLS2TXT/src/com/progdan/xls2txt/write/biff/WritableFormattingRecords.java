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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;

import com.progdan.xls2txt.common.Assert;
import com.progdan.xls2txt.biff.FormatRecord;
import com.progdan.xls2txt.biff.Fonts;
import com.progdan.xls2txt.biff.XFRecord;
import com.progdan.xls2txt.biff.FormattingRecords;
import com.progdan.xls2txt.biff.NumFormatRecordsException;
import com.progdan.xls2txt.format.CellFormat;
import com.progdan.xls2txt.format.Alignment;
import com.progdan.xls2txt.format.VerticalAlignment;
import com.progdan.xls2txt.write.WritableCellFormat;
import com.progdan.xls2txt.write.WritableWorkbook;
import com.progdan.xls2txt.write.NumberFormats;

/**
 * Handles the Format and XF record indexing.  The writable subclass
 * instantiates the predetermined list of XF records and formats
 * present in every Excel Workbook
 */
public class WritableFormattingRecords extends FormattingRecords
{
  /**
   * The statically defined normal style
   */
  public static WritableCellFormat normalStyle;

  /**
   * Constructor.  Instantiates the prerequisite list of formats and
   * styles required by all Excel workbooks
   *
   * @param f the list of Fonts
   */
  public WritableFormattingRecords(Fonts f)
  {
    super(f);

    try
    {
      // Hard code all the styles
      StyleXFRecord sxf = new StyleXFRecord
        (WritableWorkbookImpl.ARIAL_10_PT,NumberFormats.DEFAULT);
      sxf.setLocked(true);
      addStyle(sxf);

      sxf = new StyleXFRecord(getFonts().getFont(1),NumberFormats.DEFAULT);
      sxf.setLocked(true);
      sxf.setCellOptions(0xf400);
      addStyle(sxf);

      sxf = new StyleXFRecord(getFonts().getFont(1),NumberFormats.DEFAULT);
      sxf.setLocked(true);
      sxf.setCellOptions(0xf400);
      addStyle(sxf);

      sxf = new StyleXFRecord(getFonts().getFont(1),NumberFormats.DEFAULT);
      sxf.setLocked(true);
      sxf.setCellOptions(0xf400);
      addStyle(sxf);

      sxf = new StyleXFRecord(getFonts().getFont(2),NumberFormats.DEFAULT);
      sxf.setLocked(true);
      sxf.setCellOptions(0xf400);
      addStyle(sxf);

      sxf = new StyleXFRecord(getFonts().getFont(3),NumberFormats.DEFAULT);
      sxf.setLocked(true);
      sxf.setCellOptions(0xf400);
      addStyle(sxf);

      sxf = new StyleXFRecord(WritableWorkbookImpl.ARIAL_10_PT,
                              NumberFormats.DEFAULT);
      sxf.setLocked(true);
      sxf.setCellOptions(0xf400);
      addStyle(sxf);

      sxf = new StyleXFRecord(WritableWorkbookImpl.ARIAL_10_PT,
                              NumberFormats.DEFAULT);
      sxf.setLocked(true);
      sxf.setCellOptions(0xf400);
      addStyle(sxf);

      sxf = new StyleXFRecord(WritableWorkbookImpl.ARIAL_10_PT,
                              NumberFormats.DEFAULT);
      sxf.setLocked(true);
      sxf.setCellOptions(0xf400);
      addStyle(sxf);

      sxf = new StyleXFRecord(WritableWorkbookImpl.ARIAL_10_PT,
                              NumberFormats.DEFAULT);
      sxf.setLocked(true);
      sxf.setCellOptions(0xf400);
      addStyle(sxf);

      sxf = new StyleXFRecord(WritableWorkbookImpl.ARIAL_10_PT,
                              NumberFormats.DEFAULT);
      sxf.setLocked(true);
      sxf.setCellOptions(0xf400);
      addStyle(sxf);

      sxf = new StyleXFRecord(WritableWorkbookImpl.ARIAL_10_PT,
                              NumberFormats.DEFAULT);
      sxf.setLocked(true);
      sxf.setCellOptions(0xf400);
      addStyle(sxf);

      sxf = new StyleXFRecord(WritableWorkbookImpl.ARIAL_10_PT,
                              NumberFormats.DEFAULT);
      sxf.setLocked(true);
      sxf.setCellOptions(0xf400);
      addStyle(sxf);

      sxf = new StyleXFRecord(WritableWorkbookImpl.ARIAL_10_PT,
                              NumberFormats.DEFAULT);
      sxf.setLocked(true);
      sxf.setCellOptions(0xf400);
      addStyle(sxf);

      sxf = new StyleXFRecord(WritableWorkbookImpl.ARIAL_10_PT,
                              NumberFormats.DEFAULT);
      sxf.setLocked(true);
      sxf.setCellOptions(0xf400);
      addStyle(sxf);

      // That's the end of the built ins.  Write the normal style
      // cell XF here
      addStyle(WritableWorkbook.NORMAL_STYLE);

      // Continue with "user defined" styles
      sxf = new StyleXFRecord(getFonts().getFont(1),
                              NumberFormats.FORMAT7);
      sxf.setLocked(true);
      sxf.setCellOptions(0xf800);
      addStyle(sxf);

      sxf = new StyleXFRecord(getFonts().getFont(1),
                              NumberFormats.FORMAT5);
      sxf.setLocked(true);
      sxf.setCellOptions(0xf800);
      addStyle(sxf);

      sxf = new StyleXFRecord(getFonts().getFont(1),
                              NumberFormats.FORMAT8);
      sxf.setLocked(true);
      sxf.setCellOptions(0xf800);
      addStyle(sxf);

      sxf = new StyleXFRecord(getFonts().getFont(1),
                              NumberFormats.FORMAT6);
      sxf.setLocked(true);
      sxf.setCellOptions(0xf800);
      addStyle(sxf);

      sxf = new StyleXFRecord(getFonts().getFont(1),
                              NumberFormats.PERCENT_INTEGER);
      sxf.setLocked(true);
      sxf.setCellOptions(0xf800);
      addStyle(sxf);

      // Hard code in the pre-defined number formats for now
      /*
        FormatRecord fr = new FormatRecord
        ("\"$\"#,##0_);\\(\"$\"#,##0\\)",5);
        addFormat(fr);

        fr = new FormatRecord
        ("\"$\"#,##0_);[Red]\\(\"$\"#,##0\\)", 6);
        addFormat(fr);

        fr = new FormatRecord
        ("\"$\"#,##0.00_);\\(\"$\"#,##0.00\\)", 7);
        addFormat(fr);

        fr = new FormatRecord
        ("\"$\"#,##0.00_);[Red]\\(\"$\"#,##0.00\\)", 8);
        addFormat(fr);

        fr = new FormatRecord
        ("_(\"$\"* #,##0_);_(\"$\"* \\(#,##0\\);_(\"$\"* \"-\"_);_(@_)",
        0x2a);
        //        outputFile.write(fr);

        fr = new FormatRecord
        ("_(* #,##0_);_(* \\(#,##0\\);_(* \"-\"_);_(@_)",
        0x2e);
        //        outputFile.write(fr);

        fr = new FormatRecord
        ("_(\"$\"* #,##0.00_);_(\"$\"* \\(#,##0.00\\);_(\"$\"* \"-\"??_);_(@_)",
        0x2c);
        //        outputFile.write(fr);

        fr = new FormatRecord
        ("_(* #,##0.00_);_(* \\(#,##0.00\\);_(* \"-\"??_);_(@_)",
        0x2b);
        //        outputFile.write(fr);
        */
    }
    catch (NumFormatRecordsException e)
    {
      // This should not happen yet, since we are just creating the file.
      // Bomb out
      Assert.verify(false, e.getMessage());
    }
  }
}









