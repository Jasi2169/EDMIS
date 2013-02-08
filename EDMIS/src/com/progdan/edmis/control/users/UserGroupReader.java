package com.progdan.edmis.control.users;

import java.sql.*;

import com.progdan.logengine.*;
import com.progdan.edmis.control.database.*;
import com.progdan.edmis.model.user.User;

public class UserGroupReader {
    private static Logger logger = Logger.getLogger(UserGroupReader.class.
            getName());
    private User user;
    private DatabaseController bd;
    private Connection conn;
    public UserGroupReader(User user) {
        logger.debug(">>> Start of UserGroupReader.UserGroupReader()***");
        this.user = user;
        this.bd = user.getBd();
        this.conn = user.getConn();
        logger.debug("<<< End of UserGroupReader.UserGroupReader()***");
    }

    public int getUserGroup(String login) {
        logger.debug(">>> Start of UserGroupReader.getUserGroup()***");
        int id = 0;
        try {
            String sql =
                    "SELECT UserGroupID FROM UserGroups WHERE UserGroupName = '" +
                    login + "'";
            ResultSet rs = bd.executeQuery(conn, sql);
            while (rs.next()) {
                id = rs.getInt("UserGroupID");
            }
        } catch (SQLException e) {
            logger.error(e);
        }
        logger.debug("<<< End of UserGroupReader.getUserGroup()***");
        return id;
    }
}
