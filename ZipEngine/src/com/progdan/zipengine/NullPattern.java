// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 2/12/2004 17:07:37
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   NullPattern.java

package com.progdan.zipengine;


// Referenced classes of package com.progdan.zipengine:
//            Pattern, patInt, Pthings

class NullPattern extends Pattern
{

    public String toString()
    {
        return "";
    }

    public int matchInternal(int i, Pthings pthings)
    {
        return nextMatch(i, pthings);
    }

    public patInt maxChars()
    {
        return new patInt(0);
    }

    NullPattern()
    {
    }
}
