// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 2/12/2004 17:04:19
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   RegOpt.java

package com.progdan.zipengine;


// Referenced classes of package com.progdan.zipengine:
//            oneChar, Branch, FastMulti, Pattern,
//            Pthings, RegOpt

class FastChar extends oneChar
{

    FastChar(char c)
    {
        super(c);
    }

    public int matchInternal(int i, Pthings pthings)
    {
        if(i < pthings.src.length() && pthings.src.charAt(i) == super.c)
            return nextMatch(i + 1, pthings);
        else
            return -1;
    }
}
