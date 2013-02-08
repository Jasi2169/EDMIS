package com.progdan.edmis.control.documents;

import com.progdan.logengine.*;
import com.progdan.edmis.model.user.User;
import com.progdan.edmis.model.documents.DocumentGroup;

public class DocumentGroupUpdater {
    private static Logger logger = Logger.getLogger(DocumentGroupUpdater.class.
            getName());
    public void update(int id, String name, User user) {
        logger.debug(">>> Start of DocumentGroupUpdater.update()***");
        DocumentGroupReader read = new DocumentGroupReader(user);
        DocumentGroupWriter write = new DocumentGroupWriter(user);
        DocumentGroup docgrp = read.readDocumentGroup(id);
        DocumentGroupsByUserController docsbyuser = new
                DocumentGroupsByUserController(user);
        if (docsbyuser.isAdmin(id)) {
            if (docgrp.getName().compareTo(user.getLogin() + " Favorites") != 0) {
                docgrp.setName(name);
                write.update(docgrp);
            }
        }
        logger.debug("<<< End of DocumentGroupUpdater.update()***");
    }
}
