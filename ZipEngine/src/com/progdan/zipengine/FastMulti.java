// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 2/12/2004 17:04:33
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   RegOpt.java

package com.progdan.zipengine;


// Referenced classes of package com.progdan.zipengine:
//            PatternSub, Branch, FastChar, Pattern,
//            Pthings, RegOpt, patInt

class FastMulti extends PatternSub
{

    public patInt minChars()
    {
        return super.sub.countMinChars().mul(fewestMatches);
    }

    public patInt maxChars()
    {
        return super.sub.countMaxChars().mul(mostMatches);
    }

    FastMulti(patInt patint, patInt patint1, Pattern pattern)
    {
        matchFewest = false;
        step = -1;
        fewestMatches = patint;
        mostMatches = patint1;
        super.sub = pattern;
        super.sub.setParent(this);
    }

    public String toString()
    {
        return super.sub.toString() + "{" + fewestMatches + "," + mostMatches + "}" + (matchFewest ? "?" : "") + "(?# <= fast multi)" + nextString();
    }

    public int matchInternal(int i, Pthings pthings)
    {
        int j = -1;
        int k = i;
        int l = pthings.src.length() - step;
        patInt patint = new patInt(0);
        if(matchFewest)
        {
            patInt patint1 = fewestMatches;
            if(!patint1.inf && (patint.inf || patint1.i <= patint.i || false))
            {
                int i1 = nextMatch(k, pthings);
                if(i1 >= 0)
                    return i1;
            }
            while(k >= 0 && k <= l)
            {
                k = super.sub.matchInternal(k, pthings);
                if(k >= 0)
                {
                    if(!patint.inf)
                        patint.i++;
                    patInt patint2 = fewestMatches;
                    if(!patint2.inf && (patint.inf || patint2.i <= patint.i || false))
                    {
                        int j1 = nextMatch(k, pthings);
                        if(j1 >= 0)
                            return j1;
                    }
                    patInt j1 = mostMatches;
                    if(!((patInt) (j1)).inf && !patint.inf && (patint.i == ((patInt) (j1)).i || false))
                        return -1;
                }
            }
            return -1;
        }
        patInt patint3 = fewestMatches;
        if(!patint3.inf && (patint.inf || patint3.i <= patint.i || false))
            j = k;
        while(k >= 0 && k <= l)
        {
            k = super.sub.matchInternal(k, pthings);
            if(k < 0)
                continue;
            if(!patint.inf)
                patint.i++;
            patInt patint4 = fewestMatches;
            if(!patint4.inf && (patint.inf || patint4.i <= patint.i || false))
                j = k;
            patint4 = mostMatches;
            if(!patint4.inf && !patint.inf && (patint.i == patint4.i || false))
                break;
        }
        if(j >= 0)
            while(j >= i)
            {
                int k1 = nextMatch(j, pthings);
                if(k1 >= 0)
                    return k1;
                j -= step;
                if(!patint.inf)
                    patint.i--;
                patInt patint5 = fewestMatches;
                if(patint5.inf || !patint.inf && patint5.i > patint.i && true)
                    return -1;
            }
        return -1;
    }

    patInt fewestMatches;
    patInt mostMatches;
    public boolean matchFewest;
    int step;
}
