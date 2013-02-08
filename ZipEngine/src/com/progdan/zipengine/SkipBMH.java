// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 2/12/2004 17:12:42
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   SkipBMH.java

package com.progdan.zipengine;


// Referenced classes of package com.progdan.zipengine:
//            Skip

public class SkipBMH extends Skip
{

    public SkipBMH(String s, boolean flag, int i)
    {
        super(s, flag, i);
        String s1 = super.src.toUpperCase();
        String s2 = super.src.toLowerCase();
        for(int j = 0; j < 64; j++)
            skip[j] = (char)super.src.length();

        sm1 = super.src.length() - 1;
        if(flag)
        {
            ucmask = s1.charAt(sm1) ^ s2.charAt(sm1);
            ucmask = ~ucmask;
        } else
        {
            ucmask = -1;
        }
        super.c = super.src.charAt(sm1) & ucmask;
        mask = ucmask & 0x3f;
        for(int k = 0; k < super.src.length() - 1; k++)
            skip[super.src.charAt(k) & mask] = (char)(super.src.length() - k - 1);

        for(int l = 0; l < super.src.length() - 1; l++)
            if(super.c == (ucmask & super.src.charAt(sm1 - l - 1)))
            {
                jump_ahead = l;
                return;
            }

    }

    public int find(String s, int i, int j)
    {
        i += super.offset + sm1;
        int k = s.length() - 1;
        int l = j + sm1;
        k = k >= l ? l : k;
        for(int i1 = i; i1 <= k; i1 += skip[s.charAt(i1) & mask])
            if((s.charAt(i1) & ucmask) == super.c)
            {
                if(super.src.regionMatches(super.ign, 0, s, i1 - sm1, super.src.length()))
                    return i1 - sm1 - super.offset;
                i1 += jump_ahead;
                if(i1 > k)
                    return -1;
            }

        return -1;
    }

    final int MAX_CHAR = 64;
    final char skip[] = new char[64];
    int sm1;
    int mask;
    int ucmask;
    int jump_ahead;
}
