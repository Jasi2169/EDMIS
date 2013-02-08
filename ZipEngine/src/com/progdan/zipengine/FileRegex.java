// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 2/12/2004 17:04:47
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   FileRegex.java

package com.progdan.zipengine;

import java.io.File;
import java.util.StringTokenizer;
import java.util.Vector;

// Referenced classes of package com.progdan.zipengine:
//            Regex, RegRes, RegSyntax, StrPos

public class FileRegex extends Regex
{

    public FileRegex()
    {
    }

    public FileRegex(String s)
    {
        super(s);
        if(File.separatorChar == '\\')
            super.ignoreCase = true;
    }

    public void compile(String s)
        throws RegSyntax
    {
        super.compile(toFileRegex(s));
    }

    public boolean accept(File file, String s)
    {
        return matchAt(s, 0);
    }

    public static String[] list(String s)
    {
        Vector vector = new Vector();
        list(s, vector);
        String as[] = new String[vector.size()];
        vector.copyInto(as);
        return as;
    }

    static synchronized void list(String s, Vector vector)
    {
        for(StringTokenizer stringtokenizer = new StringTokenizer(s, File.pathSeparator); stringtokenizer.hasMoreTokens();)
        {
            String s1 = stringtokenizer.nextToken();
            File file = new File(s1);
            String s2 = file.getParent();
            String s3 = file.getName();
            if(dir.matchAt(s1, 0))
            {
                s2 = dir.stringMatched();
                s3 = dir.right();
            } else
            {
                s2 = "." + File.separatorChar;
                s3 = s1;
            }
            File file1 = new File(s2);
            FileRegex fileregex = new FileRegex(s3);
            String as[] = file1.list(fileregex);
            if(as != null)
            {
                for(int i = 0; i < as.length; i++)
                    vector.addElement(s2 + as[i]);

            }
        }

    }

    static FileRegex newFileRegex(String s)
    {
        return new FileRegex(s);
    }

    public static String toFileRegex(String s)
    {
        StrPos strpos = new StrPos(s, 0);
        StringBuffer stringbuffer = new StringBuffer();
        if(strpos.incMatch("{?e="))
        {
            char c = strpos.thisChar();
            strpos.inc();
            if(strpos.incMatch("}"))
                stringbuffer.append("(?e=" + c + ")^");
            else
                stringbuffer.append("^(?e=");
            strpos.esc = c;
        }
        int i = 0;
        while(!strpos.eos())
            if(strpos.incMatch("?"))
                stringbuffer.append(".");
            else
            if(strpos.incMatch("."))
            {
                stringbuffer.append(strpos.esc);
                stringbuffer.append('.');
            } else
            if(strpos.incMatch("{"))
            {
                stringbuffer.append("(?:");
                i++;
            } else
            if(strpos.incMatch("}"))
            {
                stringbuffer.append(')');
                i--;
            } else
            if(i != 0 && strpos.incMatch(","))
                stringbuffer.append('|');
            else
            if(strpos.incMatch("*"))
                stringbuffer.append(".*");
            else
            if(strpos.incMatch("{?!"))
                stringbuffer.append("(?!");
            else
            if(strpos.incMatch("{?="))
            {
                stringbuffer.append("(?=");
            } else
            {
                if(strpos.escaped() || strpos.thisChar() == strpos.esc)
                    stringbuffer.append(strpos.esc);
                stringbuffer.append(strpos.thisChar());
                strpos.inc();
            }
        stringbuffer.append("$");
        return stringbuffer.toString();
    }

    static Regex dir;

    static
    {
        dir = null;
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append("(?e=#)(?:.*" + File.separatorChar);
        if(File.separatorChar == '\\')
            stringbuffer.append("|[a-z]:");
        stringbuffer.append(')');
        dir = new Regex(stringbuffer.toString());
        if(File.separatorChar == '\\')
            dir.ignoreCase = true;
    }
}
