// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 2/12/2004 17:15:47
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   Message.java

package com.progdan.zipengine.apps;

import java.awt.*;
import java.util.Vector;

public class Message extends Frame
{

    public void ask(Component component)
    {
        c = component;
        packNShow();
    }

    public void packNShow()
    {
        GridBagLayout gridbaglayout = new GridBagLayout();
        setLayout(gridbaglayout);
        GridBagConstraints gridbagconstraints = new GridBagConstraints();
        gridbagconstraints.gridx = 0;
        gridbagconstraints.fill = 2;
        int k = 0;
        int l = 0;
        for(int i = 0; i < v.size(); i++)
            if(v.elementAt(i) instanceof Label)
            {
                Label label = (Label)v.elementAt(i);
                add(label);
                gridbagconstraints.gridy = l++;
                gridbaglayout.setConstraints(label, gridbagconstraints);
            }

        gridbagconstraints.gridy = l;
        for(int j = 0; j < v.size(); j++)
            if(v.elementAt(j) instanceof Button)
            {
                Button button = (Button)v.elementAt(j);
                add(button);
                gridbagconstraints.gridx = k++;
                gridbaglayout.setConstraints(button, gridbagconstraints);
            }

        pack();
        resize(gridbaglayout.preferredLayoutSize(this));
        show();
    }

    public void addButton(Button button)
    {
        v.addElement(button);
    }

    public void addCentered(String s)
    {
        v.addElement(new Label(s, 1));
    }

    public void addLeft(String s)
    {
        v.addElement(new Label(s, 0));
    }

    public void addRight(String s)
    {
        v.addElement(new Label(s, 2));
    }

    public boolean action(Event event, Object obj)
    {
        if(c == null)
        {
            return true;
        } else
        {
            dispose();
            btxt = ((Button)event.target).getLabel();
            event.target = this;
            c.postEvent(event);
            return true;
        }
    }

    public Message()
    {
        v = new Vector();
    }

    public Vector v;
    public Component c;
    String btxt;
}
