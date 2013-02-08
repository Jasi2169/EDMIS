// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 2/12/2004 17:09:49
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   Pattern.java

package com.progdan.zipengine;

import java.util.BitSet;

// Referenced classes of package com.progdan.zipengine:
//            Pthings, patInf, patInt

public abstract class Pattern
{

    public abstract int matchInternal(int i, Pthings pthings);

    public abstract String toString();

    public Pattern getNext()
    {
        if(next != null)
            return next;
        if(parent == null)
            return null;
        else
            return parent.getNext();
    }

    public void setParent(Pattern pattern)
    {
        if(next != null)
        {
            next.setParent(pattern);
            return;
        } else
        {
            parent = pattern;
            return;
        }
    }

    public int nextMatch(int i, Pthings pthings)
    {
        Pattern pattern = getNext();
        if(pattern == null)
            return i;
        else
            return pattern.matchInternal(i, pthings);
    }

    public String nextString()
    {
        if(next == null)
            return "";
        else
            return next.toString();
    }

    static final char caseAdjust(char c, boolean flag)
    {
        if(flag && c >= 'a' && c <= 'z')
            c += '\uFFE0';
        return c;
    }

    static final boolean inString(char c, String s)
    {
        for(int i = 0; i < s.length(); i++)
            if(s.charAt(i) == c)
                return true;

        return false;
    }

    static final String protect(String s, String s1, char c)
    {
        StringBuffer stringbuffer = new StringBuffer();
        String s2 = s1 + c;
        for(int i = 0; i < s.length(); i++)
        {
            char c1 = s.charAt(i);
            if(inString(c1, s2))
                stringbuffer.append(c);
            stringbuffer.append(c1);
        }

        return stringbuffer.toString();
    }

    public int match(String s, Pthings pthings)
    {
        return matchAt(s, 0, pthings);
    }

    public int matchAt(String s, int i, Pthings pthings)
    {
        pthings.src = s;
        int j = matchInternal(i, pthings) - i;
        if(j < -1)
            j = -1;
        return j;
    }

    final boolean Masked(int i, Pthings pthings)
    {
        if(pthings.cbits == null)
            return false;
        else
            return pthings.cbits.get(i);
    }

    public Pattern add(Pattern pattern)
    {
        if(next == null)
            next = pattern;
        else
            next.add(pattern);
        return this;
    }

    public patInt minChars()
    {
        return new patInt(0);
    }

    public patInt maxChars()
    {
        return new patInf();
    }

    public final patInt countMinChars()
    {
        Pattern pattern = this;
        patInt patint = new patInt(0);
        for(; pattern != null; pattern = pattern.next)
        {
            patInt patint1 = pattern.minChars();
            if(patint.inf || patint1.inf)
                patint.setInf(true);
            else
                patint.i += patint1.i;
        }

        return patint;
    }

    public final patInt countMaxChars()
    {
        Pattern pattern = this;
        patInt patint = new patInt(0);
        for(; pattern != null; pattern = pattern.next)
        {
            patInt patint1 = pattern.maxChars();
            if(patint.inf || patint1.inf)
                patint.setInf(true);
            else
                patint.i += patint1.i;
        }

        return patint;
    }

    final int testMatch(Pattern pattern, int i, Pthings pthings)
    {
        int ai[] = null;
        if(pthings.marks != null)
            try
            {
                ai = new int[pthings.marks.length];
                for(int j = 0; j < ai.length; j++)
                    ai[j] = pthings.marks[j];

            }
            catch(Throwable _ex) { }
        int k = pattern.matchInternal(i, pthings);
        if(k < 0)
            pthings.marks = ai;
        return k;
    }

    public Pattern()
    {
    }

    public static final char ESC = 92;
    static final String PROTECT_THESE = "[]{}(),$,-\"^.";
    Pattern next;
    Pattern parent;
}
