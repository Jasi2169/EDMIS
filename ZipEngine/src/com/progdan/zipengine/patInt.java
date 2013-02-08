// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 2/12/2004 17:09:28
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   patInt.java

package com.progdan.zipengine;


// Referenced classes of package com.progdan.zipengine:
//            patInf

public class patInt
{

    public patInt()
    {
        i = 0;
        inf = false;
    }

    public patInt(int j)
    {
        i = j;
        inf = false;
    }

    public patInt(patInt patint)
    {
        i = patint.i;
        inf = patint.inf;
    }

    public void setInf(boolean flag)
    {
        inf = flag;
        if(flag)
            i = 0x7fffffff;
    }

    public final void inc()
    {
        if(!inf)
            i++;
    }

    public final void dec()
    {
        if(!inf)
            i--;
    }

    public final boolean lessEq(patInt patint)
    {
        return !inf && (patint.inf || i <= patint.i);
    }

    public final boolean equals(patInt patint)
    {
        return !patint.inf && !inf && i == patint.i;
    }

    public final String toString()
    {
        if(inf)
            return "";
        else
            return String.valueOf(i);
    }

    public final patInt pluseq(patInt patint)
    {
        if(inf || patint.inf)
            setInf(true);
        else
            i += patint.i;
        return this;
    }

    public final patInt mul(patInt patint)
    {
        if(inf || patint.inf)
            return new patInf();
        else
            return new patInt(i * patint.i);
    }

    public final patInt mineq(patInt patint)
    {
        if(patint.inf)
            return this;
        if(inf)
            i = patint.i;
        else
        if(patint.i < i)
            i = patint.i;
        setInf(false);
        return this;
    }

    public final patInt maxeq(patInt patint)
    {
        if(inf || patint.inf)
        {
            setInf(true);
            return this;
        }
        if(patint.i > i)
            i = patint.i;
        return this;
    }

    public boolean finite()
    {
        return !inf;
    }

    public int intValue()
    {
        return i;
    }

    int i;
    boolean inf;
}
