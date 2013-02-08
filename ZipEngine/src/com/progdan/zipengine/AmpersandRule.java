// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 2/12/2004 16:59:40
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   AmpersandRule.java

package com.progdan.zipengine;


// Referenced classes of package com.progdan.zipengine:
//            ReplaceRule, RegRes

public final class AmpersandRule extends ReplaceRule
{

    public AmpersandRule()
    {
    }

    public void apply(StringBuffer stringbuffer, RegRes regres)
    {
        stringbuffer.append(regres.stringMatched());
    }

    public String toString1()
    {
        return "$&";
    }

    public Object clone1()
    {
        return new AmpersandRule();
    }
}
