package com.progdan.edmis.control.documents;

import java.sql.*;

import com.progdan.logengine.*;
import com.progdan.edmis.control.database.DatabaseController;
import com.progdan.edmis.model.documents.DocumentGroup;
import com.progdan.edmis.model.user.User;

public class DocumentGroupReader {
    private static Logger logger = Logger.getLogger(DocumentGroupReader.class.
            getName());
    User user;
    DatabaseController bd;
    Connection conn;
    public DocumentGroupReader(User user) {
        logger.debug(">>> Start of DocumentGroupReader.DocumentGroupReader()***");
        this.user = user;
        this.bd = user.getBd();
        this.conn = user.getConn();
        logger.debug("<<< End of DocumentGroupReader.DocumentGroupReader()***");
    }

    public boolean isEmpty(int id) {
        logger.debug(">>> Start of DocumentGroupReader.isEmpty()***");
        boolean empty = true;
        try {
            String sql = "SELECT * FROM Relations WHERE DocumentGroupID=" + id;
            ResultSet rs = bd.executeQuery(conn, sql);
            while (rs.next()) {
                empty = false;
            }
            sql = "SELECT * FROM SubDocumentGroups WHERE DocumentGroupRoot=" +
                  id + " OR DocumentGroupLeaf=" + id;
            rs = bd.executeQuery(conn, sql);
            while (rs.next()) {
                empty = false;
            }

        } catch (SQLException e) {
            logger.error(e);
        }
        logger.debug("<<< End of DocumentGroupReader.isEmpty()***");
        return empty;
    }

    public int exists(String name) {
        logger.debug(">>> Start of DocumentGroupReader.exists()***");
        int id = 0;
        try {
            String sql =
                    "Select DocumentGroupID FROM DocumentGroups WHERE DocumentGroupName='" +
                    name + "'";
            ResultSet rs = bd.executeQuery(conn, sql);
            while (rs.next()) {
                id = rs.getInt("DocumentGroupID");
            }
        } catch (SQLException e) {
            logger.error(e);
        }
        logger.debug("<<< End of DocumentGroupReader.exists()***");
        return id;
    }

    public DocumentGroup readDocumentGroup(int id) {
        logger.debug(">>> Start of DocumentGroupReader.readDocumentGroup()***");
        DocumentGroup doc = new DocumentGroup();
        doc.setId(id);
        String sql = "SELECT DocumentGroups.* FROM DocumentGroups NATURAL JOIN Permissions NATURAL JOIN PermissionTypes NATURAL JOIN UserGroups NATURAL JOIN Views NATURAL JOIN Users WHERE Users.UserID = " +
                     user.getId() +
                     " AND PermissionTypeStrength > 0 AND DocumentGroups.DocumentGroupID=" +
                     id;
        ResultSet rs = bd.executeQuery(conn, sql);
        try {
            while (rs.next()) {
                String name = rs.getString("DocumentGroupName");
                doc.setName(name);
                java.sql.Date date = rs.getDate("DocumentGroupDate");
                if (date != null) {
                    doc.setDate(date.toString());
                }
                java.sql.Date update = rs.getDate("DocumentGroupUpdate");
                if (update != null) {
                    doc.setLastupdate(update.toString());
                }
            }
        } catch (SQLException e) {
            logger.error(e);
        }
        logger.debug("<<< End of DocumentGroupReader.readDocumentGroup()***");
        return doc;
    }
}
