// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 2/12/2004 17:10:39
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   Regex.java

package com.progdan.zipengine;

import java.io.*;
import java.util.BitSet;

// Referenced classes of package com.progdan.zipengine:
//            RegRes, Any, BackMatch, Backup,
//            Boundary, Bracket, End, Group,
//            Key, Multi, NoPattern, NullPattern,
//            Or, OrMark, Pattern, Pthings,
//            Range, RegOpt, RegSyntax, ReplaceRule,
//            Replacer, Rthings, Skip, Start,
//            StrPos, lookAhead, oneChar, patInf,
//            patInt

public class Regex extends RegRes
    implements FilenameFilter
{

    private static boolean sharewareVersion()
    {
        return !Key.name.equals("Alexander Jacobson");
    }

    public Regex()
    {
        thePattern = none;
        minMatch = new patInt(0);
        dontMatchInQuotes = false;
        ignoreCase = false;
        esc = '\\';
        pt = new Pthings();
    }

    public Regex(String s)
    {
        thePattern = none;
        minMatch = new patInt(0);
        dontMatchInQuotes = false;
        ignoreCase = false;
        esc = '\\';
        pt = new Pthings();
        try
        {
            compile(s);
            return;
        }
        catch(RegSyntax _ex)
        {
            return;
        }
    }

    public Regex(String s, String s1)
    {
        this(s);
        rep = ReplaceRule.perlCode(s1);
    }

    public Regex(String s, ReplaceRule replacerule)
    {
        this(s);
        rep = replacerule;
    }

    public void setReplaceRule(String s)
    {
        rep = ReplaceRule.perlCode(s);
    }

    public void setReplaceRule(ReplaceRule replacerule)
    {
        rep = replacerule;
    }

    public ReplaceRule getReplaceRule()
    {
        return rep;
    }

    public String replaceFirst(String s)
    {
        return (new Replacer()).replaceFirstRegion(s, this, 0, s.length());
    }

    public String replaceFirstFrom(String s, int i)
    {
        return (new Replacer()).replaceFirstRegion(s, this, i, s.length());
    }

    public String replaceFirstRegion(String s, int i, int j)
    {
        return (new Replacer()).replaceFirstRegion(s, this, i, j);
    }

    public String replaceAll(String s)
    {
        return (new Replacer()).replaceAllRegion(s, this, 0, s.length());
    }

    public String replaceAllFrom(String s, int i)
    {
        return (new Replacer()).replaceAllRegion(s, this, i, s.length());
    }

    public String replaceAllRegion(String s, int i, int j)
    {
        return (new Replacer()).replaceAllRegion(s, this, i, j);
    }

    public Regex(Regex regex)
    {
        super(regex);
        thePattern = none;
        minMatch = new patInt(0);
        dontMatchInQuotes = false;
        ignoreCase = false;
        esc = '\\';
        pt = new Pthings();
        dontMatchInQuotes = regex.dontMatchInQuotes;
        esc = regex.esc;
        ignoreCase = regex.ignoreCase;
        rep = (ReplaceRule)regex.rep.clone();
        try
        {
            compile(regex.toString());
            return;
        }
        catch(RegSyntax _ex)
        {
            return;
        }
    }

    public void compile(String s)
        throws RegSyntax
    {
        Rthings rthings = new Rthings(this);
        int i = rthings.val;
        String s1 = s;
        thePattern = none;
        p = null;
        or = null;
        minMatch = new patInt(0);
        StrPos strpos = new StrPos(s, 0);
        if(strpos.incMatch("(?e="))
        {
            char c = strpos.c;
            strpos.inc();
            if(strpos.match(')'))
                s1 = reEscape(s.substring(6), c, '\\');
        }
        thePattern = _compile(s1, rthings);
        super.numSubs_ = rthings.val - i;
        rthings.set(this);
    }

    public Object clone()
    {
        return new Regex(this);
    }

    public RegRes result()
    {
        return (RegRes)super.clone();
    }

    final Pthings prep(String s)
    {
        super.src = s;
        pt.dotDoesntMatchCR = dotDoesntMatchCR;
        pt.ignoreCase = ignoreCase;
        if(pt.marks != null)
        {
            for(int i = 0; i < pt.marks.length; i++)
                pt.marks[i] = -1;

        }
        pt.nMarks = super.numSubs_;
        pt.src = s;
        if(dontMatchInQuotes)
            setCbits(s, pt);
        else
            pt.cbits = null;
        return pt;
    }

    public boolean matchAt(String s, int i)
    {
        return _search(s, i, i);
    }

    public boolean search(String s)
    {
        return _search(s, 0, s.length());
    }

    public boolean searchFrom(String s, int i)
    {
        return _search(s, i, s.length());
    }

    public boolean searchRegion(String s, int i, int j)
    {
        return _search(s, i, j);
    }

    boolean _search(String s, int i, int j)
    {
        Pthings pthings = prep(s);
        int k = minMatch != null ? j - minMatch.i : j;
        if(k < i && j >= i)
            k = i;
        if(skipper == null)
        {
            for(int l = i; l <= k; l++)
            {
                super.charsMatched_ = thePattern.matchAt(s, l, pthings);
                if(super.charsMatched_ >= 0)
                {
                    super.matchFrom_ = l;
                    super.marks = pthings.marks;
                    return super.didMatch_ = true;
                }
            }

        } else
        {
            for(int i1 = i; i1 <= k; i1++)
            {
                i1 = skipper.find(super.src, i1, k);
                if(i1 < 0)
                {
                    super.charsMatched_ = super.matchFrom_ = -1;
                    return super.didMatch_ = false;
                }
                super.charsMatched_ = thePattern.matchAt(s, i1, pthings);
                if(super.charsMatched_ >= 0)
                {
                    super.matchFrom_ = i1;
                    super.marks = pthings.marks;
                    return super.didMatch_ = true;
                }
            }

        }
        return super.didMatch_ = false;
    }

    static void setCbits(String s, Pthings pthings)
    {
        if(s == lasts)
        {
            pthings.cbits = lastbs;
            return;
        }
        StrPos strpos = new StrPos(s, 0);
        boolean flag = false;
        BitSet bitset = new BitSet(s.length());
        int i = 0;
        char c = '\0';
        while(!strpos.eos)
        {
            if(flag && strpos.match(c))
            {
                flag = !flag;
                bitset.clear(i);
            } else
            {
                if(strpos.dontMatch)
                {
                    if(flag)
                        bitset.set(i);
                    else
                        bitset.clear(i);
                    i++;
                }
                if(flag)
                    bitset.set(i);
                else
                    bitset.clear(i);
                if(!flag && strpos.match('"'))
                {
                    c = '"';
                    flag = !flag;
                } else
                if(!flag && strpos.match('\''))
                {
                    c = '\'';
                    flag = !flag;
                }
            }
            strpos.inc();
            i++;
        }
        pthings.cbits = bitset;
        lastbs = bitset;
        lasts = s;
    }

    Regex newRegex()
    {
        try
        {
            return (Regex)getClass().newInstance();
        }
        catch(InstantiationException instantiationexception)
        {
            System.out.println("Can't instatiate class derived from Regex");
            instantiationexception.printStackTrace();
            return null;
        }
        catch(IllegalAccessException illegalaccessexception)
        {
            System.out.println("Can't instantiate class derived from Regex");
            illegalaccessexception.printStackTrace();
            return null;
        }
    }

    protected void add(Pattern pattern)
    {
        if(p == null)
        {
            p = pattern;
            return;
        } else
        {
            p.add(pattern);
            pattern = p;
            return;
        }
    }

    protected void compile1(StrPos strpos, Rthings rthings)
        throws RegSyntax
    {
        if(strpos.match('['))
        {
            strpos.inc();
            add(matchBracket(strpos));
            return;
        }
        if(strpos.match('|'))
        {
            if(or == null)
                or = new Or();
            if(p == null)
                p = new NullPattern();
            or.addOr(p);
            p = null;
            return;
        }
        if(strpos.incMatch("(?<"))
        {
            patInt patint = strpos.getPatInt();
            add(new Backup(patint.intValue()));
            return;
        }
        if(strpos.incMatch("(?@"))
        {
            char c = strpos.c;
            strpos.inc();
            char c1 = strpos.c;
            strpos.inc();
            if(!strpos.match(')'))
            {
                throw new RegSyntax("(?@ does not have closing paren");
            } else
            {
                add(new Group(c, c1));
                return;
            }
        }
        if(strpos.incMatch("(?#"))
        {
            for(; !strpos.match(')'); strpos.inc());
            return;
        }
        if(strpos.dontMatch && strpos.c == 'w')
        {
            new Regex();
            Bracket bracket = new Bracket(false);
            bracket.addOr(new Range('a', 'z'));
            bracket.addOr(new Range('A', 'Z'));
            bracket.addOr(new Range('0', '9'));
            bracket.addOr(new oneChar('_'));
            add(bracket);
            return;
        }
        if(strpos.dontMatch && strpos.c == 's')
        {
            new Regex();
            Bracket bracket1 = new Bracket(false);
            bracket1.addOr(new oneChar(' '));
            bracket1.addOr(new Range('\b', '\n'));
            bracket1.addOr(new oneChar('\r'));
            add(bracket1);
            return;
        }
        if(strpos.dontMatch && strpos.c == 'd')
        {
            new Regex();
            Range range = new Range('0', '9');
            range.printBrackets = true;
            add(range);
            return;
        }
        if(strpos.dontMatch && strpos.c == 'W')
        {
            new Regex();
            Bracket bracket2 = new Bracket(true);
            bracket2.addOr(new Range('a', 'z'));
            bracket2.addOr(new Range('A', 'Z'));
            bracket2.addOr(new Range('0', '9'));
            bracket2.addOr(new oneChar('_'));
            add(bracket2);
            return;
        }
        if(strpos.dontMatch && strpos.c == 'S')
        {
            new Regex();
            Bracket bracket3 = new Bracket(true);
            bracket3.addOr(new oneChar(' '));
            bracket3.addOr(new Range('\b', '\n'));
            bracket3.addOr(new oneChar('\r'));
            add(bracket3);
            return;
        }
        if(strpos.dontMatch && strpos.c == 'D')
        {
            new Regex();
            Bracket bracket4 = new Bracket(true);
            bracket4.addOr(new Range('0', '9'));
            add(bracket4);
            return;
        }
        if(strpos.dontMatch && strpos.c == 'B')
        {
            Regex regex = new Regex();
            regex._compile("(?!\\b)", rthings);
            add(regex.thePattern);
            return;
        }
        if(strpos.dontMatch && strpos.c >= '1' && strpos.c <= '9')
        {
            int i = strpos.c - 48;
            StrPos strpos1 = new StrPos(strpos);
            strpos1.inc();
            if(!strpos1.dontMatch && strpos1.c >= '0' && strpos1.c <= '9')
            {
                i = 10 * i + (strpos1.c - 48);
                strpos.inc();
            }
            add(new BackMatch(i));
            return;
        }
        if(strpos.dontMatch && strpos.c == 'b')
        {
            add(new Boundary());
            return;
        }
        if(strpos.match('$'))
        {
            add(new End(true));
            return;
        }
        if(strpos.dontMatch && strpos.c == 'Z')
        {
            add(new End(false));
            return;
        }
        if(strpos.match('.'))
        {
            add(new Any());
            return;
        }
        if(strpos.match('('))
        {
            Regex regex1 = newRegex();
            strpos.inc();
            if(strpos.incMatch("?:"))
                regex1.or = new Or();
            else
            if(strpos.incMatch("?="))
                regex1.or = new lookAhead(false);
            else
            if(strpos.incMatch("?!"))
                regex1.or = new lookAhead(true);
            else
            if(strpos.match('?'))
            {
                strpos.inc();
                do
                {
                    if(strpos.c == 'i')
                        rthings.ignoreCase = true;
                    if(strpos.c == 'Q')
                        rthings.dontMatchInQuotes = true;
                    strpos.inc();
                } while(!strpos.match(')') && !strpos.eos);
                regex1 = null;
            } else
            {
                regex1.or = new OrMark(rthings.val++);
            }
            if(regex1 != null)
            {
                add(regex1._compile(strpos, rthings));
                return;
            }
        } else
        {
            if(strpos.match('^'))
            {
                add(new Start(true));
                return;
            }
            if(strpos.dontMatch && strpos.c == 'A')
            {
                add(new Start(false));
                return;
            }
            if(strpos.match('*'))
            {
                addMulti(new patInt(0), new patInf());
                return;
            }
            if(strpos.match('+'))
            {
                addMulti(new patInt(1), new patInf());
                return;
            }
            if(strpos.match('?'))
            {
                addMulti(new patInt(0), new patInt(1));
                return;
            }
            if(strpos.match('{'))
            {
                boolean flag = false;
                StrPos strpos2 = new StrPos(strpos);
                new StringBuffer();
                strpos.inc();
                patInt patint1 = strpos.getPatInt();
                Object obj = null;
                if(strpos.match('}'))
                {
                    obj = patint1;
                } else
                {
                    if(!strpos.match(','))
                        flag = true;
                    strpos.inc();
                    if(strpos.match('}'))
                        obj = new patInf();
                    else
                        obj = strpos.getPatInt();
                }
                if(patint1 == null || obj == null)
                    flag = true;
                if(flag)
                {
                    strpos.dup(strpos2);
                    add(new oneChar(strpos.c));
                    return;
                } else
                {
                    addMulti(patint1, ((patInt) (obj)));
                    return;
                }
            }
            add(new oneChar(strpos.c));
        }
    }

    private Pattern _compile(String s, Rthings rthings)
        throws RegSyntax
    {
        minMatch = null;
        StrPos strpos = new StrPos(s, 0);
        thePattern = _compile(strpos, rthings);
        pt.marks = null;
        return thePattern;
    }

    Pattern _compile(StrPos strpos, Rthings rthings)
        throws RegSyntax
    {
        for(; !strpos.eos && (or == null || !strpos.match(')')); strpos.inc())
            compile1(strpos, rthings);

        if(or != null)
        {
            if(p == null)
                p = new NullPattern();
            or.addOr(p);
            return or;
        } else
        {
            return p;
        }
    }

    void addMulti(patInt patint, patInt patint1)
        throws RegSyntax
    {
        Pattern pattern;
        for(pattern = p; pattern != null && pattern.next != null; pattern = pattern.next);
        Pattern pattern1;
        if(pattern == null || pattern == p)
            pattern1 = null;
        else
            for(pattern1 = p; pattern1.next != pattern; pattern1 = pattern1.next);
        if((pattern instanceof Multi) && patint.intValue() == 0 && patint1.intValue() == 1)
        {
            ((Multi)pattern).matchFewest = true;
            return;
        }
        if(pattern1 == null)
        {
            p = new Multi(patint, patint1, p);
            return;
        } else
        {
            pattern1.next = new Multi(patint, patint1, pattern);
            return;
        }
    }

    static Pattern matchBracket(StrPos strpos)
        throws RegSyntax
    {
        Bracket bracket;
        if(strpos.match('^'))
        {
            bracket = new Bracket(true);
            strpos.inc();
        } else
        {
            bracket = new Bracket(false);
        }
        for(; !strpos.eos && !strpos.match(']'); strpos.inc())
        {
            StrPos strpos1 = new StrPos(strpos);
            strpos1.inc();
            StrPos strpos2 = new StrPos(strpos1);
            strpos2.inc();
            if(strpos1.match('-') && !strpos2.match(']'))
            {
                StrPos strpos3 = new StrPos(strpos1);
                strpos3.inc();
                if(!strpos3.eos)
                    bracket.addOr(new Range(strpos.c, strpos3.c));
                strpos.inc();
                strpos.inc();
            } else
            if(strpos.escMatch('d'))
                bracket.addOr(new Range('0', '9'));
            else
            if(strpos.escMatch('s'))
            {
                bracket.addOr(new oneChar(' '));
                bracket.addOr(new Range('\b', '\n'));
                bracket.addOr(new oneChar('\r'));
            } else
            if(strpos.escMatch('w'))
            {
                bracket.addOr(new Range('a', 'z'));
                bracket.addOr(new Range('A', 'Z'));
                bracket.addOr(new oneChar('_'));
            } else
            if(strpos.escMatch('D'))
            {
                bracket.addOr(new Range('\0', '/'));
                bracket.addOr(new Range(':', '\uFFFF'));
            } else
            if(strpos.escMatch('S'))
            {
                bracket.addOr(new Range('\0', '\007'));
                bracket.addOr(new Range('\013', '\f'));
                bracket.addOr(new Range('\016', '\037'));
                bracket.addOr(new Range('!', '\uFFFF'));
            } else
            if(strpos.escMatch('W'))
            {
                bracket.addOr(new Range('\0', '@'));
                bracket.addOr(new Range('[', '^'));
                bracket.addOr(new oneChar('`'));
                bracket.addOr(new Range('{', '\uFFFF'));
            } else
            {
                bracket.addOr(new oneChar(strpos.c));
            }
        }

        return bracket;
    }

    public String toString()
    {
        StringBuffer stringbuffer = new StringBuffer();
        if(esc != '\\')
        {
            stringbuffer.append("(?e=");
            stringbuffer.append(esc);
            stringbuffer.append(")");
        }
        if(ignoreCase || dontMatchInQuotes)
        {
            stringbuffer.append("(?");
            if(ignoreCase)
                stringbuffer.append("i");
            if(dontMatchInQuotes)
                stringbuffer.append("Q");
            stringbuffer.append(")");
        }
        String s = thePattern.toString();
        if(esc != '\\')
            s = reEscape(s, '\\', esc);
        stringbuffer.append(s);
        return stringbuffer.toString();
    }

    static String reEscape(String s, char c, char c1)
    {
        if(c == c1)
            return s;
        StringBuffer stringbuffer = new StringBuffer();
        for(int i = 0; i < s.length(); i++)
            if(s.charAt(i) == c && i + 1 < s.length())
            {
                if(s.charAt(i + 1) == c)
                {
                    stringbuffer.append(c);
                } else
                {
                    stringbuffer.append(c1);
                    stringbuffer.append(s.charAt(i + 1));
                }
                i++;
            } else
            if(s.charAt(i) == c1)
            {
                stringbuffer.append(c1);
                stringbuffer.append(c1);
            } else
            {
                stringbuffer.append(s.charAt(i));
            }

        return stringbuffer.toString();
    }

    public boolean accept(File file, String s)
    {
        return search(s);
    }

    public static final String version()
    {
        return (Key.name.equals("Alexander Jacobson") ? "" : "shareware ") + "release 1.1.3";
    }

    public void optimize()
    {
        if(optimized() || thePattern == null)
        {
            return;
        } else
        {
            minMatch = thePattern.countMinChars();
            thePattern = RegOpt.opt(thePattern, ignoreCase, dontMatchInQuotes);
            skipper = Skip.findSkip(thePattern, ignoreCase, dontMatchInQuotes);
            return;
        }
    }

    public boolean optimized()
    {
        return minMatch != null;
    }

    public static Regex perlCode(String s)
    {
        String s1 = null;
        String s2 = null;
        if(sub == null)
        {
            sub = new Regex("s(.)(.*?[^\\\\]|)\\1(.*?[^\\\\]|)\\1");
            match = new Regex("m?(.)(.*?[^\\\\]|)\\1");
        }
        Regex regex = null;
        if(sub.matchAt(s, 0))
        {
            s1 = sub.stringMatched(2);
            s2 = sub.stringMatched(3);
            regex = sub;
        } else
        if(match.matchAt(s, 0))
        {
            s1 = match.stringMatched(2);
            regex = match;
        } else
        {
            return null;
        }
        if(regex.right().indexOf("i") >= 0)
            s1 = "(?i)" + s1;
        if(regex == sub)
            return new Regex(s1, s2);
        else
            return new Regex(s1);
    }

    public patInt countMinChars()
    {
        return thePattern.countMinChars();
    }

    public patInt countMaxChars()
    {
        return thePattern.countMaxChars();
    }

    static int BackRefOffset = 1;
    private static Pattern none = new NoPattern();
    Pattern thePattern;
    patInt minMatch;
    public boolean dontMatchInQuotes;
    public boolean ignoreCase;
    ReplaceRule rep;
    public char esc;
    Pthings pt;
    public static boolean dotDoesntMatchCR = true;
    static String lasts = null;
    static BitSet lastbs = null;
    Pattern p;
    Or or;
    Skip skipper;
    static Regex sub = null;
    static Regex match = null;

    static
    {
        if(sharewareVersion())
        {
            System.out.println("Package com.progdan.zipengine");
            System.out.println("Unregistered " + version());
            System.out.println("Home page at: http://www.win.net/~stevesoft/pat");
        }
    }
}
