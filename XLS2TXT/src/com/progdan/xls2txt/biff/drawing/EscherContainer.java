/*********************************************************************
*
*      Copyright (C) 2003 Andrew Khan
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

import java.util.Iterator;
import java.util.ArrayList;

/**
 * An escher container.  This record may contain other escher containers or
 * atoms
 */
class EscherContainer extends EscherRecord
{
  private boolean initialized;
  private ArrayList children;

  public EscherContainer(EscherRecordData erd)
  {
    super(erd);
    initialized = false;
    children = new ArrayList();
  }

  protected EscherContainer(EscherRecordType type)
  {
    super(type);
    setContainer(true);
    children = new ArrayList();
  }

  public EscherRecord[] getChildren()
  {
    if (!initialized)
    {
      initialize();
    }

    Object[] ca = children.toArray();
    EscherRecord[] era = new EscherRecord[ca.length];
    System.arraycopy(ca, 0, era, 0, ca.length);

    return era;
  }

  public void add(EscherRecord child)
  {
    children.add(child);
  }

  public void remove(EscherRecord child)
  {
    children.remove(child);
  }

  private void initialize()
  {
    int curpos = getPos() + HEADER_LENGTH;
    int endpos = Math.min(getPos() + getLength(), getStreamLength());

    EscherRecord newRecord = null;

    while (curpos < endpos)
    {
      EscherRecordData erd = new EscherRecordData(getEscherStream(), curpos);

      EscherRecordType type = erd.getType();
      if (type == EscherRecordType.DGG)
      {
        newRecord = new Dgg(erd);
      }
      else if (type == EscherRecordType.DG)
      {
        newRecord = new Dg(erd);
      }
      else if (type == EscherRecordType.BSTORE_CONTAINER)
      {
        newRecord = new BStoreContainer(erd);
      }
      else if (type == EscherRecordType.SPGR_CONTAINER)
      {
        newRecord = new SpgrContainer(erd);
      }
      else if (type == EscherRecordType.SP_CONTAINER)
      {
        newRecord = new SpContainer(erd);
      }
      else if (type == EscherRecordType.SPGR)
      {
        newRecord = new Spgr(erd);
      }
      else if (type == EscherRecordType.SP)
      {
        newRecord = new Sp(erd);
      }
      else if (type == EscherRecordType.CLIENT_ANCHOR)
      {
        newRecord = new ClientAnchor(erd);
      }
      else if (type == EscherRecordType.CLIENT_DATA)
      {
        newRecord = new ClientData(erd);
      }
      else if (type == EscherRecordType.BSE)
      {
        newRecord = new BlipStoreEntry(erd);
      }
      else if (type == EscherRecordType.OPT)
      {
        newRecord = new Opt(erd);
      }
      else if (type == EscherRecordType.SPLIT_MENU_COLORS)
      {
        newRecord = new SplitMenuColors(erd);
      }
      else
      {
        newRecord = new EscherAtom(erd);
      }

      children.add(newRecord);
      curpos += newRecord.getLength();
    }

    initialized = true;
  }

  byte[] getData()
  {
    byte[] data = new byte[0];
    for (Iterator i = children.iterator() ; i.hasNext() ; )
    {
      EscherRecord er = (EscherRecord) i.next();
      byte[] childData = er.getData();

      if (childData != null)
      {
        byte[] newData = new byte[data.length + childData.length];
        System.arraycopy(data, 0, newData, 0, data.length);
        System.arraycopy(childData, 0, newData, data.length, childData.length);
        data = newData;
      }
    }

    return setHeaderData(data);
  }
}
