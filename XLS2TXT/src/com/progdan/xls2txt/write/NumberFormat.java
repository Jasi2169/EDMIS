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

package com.progdan.xls2txt.write;

import java.text.DecimalFormat;

import com.progdan.xls2txt.biff.DisplayFormat;
import com.progdan.xls2txt.write.biff.NumberFormatRecord;

/**
 * A custom user defined number format, which may be instantiated within user
 * applications in order to present numerical values to the appropriate level
 * of accuracy.
 * The string format used to create a number format adheres to the standard
 * java specification, and JExcelAPI makes the necessary modifications so
 * that it is rendered in Excel as the nearest possible equivalent.
 * Once created, this may be used within a CellFormat object, which in turn
 * is a parameter passed to the constructor of the Number cell
 */
public class NumberFormat extends NumberFormatRecord implements DisplayFormat
{
  /**
   * Constructor, taking in the Java compliant number format
   *
   * @param format the format string
   */
  public NumberFormat(String format)
  {
    super(format);

    // Verify that the format is valid
    DecimalFormat df = new DecimalFormat(format);
  }
}
