// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 2/12/2004 17:16:04
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   ReGame.java

package com.progdan.zipengine.apps;

import com.progdan.zipengine.RegRes;
import com.progdan.zipengine.Regex;
import java.applet.Applet;
import java.awt.*;
import java.util.Random;
import java.util.Vector;

// Referenced classes of package com.progdan.zipengine.apps:
//            ColorText, Deck, Message, TestGroup

public class ReGame extends Applet
{

    String unescme(String s)
    {
        StringBuffer stringbuffer = new StringBuffer();
        for(int i = 0; i < s.length(); i++)
            if(s.charAt(i) == '_')
            {
                char c = s.charAt(++i);
                if(c == 'q')
                    stringbuffer.append('"');
                else
                if(c == 'a')
                    stringbuffer.append('&');
                else
                if(c == 'l')
                    stringbuffer.append('<');
                else
                if(c == 'r')
                    stringbuffer.append('>');
                else
                if(c == 'b')
                    stringbuffer.append('\\');
                else
                    stringbuffer.append('_');
            } else
            {
                stringbuffer.append(s.charAt(i));
            }

        return stringbuffer.toString();
    }

    void setScore(int i)
    {
        score = i;
        String s = "score: " + i;
        if(score_txt == null)
        {
            score_txt = new TextField(s);
            score_txt.setEditable(false);
            return;
        } else
        {
            score_txt.setText(s);
            return;
        }
    }

    void addScore(int i)
    {
        setScore(score + i);
    }

    public void init()
    {
        pat_msg = new Label("Pattern");
        pat = new TextField();
        String s = null;
        s = getParameter("Title");
        if(s == null)
            s = "ReGame";
        unreg = new Label(s);
        unreg.setAlignment(1);
        setScore(0);
        home_btn = new Button("About");
        add(unreg);
        add(score_txt);
        score_txt.setEditable(false);
        add(home_btn);
        Panel panel = new Panel();
        panel.add(pat_msg);
        panel.add(pat);
        add(panel);
        int i = (new Integer(getParameter("NGroups"))).intValue();
        tgroup = new TestGroup[i];
        answers = new RegRes[i];
        for(int j = 0; j < i; j++)
        {
            tgroup[j] = new TestGroup("txt" + (j + 1), false);
            add(tgroup[j]);
        }

        GridBagLayout gridbaglayout = new GridBagLayout();
        setLayout(gridbaglayout);
        GridBagConstraints gridbagconstraints = new GridBagConstraints();
        gridbagconstraints.gridy = 0;
        gridbagconstraints.gridx = 0;
        gridbagconstraints.gridwidth = 2;
        gridbagconstraints.gridheight = 1;
        gridbagconstraints.fill = 2;
        gridbagconstraints.weightx = 0.0D;
        gridbagconstraints.weighty = 0.0D;
        gridbaglayout.setConstraints(unreg, gridbagconstraints);
        gridbagconstraints.gridx = 1;
        gridbagconstraints.gridy = 1;
        gridbagconstraints.weightx = 1.0D;
        gridbagconstraints.gridwidth = 1;
        gridbaglayout.setConstraints(home_btn, gridbagconstraints);
        gridbagconstraints.gridx = 0;
        gridbagconstraints.gridy = 1;
        gridbagconstraints.weightx = 1.0D;
        gridbaglayout.setConstraints(score_txt, gridbagconstraints);
        gridbagconstraints.gridx = 0;
        gridbagconstraints.gridy = 2;
        gridbagconstraints.gridwidth = 2;
        gridbagconstraints.weightx = 0.0D;
        panel.setLayout(gridbaglayout);
        gridbaglayout.setConstraints(panel, gridbagconstraints);
        gridbagconstraints.weightx = 1.0D;
        gridbagconstraints.weighty = 1.0D;
        gridbagconstraints.gridx = 0;
        gridbagconstraints.gridwidth = 2;
        gridbagconstraints.fill = 1;
        for(int k = 0; k < i; k++)
        {
            gridbagconstraints.gridy = k + 3;
            gridbaglayout.setConstraints(tgroup[k], gridbagconstraints);
        }

        gridbagconstraints.gridx = 0;
        gridbagconstraints.gridy = 0;
        gridbagconstraints.gridwidth = 1;
        gridbagconstraints.fill = 2;
        gridbagconstraints.weightx = 0.0D;
        gridbaglayout.setConstraints(pat_msg, gridbagconstraints);
        gridbagconstraints.gridx = 1;
        gridbagconstraints.weightx = 1.0D;
        gridbaglayout.setConstraints(pat, gridbagconstraints);
    }

