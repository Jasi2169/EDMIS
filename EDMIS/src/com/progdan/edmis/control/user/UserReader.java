package com.progdan.edmis.control.user;

import java.sql.*;

import com.progdan.logengine.*;
import com.progdan.edmis.model.user.User;
import com.progdan.edmis.control.database.*;

public class UserReader {
    private static Logger logger = Logger.getLogger(UserReader.class.getName());
    private User user;
    public User readUser(String login) {
        logger.debug(">>> Start of UserReader.read()***");
        MySQLController bd = new MySQLController();
        Connection conn = bd.getConnection();
        ResultSet rs;
        String sql = "SELECT * FROM Users WHERE UserLogin='" + login + "'";
        rs = bd.executeQuery(conn, sql);
        try {
          if (rs.next()) {
            int id = rs.getInt("UserID");
            login = rs.getString("UserLogin");
            String name = rs.getString("UserName");
            String email = rs.getString("UserEmail");
            String password = rs.getString("UserPasswd");
            java.sql.Date lastlogin = rs.getDate("UserLastLogin");
            boolean active = rs.getBoolean("UserAccountActive");
            user = new User(login, password, email, name);
            user.setAccountActive(active);
            user.setId(id);
            if(lastlogin != null){
                user.setLastlogin(lastlogin.toString());
            }
          }
          conn.close();
        }
        catch (SQLException e) {
          logger.error(e);
        }
        logger.debug("<<< End of UserReader.read()***");
        return user;
    }
}
