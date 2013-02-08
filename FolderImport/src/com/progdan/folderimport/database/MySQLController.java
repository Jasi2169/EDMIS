package com.progdan.folderimport.database;

import java.sql.*;
import java.io.*;
import java.util.Properties;

import com.progdan.logengine.*;

public class MySQLController extends DatabaseController {
    private static Logger logger = Logger.getLogger(MySQLController.class.
            getName());

    public Connection getConnection() {
        logger.debug(">>> Start of MySQLController.getConnection()***");
        Connection conn = null;
        try {
            Properties props = new Properties();
            props.load(getClass().getResourceAsStream("/" + "db.properties"));
            String dbserver = props.getProperty("dbserver", "localhost");
            String dbname = props.getProperty("dbname", "edmis");
            String user = props.getProperty("user", "root");
            String password = props.getProperty("password", "");
            conn = getConnection(dbserver, dbname, user, password);
        } catch (FileNotFoundException e) {
            logger.error(e);
        } catch (IOException e) {
            logger.error(e);
        }
        logger.debug("<<< End of MySQLController.getConnection()***");
        return conn;
    }

    public Connection getConnection(String dbserver, String dbname, String user,
                                    String passwd) {
        logger.debug(
                ">>> Start of MySQLController.getConnection()***");
        Connection conn = null;
        String cs = "jdbc:mysql://" + dbserver + "/" + dbname + "?autoReconnect=true";
        try {
            conn = DriverManager.getConnection(cs, user, passwd);
        } catch (SQLException e) {
            logger.error(e);
            if (e.toString().endsWith("\"Unknown database '" + dbname + "'\"")) {
                conn = createDatabase(dbserver, dbname);
                logger.debug("<<< End of MySQLController.getConnection()***");
                return conn;
            }
        }
        logger.debug(
                "<<< End of MySQLController.getConnection()***");
        return conn;
    }

    /**
     * Tests if MySQL Driver is installed
     */
    public void testDriver() {
        logger.debug(">>> Start of MySQLController.testDriver()***");
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (ClassNotFoundException e) {
            logger.error(e);
        } catch (InstantiationException e) {
            logger.error(e);
        } catch (IllegalAccessException e) {
            logger.error(e);
        }
        logger.debug("<<< End of MySQLController.testDriver()***");
    }

