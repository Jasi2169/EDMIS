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

package com.progdan.xls2txt.read.biff;

import java.util.ArrayList;
import java.util.Iterator;

import com.progdan.xls2txt.common.Logger;

import com.progdan.xls2txt.WorkbookSettings;
import com.progdan.xls2txt.biff.BaseCompoundFile;
import com.progdan.xls2txt.biff.IntegerHelper;

/**
 * Reads in and defrags an OLE compound compound file
 * (Made public only for the PropertySets demo)
 */
public final class CompoundFile extends BaseCompoundFile
{
  /**
   * The logger
   */
  private static Logger logger = Logger.getLogger(CompoundFile.class);

  /**
   * The original OLE stream, organized into blocks, which can
   * appear at any physical location in the file
   */
  private byte[] data;
  /**
   * The number of blocks it takes to store the big block depot
   */
  private int numBigBlockDepotBlocks;
  /**
   * The start block of the small block depot
   */
  private int sbdStartBlock;
  /**
   * The start block of the root entry
   */
  private int rootStartBlock;
  /**
   * The header extension block
   */
  private int extensionBlock;
  /**
   * The number of header extension blocks
   */
  private int numExtensionBlocks;
  /**
   * The root entry
   */
  private byte[] rootEntry;
  /**
   * The sequence of blocks which comprise the big block chain
   */
  private int[] bigBlockChain;
  /**
   * The sequence of blocks which comprise the small block chain
   */
  private int[] smallBlockChain;
  /**
   * The chain of blocks which comprise the big block depot
   */
  private int[] bigBlockDepotBlocks;
  /**
   * The list of property sets
   */
  private ArrayList propertySets;

  /**
   * The workbook settings
   */
  private WorkbookSettings settings;

  /**
   * Initializes the compound file
   *
   * @param d the raw data of the ole stream
   * @param ws the workbook settings
   * @exception BiffException
   */
  public CompoundFile(byte[] d, WorkbookSettings ws) throws BiffException
  {
    super();
    data = d;
    settings = ws;

    // First verify the OLE identifier
    for (int i = 0; i < IDENTIFIER.length; i++)
    {
      if (data[i] != IDENTIFIER[i])
      {
        throw new BiffException(BiffException.unrecognizedOLEFile);
      }
    }

    propertySets = new ArrayList();
    numBigBlockDepotBlocks = IntegerHelper.getInt
      (data[NUM_BIG_BLOCK_DEPOT_BLOCKS_POS],
       data[NUM_BIG_BLOCK_DEPOT_BLOCKS_POS + 1],
       data[NUM_BIG_BLOCK_DEPOT_BLOCKS_POS + 2],
       data[NUM_BIG_BLOCK_DEPOT_BLOCKS_POS + 3]);
    sbdStartBlock = IntegerHelper.getInt
      (data[SMALL_BLOCK_DEPOT_BLOCK_POS],
       data[SMALL_BLOCK_DEPOT_BLOCK_POS + 1],
       data[SMALL_BLOCK_DEPOT_BLOCK_POS + 2],
       data[SMALL_BLOCK_DEPOT_BLOCK_POS + 3]);
    rootStartBlock = IntegerHelper.getInt
      (data[ROOT_START_BLOCK_POS],
       data[ROOT_START_BLOCK_POS + 1],
       data[ROOT_START_BLOCK_POS + 2],
       data[ROOT_START_BLOCK_POS + 3]);
    extensionBlock = IntegerHelper.getInt
      (data[EXTENSION_BLOCK_POS],
       data[EXTENSION_BLOCK_POS + 1],
       data[EXTENSION_BLOCK_POS + 2],
       data[EXTENSION_BLOCK_POS + 3]);
    numExtensionBlocks = IntegerHelper.getInt
      (data[NUM_EXTENSION_BLOCK_POS],
       data[NUM_EXTENSION_BLOCK_POS + 1],
       data[NUM_EXTENSION_BLOCK_POS + 2],
       data[NUM_EXTENSION_BLOCK_POS + 3]);

    bigBlockDepotBlocks = new int[numBigBlockDepotBlocks];

    int pos = BIG_BLOCK_DEPOT_BLOCKS_POS;

    int bbdBlocks = numBigBlockDepotBlocks;

    if (numExtensionBlocks != 0)
    {
      bbdBlocks = (BIG_BLOCK_SIZE - BIG_BLOCK_DEPOT_BLOCKS_POS) / 4;
    }

    for (int i = 0; i < bbdBlocks; i++)
    {
      bigBlockDepotBlocks[i] = IntegerHelper.getInt
        (d[pos], d[pos + 1], d[pos + 2], d[pos + 3]);
      pos += 4;
    }

    for (int j = 0; j < numExtensionBlocks; j++)
    {
      pos = (extensionBlock + 1) * BIG_BLOCK_SIZE;
      int blocksToRead = Math.min(numBigBlockDepotBlocks - bbdBlocks,
                                  BIG_BLOCK_SIZE / 4 - 1);

      for (int i = bbdBlocks; i < bbdBlocks + blocksToRead; i++)
      {
        bigBlockDepotBlocks[i] = IntegerHelper.getInt
          (d[pos], d[pos + 1], d[pos + 2], d[pos + 3]);
        pos += 4;
      }

      bbdBlocks += blocksToRead;
      if (bbdBlocks < numBigBlockDepotBlocks)
      {
        extensionBlock = IntegerHelper.getInt
          (d[pos], d[pos + 1], d[pos + 2], d[pos + 3]);
      }
    }

    readBigBlockDepot();
    readSmallBlockDepot();

    rootEntry = readData(rootStartBlock);
    readPropertySets();
  }

