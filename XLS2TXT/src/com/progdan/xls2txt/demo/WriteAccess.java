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

package com.progdan.xls2txt.demo;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.IOException;

import com.progdan.xls2txt.WorkbookSettings;
import com.progdan.xls2txt.biff.Type;
import com.progdan.xls2txt.biff.StringHelper;
import com.progdan.xls2txt.read.biff.File;
import com.progdan.xls2txt.read.biff.BiffRecordReader;
import com.progdan.xls2txt.read.biff.BiffException;
import com.progdan.xls2txt.read.biff.Record;

/**
 * Displays whatever generated the excel file (ie. the WriteAccess record)
 */
class WriteAccess
{
  private BiffRecordReader reader;

  public WriteAccess(java.io.File file)
    throws IOException, BiffException
  {
    WorkbookSettings ws = new WorkbookSettings();
    FileInputStream fis = new FileInputStream(file);
    File f = new File(fis, ws);
    reader = new BiffRecordReader(f);

    display(ws);
    fis.close();
  }

  /**
   * Dumps out the contents of the excel file
   */
  private void display(WorkbookSettings ws) throws IOException
  {
    Record r = null;
    boolean found = false;
    while (reader.hasNext() && !found)
    {
      r = reader.next();
      if (r.getType() == Type.WRITEACCESS)
      {
        found = true;
      }
    }

    if (!found)
    {
      System.err.println("Warning:  could not find write access record");
      return;
    }

    byte[] data = r.getData();

    String s = null;

    s = StringHelper.getString(data, data.length, 0, ws);

    System.out.println(s);
  }
}
