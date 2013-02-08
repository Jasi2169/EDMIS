// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 2/12/2004 17:12:49
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   Skipped.java

package com.progdan.zipengine;


// Referenced classes of package com.progdan.zipengine:
//            Pattern, patInt, Pthings

class Skipped extends Pattern
{

    Skipped(String s1)
    {
        s = s1;
    }

    public String toString()
    {
        return s + nextString();
    }

    public int matchInternal(int i, Pthings pthings)
    {
        return nextMatch(i + s.length(), pthings);
    }

    public patInt minChars()
    {
        return new patInt(s.length());
    }

    public patInt maxChars()
    {
        return new patInt(s.length());
    }

    String s;
}
