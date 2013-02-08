// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 2/12/2004 17:07:27
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   Regex.java

package com.progdan.zipengine;


// Referenced classes of package com.progdan.zipengine:
//            Pattern, Regex, Pthings

class NoPattern extends Pattern
{

    public String toString()
    {
        return "[^\\d\\D](?#NO PATTERN!)";
    }

    public int matchInternal(int i, Pthings pthings)
    {
        return -1;
    }

    NoPattern()
    {
    }
}
