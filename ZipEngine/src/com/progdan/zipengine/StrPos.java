// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 2/12/2004 17:13:17
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   StrPos.java

package com.progdan.zipengine;


// Referenced classes of package com.progdan.zipengine:
//            Pattern, patInf, patInt

public class StrPos
{

    public int pos()
    {
        return pos;
    }

    public char thisChar()
    {
        return c;
    }

    public boolean eos()
    {
        return eos;
    }

    public StrPos(StrPos strpos)
    {
        esc = '\\';
        dup(strpos);
    }

    public void dup(StrPos strpos)
    {
        s = strpos.s;
        pos = strpos.pos;
        c = strpos.c;
        dontMatch = strpos.dontMatch;
        eos = strpos.eos;
    }

    public StrPos(String s1, int i)
    {
        esc = '\\';
        s = s1;
        pos = i - 1;
        inc();
    }

    public StrPos inc()
    {
        pos++;
        if(pos >= s.length())
        {
            eos = true;
            return this;
        }
        eos = false;
        c = s.charAt(pos);
        if(c == esc && pos + 1 < s.length())
        {
            pos++;
            c = s.charAt(pos);
            if(c != esc)
                dontMatch = true;
            else
                dontMatch = false;
        } else
        {
            dontMatch = false;
        }
        return this;
    }

    public boolean match(char c1)
    {
        if(dontMatch || eos)
            return false;
        return c == c1;
    }

    public boolean escMatch(char c1)
    {
        if(!dontMatch || eos)
            return false;
        return c == c1;
    }

    public boolean escaped()
    {
        return dontMatch;
    }

    public boolean incMatch(String s1)
    {
        StrPos strpos = new StrPos(this);
        for(int i = 0; i < s1.length(); i++)
        {
            if(!strpos.match(s1.charAt(i)))
                return false;
            strpos.inc();
        }

        dup(strpos);
        return true;
    }

    public patInt getPatInt()
    {
        if(incMatch("inf"))
            return new patInf();
        int j = 0;
        StrPos strpos = new StrPos(this);
        int i;
        for(i = 0; !strpos.eos && strpos.c >= '0' && strpos.c <= '9'; i++)
        {
            j = (10 * j + strpos.c) - 48;
            strpos.inc();
        }

        if(i == 0)
        {
            return null;
        } else
        {
            dup(strpos);
            return new patInt(j);
        }
    }

    public String getString()
    {
        return s;
    }

    String s;
    int pos;
    public char esc;
    char c;
    boolean dontMatch;
    boolean eos;
}
