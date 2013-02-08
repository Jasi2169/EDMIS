package com.progdan.edmis.control.relation;

import java.util.Vector;

import com.progdan.logengine.*;
import com.progdan.edmis.control.documents.DocumentGroupReader;
import com.progdan.edmis.model.user.User;
import com.progdan.edmis.model.relation.Relationship;

public class RelationControl {
    private static Logger logger = Logger.getLogger(RelationControl.class.
            getName());
    private User user;

    public RelationControl(User user) {
        logger.debug(">>> Start of RelationControl.RelationControl()***");
        this.user = user;
        logger.debug("<<< End of RelationControl.RelationControl()***");
    }

    public void add(String doc, String group) {
        logger.debug(">>> Start of RelationControl.add()***");
        DocumentGroupReader read = new DocumentGroupReader(user);
        RelationWriter write = new RelationWriter(user);
        int docGrp = read.exists(group);
        Relationship rel = new Relationship(doc, docGrp);
        write.writeNew(rel);
        logger.debug("<<< End of RelationControl.add()***");
    }

    public void remove(String doc, int group) {
        logger.debug(">>> Start of RelationControl.remove()***");
        DocumentGroupReader read = new DocumentGroupReader(user);
        int favorites = read.exists(user.getLogin() + " Favorites");
        if(group != favorites){
            RelationWriter write = new RelationWriter(user);
            write.remove(doc, group);
        }
        logger.debug("<<< End of RelationControl.remove()***");
    }

    public Vector getRelations(String doc) {
        logger.debug(">>> Start of RelationControl.getRelations()***");
        RelationReader read = new RelationReader(user);
        Vector result = read.getRelations(doc);
        logger.debug("<<< End of RelationControl.getRelations()***");
        return result;
    }
}
