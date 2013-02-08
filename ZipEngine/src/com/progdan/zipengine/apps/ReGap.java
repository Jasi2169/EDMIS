// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 2/12/2004 17:20:07
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   ReGap.java

package com.progdan.zipengine.apps;

import com.progdan.zipengine.Regex;
import java.applet.Applet;
import java.awt.*;

// Referenced classes of package com.progdan.zipengine.apps:
//            ColorText, TestGroup

public class ReGap extends Applet
{

    public void init()
    {
        tg = new TestGroup("Text", true);
        add(pat_msg);
        add(pat);
        add(tg);
        GridBagLayout gridbaglayout = new GridBagLayout();
        setLayout(gridbaglayout);
        GridBagConstraints gridbagconstraints = new GridBagConstraints();
        gridbagconstraints.gridwidth = gridbagconstraints.gridheight = 1;
        gridbagconstraints.fill = 2;
        gridbagconstraints.weightx = 0.0D;
        gridbagconstraints.weighty = 0.0D;
        gridbagconstraints.gridx = 0;
        gridbagconstraints.gridy = 0;
        gridbaglayout.setConstraints(pat_msg, gridbagconstraints);
        gridbagconstraints.weightx = 1.0D;
        gridbagconstraints.gridx = 1;
        gridbagconstraints.gridy = 0;
        gridbaglayout.setConstraints(pat, gridbagconstraints);
        gridbagconstraints.gridwidth = 2;
        gridbagconstraints.fill = 1;
        gridbagconstraints.weighty = 1.0D;
        gridbagconstraints.gridx = 0;
        gridbagconstraints.gridy = 1;
        gridbaglayout.setConstraints(tg, gridbagconstraints);
    }

    public boolean action(Event event, Object obj)
    {
        tg.ctxt.clear();
        String s = pat.getText();
        String s1 = tg.txt.getText();
        if(s == null || s1 == null)
            return true;
        if(s.equals("") || s1.equals(""))
        {
            return true;
        } else
        {
            Regex regex = new Regex(s);
            regex.search(s1);
            tg.ShowRes(regex);
            return true;
        }
    }

    public ReGap()
    {
        pat_msg = new Label("Pattern");
        pat = new TextField();
    }

    Label pat_msg;
    public TextField pat;
    public TestGroup tg;
}
