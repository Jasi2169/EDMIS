package com.progdan.edmis.control.documents;

import java.util.*;
import java.sql.*;

import com.progdan.edmis.model.user.User;
import com.progdan.edmis.control.database.*;
import com.progdan.logengine.*;
import com.progdan.edmis.model.documents.DocumentGroup;

public class DocumentGroupsByUserController {
    private static Logger logger = Logger.getLogger(
            DocumentGroupsByUserController.class.getName());
    private User user;
    private Connection conn;
    private DatabaseController bd;
    public DocumentGroupsByUserController(User user) {
        logger.debug(
                ">>> Start of DocumentGroupsByUserController.DocumentGroupsByUserController()***");
        this.user = user;
        this.bd = user.getBd();
        this.conn = user.getConn();
        logger.debug(
                "<<< End of DocumentGroupsByUserController.DocumentGroupsByUserController()***");
    }

    public Vector getGroups() {
        logger.debug(
                ">>> Start of DocumentGroupsByUserController.getGroups()***");
        Vector result = new Vector();
        DocumentGroupReader read = new DocumentGroupReader(user);
        int i;
        String name;
        String sql = "SELECT DocumentGroups.DocumentGroupID, DocumentGroupName FROM DocumentGroups NATURAL JOIN Permissions NATURAL JOIN UserGroups NATURAL JOIN Views NATURAL JOIN Users WHERE Users.UserID=" +
                     user.getId() + " ORDER BY DocumentGroupName";
        ResultSet rs = bd.executeQuery(conn, sql);
        try {
            while (rs.next()) {
                i = rs.getInt("DocumentGroupID");
                name = rs.getString("DocumentGroupName");
                if (name.endsWith("Favorites")) {
                    result.add(0, read.readDocumentGroup(i));
                } else {
                    result.add(read.readDocumentGroup(i));
                }
            }
        } catch (SQLException e) {
            logger.error(e);
        }
        logger.debug("<<< End of DocumentGroupsByUserController.getGroups()***");
        return result;
    }

    public Vector getOtherGroups() {
        logger.debug(
                ">>> Start of DocumentGroupsByUserController.getOtherGroups()***");
        Vector result = new Vector();
        DocumentGroupReader read = new DocumentGroupReader(user);
        int i;
        String name;
        String sql = "SELECT DocumentGroupID, DocumentGroupName FROM DocumentGroups WHERE DocumentGroupID NOT IN(" +
                     "SELECT DocumentGroups.DocumentGroupID FROM DocumentGroups NATURAL JOIN Permissions NATURAL JOIN UserGroups NATURAL JOIN Views NATURAL JOIN Users WHERE Users.UserID=" +
                     user.getId() + ") ORDER BY DocumentGroupName";

        ResultSet rs = bd.executeQuery(conn, sql);
        try {
            while (rs.next()) {
                i = rs.getInt("DocumentGroupID");
                name = rs.getString("DocumentGroupName");
                if (!name.endsWith("Favorites")) {
                    result.add(read.readDocumentGroup(i));
                }
            }
        } catch (SQLException e) {
            logger.error(e);
        }
        logger.debug(
                "<<< End of DocumentGroupsByUserController.getOtherGroups()***");
        return result;
    }

    public Vector getGroups(String search) {
        logger.debug(
                ">>> Start of DocumentGroupsByUserController.getGroups()***");
        Vector result = new Vector();
        DocumentGroupReader read = new DocumentGroupReader(user);
        int i;
        String name;
        String sql = "SELECT DocumentGroups.DocumentGroupID, DocumentGroupName FROM DocumentGroups NATURAL JOIN Permissions NATURAL JOIN UserGroups NATURAL JOIN Views NATURAL JOIN Users WHERE Users.UserID=" +
                     user.getId() + " AND DocumentGroupName LIKE '%" + search + "%' ORDER BY DocumentGroupName";
        ResultSet rs = bd.executeQuery(conn, sql);
        try {
            while (rs.next()) {
                i = rs.getInt("DocumentGroupID");
                name = rs.getString("DocumentGroupName");
                if (name.endsWith("Favorites")) {
                    result.add(0, read.readDocumentGroup(i));
                } else {
                    result.add(read.readDocumentGroup(i));
                }
            }
        } catch (SQLException e) {
            logger.error(e);
        }
        logger.debug("<<< End of DocumentGroupsByUserController.getGroups()***");
        return result;
    }

