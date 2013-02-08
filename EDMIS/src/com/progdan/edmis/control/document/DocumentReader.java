package com.progdan.edmis.control.document;

import java.sql.*;
import java.util.*;

import com.progdan.logengine.*;
import com.progdan.edmis.model.user.User;
import com.progdan.edmis.control.database.*;
import com.progdan.edmis.model.document.Document;

public class DocumentReader {
    private static Logger logger = Logger.getLogger(DocumentReader.class.
            getName());
    private Connection conn;
    private DatabaseController bd;
    public DocumentReader(User user) {
        logger.debug(">>> Start of DocumentReader.DocumentReader()***");
        this.bd = user.getBd();
        this.conn = user.getConn();
        logger.debug("<<< End of DocumentReader.DocumentReader()***");
    }

    public Document readDocument(String id) {
        logger.debug(">>> Start of DocumentReader.readDocument()***");
        Document doc = new Document();
        try {
            String sql = "SELECT * FROM Documents WHERE DocumentID='" + id +
                         "'";
            ResultSet rs = bd.executeQuery(conn, sql);
            if (rs.next()) {
                doc.setId(rs.getString("DocumentID"));
                doc.setName(rs.getString("DocumentName"));
                doc.setSize(rs.getLong("DocumentSize"));
                doc.setFormat(rs.getString("DocumentFormat"));
                doc.setLanguage(rs.getString("LanguageID"));
                doc.setDate(rs.getString("DocumentDate"));
                doc.setPages(rs.getInt("DocumentPages"));
            }
        } catch (SQLException e) {
            logger.error(e);
        }
        logger.debug("<<< End of DocumentReader.readDocument()***");
        return doc;
    }

    public boolean exists(String id) {
        logger.debug(">>> Start of DocumentReader.exists()***");
        boolean result = false;
        try {
            String sql = "SELECT * FROM Documents WHERE DocumentID='" + id +
                         "'";
            ResultSet rs = bd.executeQuery(conn, sql);
            result = rs.next();
        } catch (SQLException e) {
            logger.error(e);
        }
        logger.debug("<<< End of DocumentReader.exists()***");
        return result;
    }

    public Vector getAllDocuments() {
        logger.debug(">>> Start of DocumentReader.getAllDocuments()***");
        Vector alldocs = new Vector();
        Document doc;
        try {
            String sql = "SELECT DocumentID, DocumentName, DocumentFormat, LanguageID, DocumentSize, DocumentDate FROM Documents ORDER BY DocumentName";
            ResultSet rs = bd.executeQuery(conn, sql);
            while (rs.next()) {
                doc = new Document();
                doc.setId(rs.getString("DocumentID"));
                doc.setName(rs.getString("DocumentName"));
                doc.setFormat(rs.getString("DocumentFormat"));
                doc.setLanguage(rs.getString("LanguageID"));
                doc.setSize(rs.getLong("DocumentSize"));
                doc.setDate(rs.getString("DocumentDate"));
                alldocs.add(doc);
            }
        } catch (SQLException e) {
            logger.error(e);
        }
        logger.debug("<<< End of DocumentReader.getAllDocuments()***");
        return alldocs;
    }

    public Vector getAllDocumentsByGroup(int id) {
        logger.debug(">>> Start of DocumentReader.getAllDocuments()***");
        Vector alldocs = new Vector();
        Document doc;
        try {
            String sql = "SELECT Documents.DocumentID, DocumentName, DocumentFormat, LanguageID, DocumentSize, DocumentDate FROM Documents NATURAL JOIN Relations WHERE DocumentGroupID=" +
                         id + " ORDER BY DocumentName";
            ResultSet rs = bd.executeQuery(conn, sql);
            while (rs.next()) {
                doc = new Document();
                doc.setId(rs.getString("DocumentID"));
                doc.setName(rs.getString("DocumentName"));
                doc.setFormat(rs.getString("DocumentFormat"));
                doc.setLanguage(rs.getString("LanguageID"));
                doc.setSize(rs.getLong("DocumentSize"));
                doc.setDate(rs.getString("DocumentDate"));
                alldocs.add(doc);
            }
        } catch (SQLException e) {
            logger.error(e);
        }
        logger.debug("<<< End of DocumentReader.getAllDocuments()***");
        return alldocs;
    }

    public Vector searchDocuments(String search) {
        logger.debug(
                ">>> Start of DocumentReader.searchDocuments()***");
        Vector result = new Vector();
        int i;
        String name;
        String sql = "SELECT DocumentID, DocumentName, DocumentFormat, LanguageID FROM Documents WHERE DocumentName LIKE '%" +
                     search + "%' ORDER BY DocumentName";
        ResultSet rs = bd.executeQuery(conn, sql);
        try {
            while (rs.next()) {
                Document doc = new Document();
                doc.setId(rs.getString("DocumentID"));
                doc.setName(rs.getString("DocumentName"));
                doc.setFormat(rs.getString("DocumentFormat"));
                doc.setLanguage(rs.getString("LanguageID"));
                result.add(doc);
            }
        } catch (SQLException e) {
            logger.error(e);
        }
        logger.debug("<<< End of DocumentReader.searchDocuments()***");
        return result;
    }
}
