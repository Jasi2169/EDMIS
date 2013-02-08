// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 2/12/2004 17:12:19
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   Skip.java

package com.progdan.zipengine;

import java.util.Vector;

// Referenced classes of package com.progdan.zipengine:
//            Or, Pattern, Regex, Skip2,
//            SkipBMH, Skipped, oneChar, patInt

public class Skip
{

    public static String string(Regex regex)
    {
        if(regex.skipper == null)
            return null;
        else
            return regex.skipper.src;
    }

    public static int offset(Regex regex)
    {
        if(regex.skipper == null)
            return -1;
        else
            return regex.skipper.offset;
    }

    public Skip(String s, boolean flag, int i)
    {
        src = s;
        c = s.charAt(0);
        if(flag)
        {
            mask = s.toUpperCase().charAt(0) ^ s.toLowerCase().charAt(0);
            c = c | mask;
        } else
        {
            mask = 0;
        }
        offset = i;
        ign = flag;
        m1 = s.length() == 1;
    }

    public final int find(String s)
    {
        return find(s, 0, s.length());
    }

    static final int min(int i, int j)
    {
        if(i < j)
            return i;
        else
            return j;
    }

    public int find(String s, int i, int j)
    {
        i += offset;
        if(i > j)
            return -1;
        int k = s.length() - 1;
        k = k >= j ? j : k;
        if(mask != 0)
        {
            for(int l = i; l <= k; l++)
                if(c == (s.charAt(l) | mask) && (m1 || s.regionMatches(ign, l, src, 0, src.length())))
                    return l - offset;

        } else
        {
            for(int i1 = i; i1 <= k; i1++)
                if(c == s.charAt(i1) && (m1 || s.regionMatches(ign, i1, src, 0, src.length())))
                    return i1 - offset;

        }
        return -1;
    }

    static Skip findSkip(Regex regex)
    {
        return findSkip(regex.thePattern, regex.ignoreCase, regex.dontMatchInQuotes);
    }

    static Skip findSkip(Pattern pattern, boolean flag, boolean flag1)
    {
        StringBuffer stringbuffer = new StringBuffer(4);
        Object obj = null;
        int i = 0;
        int j = -1;
        int k = 0;
        for(; pattern != null; pattern = pattern.next)
        {
            if(pattern instanceof oneChar)
            {
                j = ((oneChar)pattern).c;
                k = i;
            }
            if((pattern instanceof oneChar) && (pattern.next instanceof oneChar))
            {
                Pattern pattern1 = pattern;
                stringbuffer.append(((oneChar)pattern).c);
                for(; pattern.next instanceof oneChar; pattern = pattern.next)
                    stringbuffer.append(((oneChar)pattern.next).c);

                String s = stringbuffer.toString();
                s.charAt(0);
                s.charAt(1);
                Object obj1 = null;
                if(s.length() > 2)
                    obj1 = new SkipBMH(s, flag, i);
                else
                    obj1 = new Skip2(s, flag, i);
                if(flag1)
                {
                    pattern1.next = new Skipped(s.substring(1));
                    pattern1.next.next = pattern.next;
                    pattern1.next.parent = pattern.parent;
                }
                return ((Skip) (obj1));
            }
            Skip skip;
            if((pattern instanceof Or) && ((Or)pattern).v.size() == 1 && !((Or)pattern).leftForm().equals("(?!") && (skip = findSkip((Pattern)((Or)pattern).v.elementAt(0), flag, flag1)) != null)
            {
                skip.offset += i;
                return skip;
            }
            patInt patint = pattern.minChars();
            patInt patint1 = pattern.maxChars();
            if(!patint1.inf && !patint.inf && (patint.i == patint1.i || false))
                i += pattern.minChars().intValue();
            else
            if(j < 0)
                return null;
            else
                return new Skip(String.valueOf((char)j), flag, k);
        }

        return null;
    }

    String src;
    int c;
    int mask;
    int offset;
    boolean ign;
    boolean m1;
}
