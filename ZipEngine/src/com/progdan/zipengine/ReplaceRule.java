// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 2/12/2004 17:11:49
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   ReplaceRule.java

package com.progdan.zipengine;


// Referenced classes of package com.progdan.zipengine:
//            AmpersandRule, BackRefRule, LeftRule, NullRule,
//            RightRule, StringRule, RegRes

public abstract class ReplaceRule
{

    public void apply(StringBuffer stringbuffer, RegRes regres)
    {
    }

    public Object clone1()
    {
        throw new NoSuchMethodError("Need to define clone1() for " + getClass().getName());
    }

    public final Object clone()
    {
        ReplaceRule replacerule = (ReplaceRule)clone1();
        for(ReplaceRule replacerule1 = this; replacerule1.next != null; replacerule1 = replacerule1.next)
        {
            replacerule.next = (ReplaceRule)replacerule1.next.clone1();
            replacerule = replacerule.next;
        }

        return replacerule;
    }

    static ReplaceRule add(ReplaceRule replacerule, ReplaceRule replacerule1)
    {
        if(replacerule == null)
        {
            return replacerule = replacerule1;
        } else
        {
            replacerule.addRule(replacerule1);
            return replacerule;
        }
    }

    public void addRule(ReplaceRule replacerule)
    {
        if(next == null)
        {
            next = replacerule;
            return;
        } else
        {
            next.addRule(replacerule);
            return;
        }
    }

    static String filt(String s)
    {
        StringBuffer stringbuffer = new StringBuffer();
        for(int i = 0; i < s.length(); i++)
            if(s.charAt(i) != '\\' || i + 1 >= s.length())
                stringbuffer.append(s.charAt(i));

        return stringbuffer.toString();
    }

    public static ReplaceRule perlCode(String s)
    {
        if(s == null || s == "")
            return new NullRule();
        boolean flag = false;
        int j = 0;
        Object obj = null;
        for(int k = 1; k < s.length(); k++)
        {
            int i = k - 1;
            char c = s.charAt(k);
            char c1 = s.charAt(i);
            if((c1 == '\\' || c1 == '$') && c >= '0' && c <= '9')
            {
                int l = c - 48;
                for(int i1 = k + 1; i1 < s.length() && s.charAt(i1) >= '0' && s.charAt(i1) <= '9'; i1++)
                {
                    l = 10 * l + (s.charAt(i1) - 48);
                    k++;
                }

                if(i > j)
                {
                    Object obj10 = obj;
                    StringRule stringrule3 = new StringRule(filt(s.substring(j, i)));
                    if(obj10 == null)
                    {
                        obj = obj10 = stringrule3;
                    } else
                    {
                        ((ReplaceRule) (obj10)).addRule(stringrule3);
                        obj = obj10;
                    }
                }
                Object obj11 = obj;
                BackRefRule backrefrule = new BackRefRule(l);
                if(obj11 == null)
                {
                    obj = obj11 = backrefrule;
                } else
                {
                    ((ReplaceRule) (obj11)).addRule(backrefrule);
                    obj = obj11;
                }
                j = k + 1;
            } else
            if(c1 == '\\')
                k++;
            else
            if(c1 == '$' && (c == '&' || c == '`' || c == '\''))
            {
                if(i > j)
                {
                    Object obj2 = obj;
                    StringRule stringrule1 = new StringRule(filt(s.substring(j, i)));
                    if(obj2 == null)
                    {
                        obj = obj2 = stringrule1;
                    } else
                    {
                        ((ReplaceRule) (obj2)).addRule(stringrule1);
                        obj = obj2;
                    }
                }
                if(c == '&')
                {
                    Object obj3 = obj;
                    AmpersandRule ampersandrule = new AmpersandRule();
                    if(obj3 == null)
                    {
                        obj = obj3 = ampersandrule;
                    } else
                    {
                        ((ReplaceRule) (obj3)).addRule(ampersandrule);
                        obj = obj3;
                    }
                } else
                if(c == '\'')
                {
                    Object obj4 = obj;
                    RightRule rightrule = new RightRule();
                    if(obj4 == null)
                    {
                        obj = obj4 = rightrule;
                    } else
                    {
                        ((ReplaceRule) (obj4)).addRule(rightrule);
                        obj = obj4;
                    }
                } else
                if(c == '`')
                {
                    Object obj5 = obj;
                    LeftRule leftrule = new LeftRule();
                    if(obj5 == null)
                    {
                        obj = obj5 = leftrule;
                    } else
                    {
                        ((ReplaceRule) (obj5)).addRule(leftrule);
                        obj = obj5;
                    }
                }
                j = k + 1;
            } else
            if(c1 == '$')
            {
                String s1 = s.substring(k);
                if(i > j)
                {
                    Object obj6 = obj;
                    StringRule stringrule2 = new StringRule(filt(s.substring(j, i)));
                    if(obj6 == null)
                    {
                        obj = obj6 = stringrule2;
                    } else
                    {
                        ((ReplaceRule) (obj6)).addRule(stringrule2);
                        obj = obj6;
                    }
                }
                if(s1.startsWith("MATCH"))
                {
                    Object obj7 = obj;
                    AmpersandRule ampersandrule1 = new AmpersandRule();
                    if(obj7 == null)
                    {
                        obj = obj7 = ampersandrule1;
                    } else
                    {
                        ((ReplaceRule) (obj7)).addRule(ampersandrule1);
                        obj = obj7;
                    }
                    k += 4;
                } else
                if(s1.startsWith("PREMATCH"))
                {
                    Object obj8 = obj;
                    LeftRule leftrule1 = new LeftRule();
                    if(obj8 == null)
                    {
                        obj = obj8 = leftrule1;
                    } else
                    {
                        ((ReplaceRule) (obj8)).addRule(leftrule1);
                        obj = obj8;
                    }
                    k += 7;
                } else
                if(s1.startsWith("POSTMATCH"))
                {
                    Object obj9 = obj;
                    RightRule rightrule1 = new RightRule();
                    if(obj9 == null)
                    {
                        obj = obj9 = rightrule1;
                    } else
                    {
                        ((ReplaceRule) (obj9)).addRule(rightrule1);
                        obj = obj9;
                    }
                    k += 8;
                }
                j = k + 1;
            }
        }

        if(s.length() > j)
        {
            Object obj1 = obj;
            StringRule stringrule = new StringRule(filt(s.substring(j)));
            if(obj1 == null)
            {
                obj = obj1 = stringrule;
            } else
            {
                ((ReplaceRule) (obj1)).addRule(stringrule);
                obj = obj1;
            }
        }
        if(obj == null)
            return new NullRule();
        else
            return ((ReplaceRule) (obj));
    }

    public String toString1()
    {
        throw new NoSuchMethodError("Need to define toString1() for " + getClass().getName());
    }

    public final String toString()
    {
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append(toString1());
        for(ReplaceRule replacerule = next; replacerule != null; replacerule = replacerule.next)
            stringbuffer.append(replacerule.toString1());

        return stringbuffer.toString();
    }

    public ReplaceRule()
    {
    }

    protected ReplaceRule next;
}
