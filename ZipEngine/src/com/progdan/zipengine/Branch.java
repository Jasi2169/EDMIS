// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 2/12/2004 17:03:38
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   RegOpt.java

package com.progdan.zipengine;

import java.util.*;

// Referenced classes of package com.progdan.zipengine:
//            Pattern, FastChar, FastMulti, NullPattern,
//            Or, Pthings, RegOpt, oneChar,
//            patInt

class Branch extends Pattern
{

    Branch()
    {
        h = new Hashtable();
    }

    final Pattern reduce(boolean flag, boolean flag1)
    {
        if(h.size() == 1)
        {
            Enumeration enumeration = h.keys();
            Character character = (Character)enumeration.nextElement();
            Pattern obj;
            if(flag || flag1)
                obj = new oneChar(character.charValue());
            else
                obj = new FastChar(character.charValue());
            obj.next = (Pattern)h.get(character);
            ((Pattern) (obj)).add(super.next);
            return ((Pattern) (obj));
        }
        if(h.size() == 0)
            return null;
        else
            return this;
    }

    public patInt maxChars()
    {
        Enumeration enumeration = h.keys();
        patInt patint = new patInt(0);
        patInt patint1;
        for(; enumeration.hasMoreElements(); patint.maxeq(patint1))
        {
            Object obj = enumeration.nextElement();
            Pattern pattern = (Pattern)h.get(obj);
            patint1 = pattern.maxChars();
            if(!patint1.inf)
                patint1.i++;
        }

        return patint;
    }

    public patInt minChars()
    {
        Enumeration enumeration = h.keys();
        patInt patint = new patInt(0);
        patInt patint1;
        for(; enumeration.hasMoreElements(); patint.maxeq(patint1))
        {
            Object obj = enumeration.nextElement();
            Pattern pattern = (Pattern)h.get(obj);
            patint1 = pattern.minChars();
            if(!patint1.inf)
                patint1.i++;
        }

        return patint;
    }

    void addc(FastChar fastchar, boolean flag, boolean flag1)
    {
        Object obj = ((Pattern) (fastchar)).next;
        if(obj == null)
            obj = new NullPattern();
        else
            obj = RegOpt.opt(((Pattern) (obj)), flag, flag1);
        set(new Character(((oneChar) (fastchar)).c), ((Pattern) (obj)));
    }

    void addc(oneChar onechar, boolean flag, boolean flag1)
    {
        Object obj = ((Pattern) (onechar)).next;
        if(obj == null)
            obj = new NullPattern();
        else
            obj = RegOpt.opt(((Pattern) (obj)), flag, flag1);
        set(new Character(onechar.c), ((Pattern) (obj)));
        if(flag)
            set(new Character(onechar.altc), ((Pattern) (obj)));
    }

    void set(Character character, Pattern pattern)
    {
        Pattern pattern1 = (Pattern)h.get(character);
        super.next = null;
        if(pattern1 == null)
        {
            h.put(character, pattern);
            return;
        }
        if(pattern1 instanceof Or)
        {
            ((Or)pattern1).addOr(pattern);
            return;
        } else
        {
            Or or = new Or();
            or.addOr(pattern1);
            or.addOr(pattern);
            or.parent = this;
            h.put(character, or);
            return;
        }
    }

    public String toString()
    {
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append("(?:(?#uses Hashtable)");
        for(Enumeration enumeration = h.keys(); enumeration.hasMoreElements();)
        {
            Character character = (Character)enumeration.nextElement();
            stringbuffer.append(character);
            stringbuffer.append(h.get(character));
            if(enumeration.hasMoreElements())
                stringbuffer.append("|");
        }

        stringbuffer.append(")");
        stringbuffer.append(nextString());
        return stringbuffer.toString();
    }

    public int matchInternal(int i, Pthings pthings)
    {
        if(i >= pthings.src.length())
            return -1;
        Pattern pattern = (Pattern)h.get(new Character(pthings.src.charAt(i)));
        if(pattern == null)
            return -1;
        if(pthings.cbits != null && pthings.cbits.get(i))
            return -1;
        else
            return pattern.matchInternal(i + 1, pthings);
    }

    Hashtable h;
}
