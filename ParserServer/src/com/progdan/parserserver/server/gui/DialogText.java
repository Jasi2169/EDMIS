package com.progdan.parserserver.server.gui;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.util.*;
import java.io.*;
import javax.swing.JDialog;
import javax.swing.JPanel;
import com.borland.jbcl.layout.XYLayout;
import com.borland.jbcl.layout.*;
import javax.swing.JButton;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JList;
import javax.swing.*;
import java.awt.Font;
import com.progdan.logengine.*;
import com.progdan.parserserver.server.IndexController;

public class DialogText extends JDialog {
    private static Logger logger = Logger.getLogger(DialogText.class.getName());
    private IndexController control;
    private String reppath;
    JPanel panel1 = new JPanel();
    XYLayout xYLayout1 = new XYLayout();
    JButton jButtonClose = new JButton();
    JLabel jLabelFiles = new JLabel();
    JLabel jLabelText = new JLabel();
    JScrollPane jScrollPaneFile = new JScrollPane();
    JScrollPane jScrollPaneText = new JScrollPane();
    JTextArea jTextAreaText = new JTextArea();
    DefaultListModel items = new DefaultListModel();
    JList jListFiles = new JList(items);
    JButton jButtonAssign = new JButton();
    public DialogText(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
        try {
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            jbInit();
            pack();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        control = new IndexController();
        Vector files = control.getNoParsedFiles();
        for (int i = 0; i < files.size(); i++) {
            items.addElement(files.get(i));
        }
        try {
            Properties props = new Properties();
            props.load(getClass().getResourceAsStream("/" + "db.properties"));
            reppath = props.getProperty("reppath", "C:\\EDMIS");
        } catch (FileNotFoundException e) {
            logger.error(e);
        } catch (IOException e) {
            logger.error(e);
        }

    }

    public DialogText() {
        this(new Frame(),
             "ProgDan® Codename Avalon: Parser Server - Manual File Parser", false);
    }

    private void jbInit() throws Exception {
        panel1.setLayout(xYLayout1);
        jButtonClose.setText("Close");
        jButtonClose.addActionListener(new
                                       DialogText_jButtonClose_actionAdapter(this));
        jLabelFiles.setText("Select the file:");
        jLabelText.setText("Place the Text:");
        jTextAreaText.setText("");
        jScrollPaneText.setBorder(BorderFactory.createEtchedBorder());
        jScrollPaneFile.setBorder(BorderFactory.createEtchedBorder());
        jButtonAssign.setText("Assign text to file");
        jButtonAssign.addActionListener(new
                                        DialogText_jButtonAssign_actionAdapter(this));
        jListFiles.setFont(new java.awt.Font("Courier New", Font.PLAIN, 11));
        getContentPane().add(panel1);
        jScrollPaneText.getViewport().add(jTextAreaText);
        jScrollPaneFile.getViewport().add(jListFiles);
        panel1.add(jLabelText, new XYConstraints(289, 14, -1, -1));
        panel1.add(jLabelFiles, new XYConstraints(21, 11, -1, -1));
        panel1.add(jButtonAssign, new XYConstraints(22, 231, -1, -1));
        panel1.add(jButtonClose, new XYConstraints(412, 231, -1, -1));
        panel1.add(jScrollPaneFile, new XYConstraints(21, 33, 252, 180));
        panel1.add(jScrollPaneText, new XYConstraints(289, 33, 183, 180));
    }

    public void jButtonClose_actionPerformed(ActionEvent e) {
        this.dispose();
    }

    public void jButtonAssign_actionPerformed(ActionEvent e) {
        if ((!jListFiles.isSelectionEmpty()) &&
            (jTextAreaText.getText().length() != 0)) {
            String id = (String) jListFiles.getSelectedValue();
            try {
                File file = new File(reppath +
                                     System.getProperty("file.separator") +
                                     "body", id + ".txt");
                FileWriter out = new FileWriter(file);
                PrintWriter print = new PrintWriter(out);
                print.println(jTextAreaText.getText());
                out.close();
            } catch (FileNotFoundException err) {
                logger.error(err);
            } catch (IOException err) {
                logger.error(err);
            }
            control.manualParseFile(id);
            jTextAreaText.setText("");
            items.clear();
            Vector files = control.getNoParsedFiles();
            for (int i = 0; i < files.size(); i++) {
                items.addElement(files.get(i));
            }
        }

    }
}


class DialogText_jButtonAssign_actionAdapter implements ActionListener {
    private DialogText adaptee;
    DialogText_jButtonAssign_actionAdapter(DialogText adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButtonAssign_actionPerformed(e);
    }
}


class DialogText_jButtonClose_actionAdapter implements ActionListener {
    private DialogText adaptee;
    DialogText_jButtonClose_actionAdapter(DialogText adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButtonClose_actionPerformed(e);
    }
}
