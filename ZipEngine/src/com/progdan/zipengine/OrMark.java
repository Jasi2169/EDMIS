// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 2/12/2004 17:09:10
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   OrMark.java

package com.progdan.zipengine;


// Referenced classes of package com.progdan.zipengine:
//            Or, Pattern, Pthings, SubMark

class OrMark extends Or
{

    OrMark(int i)
    {
        sm = new SubMark();
        sm.om = this;
        id = i;
    }

    String leftForm()
    {
        return "(";
    }

    public Pattern getNext()
    {
        return sm;
    }

    public int matchInternal(int i, Pthings pthings)
    {
        sm.next = super.getNext();
        if(pthings.marks == null)
        {
            int j = 2 * pthings.nMarks + 2;
            pthings.marks = new int[j];
            for(int l = 0; l < j; l++)
                pthings.marks[l] = -1;

        }
        pthings.marks[id] = i;
        int k = super.matchInternal(i, pthings);
        if(k < 0)
            pthings.marks[id] = -1;
        return k;
    }

    SubMark sm;
    int id;
}
