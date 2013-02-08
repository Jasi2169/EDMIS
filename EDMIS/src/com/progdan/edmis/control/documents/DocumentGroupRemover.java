package com.progdan.edmis.control.documents;

import com.progdan.logengine.*;
import com.progdan.edmis.model.user.User;

public class DocumentGroupRemover {
    private static Logger logger = Logger.getLogger(DocumentGroupRegister.class.
            getName());
    public void remove(int id, User user) {
        logger.debug(">>> Start of DocumentGroupRemover.remove()***");
        DocumentGroupReader read = new DocumentGroupReader(user);
        DocumentGroupWriter write = new DocumentGroupWriter(user);
        DocumentGroupsByUserController docsbyuser = new
                DocumentGroupsByUserController(user);
        if ((read.isEmpty(id)) && (docsbyuser.isAdmin(id))) {
            write.delete(id);
        }
        logger.debug("<<< End of DocumentGroupRemover.remove()***");
    }
}
