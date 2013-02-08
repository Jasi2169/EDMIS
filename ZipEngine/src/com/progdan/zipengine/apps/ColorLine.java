// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 2/12/2004 17:14:16
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   ColorLine.java

package com.progdan.zipengine.apps;

import java.awt.*;
import java.util.Vector;

public class ColorLine
{

    int ColorLineWidth(FontMetrics fontmetrics)
    {
        int i = 0;
        for(int j = 0; j < v.size(); j++)
            if(v.elementAt(j) instanceof String)
            {
                String s = (String)v.elementAt(j);
                i += fontmetrics.stringWidth(s);
            }

        return i;
    }

    public void add(Color color, String s)
    {
        v.addElement(color);
        v.addElement(s);
    }

    public Dimension getSize(FontMetrics fontmetrics)
    {
        int i = 0;
        for(int j = 0; j < v.size(); j++)
        {
            Object obj = v.elementAt(j);
            if(obj instanceof String)
                i += fontmetrics.stringWidth((String)obj);
        }

        return new Dimension(i, fontmetrics.getHeight());
    }

    public ColorLine()
    {
        v = new Vector();
    }

    Vector v;
}
