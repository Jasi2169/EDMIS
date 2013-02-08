// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 2/12/2004 16:59:53
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   Any.java

package com.progdan.zipengine;


// Referenced classes of package com.progdan.zipengine:
//            Pattern, Pthings, patInt

class Any extends Pattern
{

    public int matchInternal(int i, Pthings pthings)
    {
        if(i < pthings.src.length() && pthings.dotDoesntMatchCR && pthings.src.charAt(i) != '\n')
            return nextMatch(i + 1, pthings);
        else
            return -1;
    }

    public String toString()
    {
        return "." + nextString();
    }

    public patInt minChars()
    {
        return new patInt(1);
    }

    public patInt maxChars()
    {
        return new patInt(1);
    }

    Any()
    {
    }
}
