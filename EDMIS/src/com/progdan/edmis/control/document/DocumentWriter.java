package com.progdan.edmis.control.document;

import java.sql.*;
import java.util.Properties;
import java.io.*;

import com.progdan.logengine.*;
import com.progdan.edmis.control.index.IndexFiles;
import com.progdan.edmis.model.user.User;
import com.progdan.edmis.control.database.*;
import com.progdan.edmis.model.document.Document;
import com.progdan.edmis.control.relation.RelationControl;
import com.progdan.edmis.control.documents.DocumentGroupReader;

public class DocumentWriter {
    private static Logger logger = Logger.getLogger(DocumentWriter.class.
            getName());
    private User user;
    private Connection conn;
    private DatabaseController bd;

    public DocumentWriter(User user) {
        logger.debug(">>> Start of DocumentWriter.DocumentWriter()***");
        this.user = user;
        this.bd = user.getBd();
        this.conn = user.getConn();
        logger.debug("<<< End of DocumentWriter.DocumentWriter()***");
    }

    public void writeNew(Document doc) {
        logger.debug(">>> Start of DocumentWriter.writeNew()***");
        DocumentGroupReader read = new DocumentGroupReader(user);
        RelationControl relation = new RelationControl(user);
        String sql = "INSERT INTO Documents (DocumentID, DocumentName, DocumentSize, DocumentFormat, LanguageID, DocumentDate) VALUES('"
                     + doc.getId() + "','" + doc.getName() + "'," +
                     doc.getSize() +
                     ",'"
                     + doc.getFormat() + "','en','" + doc.getDate() + "')";

        bd.executeUpdate(conn, sql);
        relation.add(doc.getId(), "Administrator Favorites");
        if((user.getLogin() + " Favorites").compareTo("Administrator Favorites") != 0){
            relation.add(doc.getId(), user.getLogin() + " Favorites");
        }

        String docGrp = new Integer(read.exists(user.getLogin() + " Favorites")).toString();

        Properties props = new Properties();
        String reppath = null;
        try {
            props.load(getClass().getResourceAsStream("/" + "db.properties"));
            reppath = props.getProperty("reppath", "C:\\EDMIS");
        } catch (IOException e) {
            logger.error(e);
        }
        IndexFiles index = new IndexFiles(user);
        index.index("all", doc.getId(), doc.getFormat());

        logger.debug("<<< End of DocumentWriter.writeNew()***");
    }

    public void updateDocument(Document doc) {
        logger.debug(">>> Start of DocumentWriter.updateDocument()***");
        String sql = "UPDATE Documents SET DocumentName='" + doc.getName()
                     + "',LanguageID='" + doc.getLanguage() +
                     "',DocumentPages="
                     + doc.getPages() + "  WHERE DocumentID='" + doc.getId() +
                     "'";
        bd.executeUpdate(conn, sql);
        logger.debug("<<< End of DocumentWriter.updateDocument()***");
    }
}
