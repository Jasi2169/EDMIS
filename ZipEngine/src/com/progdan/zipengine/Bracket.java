// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 2/12/2004 17:03:21
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   Bracket.java

package com.progdan.zipengine;

import java.util.Vector;

// Referenced classes of package com.progdan.zipengine:
//            Or, Pattern, Pthings, patInt

class Bracket extends Or
{

    Bracket(boolean flag)
    {
        neg = flag;
    }

    String leftForm()
    {
        if(neg)
            return "[^";
        else
            return "[";
    }

    String rightForm()
    {
        return "]";
    }

    String sepForm()
    {
        return "";
    }

    public int matchInternal(int i, Pthings pthings)
    {
        if(i >= pthings.src.length())
            return -1;
        int j = super.matchInternal(i, pthings);
        if(neg && j < 0 || !neg && j >= 0)
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

    public Or addOr(Pattern pattern)
    {
        super.v.addElement(pattern);
        pattern.setParent(null);
        return this;
    }

    boolean neg;
}
