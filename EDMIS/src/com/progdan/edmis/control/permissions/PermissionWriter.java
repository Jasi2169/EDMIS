package com.progdan.edmis.control.permissions;

import java.sql.*;

import com.progdan.logengine.*;
import com.progdan.edmis.control.database.*;
import com.progdan.edmis.model.user.User;

public class PermissionWriter {
    private static Logger logger = Logger.getLogger(PermissionWriter.class.
            getName());
    private User user;
    private DatabaseController bd;
    private Connection conn;

    public PermissionWriter(User user) {
        logger.debug(">>> Start of PermissionWriter.PermissionWriter()***");
        this.user = user;
        this.bd = user.getBd();
        this.conn = user.getConn();
        logger.debug("<<< End of PermissionWriter.PermissionWriter()***");
    }

    public void addPermission(int usrGrp, int docGrp) {
        logger.debug(">>> Start of PermissionWriter.addPermission()***");
        String sql = "INSERT INTO Permissions (UserGroupID, DocumentGroupID, PermissionTypeID) VALUES(" +
                     usrGrp + "," + docGrp + ",2)";
        bd.executeUpdate(conn, sql);
        logger.debug("<<< End of PermissionWriter.addPermission()***");
    }

    public void removePermission(int usrGrp, int docGrp) {
        logger.debug(">>> Start of PermissionWriter.removePermission()***");
        if(usrGrp != 1){
            String login = "";
            try {
                String sql =
                        "SELECT DocumentGroupName FROM DocumentGroups WHERE DocumentGroupID=" +
                        docGrp;
                ResultSet rs = bd.executeQuery(conn, sql);
                while (rs.next()) {
                    login = rs.getString("DocumentGroupName");
                }
                if (login.compareTo(user.getLogin() + " Favorites") != 0) {
                    sql = "DELETE FROM Permissions WHERE UserGroupID=" + usrGrp +
                          " AND DocumentGroupID=" + docGrp;
                    bd.executeUpdate(conn, sql);
                }
            } catch (SQLException e) {
                logger.error(e);
            }
        }
        logger.debug("<<< End of PermissionWriter.removePermission()***");
    }
}
