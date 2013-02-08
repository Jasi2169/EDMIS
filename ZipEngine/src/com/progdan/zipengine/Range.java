// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 2/12/2004 17:10:28
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   Range.java

package com.progdan.zipengine;

import java.util.BitSet;

// Referenced classes of package com.progdan.zipengine:
//            Pattern, BadRangeArgs, Pthings, RegSyntax,
//            oneChar, patInt

class Range extends Pattern
{

    public String toString()
    {
        String s = Pattern.protect(String.valueOf(lo), "[]{}(),$,-\"^.", '\\') + "-" + Pattern.protect(String.valueOf(hi), "[]{}(),$,-\"^.", '\\');
        if(!printBrackets)
            return s;
        else
            return "[" + s + "]";
    }

    Range(char c, char c1)
        throws RegSyntax
    {
        printBrackets = false;
        lo = c;
        hi = c1;
        Object obj = null;
        if(lo >= hi)
        {
            throw new BadRangeArgs();
        } else
        {
            oneChar onechar = new oneChar(lo);
            altlo = onechar.altc;
            onechar = new oneChar(hi);
            althi = onechar.altc;
            return;
        }
    }

    public int matchInternal(int i, Pthings pthings)
    {
        if(i >= pthings.src.length())
            return -1;
        if(pthings.cbits != null ? pthings.cbits.get(i) : false)
            return -1;
        char c = pthings.src.charAt(i);
        if(lo <= c && c <= hi || pthings.ignoreCase && altlo <= c && c <= althi)
            return nextMatch(i + 1, pthings);
        else
            return -1;
    }

    public patInt minChars()
    {
        return new patInt(1);
    }

    public patInt maxChars()
    {
        return new patInt(1);
    }

    char lo;
    char hi;
    char altlo;
    char althi;
    boolean printBrackets;
}
