// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 2/12/2004 17:09:02
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   Or.java

package com.progdan.zipengine;

import java.util.Vector;

// Referenced classes of package com.progdan.zipengine:
//            Pattern, patInt, Pthings

class Or extends Pattern
{

    Or()
    {
        v = new Vector();
    }

    String leftForm()
    {
        return "(?:";
    }

    String rightForm()
    {
        return ")";
    }

    String sepForm()
    {
        return "|";
    }

    public Or addOr(Pattern pattern)
    {
        v.addElement(pattern);
        pattern.setParent(this);
        return this;
    }

    public String toString()
    {
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append(leftForm());
        if(v.size() > 0)
            stringbuffer.append(((Pattern)v.elementAt(0)).toString());
        for(int i = 1; i < v.size(); i++)
        {
            stringbuffer.append(sepForm());
            stringbuffer.append(((Pattern)v.elementAt(i)).toString());
        }

        stringbuffer.append(rightForm());
        stringbuffer.append(nextString());
        return stringbuffer.toString();
    }

    public int matchInternal(int i, Pthings pthings)
    {
        for(int j = 0; j < v.size(); j++)
        {
            Pattern pattern = (Pattern)v.elementAt(j);
            int k = pattern.matchInternal(i, pthings);
            if(k >= 0)
                return k;
        }

        return -1;
    }

    public patInt minChars()
    {
        if(v.size() == 0)
            return new patInt(0);
        patInt patint = ((Pattern)v.elementAt(0)).countMinChars();
        for(int i = 1; i < v.size(); i++)
        {
            Pattern pattern = (Pattern)v.elementAt(i);
            patint.mineq(pattern.countMinChars());
        }

        return patint;
    }

    public patInt maxChars()
    {
        if(v.size() == 0)
            return new patInt(0);
        patInt patint = ((Pattern)v.elementAt(0)).countMaxChars();
        for(int i = 1; i < v.size(); i++)
        {
            Pattern pattern = (Pattern)v.elementAt(i);
            patint.maxeq(pattern.countMaxChars());
        }

        return patint;
    }

    Vector v;
}
