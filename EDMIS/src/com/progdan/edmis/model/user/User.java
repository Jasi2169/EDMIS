package com.progdan.edmis.model.user;

import java.sql.*;

import com.progdan.logengine.*;
import com.progdan.edmis.control.database.*;

public class User {
    private String lastlogin;
    private int id;
    private String login;
    private String password;
    private String email;
    private static Logger logger = Logger.getLogger(User.class.getName());
    private static DatabaseController bd;
    private static Connection conn;

    public User() {
        logger.debug(">>> Start of User.User()***");
        this.bd = new MySQLController();
        this.conn = bd.getConnection();
        logger.debug("<<< End of User.User()***");
    }

    public User(String login, String passwd, String email, String name) {
        this();
        logger.debug(">>> Start of User.User()***");
        this.login = login;
        this.password = passwd;
        this.email = email;
        this.name = name;
        logger.debug("<<< End of User.User()***");
    }

    public String getLastlogin() {
        return lastlogin;
    }

    public void setLastlogin(String lastlogin) {
        this.lastlogin = lastlogin;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        logger.debug(">>> Start of User.getLogin()***");
        logger.debug("<<< End of User.getLogin()***");
        return login;
    }

    public void setLogin(String login) {
        logger.debug(">>> Start of User.setLogin()***");
        this.login = login;
        logger.debug(">>> Start of User.setLogin()***");
    }

    public static boolean exists(String name) {
        logger.debug(">>> Start of User.Exists()***");
        bd = new MySQLController();
        conn = bd.getConnection();
        String sql = "SELECT * FROM users WHERE UserLogin='" + name + "'";
        ResultSet rs = bd.executeQuery(conn, sql);
        try {
            if (rs.next()) {
                logger.debug("<<< End of User.exists()***");
                conn.close();
                return true;
            }
            conn.close();
        } catch (SQLException e) {
            logger.error(e);
        }
        logger.debug("<<< End of User.Exists()***");
        return false;
    }

    private String name;
    private boolean accountActive;

    public String getName() {
        logger.debug(">>> Start of User.getName()***");
        logger.debug("<<< End of User.getName()***");
        return name;
    }

    public void setName(String name) {
        logger.debug(">>> Start of User.setName()***");
        this.name = name;
        logger.debug(">>> Start of User.setName()***");
    }

    public String getPassword() {
        logger.debug(">>> Start of User.getPassword()***");
        logger.debug("<<< End of User.getPassword()***");
        return password;
    }

    public void setPassword(String password) {
        logger.debug(">>> Start of User.setPassword()***");
        this.password = password;
        logger.debug("<<< End of User.setPassword()***");
    }

    public String getEmail() {
        logger.debug(">>> Start of User.getEmail()***");
        logger.debug("<<< End of User.getEmail()***");
        return email;
    }

    public void setEmail(String email) {
        logger.debug(">>> Start of User.setEmail()***");
        this.email = email;
        logger.debug("<<< End of User.setEmail()***");
    }

    public Connection getConn() {
        logger.debug(">>> Start of User.getConn()***");
        logger.debug("<<< End of User.getConn()***");
        return conn;
    }

    public void setConn(Connection conn) {
        logger.debug(">>> Start of User.setConn()***");
        this.conn = conn;
        logger.debug("<<< End of User.setConn()***");
    }

    public boolean isAccountActive() {
        logger.debug(">>> Start of User.isAccountActive()***");
        logger.debug("<<< End of User.isAccountActive()***");
        return accountActive;
    }

    public void setAccountActive(boolean accountActive) {
        logger.debug(">>> Start of User.setAccountActive()***");
        this.accountActive = accountActive;
        logger.debug("<<< End of User.setAccountActive()***");
    }

    public DatabaseController getBd() {
        return bd;
    }

    public void setBd(DatabaseController bd) {
        this.bd = bd;
    }
}
