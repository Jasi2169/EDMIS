package com.progdan.folderimport.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import java.io.*;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JFileChooser;
import com.borland.jbcl.layout.XYLayout;
import com.borland.jbcl.layout.*;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JProgressBar;


import com.progdan.checksum.Checksum;
import com.progdan.zipengine.*;

import com.progdan.folderimport.document.*;
import com.progdan.folderimport.model.document.*;
import com.progdan.logengine.Logger;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class FrameImport extends JFrame {
    private static Logger logger = Logger.getLogger(FrameImport.class.
           getName());
   JFileChooser ChooseImportList;
    JFileChooser ChooseSource;
    JFileChooser ChooseTarget;
    File ImportList;
    File Source;
    File Target;
    JPanel contentPane;
    XYLayout xYLayoutMain = new XYLayout();
    JLabel jLabelSource = new JLabel();
    JLabel jLabelTarget = new JLabel();
    JTextField jTextFieldSource = new JTextField();
    JTextField jTextFieldTarget = new JTextField();
    JButton jButtonSource = new JButton();
    JButton jButtonTarget = new JButton();
    JButton jButtonRun = new JButton();
    JButton jButtonExit = new JButton();
    JProgressBar jProgressBarImport = new JProgressBar();

    public FrameImport() {
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
        contentPane.setLayout(xYLayoutMain);
        setSize(new Dimension(400, 185));
        setTitle("Import Files from Directories");
        jLabelSource.setText("Directory to Import:");
        jLabelTarget.setText("Directory to Save:");
        jTextFieldSource.setText("");
        jTextFieldTarget.setText("");
        jButtonSource.setText("...");
        jButtonSource.addActionListener(new
                                        FrameImport_jButtonSource_actionAdapter(this));
        jButtonTarget.setText("...");
        jButtonTarget.addActionListener(new
                                        FrameImport_jButtonTarget_actionAdapter(this));
        jButtonRun.setText("Import");
        jButtonRun.addActionListener(new FrameImport_jButtonRun_actionAdapter(this));
        jButtonExit.setText("Exit");
        jButtonExit.addActionListener(new FrameImport_jButtonExit_actionAdapter(this));
        contentPane.add(jLabelSource, new XYConstraints(22, 19, -1, -1));
        contentPane.add(jTextFieldSource, new XYConstraints(121, 16, 200, -1));
        contentPane.add(jButtonSource, new XYConstraints(320, 15, -1, -1));
        contentPane.add(jTextFieldTarget, new XYConstraints(122, 50, 200, -1));
        contentPane.add(jLabelTarget, new XYConstraints(21, 53, -1, -1));
        contentPane.add(jButtonTarget, new XYConstraints(321, 49, -1, -1));
        contentPane.add(jProgressBarImport, new XYConstraints(20, 79, 343, -1));
        contentPane.add(jButtonExit, new XYConstraints(306, 114, -1, -1));
        contentPane.add(jButtonRun, new XYConstraints(47, 115, -1, -1));
    }

    public void jButtonExit_actionPerformed(ActionEvent e) {
        System.exit(0);
    }


    public void jButtonSource_actionPerformed(ActionEvent e) {
        ChooseSource = new JFileChooser();
        ChooseSource.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        ChooseSource.showOpenDialog(this);
        Source = ChooseSource.getSelectedFile();
        jTextFieldSource.setText(Source.getAbsolutePath());
    }

    public void jButtonTarget_actionPerformed(ActionEvent e) {
        ChooseTarget = new JFileChooser();
        ChooseTarget.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        ChooseTarget.showSaveDialog(this);
        Target = ChooseTarget.getSelectedFile();
        jTextFieldTarget.setText(Target.getAbsolutePath());
    }

    public void jButtonRun_actionPerformed(ActionEvent e) {
        DocumentReader read = new DocumentReader();
        DocumentWriter write = new DocumentWriter();
        Document doc;
        String checksum, name, format;
        long size;
        Checksum cs;
        File source, target;
        File imports[] = Source.listFiles();
        jProgressBarImport.setMinimum(0);
        jProgressBarImport.setMaximum(imports.length - 1);
        jProgressBarImport.setValue(0);
        this.update(this.getGraphics());
        try {
            for (int i = 0; i < imports.length; i++) {
                cs = new Checksum(new String[] {
                                  "-a", "sha512", imports[i].getAbsolutePath()
                });
                checksum = cs.getChecksum().substring(0, 128);
                name = imports[i].getName().substring(0,
                        imports[i].getName().lastIndexOf("."));
                name = name.replace('\'', '`');
                if (read.exists(checksum)) {
                    logger.info("File [" + checksum +"]: " + imports[i].getName() + " ("+ imports[i].length() + " bytes) already exists on the system");
                    write.upadteName(checksum, name);
                    imports[i].delete();
                } else {
                    doc = new Document();
                    doc.setId(checksum);
                    doc.setName(name);
                    size = imports[i].length();
                    doc.setSize(size);
                    format = imports[i].getName().substring(imports[i].getName().
                            lastIndexOf(".") + 1).toLowerCase();
                    doc.setFormat(format);
                    target = new File(Target.getAbsolutePath(),
                                      checksum + "." + format);
                    imports[i].renameTo(target);
                    ZipCreate.main(new String[] {Target.getAbsolutePath() +
                                   "\\" +
                                   checksum + ".zip", target.getAbsolutePath()});
                    source = new File(Target.getAbsolutePath(),
                                      checksum + ".zip");
                    target = new File(Target.getAbsolutePath() + "\\zip",
                                      checksum + ".zip");
                    source.renameTo(target);
                    write.writeNew(doc);
                }
                jProgressBarImport.setValue(i);
                this.update(this.getGraphics());
            }
        } catch (FileNotFoundException ex) {
            System.out.println(ex);
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
}


class FrameImport_jButtonRun_actionAdapter implements ActionListener {
    private FrameImport adaptee;
    FrameImport_jButtonRun_actionAdapter(FrameImport adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButtonRun_actionPerformed(e);
    }
}


class FrameImport_jButtonTarget_actionAdapter implements ActionListener {
    private FrameImport adaptee;
    FrameImport_jButtonTarget_actionAdapter(FrameImport adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButtonTarget_actionPerformed(e);
    }
}


class FrameImport_jButtonSource_actionAdapter implements ActionListener {
    private FrameImport adaptee;
    FrameImport_jButtonSource_actionAdapter(FrameImport adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButtonSource_actionPerformed(e);
    }
}


class FrameImport_jButtonExit_actionAdapter implements ActionListener {
    private FrameImport adaptee;
    FrameImport_jButtonExit_actionAdapter(FrameImport adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButtonExit_actionPerformed(e);
    }
}
