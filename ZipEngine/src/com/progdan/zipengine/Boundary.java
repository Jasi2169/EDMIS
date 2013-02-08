// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 2/12/2004 17:03:09
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   Boundary.java

package com.progdan.zipengine;


// Referenced classes of package com.progdan.zipengine:
//            Pattern, Pthings, patInt

class Boundary extends Pattern
{

    public String toString()
    {
        return "\\b" + nextString();
    }

    boolean isAChar(char c)
    {
        if(c >= 'a' && c <= 'z')
            return true;
        if(c >= 'A' && c <= 'Z')
            return true;
        if(c >= '0' && c <= '9')
            return true;
        return c == '_';
    }

    boolean matchLeft(int i, Pthings pthings)
    {
        if(i <= 0)
            return true;
        return !isAChar(pthings.src.charAt(i)) || !isAChar(pthings.src.charAt(i - 1));
    }

    boolean matchRight(int i, Pthings pthings)
    {
        if(i < 0)
            return false;
        if(i + 1 >= pthings.src.length())
            return true;
        return !isAChar(pthings.src.charAt(i)) || !isAChar(pthings.src.charAt(i + 1));
    }

    public int matchInternal(int i, Pthings pthings)
    {
        if(matchRight(i - 1, pthings) || matchLeft(i, pthings))
            return nextMatch(i, pthings);
        else
            return -1;
    }

    public patInt maxChars()
    {
        return new patInt(0);
    }

    Boundary()
    {
    }
}
