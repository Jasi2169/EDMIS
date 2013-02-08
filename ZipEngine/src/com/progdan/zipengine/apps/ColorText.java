// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 2/12/2004 17:14:28
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   ColorText.java

package com.progdan.zipengine.apps;

import java.awt.*;
import java.util.Vector;

// Referenced classes of package com.progdan.zipengine.apps:
//            ColorLine

public class ColorText extends Canvas
{

    ColorText()
    {
        v = new Vector();
        setBackground(Color.white);
    }

    void addColorLine(ColorLine colorline)
    {
        v.addElement(colorline);
    }

    public void clear()
    {
        v = new Vector();
        repaint();
    }

    public void paint(Graphics g)
    {
        FontMetrics fontmetrics = getFontMetrics(getFont());
        if(fontmetrics == null)
            return;
        Rectangle rectangle = g.getClipRect();
        int j = rectangle.y;
        int k = rectangle.y + rectangle.width;
        int l = (k - 10 - fontmetrics.getAscent()) / fontmetrics.getHeight() + 1;
        int i1 = (j - 10 - fontmetrics.getAscent()) / fontmetrics.getHeight() - 1;
        if(i1 < 0)
            i1 = 0;
        if(l > v.size())
            l = v.size();
        for(int i = i1; i < l; i++)
            drawColorLine(g, fontmetrics, (ColorLine)v.elementAt(i), i);

        g.setColor(Color.black);
        g.drawRect(1, 1, size().width - 2, size().height - 2);
    }

    public Dimension getMinimumSize()
    {
        return getPreferredSize();
    }

    public Dimension getPreferredSize()
    {
        FontMetrics fontmetrics = getFontMetrics(getFont());
        if(fontmetrics == null)
            return new Dimension(0, 0);
        int j = 0;
        int k = 0;
        for(int i = 0; i < v.size(); i++)
        {
            ColorLine colorline = (ColorLine)v.elementAt(i);
            Dimension dimension1 = colorline.getSize(fontmetrics);
            k += dimension1.height;
            j = j <= dimension1.width ? dimension1.width : j;
        }

        k += fontmetrics.getAscent();
        Dimension dimension = new Dimension(j, k);
        return dimension;
    }

    final void drawColorLine(Graphics g, FontMetrics fontmetrics, ColorLine colorline, int i)
    {
        int k = 10;
        int l = fontmetrics.getAscent() + i * fontmetrics.getHeight() + 10;
        for(int j = 0; j < colorline.v.size(); j++)
        {
            Object obj = colorline.v.elementAt(j);
            if(obj instanceof Color)
            {
                g.setColor((Color)obj);
            } else
            {
                g.drawString((String)obj, k, l);
                k += fontmetrics.stringWidth((String)obj);
            }
        }

    }

    Vector v;
    static final int x_margin = 10;
    static final int y_margin = 10;
}
