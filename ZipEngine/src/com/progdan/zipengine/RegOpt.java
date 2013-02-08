// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 2/12/2004 17:11:05
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   RegOpt.java

package com.progdan.zipengine;

import java.util.*;

// Referenced classes of package com.progdan.zipengine:
//            Bracket, Branch, FastChar, FastMulti,
//            Multi, Or, Pattern, PatternSub,
//            Range, oneChar

public class RegOpt
{

    static Pattern opt(Pattern pattern, boolean flag, boolean flag1)
    {
        if(pattern == null)
            return pattern;
        if((pattern instanceof oneChar) && !flag && !flag1)
        {
            oneChar onechar = (oneChar)pattern;
            pattern = new FastChar(onechar.c);
            pattern.next = ((Pattern) (onechar)).next;
            pattern.parent = ((Pattern) (onechar)).parent;
        } else
        if(pattern instanceof Or)
        {
            Or or = (Or)pattern;
            Vector vector = or.v;
            or.v = new Vector();
            Branch branch = new Branch();
            for(int i = 0; i < vector.size(); i++)
            {
                Pattern pattern1 = (Pattern)vector.elementAt(i);
                if(pattern1 instanceof oneChar)
                    branch.addc((oneChar)pattern1, flag, flag1);
                else
                if(pattern1 instanceof FastChar)
                    branch.addc((FastChar)pattern1, flag, flag1);
                else
                    or.addOr(opt(pattern1, flag, flag1));
            }

            Pattern pattern2 = branch.reduce(flag, flag1);
            if(pattern2 instanceof Branch)
            {
                Pattern pattern3;
                for(Enumeration enumeration = branch.h.keys(); enumeration.hasMoreElements(); pattern3.setParent(branch))
                {
                    Object obj = enumeration.nextElement();
                    pattern3 = (Pattern)branch.h.get(obj);
                    branch.h.put(obj, pattern3 = opt(pattern3, flag, flag1));
                }

            }
            if(pattern2 != null && or.v.size() == 0 && or.leftForm().equals("(?:"))
            {
                pattern2.add(((Pattern) (or)).next);
                pattern2.parent = ((Pattern) (or)).parent;
                pattern = pattern2;
            } else
            if(pattern2 != null)
            {
                pattern2.setParent(or);
                or.v.insertElementAt(pattern2, 0);
            }
        } else
        if((pattern instanceof Multi) && (pattern instanceof PatternSub) && ((PatternSub)pattern).sub.next == null && ((((PatternSub)pattern).sub instanceof Bracket) || (((PatternSub)pattern).sub instanceof Range) || (((PatternSub)pattern).sub instanceof FastChar) || (((PatternSub)pattern).sub instanceof oneChar)))
        {
            Multi multi = (Multi)pattern;
            FastMulti fastmulti = new FastMulti(multi.a, multi.b, opt(((PatternSub) (multi)).sub, flag, flag1));
            fastmulti.parent = ((Pattern) (multi)).parent;
            fastmulti.matchFewest = multi.matchFewest;
            ((PatternSub) (fastmulti)).sub.parent = null;
            ((PatternSub) (fastmulti)).sub.next = null;
            fastmulti.next = ((Pattern) (multi)).next;
            fastmulti.step = 1;
            pattern = fastmulti;
        }
        if(pattern.next != null)
            pattern.next = opt(pattern.next, flag, flag1);
        return pattern;
    }

    public RegOpt()
    {
    }
}