    public Connection createDatabase(String dbserver, String dbname) {
        logger.debug(
                ">>> Start of MySQLController.createDatabase()***");
        Connection conn = getConnection(dbserver, "test", "root", "");

        logger.info("Database " + dbname + " creation on server " + dbserver);
        String sql = "CREATE DATABASE IF NOT EXISTS " + dbname;
        executeUpdate(conn, sql);
        conn = getConnection();
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        String register = sdf.format(new java.util.Date());

        logger.info("Table DocumentDataTypes creation");
        sql = "CREATE TABLE IF NOT EXISTS DocumentDataTypes("
              +
              "DocumentDataTypeID INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,"
              + "DocumentDataTypeName TEXT"
              + ") TYPE=INNODB";
        executeUpdate(conn, sql);

        logger.info("Table DocumentDataType data");
        sql = "INSERT INTO DocumentDataTypes (DocumentDataTypeName) VALUES('Parsed')";
        executeUpdate(conn, sql);

        logger.info("Table DocumentGroups creation");
        sql = "CREATE TABLE IF NOT EXISTS DocumentGroups("
              +
              "DocumentGroupID INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,"
              + "DocumentGroupName TEXT,"
              + "DocumentGroupDate DATETIME,"
              + "DocumentGroupUpdate DATETIME"
              + ") TYPE=INNODB";
        executeUpdate(conn, sql);

        logger.info("Table DocumentGroups data");
        sql = "INSERT INTO DocumentGroups (DocumentGroupName, DocumentGroupDate, DocumentGroupUpdate) VALUES('Administrator Favorites','" +
              register + "','" + register + "')";
        executeUpdate(conn, sql);

        logger.info("Table SubDocumentGroups creation");
        sql = "CREATE TABLE IF NOT EXISTS SubDocumentGroups("
              + "DocumentGroupRoot INT UNSIGNED NOT NULL,"
              + "DocumentGroupLeaf INT UNSIGNED NOT NULL,"
              + "PRIMARY KEY (DocumentGroupRoot, DocumentGroupLeaf),"
              + "INDEX indDocumentGroupRoot(DocumentGroupRoot),"
              + "FOREIGN KEY (DocumentGroupRoot) REFERENCES DocumentGroups(DocumentGroupID) ON DELETE RESTRICT,"
              + "INDEX indDocumentGroupLeaf(DocumentGroupLeaf),"
              + "FOREIGN KEY (DocumentGroupLeaf) REFERENCES DocumentGroups(DocumentGroupID) ON DELETE RESTRICT"
              + ") TYPE=INNODB";
        executeUpdate(conn, sql);

        logger.info("Table Languages creation");
        sql = "CREATE TABLE IF NOT EXISTS Languages("
              + "LanguageID CHAR(2) NOT NULL PRIMARY KEY,"
              + "LanguageName TEXT NOT NULL"
              + ") TYPE=INNODB";
        executeUpdate(conn, sql);

        logger.info("Table Languages data");
        sql =
                "INSERT INTO Languages (LanguageID, LanguageName) VALUES('en', 'English')";
        executeUpdate(conn, sql);
        sql =
                "INSERT INTO Languages (LanguageID, LanguageName) VALUES('pt', 'Portuguese')";
        executeUpdate(conn, sql);

        logger.info("Table Users creation");
        sql = "CREATE TABLE IF NOT EXISTS Users("
              + "UserID INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,"
              + "UserLogin TEXT NOT NULL,"
              + "UserName TEXT,"
              + "UserEmail TEXT NOT NULL,"
              + "UserPasswd TEXT NOT NULL,"
              + "UserLastLogin DATETIME,"
              + "UserRegister DATETIME,"
              + "UserActive BOOL NOT NULL DEFAULT 0,"
              + "UserAccountActive BOOL NOT NULL DEFAULT 0"
              + ") TYPE=INNODB";
        executeUpdate(conn, sql);

        logger.info("Table Users data");
        //System Administrator: Administrator - edmisadmin
        sql = "INSERT INTO Users (UserLogin, UserName, UserEmail, UserPasswd, UserRegister, UserAccountActive)"
              + " VALUES('Administrator', 'System Administrator', 'root@localhost', 'an9BaEezMum8A','" +
              register + "',1)";
        executeUpdate(conn, sql);

        logger.info("Table Documents creation");
        sql = "CREATE TABLE IF NOT EXISTS Documents("
              + "DocumentID VARCHAR(128) NOT NULL PRIMARY KEY,"
              + "DocumentName TEXT,"
              + "DocumentSize BIGINT NOT NULL,"
              + "DocumentFormat CHAR(5) NOT NULL,"
              + "LanguageID CHAR(2) DEFAULT 'en',"
              + "DocumentDate DATETIME,"
              + "DocumentPages INT UNSIGNED NOT NULL DEFAULT 1,"
              + "INDEX indLanguageID(LanguageID),"
              +
              "FOREIGN KEY (LanguageID) REFERENCES Languages(LanguageID) ON DELETE RESTRICT"
              + ") TYPE=INNODB";
        executeUpdate(conn, sql);

        logger.info("Table DocumentData creation");
        sql = "CREATE TABLE IF NOT EXISTS DocumentData("
              +
              "DocumentDataID INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,"
              + "DocumentID VARCHAR(128) NOT NULL,"
              + "DocumentDataTypeID INT UNSIGNED NOT NULL,"
              + "DocumentData TEXT,"
              + "INDEX indDocumentID(DocumentID),"
              +
              "FOREIGN KEY (DocumentID) REFERENCES Documents(DocumentID) ON DELETE RESTRICT,"
              + "INDEX indDocumentDataTypeID(DocumentDataTypeID),"
              + "FOREIGN KEY (DocumentDataTypeID) REFERENCES DocumentDataTypes(DocumentDataTypeID) ON DELETE RESTRICT"
              + ") TYPE=INNODB";
        executeUpdate(conn, sql);

        logger.info("Table DocumentIndex creation");
        sql = "CREATE TABLE IF NOT EXISTS DocumentIndex("
              +
              "DocumentIndexID INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,"
              + "DocumentID VARCHAR(128) NOT NULL,"
              + "DocumentGroup TEXT,"
              + "INDEX indDocumentID(DocumentID),"
              + "FOREIGN KEY (DocumentID) REFERENCES Documents(DocumentID) ON DELETE RESTRICT"
              + ") TYPE=INNODB";
        executeUpdate(conn, sql);

        logger.info("Table Relations creation");
        sql = "CREATE TABLE IF NOT EXISTS Relations("
              + "DocumentID VARCHAR(128) NOT NULL,"
              + "DocumentGroupID INT UNSIGNED NOT NULL,"
              + "Relation_A DOUBLE UNSIGNED NOT NULL DEFAULT 75,"
              + "Relation_B DOUBLE UNSIGNED NOT NULL DEFAULT 50,"
              + "Relation_E_MIN DOUBLE UNSIGNED NOT NULL DEFAULT 50,"
              + "Relation_E_MAX DOUBLE UNSIGNED NOT NULL DEFAULT 100,"
              + "PRIMARY KEY (DocumentID, DocumentGroupID),"
              + "INDEX indDocumentID(DocumentID),"
              +
              "FOREIGN KEY (DocumentID) REFERENCES Documents(DocumentID) ON DELETE RESTRICT,"
              + "INDEX indDocumentGroupID(DocumentGroupID),"
              + "FOREIGN KEY (DocumentGroupID) REFERENCES DocumentGroups(DocumentGroupID) ON DELETE RESTRICT"
              + ") TYPE=INNODB";
        executeUpdate(conn, sql);

        logger.info("Table PermissionTypes creation");
        sql = "CREATE TABLE IF NOT EXISTS PermissionTypes("
              +
              "PermissionTypeID INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,"
              + "PermissionTypeName TEXT,"
              + "PermissionTypeStrength INT UNSIGNED NOT NULL"
              + ") TYPE=INNODB";
        executeUpdate(conn, sql);

        logger.info("Table PermissionTypes data");
        sql = "INSERT INTO PermissionTypes (PermissionTypeName, PermissionTypeStrength) VALUES('admin', 100)";
        executeUpdate(conn, sql);
        sql = "INSERT INTO PermissionTypes (PermissionTypeName, PermissionTypeStrength) VALUES('member', 50)";
        executeUpdate(conn, sql);
        sql = "INSERT INTO PermissionTypes (PermissionTypeName, PermissionTypeStrength) VALUES('guest', 10)";
        executeUpdate(conn, sql);

        logger.info("Table UserDataTypes creation");
        sql = "CREATE TABLE IF NOT EXISTS UserDataTypes("
              +
              "UserDataTypeID INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,"
              + "UserDataTypeName TEXT"
              + ") TYPE=INNODB";
        executeUpdate(conn, sql);

        logger.info("Table UserData creation");
        sql = "CREATE TABLE IF NOT EXISTS UserData("
              + "UserDataID INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,"
              + "UserID INT UNSIGNED NOT NULL,"
              + "UserDataTypeID INT UNSIGNED NOT NULL,"
              + "UserData TEXT,"
              + "INDEX indUserID(UserID),"
              +
              "FOREIGN KEY (UserID) REFERENCES Users(UserID) ON DELETE RESTRICT,"
              + "INDEX indUserDataTypeID(UserDataTypeID),"
              + "FOREIGN KEY (UserDataTypeID) REFERENCES UserDataTypes(UserDataTypeID) ON DELETE RESTRICT"
              + ") TYPE=INNODB";
        executeUpdate(conn, sql);

        logger.info("Table UserGroups creation");
        sql = "CREATE TABLE IF NOT EXISTS UserGroups("
              + "UserGroupID INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,"
              + "UserGroupName TEXT NOT NULL"
              + ") TYPE=INNODB";
        executeUpdate(conn, sql);

        logger.info("Table UserGroups data");
        sql =
                "INSERT INTO UserGroups (UserGroupName) VALUES('Administrator')";
        executeUpdate(conn, sql);

        logger.info("Table Permissions creation");
        sql = "CREATE TABLE IF NOT EXISTS Permissions("
              + "UserGroupID INT UNSIGNED NOT NULL,"
              + "DocumentGroupID INT UNSIGNED NOT NULL,"
              + "PermissionTypeID INT UNSIGNED NOT NULL,"
              + "PRIMARY KEY (UserGroupID, DocumentGroupID, PermissionTypeID),"
              + "INDEX indUserGroupID(UserGroupID),"
              + "FOREIGN KEY (UserGroupID) REFERENCES UserGroups(UserGroupID) ON DELETE CASCADE,"
              + "INDEX indDocumentGroupID(DocumentGroupID),"
              + "FOREIGN KEY (DocumentGroupID) REFERENCES DocumentGroups(DocumentGroupID) ON DELETE CASCADE,"
              + "INDEX indPermissionTypeID(PermissionTypeID),"
              + "FOREIGN KEY (PermissionTypeID) REFERENCES PermissionTypes(PermissionTypeID) ON DELETE CASCADE"
              + ") TYPE=INNODB";
        executeUpdate(conn, sql);

        logger.info("Table Permissions data");
        sql =
                "INSERT INTO Permissions (UserGroupID, DocumentGroupID, PermissionTypeID) VALUES(1,1,1)";
        executeUpdate(conn, sql);

        logger.info("Table Views creation");
        sql = "CREATE TABLE IF NOT EXISTS Views("
              + "UserID INT UNSIGNED NOT NULL,"
              + "UserGroupID INT UNSIGNED NOT NULL,"
              + "INDEX indUserID(UserID),"
              +
              "FOREIGN KEY (UserID) REFERENCES Users(UserID) ON DELETE RESTRICT,"
              + "INDEX indUserGroupID(UserGroupID),"
              + "FOREIGN KEY (UserGroupID) REFERENCES UserGroups(UserGroupID) ON DELETE RESTRICT"
              + ") TYPE=INNODB";
        executeUpdate(conn, sql);

        logger.info("Table Views data");
        sql = "INSERT INTO Views (UserID, UserGroupID) VALUES(1,1)";
        executeUpdate(conn, sql);

        logger.debug(
                "<<< End of MySQLController.createDatabase()***");
        return conn;
    }

}