  /**
   * Reads the big block depot entries
   */
  private void readBigBlockDepot()
  {
    int pos = 0;
    int index = 0;
    bigBlockChain = new int[numBigBlockDepotBlocks * BIG_BLOCK_SIZE / 4];

    for (int i = 0; i < numBigBlockDepotBlocks; i++)
    {
      pos = (bigBlockDepotBlocks[i] + 1) * BIG_BLOCK_SIZE;

      for (int j = 0; j < BIG_BLOCK_SIZE / 4; j++)
      {
        bigBlockChain[index] = IntegerHelper.getInt
          (data[pos], data[pos + 1], data[pos + 2], data[pos + 3]);
        pos += 4;
        index++;
      }
    }
  }

  /**
   * Reads the small block chain's depot entries
   */
  private void readSmallBlockDepot()
  {
    int pos = 0;
    int index = 0;
    int sbdBlock = sbdStartBlock;
    smallBlockChain = new int[0];

    while (sbdBlock != -2)
    {
      // Allocate some more space to the small block chain
      int[] oldChain = smallBlockChain;
      smallBlockChain = new int[smallBlockChain.length + BIG_BLOCK_SIZE / 4];
      System.arraycopy(oldChain, 0, smallBlockChain, 0, oldChain.length);

      pos = (sbdBlock + 1) * BIG_BLOCK_SIZE;

      for (int j = 0; j < BIG_BLOCK_SIZE / 4; j++)
      {
        smallBlockChain[index] = IntegerHelper.getInt
          (data[pos], data[pos + 1], data[pos + 2], data[pos + 3]);
        pos += 4;
        index++;
      }

      sbdBlock = bigBlockChain[sbdBlock];
    }
  }

  /**
   * Reads all the property sets
   */
  private void readPropertySets()
  {
    int offset = 0;
    byte[] d = null;

    while (offset < rootEntry.length)
    {
      d = new byte[PROPERTY_STORAGE_BLOCK_SIZE];
      System.arraycopy(rootEntry, offset, d, 0, d.length);
      PropertyStorage ps = new PropertyStorage(d);
      propertySets.add(ps);
      offset += PROPERTY_STORAGE_BLOCK_SIZE;
    }
  }

  /**
   * Gets the defragmented stream from this ole compound file
   *
   * @param streamName the stream name to get
   * @return the defragmented ole stream
   * @exception BiffException
   */
  public byte[] getStream(String streamName) throws BiffException
  {
    PropertyStorage ps = getPropertyStorage(streamName);

    if (ps.size >= SMALL_BLOCK_THRESHOLD ||
        streamName.equalsIgnoreCase(ROOT_ENTRY_NAME))
    {
      return getBigBlockStream(ps);
    }
    else
    {
      return getSmallBlockStream(ps);
    }
  }

