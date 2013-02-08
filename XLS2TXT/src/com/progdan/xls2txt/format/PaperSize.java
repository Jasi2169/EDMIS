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

package com.progdan.xls2txt.format;

/**
 * Enumeration type which contains the available excel paper sizes and
 * their codes
 */
public final class PaperSize
{
  /**
   * The excel encoding
   */
  private int val;

  /**
   * The paper sizes
   */
  private static PaperSize[] paperSizes = new PaperSize[0];

  /**
   * Constructor
   */
  private PaperSize(int v)
  {
    val = v;

    // Grow the array and add this to it
    PaperSize[] newarray = new PaperSize[paperSizes.length + 1];
    System.arraycopy(paperSizes, 0, newarray, 0, paperSizes.length);
    newarray[paperSizes.length] = this;
    paperSizes = newarray;
  }

  private static class Dummy{};
  private final static Dummy unknown = new Dummy();

  /**
   * Constructor with a dummy parameter for unknown paper sizes
   */
  private PaperSize(int v, Dummy u)
  {
    val = v;
  }

  /**
   * Accessor for the internal binary value association with this paper
   * size
   *
   * @return the internal value
   */
  public int getValue()
  {
    return val;
  }

  /**
   * Gets the paper size for a specific value
   *
   * @param val the value
   * @return the paper size
   */
  public static PaperSize getPaperSize(int val)
  {
    boolean found = false;
    int pos = 0;

    while (!found && pos < paperSizes.length)
    {
      if (paperSizes[pos].getValue() == val)
      {
        found = true;
      }
      else
      {
        pos++;
      }
    }

    if (found)
    {
      return paperSizes[pos];
    }

    return new PaperSize(val, unknown);
  }

  /**
   * A4
   */
  public static PaperSize A4 = new PaperSize(0x9);

  /**
   * Small A4
   */
  public static PaperSize A4_SMALL = new PaperSize(0xa);

  /**
   * A5
   */
  public static PaperSize A5 = new PaperSize(0xb);

  /**
   * US Letter
   */
  public static PaperSize LETTER = new PaperSize(0x1);

  /**
   * US Legal
   */
  public static PaperSize LEGAL = new PaperSize(0x5);

  /**
   * A3
   */
  public static PaperSize A3 = new PaperSize(0x8);
}









