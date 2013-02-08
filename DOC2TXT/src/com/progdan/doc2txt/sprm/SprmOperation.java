/*  Copyright 2004 Ryan Ackley
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.progdan.doc2txt.sprm;

import org.apache.poi.util.BitField;
import org.apache.poi.util.LittleEndian;

public class SprmOperation
{
  final static private BitField OP_BITFIELD = new BitField(0x1ff);
  final static private BitField SPECIAL_BITFIELD = new BitField(0x200);
  final static private BitField TYPE_BITFIELD = new BitField(0x1c00);
  final static private BitField SIZECODE_BITFIELD = new BitField(0xe000);

  private int _type;
  //private boolean _variableLen;
  private int _operation;
  private int _operand;
  private byte[] _varOperand;
  private int _sizeNeeded;

  public SprmOperation(byte[] grpprl, int offset)
  {
    short sprmStart = LittleEndian.getShort(grpprl, offset);
    offset += 2;

    _operation = OP_BITFIELD.getValue(sprmStart);
    _type = TYPE_BITFIELD.getValue(sprmStart);
    int sizeCode = SIZECODE_BITFIELD.getValue(sprmStart);

    switch (sizeCode)
    {
      case 0:
      case 1:
        _operand = LittleEndian.getUnsignedByte(grpprl, offset);
        _sizeNeeded = 3;
        break;
      case 2:
      case 4:
      case 5:
        _operand = LittleEndian.getShort(grpprl, offset);
        _sizeNeeded = 4;
        break;
      case 3:
        _operand = LittleEndian.getInt(grpprl, offset);
        _sizeNeeded = 6;
        break;
      case 6:
        _varOperand = new byte[grpprl[offset++]];
        System.arraycopy(grpprl, offset, _varOperand, 0, _varOperand.length);
        _sizeNeeded = _varOperand.length + 3;
        break;
      case 7:
        byte threeByteInt[] = new byte[4];
        threeByteInt[0] = grpprl[offset];
        threeByteInt[1] = grpprl[offset + 1];
        threeByteInt[2] = grpprl[offset + 2];
        threeByteInt[3] = (byte)0;
        _operand = LittleEndian.getInt(threeByteInt, 0);
        _sizeNeeded = 5;
        break;

    }
  }

  public int getType()
  {
    return _type;
  }

  public int getOperation()
  {
    return _operation;
  }

  public int getOperand()
  {
    return _operand;
  }

  public int size()
  {
    return _sizeNeeded;
  }
}
