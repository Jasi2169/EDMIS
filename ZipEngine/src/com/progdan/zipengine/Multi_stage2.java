// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 2/12/2004 17:06:46
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   Multi_stage2.java

package com.progdan.zipengine;


// Referenced classes of package com.progdan.zipengine:
//            PatternSub, BadMultiArgs, Pattern, Pthings,
//            RegSyntax, patInt

class Multi_stage2 extends PatternSub
{

    public String toString()
    {
        String s = "";
        s = s + super.sub.toString();
        s = s + "{" + matchMin + "," + matchMax + "}";
        if(matchFewest)
            s = s + "?";
        s = s + super.parent.nextString();
        return s;
    }

    Multi_stage2(patInt patint, patInt patint1, Pattern pattern)
        throws RegSyntax
    {
        matchFewest = false;
        pos_old = -1;
        if(pattern == null)
            throw new RegSyntax("Multiple match of Null pattern requested.");
        super.sub = pattern;
        nextRet = this;
        super.sub.setParent(this);
        matchMin = patint;
        matchMax = patint1;
        count = new patInt(0);
        if(patint.inf || !patint1.inf && patint.i > patint1.i)
            throw new BadMultiArgs();
        patInt patint2 = new patInt(-1);
        if(!patint.inf && (patint2.inf || patint.i <= patint2.i))
            throw new BadMultiArgs();
        else
            return;
    }

    public Pattern getNext()
    {
        return nextRet;
    }

    public int matchInternal(int i, Pthings pthings)
    {
        int j;
label0:
        {
            super.sub.setParent(this);
            j = -1;
            if(pos_old >= 0 && i == pos_old)
                return -1;
            pos_old = i;
            patInt patint = matchMin;
            patInt patint3 = count;
            if(!patint.inf && (patint3.inf || patint.i <= patint3.i || false))
                j = i;
            patint = count;
            patint3 = matchMax;
            if(patint.inf || !patint3.inf && patint.i > patint3.i && true || i > pthings.src.length())
                return -1;
            if(!matchFewest)
            {
                patInt patint1 = count;
                patInt patint4 = matchMax;
                if(patint4.inf || patint1.inf || patint1.i != patint4.i && true)
                    break label0;
            }
            if(j >= 0)
            {
                Pattern pattern = super.getNext();
                if(pattern == null)
                    return j;
                int l = testMatch(pattern, i, pthings);
                if(l >= 0)
                    return l;
                j = -1;
            }
        }
        patInt patint2 = count;
        if(!patint2.inf)
            patint2.i++;
        try
        {
            patInt patint5 = count;
            patInt patint7 = matchMax;
            if(!patint5.inf && (patint7.inf || patint5.i <= patint7.i || false))
            {
                int i1 = testMatch(super.sub, i, pthings);
                if(i1 >= 0)
                {
                    int k = i1;
                    return k;
                }
            }
        }
        finally
        {
            patInt patint6 = count;
            if(!patint6.inf)
                patint6.i--;
        }
        if(!matchFewest && j >= 0)
        {
            Pattern pattern1 = super.getNext();
            if(pattern1 == null)
                return j;
            else
                return testMatch(pattern1, i, pthings);
        } else
        {
            return j;
        }
    }

    Pattern nextRet;
    patInt count;
    patInt matchMin;
    patInt matchMax;
    public boolean matchFewest;
    int pos_old;
}
