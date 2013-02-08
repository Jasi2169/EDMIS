// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 2/12/2004 17:13:09
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   StringRule.java

package com.progdan.zipengine;


// Referenced classes of package com.progdan.zipengine:
//            ReplaceRule, RegRes

public class StringRule extends ReplaceRule
{

    public StringRule(String s1)
    {
        s = s1;
    }

    public void apply(StringBuffer stringbuffer, RegRes regres)
    {
        stringbuffer.append(s);
    }

    public String toString1()
    {
        return s;
    }

    public Object clone1()
    {
        return new StringRule(s);
    }

    String s;
}
