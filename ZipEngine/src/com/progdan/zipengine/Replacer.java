// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 2/12/2004 17:11:35
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   Replacer.java

package com.progdan.zipengine;


// Referenced classes of package com.progdan.zipengine:
//            AmpersandRule, RegRes, Regex, ReplaceRule

public class Replacer
{

    public Replacer()
    {
    }

    public String replaceFirstRegion(String s, Regex regex, int i, int j)
    {
        if(regex.searchRegion(s, i, j))
        {
            apply(regex);
            String s1 = finish();
            if(s1 == null)
                return s;
            else
                return s1;
        } else
        {
            return s;
        }
    }

    public String replaceAllRegion(String s, Regex regex, int i, int j)
    {
        if(regex.searchRegion(s, i, j))
        {
            apply(regex);
            for(int k = regex.matchedTo(); regex.searchRegion(s, k, j); k = regex.matchedTo())
                apply(regex);

            String s1 = finish();
            if(s1 == null)
                return s;
            else
                return s1;
        } else
        {
            return s;
        }
    }

    public void apply(RegRes regres, ReplaceRule replacerule)
    {
        if(replacerule == null || replacerule.next == null && (replacerule instanceof AmpersandRule))
            return;
        if(regres.didMatch())
        {
            if(src == null)
            {
                src = regres.getString();
                sb = new StringBuffer();
            }
            sb.append(src.substring(pos, regres.matchedFrom()));
            for(ReplaceRule replacerule1 = replacerule; replacerule1 != null; replacerule1 = replacerule1.next)
                replacerule1.apply(sb, regres);

            pos = regres.matchedTo();
        }
    }

    public void apply(Regex regex)
    {
        apply(((RegRes) (regex)), regex.getReplaceRule());
    }

    public String finish()
    {
        if(src == null)
        {
            return null;
        } else
        {
            sb.append(src.substring(pos));
            src = null;
            pos = 0;
            String s = sb.toString();
            sb = null;
            return s;
        }
    }

    StringBuffer sb;
    String src;
    int pos;
}
