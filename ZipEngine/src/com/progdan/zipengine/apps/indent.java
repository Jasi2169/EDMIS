// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 2/12/2004 17:15:38
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   indent.java

package com.progdan.zipengine.apps;

import com.progdan.zipengine.*;
import java.io.*;

public class indent
{

    public static void main(String args[])
    {
        for(int i = 0; i < args.length; i++)
        {
            String args1[] = FileRegex.list(args[i]);
            for(int j = 0; j < args1.length; j++)
                dofile(args1[j]);

        }

    }

    static void do_substring(String s, PrintWriter printwriter)
    {
        if(!special.search(s))
        {
            printwriter.print(s);
            return;
        }
        String s1 = special.left();
        String s2 = special.stringMatched();
        String s3 = special.right();
        char c = '\0';
        char c1 = '\0';
        if(s2.length() >= 1)
            c = s2.charAt(0);
        if(s2.length() >= 2)
            c1 = s2.charAt(1);
        if(c == '/' && c1 == '/')
        {
            printwriter.print(s);
            return;
        }
        if(s2.equals("/*"))
        {
            in_comment = true;
            printwriter.print(s);
            return;
        }
        if(c == '"' || c == '\'')
        {
            printwriter.print(s1 + s2);
            do_substring(s3, printwriter);
            return;
        }
        if(c == ';')
        {
            line_ended = true;
            printwriter.print(s1 + s2);
            do_substring(s3, printwriter);
            return;
        }
        if(c == '{')
        {
            spaces++;
            printwriter.print(s1 + s2);
            do_substring(s3, printwriter);
            if(all_white.matchAt(s3, 0))
            {
                line_ended = true;
                return;
            }
        } else
        if(c == '}')
        {
            spaces--;
            printwriter.print(s1 + s2);
            do_substring(s3, printwriter);
            if(all_white.matchAt(s3, 0))
            {
                line_ended = true;
                return;
            }
        } else
        {
            printwriter.print(s1 + s2);
            if(all_white.matchAt(s3, 0))
            {
                line_ended = true;
                return;
            }
            do_substring(s3, printwriter);
        }
    }

    static void doline(String s, PrintWriter printwriter)
    {
        if(all_white.matchAt(s, 0))
        {
            printwriter.println("");
            return;
        }
        if(in_comment)
        {
            if(end_comment.search(s))
            {
                printwriter.print(end_comment.left() + "*/");
                s = end_comment.right();
                in_comment = false;
                if(all_white.matchAt(s, 0))
                    line_ended = true;
                else
                    do_substring(s, printwriter);
            } else
            {
                printwriter.print(s);
            }
        } else
        {
            white.matchAt(s, 0);
            s = white.right();
            boolean flag = false;
            String s1 = "";
            for(; pre_space.matchAt(s, 0); s = pre_space.right())
            {
                spaces--;
                flag = true;
                s1 = s1 + pre_space.left() + pre_space.stringMatched();
            }

            byte byte0 = 0;
            if(s.startsWith("case"))
                byte0 = -1;
            for(int i = 0; i < spaces + byte0; i++)
                printwriter.print("    ");

            if(!line_ended)
                printwriter.print("    ");
            if(flag)
                printwriter.print(s1);
            else
            if(byte0 >= 0 && !s.startsWith("//"))
                line_ended = false;
            do_substring(s, printwriter);
        }
        printwriter.println("");
    }

    static void dofile(String s)
    {
        System.out.println("Processing File: " + s);
        try
        {
            File file = new File(s);
            File file1 = new File(s + ".bak");
            file1.delete();
            file.renameTo(file1);
            FileReader filereader = new FileReader(file1);
            BufferedReader bufferedreader = new BufferedReader(filereader);
            FileWriter filewriter = new FileWriter(file);
            BufferedWriter bufferedwriter = new BufferedWriter(filewriter);
            PrintWriter printwriter = new PrintWriter(bufferedwriter);
            for(String s1 = bufferedreader.readLine(); s1 != null; s1 = bufferedreader.readLine())
                doline(s1, printwriter);

            printwriter.close();
            bufferedreader.close();
            return;
        }
        catch(Throwable throwable)
        {
            throwable.printStackTrace();
        }
    }

    public indent()
    {
    }

    static Regex special = new Regex("(?://.*|/\\*.*?\\*/|/\\*|\".*?[^\"^]\"|'.*?[^'^]'|[{}]|;(?=\\s*//|\\s*$))");
    static Regex white = new Regex("\\s*");
    static Regex all_white = new Regex("\\s*$");
    static Regex end_comment = new Regex("\\*/");
    static Regex pre_space = new Regex("\\s*}");
    static int spaces;
    static boolean in_comment;
    static boolean line_ended = true;

}
