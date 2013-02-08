// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 2/12/2004 17:13:00
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   Start.java

package com.progdan.zipengine;


// Referenced classes of package com.progdan.zipengine:
//            Pattern, Pthings, patInt

class Start extends Pattern
{

    Start(boolean flag)
    {
        retIsStart = flag;
    }

    public int matchInternal(int i, Pthings pthings)
    {
        if(retIsStart && i > 0 && pthings.src.charAt(i - 1) == '\n')
            return nextMatch(i, pthings);
        if(i == 0)
            return nextMatch(i, pthings);
        else
            return -1;
    }

    public String toString()
    {
        if(retIsStart)
            return "^" + nextString();
        else
            return "\\A" + nextString();
    }

    public patInt maxChars()
    {
        return new patInt(0);
    }

    boolean retIsStart;
}
