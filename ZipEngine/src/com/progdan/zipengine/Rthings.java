// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 2/12/2004 17:12:08
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   Rthings.java

package com.progdan.zipengine;


// Referenced classes of package com.progdan.zipengine:
//            Regex

public class Rthings
{

    Rthings(Regex regex)
    {
        val = Regex.BackRefOffset;
        optimizeMe = false;
        ignoreCase = regex.ignoreCase;
        dontMatchInQuotes = regex.dontMatchInQuotes;
    }

    void set(Regex regex)
    {
        regex.ignoreCase = ignoreCase;
        regex.dontMatchInQuotes = dontMatchInQuotes;
        if(optimizeMe)
            regex.optimize();
    }

    public int val;
    public boolean ignoreCase;
    public boolean dontMatchInQuotes;
    public boolean optimizeMe;
}
