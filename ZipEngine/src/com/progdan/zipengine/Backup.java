// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 2/12/2004 17:00:29
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   Backup.java

package com.progdan.zipengine;


// Referenced classes of package com.progdan.zipengine:
//            Pattern, patInt, Pthings

class Backup extends Pattern
{

    Backup(int i)
    {
        bk = i;
    }

    public String toString()
    {
        return "(?<" + bk + ")" + nextString();
    }

    public int matchInternal(int i, Pthings pthings)
    {
        if(i < bk)
            return -1;
        else
            return nextMatch(i - bk, pthings);
    }

    public patInt minChars()
    {
        return new patInt(-bk);
    }

    public patInt maxChars()
    {
        return new patInt(-bk);
    }

    int bk;
}
