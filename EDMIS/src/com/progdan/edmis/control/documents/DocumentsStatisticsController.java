package com.progdan.edmis.control.documents;

import java.sql.*;
import java.util.*;
import java.text.*;

import com.progdan.logengine.*;
import com.progdan.edmis.model.user.User;
import com.progdan.edmis.control.database.*;
import com.progdan.edmis.model.document.Document;

public class DocumentsStatisticsController {
    private static Logger logger = Logger.getLogger(
            DocumentsStatisticsController.class.
            getName());
    public static DocumentsStatisticsController getInstance() {
        logger.debug(
                ">>> Start of DocumentsStatisticsController.getInstance()***");
        if (instance == null) {
            instance = new DocumentsStatisticsController();
        }
        logger.debug(
                "<<< End of DocumentsStatisticsController.getInstance()***");
        return instance;
    }

    /**
     * @link
     * @shapeType PatternLink
     * @pattern Singleton
     * @supplierRole Singleton factory
     */
    /*# private DocumentsStatisticsController _documentsStatisticsController; */
    private static DocumentsStatisticsController instance = null;

    public String getNumDocs(Locale loc, User user) {
        logger.debug(
                ">>> Start of DocumentsStatisticsController.getNumDocs()***");
        int n = 0;
        DatabaseController bd = user.getBd();
        Connection conn = user.getConn();
        NumberFormat nf = NumberFormat.getNumberInstance(loc);
        DecimalFormat df = (DecimalFormat) nf;
        df.applyPattern("###,###");
        String sql = "SELECT COUNT(*) FROM Documents";
        ResultSet rs = bd.executeQuery(conn, sql);
        try {
            while (rs.next()) {
                n = rs.getInt("COUNT(*)");
            }
        } catch (SQLException e) {
            logger.error(e);
        }
        logger.debug("<<< End of DocumentsStatisticsController.getNumDocs()***");
        return df.format(n);
    }

    public String getTotalSize(Locale loc, User user) {
        logger.debug(
                ">>> Start of DocumentsStatisticsController.getTotalSize()***");
        long size = 0;
        DatabaseController bd = user.getBd();
        Connection conn = user.getConn();
        NumberFormat nf = NumberFormat.getNumberInstance(loc);
        DecimalFormat df = (DecimalFormat) nf;
        df.applyPattern("###,###");
        String sql = "SELECT SUM(DocumentSize) FROM Documents";
        ResultSet rs = bd.executeQuery(conn, sql);
        try {
            while (rs.next()) {
                size = rs.getLong("SUM(DocumentSize)");
            }
        } catch (SQLException e) {
            logger.error(e);
        }

        logger.debug(
                "<<< End of DocumentsStatisticsController.getTotalSize()***");
        return df.format(size);
    }

    public Vector getNewDocs(User user) {
        logger.debug(">>> Start of NewUsersController.getNewUsers()***");
        DatabaseController bd = user.getBd();
        Connection conn = user.getConn();
        Vector newdocs = new Vector();
        Document doc;
        String sql = "SELECT DocumentID, DocumentName, DocumentFormat FROM Documents WHERE DocumentDate >= DATE_SUB(CURRENT_DATE(), INTERVAL 1 DAY) ORDER BY DocumentName";
        ResultSet rs = bd.executeQuery(conn, sql);
        try {
            while (rs.next()) {
                doc = new Document();
                doc.setId(rs.getString("DocumentID"));
                doc.setName(rs.getString("DocumentName"));
                doc.setFormat(rs.getString("DocumentFormat"));
                newdocs.add(doc);
            }
        } catch (SQLException e) {
            logger.error(e);
        }
        logger.debug("<<< End of NewUsersController.getNewUsers()***");
        return newdocs;
    }

    public Vector getNewDocsByGroup(int id, User user) {
        logger.debug(">>> Start of NewUsersController.getNewUsers()***");
        Vector newdocs = new Vector();
        Document doc;
        DatabaseController bd = user.getBd();
        Connection conn = user.getConn();
        String sql = "SELECT Documents.DocumentID, DocumentName, DocumentFormat FROM Relations NATURAL JOIN Documents WHERE  DocumentDate >= DATE_SUB(CURRENT_DATE(), INTERVAL 1 DAY) AND DocumentGroupID=" +
                     id + " ORDER BY DocumentName";
        ResultSet rs = bd.executeQuery(conn, sql);
        try {
            while (rs.next()) {
                doc = new Document();
                doc.setId(rs.getString("DocumentID"));
                doc.setName(rs.getString("DocumentName"));
                doc.setFormat(rs.getString("DocumentFormat"));
                newdocs.add(doc);
            }
        } catch (SQLException e) {
            logger.error(e);
        }
        logger.debug("<<< End of NewUsersController.getNewUsers()***");
        return newdocs;
    }
}
