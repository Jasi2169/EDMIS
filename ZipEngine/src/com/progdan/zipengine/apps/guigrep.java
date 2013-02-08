// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 2/12/2004 17:14:43
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   guigrep.java

package com.progdan.zipengine.apps;

import com.progdan.zipengine.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

// Referenced classes of package com.progdan.zipengine.apps:
//            ColorLine, ColorText, Message

public class guigrep
{
    static class grepFrame extends Frame
        implements ActionListener
    {

        public void actionPerformed(ActionEvent actionevent)
        {
            spat = new Regex(t1.getText());
            spat.optimize();
            String s = t2.getText();
            t3.clear();
            String as[] = FileRegex.list(s);
            for(int i = 0; i < as.length; i++)
                try
                {
                    System.out.println("Searching file " + as[i]);
                    FileReader filereader = new FileReader(as[i]);
                    BufferedReader bufferedreader = new BufferedReader(filereader);
                    String s1 = bufferedreader.readLine();
                    int j = 1;
                    for(; s1 != null; s1 = bufferedreader.readLine())
                    {
                        if(spat.search(s1))
                        {
                            RegRes regres = spat.result();
                            ColorLine colorline = new ColorLine();
                            colorline.add(Color.blue, as[i] + " " + j + ": ");
                            colorline.add(Color.black, regres.left().replace('\t', ' '));
                            colorline.add(Color.red, regres.substring().replace('\t', ' '));
                            colorline.add(Color.black, regres.right().replace('\t', ' '));
                            t3.addColorLine(colorline);
                            repaint();
                        }
                        j++;
                    }

                    bufferedreader.close();
                }
                catch(Exception exception)
                {
                    exception.printStackTrace();
                    System.exit(255);
                }

            p3.doLayout();
            System.out.println("Done");
        }

        TextField t1;
        TextField t2;
        ColorText t3;
        ScrollPane p3;
        Regex spat;
        Regex dpat;

        grepFrame()
        {
            MenuBar menubar = new MenuBar();
            Menu menu = new Menu("Menu");
            MenuItem menuitem = new MenuItem("Exit", new MenuShortcut(120));
            menuitem.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent actionevent)
                {
                    System.exit(0);
                }

            });
            menu.add(menuitem);
            menubar.add(menu);
            MenuItem menuitem1 = new MenuItem("About", new MenuShortcut(97));
            menuitem1.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent actionevent)
                {
                    final Message m = new Message();
                    m.setTitle("About");
                    m.addCentered("guigrep");
                    m.addCentered("A file search utility");
                    m.addCentered("by Steven R. Brandt");
                    m.addCentered("Home page at");
                    m.addCentered("http://www.win.net/~stevesoft/pat");
                    Button button = new Button("OK");
                    m.addButton(button);
                    m.packNShow();
                    button.addActionListener(new ActionListener() {

                        public void actionPerformed(ActionEvent actionevent1)
                        {
                            m.dispose();
                        }

                    });
                }

            });
            menu.add(menuitem1);
            setMenuBar(menubar);
            GridBagLayout gridbaglayout = new GridBagLayout();
            setLayout(gridbaglayout);
            GridBagConstraints gridbagconstraints = new GridBagConstraints();
            Label label = new Label("search pattern");
            gridbagconstraints.gridwidth = gridbagconstraints.gridheight = 1;
            gridbagconstraints.fill = 2;
            gridbagconstraints.weightx = 0.0D;
            gridbagconstraints.weighty = 0.0D;
            gridbagconstraints.gridx = 0;
            gridbagconstraints.gridy = 0;
            gridbaglayout.setConstraints(label, gridbagconstraints);
            add(label);
            t1 = new TextField("");
            t1.addActionListener(this);
            gridbagconstraints.weightx = 1.0D;
            gridbagconstraints.gridx = 1;
            gridbagconstraints.gridy = 0;
            gridbaglayout.setConstraints(t1, gridbagconstraints);
            add(t1);
            Label label1 = new Label("file pattern");
            gridbagconstraints.gridwidth = gridbagconstraints.gridheight = 1;
            gridbagconstraints.fill = 2;
            gridbagconstraints.weightx = 0.0D;
            gridbagconstraints.weighty = 0.0D;
            gridbagconstraints.gridx = 0;
            gridbagconstraints.gridy = 1;
            gridbaglayout.setConstraints(label1, gridbagconstraints);
            add(label1);
            t2 = new TextField("");
            t2.addActionListener(this);
            gridbagconstraints.weightx = 1.0D;
            gridbagconstraints.gridx = 1;
            gridbagconstraints.gridy = 1;
            gridbaglayout.setConstraints(t2, gridbagconstraints);
            add(t2);
            t3 = new ColorText();
            p3 = new ScrollPane();
            gridbagconstraints.gridwidth = 2;
            gridbagconstraints.fill = 1;
            gridbagconstraints.weighty = 1.0D;
            gridbagconstraints.gridx = 0;
            gridbagconstraints.gridy = 2;
            gridbaglayout.setConstraints(p3, gridbagconstraints);
            add(p3);
            p3.add(t3);
            pack();
            setSize(500, 350);
            show();
        }
    }


    public static void main(String args[])
    {
        new grepFrame();
    }

    public guigrep()
    {
    }

    static Regex pat = null;

}

