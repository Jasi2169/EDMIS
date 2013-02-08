// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 2/12/2004 17:10:57
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   RegexTokenizer.java

package com.progdan.zipengine;

import java.util.Enumeration;
import java.util.Vector;

// Referenced classes of package com.progdan.zipengine:
//            RegRes, RegSyntax, Regex

public class RegexTokenizer
    implements Enumeration
{

    void getMore()
    {
        String s = r.right();
        if(r.searchFrom(toParse, pos))
        {
            v.addElement(r.left().substring(pos));
            vi.addElement(new Integer(r.matchFrom() + r.charsMatched()));
            for(int i = 0; i < r.numSubs(); i++)
                if(r.substring() != null)
                {
                    v.addElement(r.substring(i + offset));
                    vi.addElement(new Integer(r.matchFrom(i + offset) + r.charsMatched(i + offset)));
                }

            pos = r.matchFrom() + r.charsMatched();
            return;
        }
        if(s != null)
            v.addElement(s);
    }

    public RegexTokenizer(String s, String s1)
    {
        v = new Vector();
        vi = new Vector();
        offset = 1;
        toParse = s;
        r = new Regex(s1);
        offset = Regex.BackRefOffset;
        getMore();
    }

    public RegexTokenizer(String s, Regex regex)
    {
        v = new Vector();
        vi = new Vector();
        offset = 1;
        toParse = s;
        r = regex;
        offset = Regex.BackRefOffset;
        getMore();
    }

    public Object nextElement()
    {
        if(count >= v.size())
            getMore();
        return v.elementAt(count++);
    }

    public String nextToken()
    {
        return (String)nextElement();
    }

    public String nextToken(String s)
    {
        try
        {
            r.compile(s);
        }
        catch(RegSyntax _ex) { }
        return nextToken(r);
    }

    public String nextToken(Regex regex)
    {
        r = regex;
        if(vi.size() > count)
        {
            pos = ((Integer)vi.elementAt(count)).intValue();
            v.setSize(count);
            vi.setSize(count);
        }
        getMore();
        return nextToken();
    }

    public boolean hasMoreElements()
    {
        if(count >= v.size())
            getMore();
        return count < v.size();
    }

    public boolean hasMoreTokens()
    {
        return hasMoreElements();
    }

    public int countTokens()
    {
        int i = count;
        for(; hasMoreTokens(); nextToken());
        count = i;
        return v.size() - count;
    }

    public String[] allTokens()
    {
        countTokens();
        String as[] = new String[v.size()];
        v.copyInto(as);
        return as;
    }

    String toParse;
    Regex r;
    int count;
    Vector v;
    Vector vi;
    int pos;
    int offset;
}
