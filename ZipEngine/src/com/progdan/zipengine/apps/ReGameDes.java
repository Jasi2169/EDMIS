// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 2/12/2004 17:16:12
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   ReGameDes.java

package com.progdan.zipengine.apps;

import com.progdan.zipengine.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Enumeration;
import java.util.Hashtable;

// Referenced classes of package com.progdan.zipengine.apps:
//            ColorText, TestGroup

public class ReGameDes extends Frame
{
    private class MenuNum
        implements ActionListener
    {

        public void actionPerformed(ActionEvent actionevent)
        {
            UpdateCurrentQuestion();
            quizno = num;
            LoadQuestion();
            setnLabel();
        }

        int num;

        MenuNum(int i)
        {
            num = i;
        }
    }


    void SetGotoQuestionMenu()
    {
        Menu menu = GotoQuestionMenu;
        for(; GotoQuestionMenu.getItemCount() > 0; GotoQuestionMenu.remove(0));
        Object obj = null;
        for(int i = 0; QuizDataTable.get("pat" + i) != null; i++)
        {
            MenuItem menuitem;
            menu.add(menuitem = new MenuItem((String)QuizDataTable.get("pat" + i)));
            menuitem.addActionListener(new MenuNum(i));
            if(i % 10 == 9)
            {
                Menu menu1 = new Menu("More");
                menu.add(menu1);
                menu = menu1;
            }
        }

    }

    void LoadQuestion()
    {
        try
        {
            InputPattern.setText((String)QuizDataTable.get("pat" + quizno));
            for(int i = 0; i < tg.length; i++)
                tg[i].txt.setText((String)QuizDataTable.get("txt" + (i + 1) + "-" + quizno));

            RunRegexOnTxtFields();
            return;
        }
        catch(Throwable _ex)
        {
            return;
        }
    }

    void UpdateCurrentQuestion()
    {
        String s = InputPattern.getText();
        if(s == null || s.equals(""))
            return;
        String s1 = "pat" + quizno;
        String s2 = (String)QuizDataTable.get(s1);
        if(s2 == null || !s2.equals(InputPattern.getText()))
            modified = true;
        QuizDataTable.put(s1, InputPattern.getText());
        for(int i = 0; i < tg.length; i++)
        {
            String s3 = "txt" + (i + 1) + "-" + quizno;
            String s4 = (String)QuizDataTable.get(s3);
            if(s4 == null || !s4.equals(tg[i].txt.getText()))
                modified = true;
            QuizDataTable.put(s3, tg[i].txt.getText());
        }

        if(modified)
            SetGotoQuestionMenu();
    }

    void setnLabel()
    {
        String s = (String)QuizDataTable.get("NDiscards");
        String s1 = (String)QuizDataTable.get("NQuizes");
        int i = 0;
        int j = 0;
        if(s != null)
            try
            {
                i = (new Integer(s)).intValue();
            }
            catch(Throwable _ex) { }
        if(s1 != null)
            try
            {
                j = (new Integer(s1)).intValue();
            }
            catch(Throwable _ex) { }
        i = j - 1 >= i ? i : j - 1;
        i = i < 0 ? 0 : i;
        nlabel.setText("NQuizes=" + j + ", NDiscards=" + i + ", quizno=" + quizno);
    }

    void addc(Component component)
    {
        gb.setConstraints(component, gc);
        add(component);
    }

