package com.progdan.folderimport.gui;

import java.io.*;

public class TextFilter extends javax.swing.filechooser.FileFilter {
    public boolean accept(File file) {
        String filename = file.getName();
        return filename.endsWith(".txt");
    }

    public String getDescription() {
        return "*.txt";
    }
}
