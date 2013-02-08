package com.progdan.parserserver.server.gui;

import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;

import javax.swing.*;
import java.io.*;

import com.borland.jbcl.layout.XYConstraints;
import com.borland.jbcl.layout.XYLayout;

import com.progdan.logengine.*;
import com.progdan.parserserver.server.IndexController;

public class DialogConfig extends JDialog {
    private static Logger logger = Logger.getLogger(DialogConfig.class.getName());
    JPanel panel1 = new JPanel();
    XYLayout xYLayout1 = new XYLayout();
    JPanel jPanelDatabase = new JPanel();
    XYLayout xYLayout2 = new XYLayout();
    JLabel jLabelDatabase = new JLabel();
    JLabel jLabelDBServer = new JLabel();
    JLabel jLabelDBName = new JLabel();
    JLabel jLabelDBUser = new JLabel();
    JLabel jLabelDBPasswd = new JLabel();
    JTextField jTextFieldDBServer = new JTextField();
    JTextField jTextFieldDBName = new JTextField();
    JTextField jTextFieldDBUser = new JTextField();
    JPanel jPanelParse = new JPanel();
    XYLayout xYLayout3 = new XYLayout();
    JLabel jLabelParser = new JLabel();
    JLabel jLabelPort = new JLabel();
    JTextField jTextFieldPort = new JTextField();
    JPanel jPanelPaths = new JPanel();
    JLabel jLabelPaths = new JLabel();
    XYLayout xYLayout4 = new XYLayout();
    JLabel jLabelRepPath = new JLabel();
    JLabel jLabelXCmdPath = new JLabel();
    JTextField jTextFieldRepPath = new JTextField();
    JTextField jTextFieldCmdPath = new JTextField();
    JButton jButtonRepPath = new JButton();
    JButton jButtonCmdPath = new JButton();
    JButton jButtonSave = new JButton();
    JButton jButtonClose = new JButton();
    JPasswordField jPasswordFieldDBPasswd = new JPasswordField();
    public DialogConfig(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
        try {
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            jbInit();
            pack();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public DialogConfig() {
        this(new Frame(),
             "ProgDan® Codename Avalon: Parser Server - Configuration", false);
    }

    private void jbInit() throws Exception {
        panel1.setLayout(xYLayout1);
        jPanelDatabase.setLayout(xYLayout2);
        jPanelDatabase.setBorder(BorderFactory.createEtchedBorder());
        jLabelDatabase.setFont(new java.awt.Font("Dialog", Font.BOLD, 11));
        jLabelDatabase.setText("ProgDan® Codename Avalon Database:");
        jLabelDBServer.setText("Server:");
        jLabelDBName.setText("Database Name:");
        jLabelDBUser.setText("Username:");
        jLabelDBPasswd.setText("Password:");
        jPanelParse.setBorder(BorderFactory.createEtchedBorder());
        jPanelParse.setLayout(xYLayout3);
        jLabelParser.setFont(new java.awt.Font("Dialog", Font.BOLD, 11));
        jLabelParser.setText("Parser Server:");
        jLabelPort.setText("Port:");
        jPanelPaths.setBorder(BorderFactory.createEtchedBorder());
        jPanelPaths.setLayout(xYLayout4);
        jLabelPaths.setFont(new java.awt.Font("Dialog", Font.BOLD, 11));
        jLabelPaths.setText("Working Paths:");
        jLabelRepPath.setText("Repository Path:");
        jLabelXCmdPath.setText("Command Path:");
        jButtonRepPath.setText("...");
        jButtonRepPath.addActionListener(new
                DialogConfig_jButtonRepPath_actionAdapter(this));
        jButtonCmdPath.setText("...");
        jButtonCmdPath.addActionListener(new
                DialogConfig_jButtonCmdPath_actionAdapter(this));
        jButtonSave.setText("Save");
        jButtonSave.addActionListener(new
                                      DialogConfig_jButtonSave_actionAdapter(this));
        jButtonClose.setText("Close");
        jButtonClose.addActionListener(new
                                       DialogConfig_jButtonClose_actionAdapter(this));
        getContentPane().add(panel1);
        jPanelDatabase.add(jLabelDBPasswd, new XYConstraints(6, 99, -1, -1));
        jPanelDatabase.add(jTextFieldDBServer,
                           new XYConstraints(91, 19, 146, -1));
        jPanelDatabase.add(jTextFieldDBName, new XYConstraints(91, 45, 146, -1));
        jPanelDatabase.add(jTextFieldDBUser, new XYConstraints(91, 70, 146, -1));
        jPanelDatabase.add(jLabelDBUser, new XYConstraints(6, 73, -1, -1));
        jPanelDatabase.add(jLabelDBName, new XYConstraints(6, 48, -1, -1));
        jPanelDatabase.add(jLabelDBServer, new XYConstraints(6, 22, -1, -1));
        jPanelParse.add(jTextFieldPort, new XYConstraints(43, 31, 60, -1));
        jPanelParse.add(jLabelPort, new XYConstraints(8, 34, -1, -1));
        jPanelParse.add(jLabelParser, new XYConstraints(15, 6, -1, -1));
        panel1.add(jPanelPaths, new XYConstraints(13, 9, 375, 83));
        panel1.add(jPanelDatabase, new XYConstraints(13, 104, 251, 129));
        panel1.add(jPanelParse, new XYConstraints(273, 103, 115, 61));
        jPanelPaths.add(jLabelPaths, new XYConstraints(142, 3, -1, -1));
        jPanelPaths.add(jButtonRepPath, new XYConstraints(319, 21, -1, -1));
        jPanelPaths.add(jButtonCmdPath, new XYConstraints(319, 51, -1, -1));
        jPanelPaths.add(jTextFieldRepPath, new XYConstraints(91, 22, 230, -1));
        jPanelPaths.add(jLabelRepPath, new XYConstraints(4, 25, -1, -1));
        jPanelPaths.add(jTextFieldCmdPath, new XYConstraints(91, 52, 230, -1));
        jPanelPaths.add(jLabelXCmdPath, new XYConstraints(3, 55, -1, -1));
        panel1.add(jButtonSave, new XYConstraints(290, 171, 80, -1));
        panel1.add(jButtonClose, new XYConstraints(290, 205, 80, -1));
        jPanelDatabase.add(jLabelDatabase, new XYConstraints(17, 0, -1, -1));
        jPanelDatabase.add(jPasswordFieldDBPasswd, new XYConstraints(91, 95, 146, -1));
        try {
            Properties props = new Properties();
            props.load(getClass().getResourceAsStream("/" + "db.properties"));
            jTextFieldRepPath.setText(props.getProperty("reppath", "C:\\EDMIS"));
            jTextFieldCmdPath.setText(props.getProperty("cmdpath", "C:\\Documents and Settings\\ProgDan\\My Documents\\EDMIS\\Source\\ParserServer\\external"));
            jTextFieldDBServer.setText(props.getProperty("dbserver",
                    "localhost"));
            jTextFieldDBName.setText(props.getProperty("dbname", "edmis"));
            jTextFieldDBUser.setText(props.getProperty("user", "root"));
            jPasswordFieldDBPasswd.setText(props.getProperty("password", ""));
            jTextFieldPort.setText(props.getProperty("parsePort", "4444"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            logger.error(e);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e);
        }

    }

    public void jButtonClose_actionPerformed(ActionEvent e) {
        this.dispose();
    }

    public void jButtonSave_actionPerformed(ActionEvent e) {
        logger.debug(
                ">>> Start of DialogConfig.jButtonSave_actionPerformed()***");
        Properties props = new Properties();
        props.setProperty("dbserver", jTextFieldDBServer.getText());
        props.setProperty("dbname", jTextFieldDBName.getText());
        props.setProperty("user", jTextFieldDBUser.getText());
        props.setProperty("password", new String(jPasswordFieldDBPasswd.getPassword()));
        props.setProperty("reppath", jTextFieldRepPath.getText());
        props.setProperty("cmdpath", jTextFieldCmdPath.getText());
        props.setProperty("parsePort", jTextFieldPort.getText());
        String file = getClass().getResource("/db.properties").getFile();
        file = file.replaceAll("%20", " ").substring(1);
        File config = new File(file);
        try {
            FileOutputStream out = new FileOutputStream(config);
            props.store(out,
                        "ProgDan® Codename Avalon: ParserServer - Configuration File");
            out.close();
        } catch (FileNotFoundException err) {
            logger.error(err);
        } catch (IOException err) {
            logger.error(err);
        }
        logger.debug("<<< End of DialogConfig.jButtonSave_actionPerformed()***");
    }

    public void jButtonRepPath_actionPerformed(ActionEvent e) {
        JFileChooser pathChooser = new JFileChooser();
        pathChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        pathChooser.showOpenDialog(this);
        File reppath = pathChooser.getSelectedFile();
        jTextFieldRepPath.setText(reppath.getAbsolutePath());
    }

    public void jButtonCmdPath_actionPerformed(ActionEvent e) {
        JFileChooser pathChooser = new JFileChooser();
        pathChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        pathChooser.showOpenDialog(this);
        File cmdpath = pathChooser.getSelectedFile();
        jTextFieldCmdPath.setText(cmdpath.getAbsolutePath());
    }
}


class DialogConfig_jButtonCmdPath_actionAdapter implements ActionListener {
    private DialogConfig adaptee;
    DialogConfig_jButtonCmdPath_actionAdapter(DialogConfig adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButtonCmdPath_actionPerformed(e);
    }
}


class DialogConfig_jButtonRepPath_actionAdapter implements ActionListener {
    private DialogConfig adaptee;
    DialogConfig_jButtonRepPath_actionAdapter(DialogConfig adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButtonRepPath_actionPerformed(e);
    }
}


class DialogConfig_jButtonSave_actionAdapter implements ActionListener {
    private DialogConfig adaptee;
    DialogConfig_jButtonSave_actionAdapter(DialogConfig adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButtonSave_actionPerformed(e);
    }
}


class DialogConfig_jButtonClose_actionAdapter implements ActionListener {
    private DialogConfig adaptee;
    DialogConfig_jButtonClose_actionAdapter(DialogConfig adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButtonClose_actionPerformed(e);
    }
}