    public void start()
    {
        if(started)
            return;
        started = true;
        int i = (new Integer(getParameter("NQuizes"))).intValue();
        quizes = new Deck(i);
        int j;
        try
        {
            j = (new Integer(getParameter("NDiscards"))).intValue();
        }
        catch(Throwable _ex)
        {
            j = 0;
        }
        while(j > 0)
        {
            j--;
            quizes.draw();
        }
        setScore(0);
        getquiz();
        game_over = false;
    }

    Regex getquiz()
    {
        if(quizes.ncards() == 0)
        {
            score_txt.setText("Game Over: " + score + "/" + max_score);
            game_over = true;
            return null;
        }
        quizno = quizes.draw();
        String s = unescme(getParameter("pat" + quizno));
        p_len = s.length();
        Regex regex = new Regex(s);
        for(int i = 0; i < tgroup.length; i++)
        {
            String s1 = "txt" + (i + 1) + "-" + quizno;
            s1 = unescme(getParameter(s1));
            tgroup[i].txt.setText(s1);
            regex.search(s1);
            tgroup[i].ShowRes(regex);
            answers[i] = regex.result();
        }

        return regex;
    }

    public boolean action(Event event, Object obj)
    {
        repaint();
        if(event.target instanceof Button)
        {
            Message message = new Message();
            message.setTitle("About");
            message.addCentered("ReGame");
            message.addCentered("A regular expression game");
            message.addCentered("by Steven R. Brandt");
            message.addCentered("Home page at");
            message.addCentered("http://www.win.net/~stevesoft/pat");
            message.addButton(new Button("OK"));
            message.ask(this);
            return true;
        }
        if(event.target instanceof Message)
        {
            Message message1 = (Message)event.target;
            if(message1.getTitle().equals("About"))
                return true;
            for(int i = 0; i < tgroup.length; i++)
                tgroup[i].ctxt.clear();

            getquiz();
            mes = null;
            return true;
        }
        if((event.target instanceof TextField) && mes == null)
        {
            if(game_over)
                return true;
            String s = pat.getText();
            Regex regex = new Regex(s);
            mes = new Message();
            mes.setTitle("Score");
            for(int j = 0; j < tgroup.length; j++)
            {
                regex.search(tgroup[j].txt.getText());
                RegRes regres = regex.result();
                if(regres.equals(answers[j]))
                {
                    String s1;
                    if(s.length() > p_len)
                    {
                        s1 = "Long Match: 5 pts";
                        addScore(5);
                    } else
                    if(s.length() == p_len)
                    {
                        s1 = "Match: 10 pts";
                        addScore(10);
                    } else
                    {
                        s1 = "Short Match: 12 pts";
                        addScore(12);
                    }
                    mes.v.addElement(new Label(s1));
                } else
                {
                    mes.v.addElement(new Label("No Match"));
                }
                max_score += 10;
            }

            mes.v.addElement(new Label("answer: " + unescme(getParameter("pat" + quizno))));
            mes.v.addElement(new Button("OK"));
            mes.ask(this);
            return true;
        } else
        {
            return false;
        }
    }

    public ReGame()
    {
        inited = false;
        started = false;
        qrand = new Random();
        game_over = false;
    }

    Label pat_msg;
    public TextField pat;
    public TestGroup tgroup[];
    public RegRes answers[];
    int score;
    Label unreg;
    public TextField score_txt;
    public Button home_btn;
    boolean inited;
    boolean started;
    public Random qrand;
    int quizno;
    int p_len;
    boolean game_over;
    public Deck quizes;
    int max_score;
    public Message mes;
}
