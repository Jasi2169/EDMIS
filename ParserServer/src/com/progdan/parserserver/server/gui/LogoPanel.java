package com.progdan.parserserver.server.gui;

import java.awt.*;
import javax.swing.*;


public class LogoPanel extends JPanel {

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Get a Toolkit instance.
        Toolkit tk = Toolkit.getDefaultToolkit();

        // Provide a filename to the getImage() method.
        Image img = tk.getImage("programmer.gif");

        // Use the drawImage method.
        g.drawImage(img, 0, 0, this);
    }

}
