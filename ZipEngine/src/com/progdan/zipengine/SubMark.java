// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 2/12/2004 17:13:27
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   SubMark.java

package com.progdan.zipengine;


// Referenced classes of package com.progdan.zipengine:
//            Pattern, OrMark, Pthings

class SubMark extends Pattern
{

    public String toString()
    {
        return "";
    }

    public int matchInternal(int i, Pthings pthings)
    {
        pthings.marks[om.id + pthings.nMarks] = i;
        int j = nextMatch(i, pthings);
        if(j < 0)
            pthings.marks[om.id + pthings.nMarks] = -1;
        return j;
    }

    SubMark()
    {
    }

    int end_pos;
    int start_pos;
    OrMark om;
}
