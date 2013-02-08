// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 2/12/2004 17:08:32
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   oneChar.java

package com.progdan.zipengine;

import java.util.BitSet;

// Referenced classes of package com.progdan.zipengine:
//            Pattern, Pthings, patInt

class oneChar extends Pattern
{

    public oneChar(char c1)
    {
        c = c1;
        char c2 = String.valueOf(c).toUpperCase().charAt(0);
        char c3 = String.valueOf(c).toLowerCase().charAt(0);
        altc = c != c2 ? c2 : c3;
        mask = c & altc;
    }

    public int matchInternal(int i, Pthings pthings)
    {
        int j = -1;
        char c1;
        if(i < pthings.src.length() && (pthings.cbits != null ? !pthings.cbits.get(i) : true) && ((c1 = pthings.src.charAt(i)) == c || pthings.ignoreCase && c1 == altc))
            j = nextMatch(i + 1, pthings);
        return j;
    }

    public String toString()
    {
        return Pattern.protect(String.valueOf(c), "[]{}(),$,-\"^.", '\\') + nextString();
    }

    public patInt minChars()
    {
        return new patInt(1);
    }

    public patInt maxChars()
    {
        return new patInt(1);
    }

    char c;
    char altc;
    int mask;
}