  /**
   * Gets the property set with the specified name
   * @param name the property storage name
   * @return the property storage record
   * @exception BiffException
   */
  private PropertyStorage getPropertyStorage(String name)
    throws BiffException
  {
    // Find the workbook property
    Iterator i = propertySets.iterator();
    boolean found = false;
    PropertyStorage ps = null;
    while (!found && i.hasNext())
    {
      ps = (PropertyStorage) i.next();
      if (ps.name.equalsIgnoreCase(name))
      {
        found = true;
      }
    }

    if (!found)
    {
      throw new BiffException(BiffException.streamNotFound);
    }

    return ps;
  }

  /**
   * Build up the resultant stream using the big blocks
   *
   * @param ps the property storage
   * @return the big block stream
   */
  private byte[] getBigBlockStream(PropertyStorage ps)
  {
    int numBlocks = ps.size / BIG_BLOCK_SIZE;
    if (ps.size % BIG_BLOCK_SIZE != 0)
    {
      numBlocks++;
    }

    byte[] streamData = new byte[numBlocks * BIG_BLOCK_SIZE];

    int block = ps.startBlock;

    int count = 0;
    int pos = 0;
    while (block != -2 && count < numBlocks)
    {
      pos = (block + 1) * BIG_BLOCK_SIZE;
      System.arraycopy(data, pos, streamData,
                       count * BIG_BLOCK_SIZE, BIG_BLOCK_SIZE);
      count++;
      block = bigBlockChain[block];
    }

    if (block != -2 && count == numBlocks)
    {
      logger.warn("Property storage size inconsistent with block chain.");
    }

    return streamData;
  }

  /**
   * Build up the resultant stream using the small blocks
   * @param ps the property storage
   * @return  the data
   * @exception BiffException
   */
  private byte[] getSmallBlockStream(PropertyStorage ps)
    throws BiffException
  {
    PropertyStorage rootps = null;
    try
    {
      rootps = getPropertyStorage(ROOT_ENTRY_NAME);
    }
    catch (BiffException e)
    {
      rootps = (PropertyStorage) propertySets.get(0);
    }

    byte[] rootdata = readData(rootps.startBlock);
    byte[] sbdata = new byte[0];

    int block = ps.startBlock;
    int count = 0;
    int pos = 0;
    while (block != -2)
    {
      // grow the array
      byte[] olddata = sbdata;
      sbdata = new byte[olddata.length + SMALL_BLOCK_SIZE];
      System.arraycopy(olddata, 0, sbdata, 0, olddata.length);

      // Copy in the new data
      pos = block * SMALL_BLOCK_SIZE;
      System.arraycopy(rootdata, pos, sbdata,
                       olddata.length, SMALL_BLOCK_SIZE);
      block = smallBlockChain[block];
    }

    return sbdata;
  }

  /**
   * Reads the block chain from the specified block and returns the
   * data as a continuous stream of bytes
   *
   * @param bl the block number
   * @return the data
   */
  private byte[] readData(int bl)
  {
    int block = bl;
    int pos = 0;
    byte[] entry = new byte[0];

    while (block != -2)
    {
      // Grow the array
      byte[] oldEntry = entry;
      entry = new byte[oldEntry.length + BIG_BLOCK_SIZE];
      System.arraycopy(oldEntry, 0, entry, 0, oldEntry.length);
      pos = (block + 1) * BIG_BLOCK_SIZE;
      System.arraycopy(data, pos, entry,
                       oldEntry.length, BIG_BLOCK_SIZE);
      block = bigBlockChain[block];
    }
    return entry;
  }

  /**
   * Gets the property sets
   * @return the list of property sets
   */
  public String[] getPropertySetNames()
  {
    String[] sets = new String[propertySets.size()];
    for (int i = 0; i < sets.length; i++)
    {
      PropertyStorage ps = (PropertyStorage) propertySets.get(i);
      sets[i] = ps.name;
    }

    return sets;
  }

  /**
   * Gets the property set.  Invoked when copying worksheets with macros
   *
   * @param ps the property set name
   * @return the property set with the given name
   */
  public PropertyStorage getPropertySet(String ps)
  {
    boolean found = false;
    PropertyStorage propertySet = null;
    for (Iterator i = propertySets.iterator() ; i.hasNext() && !found ; )
    {
      propertySet = (PropertyStorage) i.next();
      if (propertySet.name.equalsIgnoreCase(ps))
      {
        found = true;
      }
    }

    return found ? propertySet : null;
  }
}












