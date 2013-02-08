// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 2/12/2004 17:15:08
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   guigrep.java

package com.progdan.zipengine.apps;

import java.awt.Button;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Referenced classes of package com.progdan.zipengine.apps:
//            Message, guigrep

final class guigrep$2
    implements ActionListener
{
 Message m;
    public void actionPerformed(ActionEvent actionevent)
    {
        m = new Message();
        m.setTitle("About");
        m.addCentered("guigrep");
        m.addCentered("A file search utility");
        m.addCentered("by Steven R. Brandt");
        m.addCentered("Home page at");
        m.addCentered("http://www.win.net/~stevesoft/pat");
        Button button = new Button("OK");
        m.addButton(button);
        m.packNShow();
        button.addActionListener(new guigrep$3());
    }

    guigrep$2()
    {
    }

    // Unreferenced inner class COM/stevesoft/pat/apps/guigrep$3

/* anonymous class */

    final class guigrep$3
        implements ActionListener
    {

        public void actionPerformed(ActionEvent actionevent)
        {
            m.dispose();
        }

        guigrep$3()
        {
        }
}

}
