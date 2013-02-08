package com.progdan.parserserver.server;

import java.sql.*;
import java.util.*;

import com.progdan.logengine.*;
import com.progdan.parserserver.database.*;

public class IndexController {
    private static Logger logger = Logger.getLogger(IndexController.class.
            getName());
    private DatabaseController bd;
    private Connection conn;
    public IndexController() {
        logger.debug(">>> Start of IndexController.IndexController()***");
        bd = new MySQLController();
        bd.testDriver();
        conn = bd.getConnection();
        logger.debug("<<< End of IndexController.IndexController()***");
    }

    public String fileParsed(String id) {
        logger.debug(">>> Start of IndexController.getFile()***");
        String result = null;
        try {
            String sql =
                    "SELECT DocumentData FROM DocumentData WHERE DocumentID='" +
                    id + "' AND DocumentDataTypeID=1";
            ResultSet rs = bd.executeQuery(conn, sql);
            if (rs.next()) {
                result = rs.getString("DocumentData");
            }
        } catch (SQLException e) {
            logger.error(e);
        }
        logger.debug("<<< End of IndexController.getFile()***");
        return result;
    }

    public LinkedList getIndexTasks() {
        logger.debug(">>> Start of IndexController.getIndexTasks()***");
        Hashtable task;
        LinkedList tasks = new LinkedList();
        try {
            String sql = "SELECT Documents.DocumentID, DocumentGroup, DocumentFormat FROM DocumentIndex NATURAL JOIN Documents";
            ResultSet rs = bd.executeQuery(conn, sql);
            while (rs.next()) {
                task = new Hashtable();
                task.put("indexPath", rs.getString("DocumentGroup"));
                task.put("id", rs.getString("DocumentID"));
                task.put("format", rs.getString("DocumentFormat"));
                tasks.add(task);
            }
        } catch (SQLException e) {
            logger.error(e);
        }
        logger.debug("<<< End of IndexController.getIndexTasks()***");
        return tasks;
    }

    public void noParseFile(String id) {
        logger.debug(">>> Start of IndexController.addFile()***");
        try {
            String sql = "SELECT * FROM DocumentData WHERE DocumentID='" +
                         id + "'";
            ResultSet rs = bd.executeQuery(conn, sql);
            if (!rs.next()) {
                sql = "INSERT INTO DocumentData (DocumentID, DocumentDataTypeID, DocumentData) VALUES('" +
                      id + "',1,'false')";
                bd.executeUpdate(conn, sql);
            }
        } catch (SQLException e) {
            logger.error(e);
        }
        logger.debug("<<< End of IndexController.addFile()***");
    }

    public void parseFile(String id) {
        logger.debug(">>> Start of IndexController.parseFile()***");
        try {
            String sql = "SELECT * FROM DocumentData WHERE DocumentID='" +
                         id + "'";
            ResultSet rs = bd.executeQuery(conn, sql);
            if (!rs.next()) {
                sql = "INSERT INTO DocumentData (DocumentID, DocumentDataTypeID, DocumentData) VALUES('" +
                      id + "',1,'true')";
                bd.executeUpdate(conn, sql);
            }
        } catch (SQLException e) {
            logger.error(e);
        }
        logger.debug("<<< End of IndexController.parseFile()***");
    }

    public void manualParseFile(String id) {
        logger.debug(">>> Start of IndexController.manualParseFile()***");
        String sql = "UPDATE DocumentData SET DocumentData='true' WHERE DocumentDataTypeID=1 AND DocumentID='" +
                     id + "'";
        bd.executeUpdate(conn, sql);
        logger.debug("<<< End of IndexController.manualParseFile()***");
    }

    public void completeTask(Hashtable task) {
        logger.debug(">>> Start of IndexController.completeTask()***");
        String id = (String) task.get("id");
        String docGroup = (String) task.get("indexPath");
        String sql = "DELETE FROM DocumentIndex WHERE DocumentID='" +
                     id + "' AND DocumentGroup='" + docGroup + "'";
        bd.executeUpdate(conn, sql);
        logger.debug("<<< End of IndexController.completeTask()***");
    }

    public int countFiles() {
        logger.debug(
                ">>> Start of IndexController.countFiles()***");
        int n = 0;
        String sql = "SELECT COUNT(*) FROM Documents";
        ResultSet rs = bd.executeQuery(conn, sql);
        try {
            while (rs.next()) {
                n = rs.getInt("COUNT(*)");
            }
        } catch (SQLException e) {
            logger.error(e);
        }
        logger.debug("<<< End of IndexController.countFiles()***");
        return n;
    }

    public int countParsedFiles() {
        logger.debug(">>> Start of IndexController.countParsedFiles()***");
        int n = 0;
        String sql =
                "SELECT COUNT(*) FROM DocumentData WHERE DocumentDataTypeID=1 AND DocumentData='true'";
        ResultSet rs = bd.executeQuery(conn, sql);
        try {
            while (rs.next()) {
                n = rs.getInt("COUNT(*)");
            }
        } catch (SQLException e) {
            logger.error(e);
        }
        logger.debug("<<< End of IndexController.countParsedFiles()***");
        return n;
    }

    public int countNoParsedFiles() {
        logger.debug(">>> Start of IndexController.countParsedFiles()***");
        int n = 0;
        String sql =
                "SELECT COUNT(*) FROM DocumentData WHERE DocumentDataTypeID=1 AND DocumentData='false'";
        ResultSet rs = bd.executeQuery(conn, sql);
        try {
            while (rs.next()) {
                n = rs.getInt("COUNT(*)");
            }
        } catch (SQLException e) {
            logger.error(e);
        }
        logger.debug("<<< End of IndexController.countParsedFiles()***");
        return n;
    }

    public Vector getNoParsedFiles() {
        logger.debug(">>> Start of IndexController.getNoParsedFiles()***");
        Vector result = new Vector();
        String sql = "SELECT DocumentID FROM DocumentData DocumentData WHERE DocumentDataTypeID=1 AND DocumentData='false'";
        try {
            ResultSet rs = bd.executeQuery(conn, sql);
            while (rs.next()) {
                result.add(rs.getString("DocumentID"));
            }
        } catch (SQLException e) {
            logger.error(e);
        }
        logger.debug("<<< End of IndexController.getNoParsedFiles()***");
        return result;
    }
}
