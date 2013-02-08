// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 2/12/2004 17:04:07
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   End.java

package com.progdan.zipengine;


// Referenced classes of package com.progdan.zipengine:
//            Pattern, AddToEnd, Pthings, patInt

class End extends Pattern
{

    End(boolean flag)
    {
        retIsEnd = flag;
    }

    public int matchInternal(int i, Pthings pthings)
    {
        if(retIsEnd && i < pthings.src.length() && pthings.src.charAt(i) == '\n')
            return nextMatch(i + 1, pthings);
        if(pthings.src.length() == i)
            return nextMatch(i, pthings);
        else
            return -1;
    }

    public String toString()
    {
        if(retIsEnd)
            return "$";
        else
            return "\\Z";
    }

    public patInt maxChars()
    {
        return new patInt(1);
    }

    boolean retIsEnd;
}
