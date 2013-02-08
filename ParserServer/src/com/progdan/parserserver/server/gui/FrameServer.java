package com.progdan.parserserver.server.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import java.util.*;
import java.io.*;

import javax.swing.*;
import com.borland.jbcl.layout.XYLayout;
import com.borland.jbcl.layout.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.progdan.parserserver.server.Server;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class FrameServer extends JFrame {
    Server server;
    JPanel contentPane;
    JLabel statusBar = new JLabel();
    XYLayout xYLayoutServer = new XYLayout();
    JPanel jPanelFiles = new JPanel();
    XYLayout xYLayout1 = new XYLayout();
    JLabel jLabelFiles = new JLabel();
    JLabel jLabelConverted = new JLabel();
    JProgressBar jProgressBarConvert = new JProgressBar();
    JLabel jLabelNoConvert = new JLabel();
    JProgressBar jProgressBarNoConvert = new JProgressBar();
    JButton jButtonStart = new JButton();
    JButton jButtonStop = new JButton();
    JButton jButtonExit = new JButton();
    JButton jButtonConfig = new JButton();
    JLabel jLabelActiveConnections = new JLabel();
    JTextField jTextFieldConnections = new JTextField();
    JProgressBar jProgressBarFiles = new JProgressBar();
    JButton jButtonText = new JButton();
    LogoPanel jPanelLogo = new LogoPanel();
    JLabel jLabelTitle = new JLabel();
    JLabel jLabelApplication = new JLabel();
    JLabel jLabelRelease = new JLabel();
    JLabel jLabelCopyright = new JLabel();
    public void connections(int n) {
        jTextFieldConnections.setText((new Integer(n)).toString());
    }

    public void files(int n, int p, int np) {
        jProgressBarFiles.setMaximum(n);
        jProgressBarConvert.setMaximum(n);
        jProgressBarNoConvert.setMaximum(n);
        jProgressBarFiles.setValue(n);
        jProgressBarConvert.setValue(p);
        jProgressBarNoConvert.setValue(np);
        jProgressBarFiles.setString((new Integer(n)).toString());
        jProgressBarConvert.setString((new Integer(p)).toString());
        jProgressBarNoConvert.setString((new Integer(np)).toString());
    }

    public FrameServer() {
        try {
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            jbInit();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Component initialization.
     *
     * @throws java.lang.Exception
     */
    private void jbInit() throws Exception {
        contentPane = (JPanel) getContentPane();
        contentPane.setLayout(xYLayoutServer);
        setSize(new Dimension(365, 327));
        setTitle("ProgDan® Codename Avalon: Parser Server v. 0.1");
        statusBar.setBorder(BorderFactory.createLoweredBevelBorder());
        statusBar.setText("Server stopped!");
        jPanelFiles.setBorder(BorderFactory.createEtchedBorder());
        jPanelFiles.setToolTipText("");
        jPanelFiles.setLayout(xYLayout1);
        jLabelFiles.setFont(new java.awt.Font("Dialog", Font.BOLD, 11));
        jLabelFiles.setText("Files received:");
        jLabelConverted.setText("Files converted:");
        jProgressBarConvert.setMaximum(0);
        jProgressBarConvert.setString("0");
        jProgressBarConvert.setStringPainted(true);
        jLabelNoConvert.setText("Files not converted:");
        jProgressBarNoConvert.setMaximum(0);
        jProgressBarNoConvert.setString("0");
        jProgressBarNoConvert.setStringPainted(true);
        jProgressBarNoConvert.addPropertyChangeListener(new
                FrameServer_jProgressBarNoConvert_propertyChangeAdapter(this));
        jButtonStart.setSelected(false);
        jButtonStart.setText("Start");
        jButtonStart.addActionListener(new
                                       FrameServer_jButtonStart_actionAdapter(this));
        jButtonStop.setEnabled(false);
        jButtonStop.setText("Stop");
        jButtonStop.addActionListener(new FrameServer_jButtonStop_actionAdapter(this));
        jButtonExit.setText("Exit");
        jButtonExit.addActionListener(new FrameServer_jButtonExit_actionAdapter(this));
        jButtonConfig.setText("Config");
        jButtonConfig.addActionListener(new
                                        FrameServer_jButtonConfig_actionAdapter(this));
        jLabelActiveConnections.setText("Active Connections:");
        jTextFieldConnections.setEditable(false);
        jTextFieldConnections.setText("0");
        jTextFieldConnections.setHorizontalAlignment(SwingConstants.CENTER);
        jProgressBarFiles.setMaximum(0);
        jProgressBarFiles.setString("0");
        jProgressBarFiles.setStringPainted(true);
        jButtonText.setEnabled(false);
        jButtonText.setText("...");
        jButtonText.addActionListener(new FrameServer_jButtonText_actionAdapter(this));
        jPanelLogo.setBorder(null);
        jPanelLogo.setMinimumSize(new Dimension(10, 10));
        jPanelLogo.setPreferredSize(new Dimension(10, 10));
        jPanelLogo.setLayout(null);
        jLabelTitle.setFont(new java.awt.Font("Dialog", Font.BOLD, 14));
        jLabelTitle.setText("ProgDan® Codename Avalon");
        jLabelApplication.setFont(new java.awt.Font("Dialog", Font.BOLD, 24));
        jLabelApplication.setForeground(Color.red);
        jLabelApplication.setText("Parser Server");
        jLabelRelease.setFont(new java.awt.Font("Dialog", Font.BOLD, 11));
        jLabelRelease.setForeground(Color.red);
        jLabelRelease.setText("Release 0.1 - Beta");
        jLabelCopyright.setText("Copyright 2004-2005 - ProgDan® Software");
        jPanelFiles.add(jLabelFiles, new XYConstraints(8, 5, -1, -1));
        jPanelFiles.add(jLabelNoConvert, new XYConstraints(8, 50, -1, -1));
        jPanelFiles.add(jLabelConverted, new XYConstraints(8, 28, -1, -1));
        jPanelFiles.add(jProgressBarFiles, new XYConstraints(108, 3, 160, -1));
        jPanelFiles.add(jProgressBarConvert, new XYConstraints(108, 26, 160, -1));
        jPanelFiles.add(jProgressBarNoConvert,
                        new XYConstraints(108, 48, 160, -1));
        jPanelFiles.add(jButtonText, new XYConstraints(279, 46, -1, -1));
        contentPane.add(jTextFieldConnections,
                        new XYConstraints(158, 118, 131, -1));
        contentPane.add(jLabelActiveConnections,
                        new XYConstraints(45, 121, -1, -1));
        contentPane.add(jButtonExit, new XYConstraints(277, 238, 65, -1));
        contentPane.add(jButtonStart, new XYConstraints(11, 238, 65, -1));
        contentPane.add(jButtonStop, new XYConstraints(100, 238, 65, -1));
        contentPane.add(jButtonConfig, new XYConstraints(188, 238, 65, -1));
        contentPane.add(statusBar, new XYConstraints(6, 272, 344, -1));
        contentPane.add(jPanelFiles, new XYConstraints(13, 148, 327, 77));
        contentPane.add(jPanelLogo, new XYConstraints(13, 15, 112, 96));
        contentPane.add(jLabelTitle, new XYConstraints(141, 14, -1, -1));
        contentPane.add(jLabelApplication, new XYConstraints(165, 40, -1, -1));
        contentPane.add(jLabelRelease, new XYConstraints(250, 66, -1, -1));
        contentPane.add(jLabelCopyright, new XYConstraints(137, 95, -1, -1));
    }

    public void jButtonExit_actionPerformed(ActionEvent e) {
        if (jButtonStop.isEnabled()) {
            jButtonStop_actionPerformed(e);
        }
        System.exit(0);
    }

    public void jButtonStart_actionPerformed(ActionEvent e) {
        GregorianCalendar now = new GregorianCalendar();
        if (now.before(new GregorianCalendar(2005, 12, 31))) {
            jButtonStop.setEnabled(true);
            server = new Server(this);
            server.start();
//        System.out.println(System.getProperty("sun.boot.class.path"));
            statusBar.setText("Server is running...");
            jButtonStart.setEnabled(false);
            jButtonConfig.setEnabled(false);
        } else {
            JOptionPane.showMessageDialog(this,
                                          "Evaluation License has expired or is obsolete;\nplease contact vendor for renewal.",
                                          "License Error",
                                          JOptionPane.ERROR_MESSAGE);

        }
    }

    public void jButtonStop_actionPerformed(ActionEvent e) {
        jButtonStart.setEnabled(true);
        server.interrupt();
        connections(0);
        statusBar.setText("Server stopped!");
        jButtonStop.setEnabled(false);
        jButtonConfig.setEnabled(false);
    }

    public void jButtonText_actionPerformed(ActionEvent e) {
        DialogText text = new DialogText();
        text.setSize(500, 300);
        // Center the window
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = text.getSize();
        if (frameSize.height > screenSize.height) {
            frameSize.height = screenSize.height;
        }
        if (frameSize.width > screenSize.width) {
            frameSize.width = screenSize.width;
        }
        text.setLocation((screenSize.width - frameSize.width) / 2,
                         (screenSize.height - frameSize.height) / 2);
        text.setModal(true);
        text.show();
        server.updateGUI();
    }

    public void jButtonConfig_actionPerformed(ActionEvent e) {
        DialogConfig config = new DialogConfig();
        config.setSize(410, 280);
        // Center the window
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = config.getSize();
        if (frameSize.height > screenSize.height) {
            frameSize.height = screenSize.height;
        }
        if (frameSize.width > screenSize.width) {
            frameSize.width = screenSize.width;
        }
        config.setLocation((screenSize.width - frameSize.width) / 2,
                           (screenSize.height - frameSize.height) / 2);
        config.setModal(true);
        config.show();
    }

    public void jProgressBarNoConvert_propertyChange(PropertyChangeEvent evt) {
        jButtonText.setEnabled(jProgressBarNoConvert.getValue() > 0);
    }
}


class FrameServer_jProgressBarNoConvert_propertyChangeAdapter implements
        PropertyChangeListener {
    private FrameServer adaptee;
    FrameServer_jProgressBarNoConvert_propertyChangeAdapter(FrameServer adaptee) {
        this.adaptee = adaptee;
    }

    public void propertyChange(PropertyChangeEvent evt) {
        adaptee.jProgressBarNoConvert_propertyChange(evt);
    }
}


class FrameServer_jButtonConfig_actionAdapter implements ActionListener {
    private FrameServer adaptee;
    FrameServer_jButtonConfig_actionAdapter(FrameServer adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButtonConfig_actionPerformed(e);
    }
}


class FrameServer_jButtonText_actionAdapter implements ActionListener {
    private FrameServer adaptee;
    FrameServer_jButtonText_actionAdapter(FrameServer adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButtonText_actionPerformed(e);
    }
}


class FrameServer_jButtonStop_actionAdapter implements ActionListener {
    private FrameServer adaptee;
    FrameServer_jButtonStop_actionAdapter(FrameServer adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButtonStop_actionPerformed(e);
    }
}


class FrameServer_jButtonStart_actionAdapter implements ActionListener {
    private FrameServer adaptee;
    FrameServer_jButtonStart_actionAdapter(FrameServer adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {

        adaptee.jButtonStart_actionPerformed(e);
    }
}


class FrameServer_jButtonExit_actionAdapter implements ActionListener {
    private FrameServer adaptee;
    FrameServer_jButtonExit_actionAdapter(FrameServer adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButtonExit_actionPerformed(e);
    }
}
