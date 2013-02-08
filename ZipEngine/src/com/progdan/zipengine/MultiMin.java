// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 2/12/2004 17:06:35
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   MultiMin.java

package com.progdan.zipengine;


// Referenced classes of package com.progdan.zipengine:
//            Multi, RegSyntax, patInt, Pattern

class MultiMin extends Multi
{

    MultiMin(patInt patint, patInt patint1, Pattern pattern)
        throws RegSyntax
    {
        super(patint, patint1, pattern);
        super.matchFewest = true;
    }
}
