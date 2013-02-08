// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 2/12/2004 17:11:13
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   RegRes.java

package com.progdan.zipengine;


public class RegRes
    implements Cloneable
{

    public String getString()
    {
        return src;
    }

    public String toString()
    {
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append("match=" + matchedFrom() + ":" + charsMatched());
        if(!didMatch())
            return stringbuffer.toString();
        for(int i = 0; i < numSubs(); i++)
        {
            int j = i + 1;
            stringbuffer.append(" sub(" + j + ")=" + matchedFrom(j) + ":" + charsMatched(j));
        }

        return stringbuffer.toString();
    }

    public RegRes()
    {
        didMatch_ = false;
    }

    public RegRes(RegRes regres)
    {
        didMatch_ = false;
        if(regres.marks == null)
            marks = null;
        else
            try
            {
                marks = new int[regres.marks.length];
                for(int i = 0; i < marks.length; i++)
                    marks[i] = regres.marks[i];

            }
            catch(Throwable _ex) { }
        didMatch_ = regres.didMatch_;
        src = regres.src;
        charsMatched_ = regres.charsMatched_;
        matchFrom_ = regres.matchFrom_;
        numSubs_ = regres.numSubs_;
    }

    public Object clone()
    {
        return new RegRes(this);
    }

    public boolean equals(RegRes regres)
    {
        if(charsMatched_ != regres.charsMatched_ || matchFrom_ != regres.matchFrom_ || didMatch_ != regres.didMatch_ || numSubs_ != regres.numSubs_ || !src.equals(regres.src))
            return false;
        if(marks == null && regres.marks != null)
            return false;
        if(marks != null && regres.marks == null)
            return false;
        for(int i = 0; i < numSubs_; i++)
        {
            if(matchedFrom(i) != regres.matchedFrom(i))
                return false;
            if(charsMatched(i) != regres.charsMatched(i))
                return false;
        }

        return true;
    }

    public String stringMatched()
    {
        int i = matchedFrom();
        int j = charsMatched();
        if(!didMatch_ || i < 0 || j < 0)
            return null;
        else
            return src.substring(i, i + j);
    }

    public int matchedFrom(int i)
    {
        if(marks == null || i > numSubs_)
            return -1;
        else
            return marks[i];
    }

    public int charsMatched(int i)
    {
        if(marks == null || i > numSubs_ || !didMatch_)
            return -1;
        else
            return marks[i + numSubs_] - matchedFrom(i);
    }

    public int matchedTo(int i)
    {
        if(marks == null || i > numSubs_ || !didMatch_)
            return -1;
        else
            return marks[i + numSubs_];
    }

    public String stringMatched(int i)
    {
        int j = matchedFrom(i);
        int k = charsMatched(i);
        if(!didMatch_ || j < 0 || k < 0)
            return null;
        else
            return src.substring(j, j + k);
    }

    public String left()
    {
        int i = matchedFrom();
        if(!didMatch_ || i < 0)
            return null;
        else
            return src.substring(0, i);
    }

    public String left(int i)
    {
        int j = matchedFrom(i);
        if(!didMatch_ || j < 0)
            return null;
        else
            return src.substring(0, j);
    }

    public String right()
    {
        int i = matchedFrom();
        int j = charsMatched();
        if(!didMatch_ || i < 0 || j < 0)
            return null;
        else
            return src.substring(i + j, src.length());
    }

    public String right(int i)
    {
        int j = matchedFrom(i);
        int k = charsMatched(i);
        if(!didMatch_ || j < 0 || k < 0)
            return null;
        else
            return src.substring(j + k, src.length());
    }

    public int matchedFrom()
    {
        if(!didMatch_)
            return -1;
        else
            return matchFrom_;
    }

    public int charsMatched()
    {
        if(!didMatch_)
            return -1;
        else
            return charsMatched_;
    }

    public int matchedTo()
    {
        if(!didMatch_)
            return -1;
        else
            return matchFrom_ + charsMatched_;
    }

    public int numSubs()
    {
        return numSubs_;
    }

    public boolean didMatch()
    {
        return didMatch_;
    }

    public int matchFrom()
    {
        return matchedFrom();
    }

    public String substring()
    {
        return stringMatched();
    }

    public int matchFrom(int i)
    {
        return matchedFrom(i);
    }

    public String substring(int i)
    {
        return stringMatched(i);
    }

    protected int marks[];
    protected boolean didMatch_;
    protected String src;
    protected int charsMatched_;
    protected int matchFrom_;
    protected int numSubs_;
}