    public ReGameDes(String s)
    {
        nlabel = new Label();
        gb = new GridBagLayout();
        gc = new GridBagConstraints();
        QuizDataTable = new Hashtable();
        modified = false;
        begin_app = new Regex("(?i)<\\s*applet");
        end_app = new Regex("(?i)<\\s*/\\s*applet\\s*>");
        CurrentFile = s;
        ReadAndProcessFile(s);
        setTitle("ReGame Designer");
        Object obj = QuizDataTable.get("NGroups");
        int i = 3;
        if(obj != null)
            try
            {
                i = (new Integer((String)obj)).intValue();
            }
            catch(Throwable _ex)
            {
                System.out.println("Badly formatted NGroups...");
            }
        tg = new TestGroup[i];
        setLayout(gb);
        MenuItem menuitem = null;
        MenuBar menubar = new MenuBar();
        setMenuBar(menubar);
        Menu menu = new Menu("File");
        Menu menu1 = new Menu("Patterns");
        Menu menu2 = new Menu("Options");
        GotoQuestionMenu = menu1;
        menubar.add(menu);
        menubar.add(menu1);
        menubar.add(menu2);
        menu1.add(menuitem = new MenuItem("New Pattern", new MenuShortcut(87)));
        menuitem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent actionevent)
            {
                UpdateCurrentQuestion();
                AddNewQuestion();
            }

        });
        menu1.add(menuitem = new MenuItem("Next Pattern", new MenuShortcut(78)));
        menuitem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent actionevent)
            {
                UpdateCurrentQuestion();
                quizno++;
                if(QuizDataTable.get("pat" + quizno) == null)
                    quizno--;
                else
                    LoadQuestion();
                setnLabel();
            }

        });
        menu1.add(menuitem = new MenuItem("Prev Pattern", new MenuShortcut(80)));
        menuitem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent actionevent)
            {
                UpdateCurrentQuestion();
                quizno--;
                if(quizno < 0)
                    quizno = 0;
                LoadQuestion();
                setnLabel();
            }

        });
        menu1.add(menuitem = new MenuItem("Delete Pattern", new MenuShortcut(68)));
        menuitem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent actionevent)
            {
                int l = 1;
                for(int i1 = quizno; QuizDataTable.get("pat" + i1) != null; i1++)
                    l = i1;

                QuizDataTable.put("NQuizes", String.valueOf(l));
                if(l <= 1)
                    return;
                int l1 = 0;
                Object obj1 = QuizDataTable.get("NDiscards");
                if(obj1 != null)
                    try
                    {
                        l1 = (new Integer((String)obj1)).intValue();
                    }
                    catch(Throwable _ex) { }
                l1 = l1 <= l - 1 ? l1 : l - 1;
                QuizDataTable.put("NDiscards", String.valueOf(l1));
                setnLabel();
                if(l != quizno)
                {
                    QuizDataTable.put("pat" + quizno, QuizDataTable.get("pat" + l));
                    for(int j1 = 0; j1 < tg.length; j1++)
                        QuizDataTable.put("txt" + (j1 + 1) + "-" + quizno, QuizDataTable.get("txt" + (j1 + 1) + "-" + l));

                }
                QuizDataTable.remove("pat" + l);
                for(int k1 = 0; k1 < tg.length; k1++)
                    QuizDataTable.remove("txt" + (k1 + 1) + "-" + l);

                if(l == quizno)
                    quizno--;
                LoadQuestion();
            }

        });
        menu1.add(GotoQuestionMenu = new Menu("List Pattern"));
        menu2.add(menuitem = new MenuItem("Increment NDiscards", new MenuShortcut(73)));
        menuitem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent actionevent)
            {
                String s1 = (String)QuizDataTable.get("NDiscards");
                int l = 0;
                if(s1 != null)
                    try
                    {
                        l = (new Integer(s1)).intValue();
                    }
                    catch(Throwable _ex) { }
                l++;
                QuizDataTable.put("NDiscards", String.valueOf(l));
                setnLabel();
            }

        });
        menu2.add(menuitem = new MenuItem("Increment NDiscards", new MenuShortcut(68)));
        menuitem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent actionevent)
            {
                String s1 = (String)QuizDataTable.get("NDiscards");
                int l = 0;
                if(s1 != null)
                    try
                    {
                        l = (new Integer(s1)).intValue();
                    }
                    catch(Throwable _ex) { }
                if(--l < 0)
                    l = 0;
                QuizDataTable.put("NDiscards", String.valueOf(l));
                setnLabel();
            }

        });
        menu.add(menuitem = new MenuItem("New", new MenuShortcut(87)));
        menuitem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent actionevent)
            {
                newfile(null);
            }

        });
        menu.add(menuitem = new MenuItem("Load", new MenuShortcut(76)));
        menuitem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent actionevent)
            {
                if(!outfile())
                    return;
                FileDialog filedialog = new FileDialog(for_dialog, "Load", 0);
                Regex regex = new Regex("\\.html?$");
                filedialog.setFile("*.html");
                filedialog.setFilenameFilter(regex);
                filedialog.show();
                String s1 = filedialog.getDirectory() + File.separator + filedialog.getFile();
                if(!(new File(s1)).exists())
                {
                    System.out.println("No such file: " + s1);
                    return;
                } else
                {
                    System.out.println("Loading: " + s1);
                    ReadAndProcessFile(s1);
                    System.out.println("File Loaded: " + s1);
                    SetGotoQuestionMenu();
                    quizno = 0;
                    LoadQuestion();
                    modified = false;
                    return;
                }
            }

        });
        menu.add(menuitem = new MenuItem("Save", new MenuShortcut(83)));
        menuitem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent actionevent)
            {
                savefile();
            }

        });
        menu.add(menuitem = new MenuItem("Save As...", new MenuShortcut(65)));
        menuitem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent actionevent)
            {
                FileDialog filedialog = new FileDialog(for_dialog, "Save", 1);
                Regex regex = new Regex("\\.html?$");
                filedialog.setFile("*.html");
                filedialog.setFilenameFilter(regex);
                filedialog.show();
                String s1 = filedialog.getDirectory() + File.separator + filedialog.getFile();
                File file = new File(CurrentFile);
                File file1 = new File(s1);
                if(!file1.exists())
                    newfile(s1);
                copy(file, file1);
                CurrentFile = s1;
                savefile();
            }

        });
        menu.add(menuitem = new MenuItem("Quit", new MenuShortcut(81)));
        menuitem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent actionevent)
            {
                if(outfile())
                    System.exit(0);
            }

        });
        gc.gridx = 0;
        gc.gridy = 1;
        gc.fill = 0;
        addc(new Label("pattern"));
        gc.gridx = 1;
        gc.gridy = 1;
        gc.fill = 2;
        gc.weightx = 1.0D;
        InputPattern = new TextField();
        addc(InputPattern);
        gc.gridx = 0;
        gc.gridwidth = 2;
        gc.gridy = 0;
        setnLabel();
        addc(nlabel);
        gc.fill = 1;
        gc.weighty = 1.0D;
        for(int j = 0; j < i; j++)
        {
            gc.gridy = j + 2;
            addc(tg[j] = new TestGroup("txt" + (j + 1), true));
        }

        ActionListener actionlistener = new ActionListener() {

            public void actionPerformed(ActionEvent actionevent)
            {
                try
                {
                    String s1 = actionevent.paramString();
                    UpdateCurrentQuestion();
                    for(int l = 0; QuizDataTable.get("pat" + l) != null; l++)
                        if(s1.equals(QuizDataTable.get("pat" + l)))
                        {
                            quizno = l;
                            LoadQuestion();
                            return;
                        }

                    RunRegexOnTxtFields();
                    return;
                }
                catch(Exception exception)
                {
                    exception.printStackTrace();
                }
                System.exit(255);
            }

        };
        InputPattern.addActionListener(actionlistener);
        for(int k = 0; k < tg.length; k++)
            tg[k].txt.addActionListener(actionlistener);

        LoadQuestion();
        SetGotoQuestionMenu();
    }

    public static void main(String args[])
    {
        ReGameDes regamedes = null;
        if(args.length < 1)
            regamedes = new ReGameDes("ReGame.html");
        else
            regamedes = new ReGameDes(args[0]);
        int i = 0;
        try
        {
            i = (new Integer((String)regamedes.QuizDataTable.get("NQuizes"))).intValue();
        }
        catch(Throwable _ex) { }
        if(i == 0)
            regamedes.AddNewQuestion();
        regamedes.SizeAndShow();
    }

    public void SizeAndShow()
    {
        pack();
        Dimension dimension = getPreferredSize();
        setSize(dimension.width + 100, dimension.height + 200);
        modified = false;
        show();
    }

    void AddNewQuestion()
    {
        for(; QuizDataTable.get("pat" + quizno) != null; quizno++);
        InputPattern.setText("");
        for(int i = 0; i < tg.length; i++)
        {
            tg[i].txt.setText("");
            tg[i].ctxt.clear();
        }

        QuizDataTable.put("NQuizes", String.valueOf(quizno + 1));
        setnLabel();
    }

    void read_to_applet(BufferedReader bufferedreader, PrintWriter printwriter)
    {
        try
        {
            for(String s = bufferedreader.readLine(); s != null; s = bufferedreader.readLine())
                if(s != null && begin_app.search(s))
                {
                    for(; s.indexOf(">") < 0; s = s + " " + bufferedreader.readLine());
                    if(printwriter != null)
                        printwriter.println(s);
                    if(s.indexOf("ReGame.class") >= 0)
                        return;
                } else
                if(printwriter != null)
                    printwriter.println(s);

            return;
        }
        catch(Throwable throwable)
        {
            throwable.printStackTrace();
        }
    }

    public void ReadAndProcessFile(String s)
    {
        try
        {
            setTitle("ReGameDes: " + (new File(s)).getName());
            QuizDataTable = new Hashtable();
            FileReader filereader = new FileReader(s);
            BufferedReader bufferedreader = new BufferedReader(filereader);
            Regex regex = new Regex("(?i)name\\s*=\\s*\"(.*?[^\"\\\\])\"\\s*value\\s*=\\s*\"(.*?[^\"\\\\])\"");
            String s1 = bufferedreader.readLine();
            read_to_applet(bufferedreader, null);
            for(; s1 != null; s1 = bufferedreader.readLine())
            {
                if(s1 != null && regex.search(s1))
                {
                    String s2 = unescme(regex.stringMatched(1));
                    String s3 = unescme(regex.stringMatched(2));
                    QuizDataTable.put(s2, s3);
                    continue;
                }
                if(s1 != null && end_app.search(s1))
                    break;
            }

            bufferedreader.close();
            modified = false;
            return;
        }
        catch(Throwable _ex)
        {
            return;
        }
    }

    void copy(File file, File file1)
    {
        try
        {
            FileReader filereader = new FileReader(file);
            FileWriter filewriter = new FileWriter(file1);
            PrintWriter printwriter = new PrintWriter(filewriter);
            BufferedReader bufferedreader = new BufferedReader(filereader);
            for(String s = bufferedreader.readLine(); s != null; s = bufferedreader.readLine())
                printwriter.println(s);

            printwriter.close();
            bufferedreader.close();
            return;
        }
        catch(Throwable _ex)
        {
            return;
        }
    }

    boolean outfile()
    {
        UpdateCurrentQuestion();
        if(!modified)
        {
            return true;
        } else
        {
            final Dialog d = new Dialog(this, true);
            d.setLayout(new GridLayout(4, 1));
            d.setTitle("Abandon");
            d.add(new Label("You have unsaved"));
            d.add(new Label("changes, do you really"));
            d.add(new Label("want to abandon them?"));
            Panel panel = new Panel();
            d.add(panel);
            panel.setLayout(new GridLayout(1, 2));
            final boolean b[] = new boolean[1];
            b[0] = false;
            Button button = new Button("Yes");
            Button button1 = new Button("No");
            button.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent actionevent)
                {
                    b[0] = true;
                    d.dispose();
                }

            });
            button1.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent actionevent)
                {
                    b[0] = false;
                    d.dispose();
                }

            });
            panel.add(button);
            panel.add(button1);
            d.pack();
            Dimension dimension = d.getLayout().preferredLayoutSize(d);
            d.setSize(dimension);
            d.show();
            return b[0];
        }
    }

    boolean newfile(String s)
    {
        if(!outfile())
            return true;
        TextField textfield = null;
        Choice choice = null;
        try
        {
            if(s == null)
            {
                File file = null;
                int i = 0;
                i = 0;
                do
                {
                    File file1 = new File("ReGame" + i + ".html");
                    if(!file1.exists())
                        break;
                    i++;
                } while(true);
                do
                {
                    final Dialog d = new Dialog(this, true);
                    d.setLayout(new GridLayout(3, 2));
                    d.setTitle("Creating new file...");
                    d.add(new Label("File Name"));
                    textfield = new TextField("ReGame" + i + ".html");
                    d.add(textfield);
                    d.add(new Label("Number of Text Fields"));
                    choice = new Choice();
                    for(int j = 2; j <= 10; j++)
                        choice.addItem(String.valueOf(j));

                    d.add(choice);
                    choice.select(1);
                    Button button = new Button("Done");
                    button.addActionListener(new ActionListener() {

                        public void actionPerformed(ActionEvent actionevent)
                        {
                            d.dispose();
                        }

                    });
                    final boolean breakout[] = new boolean[1];
                    breakout[0] = false;
                    Button button1 = new Button("Cancel");
                    button1.addActionListener(new ActionListener() {

                        public void actionPerformed(ActionEvent actionevent)
                        {
                            breakout[0] = true;
                            d.dispose();
                        }

                    });
                    d.add(button);
                    d.add(button1);
                    d.pack();
                    Dimension dimension = d.getLayout().preferredLayoutSize(d);
                    d.setSize(dimension);
                    d.show();
                    if(breakout[0])
                        return true;
                    file = new File(textfield.getText());
                } while(file.exists());
            }
            String s1 = s != null ? s : textfield.getText();
            FileWriter filewriter = new FileWriter(s1);
            BufferedWriter bufferedwriter = new BufferedWriter(filewriter);
            PrintWriter printwriter = new PrintWriter(bufferedwriter);
            printwriter.println("<html><body>");
            printwriter.println("<applet code=\"com.progdan.zipengine.apps.ReGame.class\" width=410 height=450>");
            printwriter.println("<param name=\"NGroups\" value=\"" + (choice != null ? choice.getSelectedIndex() + 2 : tg.length) + "\">");
            printwriter.println("</applet>");
            printwriter.println("</body></html>");
            printwriter.close();
            ReadAndProcessFile(s1);
            AddNewQuestion();
            SetGotoQuestionMenu();
            quizno = 0;
            LoadQuestion();
            modified = false;
        }
        catch(IOException ioexception)
        {
            System.err.println("Error creating new File");
            ioexception.printStackTrace();
        }
        return true;
    }

    void savefile()
    {
        try
        {
            File file = new File(CurrentFile + ".bak");
            File file1 = new File(CurrentFile);
            file.delete();
            copy(file1, file);
            FileReader filereader = new FileReader(file);
            BufferedReader bufferedreader = new BufferedReader(filereader);
            FileWriter filewriter = new FileWriter(CurrentFile);
            PrintWriter printwriter = new PrintWriter(filewriter);
            String s = null;
            read_to_applet(bufferedreader, printwriter);
            String s1;
            String s2;
            for(Enumeration enumeration = QuizDataTable.keys(); enumeration.hasMoreElements(); printwriter.println("<param name=\"" + s1 + "\" value=\"" + s2 + "\">"))
            {
                s1 = (String)enumeration.nextElement();
                s2 = (String)QuizDataTable.get(s1);
                s1 = escme(s1);
                s2 = escme(s2);
            }

            for(s = bufferedreader.readLine(); s != null;)
            {
                s = bufferedreader.readLine();
                if(s != null && end_app.search(s))
                    break;
            }

            for(; s != null; s = bufferedreader.readLine())
                printwriter.println(s);

            printwriter.close();
            bufferedreader.close();
            modified = false;
            System.out.println("Write complete.");
            return;
        }
        catch(Throwable throwable)
        {
            throwable.printStackTrace();
        }
    }

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

    String escme(String s)
    {
        StringBuffer stringbuffer = new StringBuffer();
        for(int i = 0; i < s.length(); i++)
            if(s.charAt(i) == '"')
                stringbuffer.append("_q");
            else
            if(s.charAt(i) == '_')
                stringbuffer.append("__");
            else
            if(s.charAt(i) == '<')
                stringbuffer.append("_l");
            else
            if(s.charAt(i) == '>')
                stringbuffer.append("_r");
            else
            if(s.charAt(i) == '&')
                stringbuffer.append("_a");
            else
            if(s.charAt(i) == '\\')
                stringbuffer.append("_b");
            else
                stringbuffer.append(s.charAt(i));

        return stringbuffer.toString();
    }

    void RunRegexOnTxtFields()
    {
        String s = InputPattern.getText();
        if(s == null)
            return;
        Regex regex = new Regex();
        try
        {
            regex.compile(s);
        }
        catch(RegSyntax regsyntax)
        {
            regsyntax.printStackTrace();
            return;
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
            return;
        }
        for(int i = 0; i < tg.length; i++)
        {
            tg[i].ctxt.clear();
            if(tg[i].txt.getText() != null)
                regex.search(tg[i].txt.getText());
            tg[i].ShowRes(regex);
        }

    }

    final Frame for_dialog = this;
    Label nlabel;
    TestGroup tg[];
    TextField InputPattern;
    GridBagLayout gb;
    GridBagConstraints gc;
    int quizno;
    Hashtable QuizDataTable;
    String CurrentFile;
    Menu GotoQuestionMenu;
    boolean modified;
    Regex begin_app;
    Regex end_app;
}
