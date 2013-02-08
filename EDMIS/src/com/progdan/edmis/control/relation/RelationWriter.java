package com.progdan.edmis.control.relation;

import java.sql.*;
import java.util.Properties;
import java.io.IOException;

import com.progdan.logengine.*;
import com.progdan.edmis.model.user.User;
import com.progdan.edmis.model.relation.Relationship;
import com.progdan.edmis.control.database.*;
import com.progdan.edmis.control.documents.DocumentGroupsByUserController;
import com.progdan.edmis.control.document.DocumentReader;
import com.progdan.edmis.model.document.Document;
import com.progdan.edmis.control.index.IndexFiles;

public class RelationWriter {
    private static Logger logger = Logger.getLogger(RelationWriter.class.
            getName());
    private User user;
    private Connection conn;
    private DatabaseController bd;
    public RelationWriter(User user) {
        logger.debug(">>> Start of RelationWriter.RelationWriter()***");
        this.user = user;
        this.bd = user.getBd();
        this.conn = user.getConn();
        logger.debug("<<< End of RelationWriter.RelationWriter()***");
    }

    public void writeNew(Relationship rel) {
        logger.debug(">>> Start of RelationWriter.writeNew()***");
        String sql =
                "INSERT INTO Relations(DocumentID, DocumentGroupID) VALUES('" +
                rel.getDocumentId() + "'," + rel.getDocumentGroupId() + ")";

        bd.executeUpdate(conn, sql);
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        String lastUpdate = sdf.format(new java.util.Date());
        sql = "UPDATE DocumentGroups SET DocumentGroupUpdate='" + lastUpdate +
              "' WHERE DocumentGroupID=" + rel.getDocumentGroupId();
        bd.executeUpdate(conn, sql);

        Properties props = new Properties();
        String reppath = null;
        try {
            props.load(getClass().getResourceAsStream("/" + "db.properties"));
            reppath = props.getProperty("reppath", "C:\\EDMIS");
        } catch (IOException e) {
            logger.error(e);
        }

        IndexFiles index = new IndexFiles(user);
        String docGrp = new Integer(rel.getDocumentGroupId()).toString();
        DocumentReader read = new DocumentReader(user);
        Document doc = read.readDocument(rel.getDocumentId());
        index.index(docGrp, rel.getDocumentId(), doc.getFormat());

        logger.debug("<<< End of RelationWriter.writeNew()***");
    }

    public void remove(String doc, int docGrp) {
        logger.debug(">>> Start of RelationWriter.remove()***");
        DocumentGroupsByUserController docgrp = new
                                                DocumentGroupsByUserController(
                user);
        String sql;
        if (docgrp.isMember(docGrp)) {
            sql =
                    "DELETE FROM Relations WHERE DocumentID='" +
                    doc + "' AND DocumentGroupID=" + docGrp;
            bd.executeUpdate(conn, sql);
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss");
            String lastUpdate = sdf.format(new java.util.Date());
            sql = "UPDATE DocumentGroups SET DocumentGroupUpdate='" +
                  lastUpdate + "' WHERE DocumentGroupID=" + docGrp;
            bd.executeUpdate(conn, sql);
        }
        logger.debug("<<< End of RelationWriter.remove()***");
    }
}
