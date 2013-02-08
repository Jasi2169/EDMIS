// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 2/12/2004 17:07:16
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   NonDirFileRegex.java

package com.progdan.zipengine;

import java.io.File;

// Referenced classes of package com.progdan.zipengine:
//            FileRegex, Regex

public class NonDirFileRegex extends FileRegex
{

    public NonDirFileRegex()
    {
    }

    public NonDirFileRegex(String s)
    {
        super(s);
    }

    public boolean accept(File file, String s)
    {
        return matchAt(s, 0) && !(new File(file, s)).isDirectory();
    }

    static FileRegex newFileRegex(String s)
    {
        return new NonDirFileRegex(s);
    }
}
