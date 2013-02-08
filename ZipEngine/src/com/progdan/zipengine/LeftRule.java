// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 2/12/2004 17:05:33
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   LeftRule.java

package com.progdan.zipengine;


// Referenced classes of package com.progdan.zipengine:
//            ReplaceRule, RegRes

public class LeftRule extends ReplaceRule
{

    public LeftRule()
    {
    }

    public void apply(StringBuffer stringbuffer, RegRes regres)
    {
        stringbuffer.append(regres.left());
    }

    public String toString1()
    {
        return "$`";
    }

    public Object clone1()
    {
        return new LeftRule();
    }
}
