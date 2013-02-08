package com.progdan.edmis.control.index;

import java.sql.*;

import com.progdan.logengine.*;
import com.progdan.edmis.control.database.*;
import com.progdan.edmis.model.user.User;

public class ParserController {
    private static Logger logger = Logger.getLogger(ParserController.class.
            getName());
    private DatabaseController bd;
    private Connection conn;
    public ParserController(User user) {
        bd = user.getBd();
        conn = user.getConn();
    }

    public String fileParsed(String id) {
        logger.debug(">>> Start of IndexController.getFile()***");
        String result = null;
        try {
            String sql =
                    "SELECT DocumentData FROM DocumentData WHERE DocumentID='" +
                    id + "' AND DocumentDataTypeID=1";
            ResultSet rs = bd.executeQuery(conn, sql);
            logger.debug("<<< End of IndexController.getFile()***");
            if (rs.next()) {
                result = rs.getString("DocumentData");
            }
        } catch (SQLException e) {
            logger.error(e);
        }
        logger.debug("<<< End of IndexController.getFile()***");
        return result;
    }

    public void addTask(String indexPath, String id) {
        logger.debug(">>> Start of IndexController.addTask()***");
        String sql =
                "INSERT INTO DocumentIndex (DocumentID, DocumentGroup) VALUES('" +
                id + "','" + indexPath + "')";
        bd.executeUpdate(conn, sql);
        logger.debug("<<< End of IndexController.addTask()***");
    }
}
