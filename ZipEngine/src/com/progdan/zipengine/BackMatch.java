// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 2/12/2004 17:00:04
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   BackMatch.java

package com.progdan.zipengine;


// Referenced classes of package com.progdan.zipengine:
//            Pattern, Pthings

class BackMatch extends Pattern
{

    BackMatch(int i)
    {
        id = i;
    }

    public String toString()
    {
        return "\\" + id;
    }

    public int matchInternal(int i, Pthings pthings)
    {
        int j = pthings.marks[id];
        int k = pthings.marks[id + pthings.nMarks];
        int l = k - j;
        if(j < 0 || l < 0 || i + l > pthings.src.length())
            return -1;
        int i1 = pthings.src.length() - i;
        if(l < i1)
            i1 = l;
        for(int j1 = 0; j1 < i1; j1++)
            if(pthings.src.charAt(j1 + j) != pthings.src.charAt(i + j1))
                return -1;

        return nextMatch(i + l, pthings);
    }

    int id;
}
