package com.progdan.edmis.control.documents;

import com.progdan.logengine.*;
import com.progdan.edmis.model.documents.DocumentGroup;
import com.progdan.edmis.model.user.User;
import com.progdan.edmis.control.permissions.PermissionWriter;

public class DocumentGroupRegister {
    private static Logger logger = Logger.getLogger(DocumentGroupRegister.class.
            getName());
    public void register(String name, User user) {
        logger.debug(">>> Start of DocumentGroupRegister.register()***");
        DocumentGroupReader read = new DocumentGroupReader(user);
        int docGrp = read.exists(name);
        if (docGrp == 0) {
            DocumentGroup docgrp = new DocumentGroup(name);
            DocumentGroupWriter writer = new DocumentGroupWriter(user);
            writer.writeNew(docgrp);
        } else {
            int usrGrp = read.exists(user.getLogin() + " Favorites");
            PermissionWriter perm = new PermissionWriter(user);
            perm.addPermission(usrGrp, docGrp);
        }
        logger.debug("<<< End of DocumentGroupRegister.register()***");
    }
}
