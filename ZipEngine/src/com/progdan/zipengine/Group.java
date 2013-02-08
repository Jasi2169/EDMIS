// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 2/12/2004 17:05:01
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   Group.java

package com.progdan.zipengine;

import java.util.BitSet;

// Referenced classes of package com.progdan.zipengine:
//            Pattern, Pthings, patInt

class Group extends Pattern
{

    Group(char c, char c1)
    {
        op = c;
        cl = c1;
    }

    public int matchInternal(int i, Pthings pthings)
    {
        int k = 1;
        if(i < pthings.src.length() && (pthings.cbits != null ? !pthings.cbits.get(i) : true) && pthings.src.charAt(i) != op)
            return -1;
        for(int j = i + 1; j < pthings.src.length(); j++)
        {
            char c = pthings.src.charAt(j);
            boolean flag = pthings.cbits != null ? !pthings.cbits.get(j) : true;
            if(flag && c == '\\')
            {
                j++;
            } else
            {
                if(flag && c == cl)
                    k--;
                if(k == 0)
                    return nextMatch(j + 1, pthings);
                if(flag && c == op)
                    k++;
            }
        }

        return -1;
    }

    public String toString()
    {
        return "(?@" + op + cl + ")" + nextString();
    }

    public patInt minChars()
    {
        return new patInt(2);
    }

    char op;
    char cl;
}