    public Vector getOtherGroups(String search) {
        logger.debug(
                ">>> Start of DocumentGroupsByUserController.getOtherGroups()***");
        Vector result = new Vector();
        DocumentGroupReader read = new DocumentGroupReader(user);
        int i;
        String name;
        String sql = "SELECT DocumentGroupID, DocumentGroupName FROM DocumentGroups WHERE DocumentGroupID NOT IN(" +
                     "SELECT DocumentGroups.DocumentGroupID FROM DocumentGroups NATURAL JOIN Permissions NATURAL JOIN UserGroups NATURAL JOIN Views NATURAL JOIN Users WHERE Users.UserID=" +
                     user.getId() + ") AND DocumentGroupName LIKE '%" + search + "%' ORDER BY DocumentGroupName";

        ResultSet rs = bd.executeQuery(conn, sql);
        try {
            while (rs.next()) {
                i = rs.getInt("DocumentGroupID");
                name = rs.getString("DocumentGroupName");
                if (!name.endsWith("Favorites")) {
                    result.add(read.readDocumentGroup(i));
                }
            }
        } catch (SQLException e) {
            logger.error(e);
        }
        logger.debug(
                "<<< End of DocumentGroupsByUserController.getOtherGroups()***");
        return result;
    }

    public boolean isAdmin(int id) {
        logger.debug(">>> Start of DocumentGroupsByUserController.isAdmin()***");
        boolean admin = false;
        String sql = "SELECT UserID FROM Views NATURAL JOIN UserGroups NATURAL JOIN Permissions NATURAL JOIN PermissionTypes WHERE PermissionTypeStrength = 100 AND DocumentGroupID=" +
                     id;
        ResultSet rs = bd.executeQuery(conn, sql);
        try {
            while (rs.next()) {
                int userID = rs.getInt("UserID");
                if (userID == user.getId()) {
                    admin = true;
                }
            }
        } catch (SQLException e) {
            logger.error(e);
        }
        logger.debug("<<< End of DocumentGroupsByUserController.isAdmin()***");
        return admin;
    }

    public boolean isMember(int id) {
        logger.debug(">>> Start of DocumentGroupsByUserController.isAdmin()***");
        boolean admin = false;
        String sql = "SELECT UserID FROM Views NATURAL JOIN UserGroups NATURAL JOIN Permissions NATURAL JOIN PermissionTypes WHERE PermissionTypeStrength >= 50 AND DocumentGroupID=" +
                     id;
        ResultSet rs = bd.executeQuery(conn, sql);
        try {
            while (rs.next()) {
                int userID = rs.getInt("UserID");
                if (userID == user.getId()) {
                    admin = true;
                }
            }
        } catch (SQLException e) {
            logger.error(e);
        }
        logger.debug("<<< End of DocumentGroupsByUserController.isAdmin()***");
        return admin;
    }

    public Vector getAdmins(int id) {
        logger.debug(
                ">>> Start of DocumentGroupsByUserController.getAdmins()***");
        Vector admins = new Vector();
        String sql = "SELECT UserLogin FROM (SELECT * FROM DocumentGroups NATURAL JOIN Permissions NATURAL JOIN PermissionTypes WHERE PermissionTypeStrength = 100 AND DocumentGroups.DocumentGroupID=" +
                     id + ")UserGroups NATURAL JOIN Views NATURAL JOIN Users";
        ResultSet rs = bd.executeQuery(conn, sql);
        try {
            while (rs.next()) {
                admins.add(rs.getString("UserLogin"));
            }
        } catch (SQLException e) {
            logger.error(e);
        }
        logger.debug(
                "<<< End of DocumentGroupsByUserController.getAdmins()***");
        return admins;
    }
}
