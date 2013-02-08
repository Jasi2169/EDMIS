// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 2/12/2004 17:20:28
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   TestGroup.java

package com.progdan.zipengine.apps;

import com.progdan.zipengine.RegRes;
import com.progdan.zipengine.Regex;
import java.awt.*;

// Referenced classes of package com.progdan.zipengine.apps:
//            ColorLine, ColorText

public class TestGroup extends Panel
{

    public TestGroup(String s, boolean flag)
    {
        lab = new Label(s);
        lab.setAlignment(2);
        txt = new TextField();
        txt.setEditable(flag);
        ctxt = new ColorText();
        GridBagLayout gridbaglayout = new GridBagLayout();
        setLayout(gridbaglayout);
        GridBagConstraints gridbagconstraints = new GridBagConstraints();
        gridbagconstraints.fill = 2;
        gridbagconstraints.gridx = 0;
        gridbagconstraints.gridy = 0;
        gridbagconstraints.weightx = 0.0D;
        gridbagconstraints.weighty = 0.0D;
        gridbaglayout.setConstraints(lab, gridbagconstraints);
        gridbagconstraints.weightx = 1.0D;
        gridbagconstraints.gridx = 1;
        gridbaglayout.setConstraints(txt, gridbagconstraints);
        gridbagconstraints.fill = 1;
        gridbagconstraints.weighty = 1.0D;
        gridbagconstraints.gridwidth = 2;
        gridbagconstraints.gridx = 0;
        gridbagconstraints.gridy = 1;
        gridbaglayout.setConstraints(ctxt, gridbagconstraints);
        add(lab);
        add(txt);
        add(ctxt);
    }

    public void ShowRes(Regex regex)
    {
        if(regex.didMatch())
        {
            ShowSuccess(regex);
            return;
        } else
        {
            ShowFail();
            return;
        }
    }

    public void ShowFail()
    {
        ColorLine colorline = new ColorLine();
        colorline.add(darkred, "Match failed!");
        ctxt.addColorLine(colorline);
        ctxt.repaint();
    }

    public void ShowSuccess(Regex regex)
    {
        ColorLine colorline = new ColorLine();
        colorline.add(darkgreen, "==>");
        colorline.add(Color.black, regex.left());
        colorline.add(darkgreen, "|");
        colorline.add(darkred, regex.substring());
        colorline.add(darkgreen, "|");
        colorline.add(Color.black, regex.right());
        colorline.add(darkgreen, "<==");
        ctxt.addColorLine(colorline);
        ctxt.addColorLine(new ColorLine());
        if(regex.numSubs() > 0)
        {
            ColorLine colorline1 = new ColorLine();
            colorline1.add(darkblue, "Backreferences:");
            ctxt.addColorLine(colorline1);
        }
        for(int i = 1; i <= regex.numSubs(); i++)
        {
            ColorLine colorline2 = new ColorLine();
            colorline2.add(darkblue, "(" + i + ") : ");
            if(regex.left(i) == null)
            {
                colorline2.add(darkblue, "[null]");
            } else
            {
                colorline2.add(Color.black, regex.left(i));
                colorline2.add(darkgreen, "|");
                colorline2.add(darkred, regex.substring(i));
                colorline2.add(darkgreen, "|");
                colorline2.add(Color.black, regex.right(i));
            }
            ctxt.addColorLine(colorline2);
        }

        ctxt.repaint();
    }

    Label lab;
    static final Color darkgreen = new Color(0, 119, 0);
    static final Color darkblue = new Color(0, 0, 119);
    static final Color darkred = new Color(119, 0, 0);
    public TextField txt;
    public ColorText ctxt;

}
