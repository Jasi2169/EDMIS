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

import com.progdan.xls2txt.write.WritableFont;
import com.progdan.xls2txt.write.WritableWorkbook;
import com.progdan.xls2txt.biff.Fonts;

/**
 * A container for the list of fonts used in this workbook  The writable
 * subclass instantiates the predetermined list of fonts available to
 * users of the writable API
 */
public class WritableFonts extends Fonts
{
  /**
   * Constructor.  Creates the predetermined list of fonts
   */
  public WritableFonts()
  {
    super();

    addFont(WritableWorkbook.ARIAL_10_PT);

    // Create the default fonts
    WritableFont f = new WritableFont(WritableFont.ARIAL);
    addFont(f);

    f = new WritableFont(WritableFont.ARIAL);
    addFont(f);

    f = new WritableFont(WritableFont.ARIAL);
    addFont(f);
  }
}
