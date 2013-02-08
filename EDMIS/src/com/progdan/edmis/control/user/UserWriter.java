package com.progdan.edmis.control.user;

import java.sql.*;
import java.util.Properties;
import java.io.*;

import com.progdan.logengine.*;
import com.progdan.edmis.control.index.CreateIndex;
import com.progdan.edmis.model.user.User;
import com.progdan.edmis.control.database.*;

public class UserWriter {
    private static Logger logger = Logger.getLogger(User.class.getName());
    private static DatabaseController bd;
    private static Connection conn;
    public void writeNew(User user) {
        logger.debug(">>> Start of UserWriter.write()***");
        bd = user.getBd();
        conn = user.getConn();
        int userID = 0, userGrpID = 0, docGrpID = 0;
        ResultSet rs;
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        String register = sdf.format(new java.util.Date());
        try {
            logger.info("User " + user.getLogin() + " register");
            String sql =
                    "INSERT INTO Users(UserLogin, UserPasswd, UserEmail, UserName, UserRegister, UserAccountActive) VALUES('"
                    + user.getLogin() + "','" + user.getPassword() + "','" +
                    user.getEmail()
                    + "','" + user.getName() + "','" + register + "',"
                    + (user.isAccountActive() ? 1 : 0) + ")";
            bd.executeUpdate(conn, sql);

            sql = "SELECT UserID FROM Users WHERE UserLogin='" + user.getLogin() +
                  "'";
            rs = bd.executeQuery(conn, sql);
            while (rs.next()) {
                userID = rs.getInt("UserID");
            }

            logger.info("DocumentGroup " + user.getLogin() +
                        " Favorites register");
            sql = "INSERT INTO DocumentGroups (DocumentGroupName, DocumentGroupDate, DocumentGroupUpdate) VALUES('" +
                  user.getLogin() + " Favorites','" +
                  register + "','" + register + "')";
            bd.executeUpdate(conn, sql);

            sql =
                    "SELECT DocumentGroupID FROM DocumentGroups WHERE DocumentGroupName='" +
                    user.getLogin() + " Favorites'";
            rs = bd.executeQuery(conn, sql);
            while (rs.next()) {
                docGrpID = rs.getInt("DocumentGroupID");
            }

            Properties props = new Properties();
            String reppath = null;
            try {
                props.load(getClass().getResourceAsStream("/" + "db.properties"));
                reppath = props.getProperty("reppath", "C:\\EDMIS");
            } catch (IOException e) {
                logger.error(e);
            }
            String dir = new Integer(docGrpID).toString();
            File indexdir = new File(reppath +
                                     System.getProperty("file.separator") +
                                     "index", dir);
            indexdir.mkdir();
            CreateIndex.create(indexdir.getAbsolutePath());

            logger.info("UserGroup " + user.getLogin() + " register");
            sql =
                    "INSERT INTO UserGroups (UserGroupName) VALUES('" +
                    user.getLogin() + "')";
            bd.executeUpdate(conn, sql);

            sql = "SELECT UserGroupID FROM UserGroups WHERE UserGroupName='" +
                  user.getLogin() + "'";
            rs = bd.executeQuery(conn, sql);
            while (rs.next()) {
                userGrpID = rs.getInt("UserGroupID");
            }

            logger.info("Table Permissions register");
            sql =
                    "INSERT INTO Permissions (UserGroupID, DocumentGroupID, PermissionTypeID) VALUES(" +
                    userGrpID + "," + docGrpID + ",1)";
            bd.executeUpdate(conn, sql);

            logger.info("Table Views register");
            sql = "INSERT INTO Views (UserID, UserGroupID) VALUES(" + userID +
                  "," + userGrpID + ")";
            bd.executeUpdate(conn, sql);
        } catch (SQLException e) {
            logger.error(e);
        }

        logger.debug("<<< End of UserWriter.write()***");
    }

    public static void activate(String login) {
        logger.debug(">>> Start of UserWriter.activate()***");
        MySQLController bd = new MySQLController();
        String sql = "UPDATE Users SET UserAccountActive='" + 1 +
                     "' WHERE UserLogin='" + login + "'";
        bd.executeUpdate(bd.getConnection(), sql);
        logger.debug("<<< End of UserWriter.activate()***");
    }

    public void login(User user) {
        logger.debug(">>> Start of UserWriter.login()***");
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        String lastLogin = sdf.format(new java.util.Date());
        user.setLastlogin(lastLogin);
        String sql = "UPDATE Users SET UserLastLogin='" + lastLogin +
                     "', UserActive=" + 1 + " WHERE UserID=" + user.getId();
        user.getBd().executeUpdate(user.getConn(), sql);
        logger.debug("<<< End of UserWriter.login()***");
    }

    public void logout(User user) {
        logger.debug(">>> Start of UserWriter.logout()***");
        try {
            Connection conn;
            String sql = "UPDATE Users SET UserActive=" + 0 + " WHERE UserID=" +
                         user.getId();
            conn = user.getBd().getConnection();
            user.getBd().executeUpdate(conn, sql);
            conn.close();
            conn = user.getConn();
            conn.close();
        } catch (SQLException e) {
            logger.error(e);
        }
        logger.info("User " + user.getLogin() + " exited of the system.");
        logger.debug("<<< End of UserWriter.logout()***");
    }

    public void updatePassword(User user) {
        logger.debug(">>> Start of UserWriter.logout()***");
        String sql = "UPDATE Users SET UserPasswd='" + user.getPassword() +
                     "' WHERE UserID=" +
                     user.getId();
        user.getBd().executeUpdate(user.getConn(), sql);
        logger.debug("<<< End of UserWriter.logout()***");
    }
}
