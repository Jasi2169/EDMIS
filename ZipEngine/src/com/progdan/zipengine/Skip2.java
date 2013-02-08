// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 2/12/2004 17:12:29
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   Skip2.java

package com.progdan.zipengine;


// Referenced classes of package com.progdan.zipengine:
//            Skip

public class Skip2 extends Skip
{

    public Skip2(String s, boolean flag, int i)
    {
        super(s, flag, i);
        c1 = s.charAt(1);
        super.m1 = s.length() == 2;
        if(flag)
        {
            String s1 = s.toUpperCase();
            String s2 = s.toLowerCase();
            mask1 = s1.charAt(1) ^ s2.charAt(1);
            c1 |= mask1;
            return;
        } else
        {
            mask1 = 0;
            return;
        }
    }

    public int find(String s, int i, int j)
    {
        i += super.offset;
        if(i > j)
            return -1;
        int k = s.length() - 2;
        k = k >= j ? j : k;
        for(int l = i; l <= k; l++)
            if(super.c == (s.charAt(l) | super.mask) && c1 == (s.charAt(l + 1) | mask1) && (super.m1 || s.regionMatches(super.ign, l, super.src, 0, super.src.length())))
                return l - super.offset;

        return -1;
    }

    int c1;
    int mask1;
}
