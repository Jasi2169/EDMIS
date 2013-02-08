// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 2/12/2004 17:00:15
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   BackRefRule.java

package com.progdan.zipengine;


// Referenced classes of package com.progdan.zipengine:
//            ReplaceRule, RegRes

public class BackRefRule extends ReplaceRule
{

    public BackRefRule(int i)
    {
        n = i;
    }

    public void apply(StringBuffer stringbuffer, RegRes regres)
    {
        String s = regres.stringMatched(n);
        stringbuffer.append(s != null ? s : "");
    }

    public String toString1()
    {
        return "$" + n;
    }

    public Object clone1()
    {
        return new BackRefRule(n);
    }

    int n;
}
