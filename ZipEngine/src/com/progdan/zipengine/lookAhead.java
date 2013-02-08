// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 2/12/2004 17:05:46
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   lookAhead.java

package com.progdan.zipengine;


// Referenced classes of package com.progdan.zipengine:
//            Or, Pattern, patInt, Pthings

class lookAhead extends Or
{

    lookAhead(boolean flag)
    {
        reverse = flag;
    }

    public Pattern getNext()
    {
        return null;
    }

    public int nextMatch(int i, Pthings pthings)
    {
        Pattern pattern = super.getNext();
        if(pattern != null)
            return pattern.matchInternal(i, pthings);
        else
            return i;
    }

    public int matchInternal(int i, Pthings pthings)
    {
        if(super.matchInternal(i, pthings) >= 0)
            if(reverse)
                return -1;
            else
                return nextMatch(i, pthings);
        if(reverse)
            return nextMatch(i, pthings);
        else
            return -1;
    }

    String leftForm()
    {
        if(reverse)
            return "(?!";
        else
            return "(?=";
    }

    public patInt minChars()
    {
        return new patInt(0);
    }

    public patInt maxChars()
    {
        return new patInt(0);
    }

    boolean reverse;
}
