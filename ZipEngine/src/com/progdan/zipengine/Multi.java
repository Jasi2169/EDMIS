// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 2/12/2004 17:06:12
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   Multi.java

package com.progdan.zipengine;


// Referenced classes of package com.progdan.zipengine:
//            PatternSub, Multi_stage2, Pattern, RegSyntax,
//            patInt, Pthings

class Multi extends PatternSub
{

    public patInt minChars()
    {
        return a.mul(p.countMinChars());
    }

    public patInt maxChars()
    {
        return b.mul(p.countMaxChars());
    }

    public Multi(patInt patint, patInt patint1, Pattern pattern)
        throws RegSyntax
    {
        matchFewest = false;
        a = patint;
        b = patint1;
        p = pattern;
        st2 = new Multi_stage2(patint, patint1, pattern);
        st2.parent = this;
        super.sub = ((PatternSub) (st2)).sub;
    }

    public String toString()
    {
        st2.matchFewest = matchFewest;
        return st2.toString();
    }

    public int matchInternal(int i, Pthings pthings)
    {
        try
        {
            st2 = new Multi_stage2(a, b, p);
        }
        catch(RegSyntax _ex) { }
        st2.matchFewest = matchFewest;
        st2.parent = this;
        return st2.matchInternal(i, pthings);
    }

    patInt a;
    patInt b;
    Pattern p;
    Multi_stage2 st2;
    public boolean matchFewest;
}
