package com.progdan.edmis.control.database;

import java.sql.*;
import java.io.*;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.progdan.logengine.*;
import com.progdan.edmis.control.index.CreateIndex;

public class MySQLController extends DatabaseController {
    private static Logger logger = Logger.getLogger(MySQLController.class.
            getName());

    public Connection getConnection() {
        logger.debug(">>> Start of MySQLController.getConnection()***");
        Connection conn = null;
        try {
            /*
             * Create a JNDI Initial context to be able to
             *  lookup  the DataSource
             *
             * In production-level code, this should be cached as
             * an instance or static variable, as it can
             * be quite expensive to create a JNDI context.
             *
             * Note: This code only works when you are using servlets
             * or EJBs in a J2EE application server. If you are
             * using connection pooling in standalone Java code, you
             * will have to create/configure datasources using whatever
             * mechanisms your particular connection pooling library
             * provides.
             */

            InitialContext ctx = new InitialContext();

            /*
             * Lookup the DataSource, which will be backed by a pool
             * that the application server provides. DataSource instances
             * are also a good candidate for caching as an instance
             * variable, as JNDI lookups can be expensive as well.
             */

            DataSource ds = (DataSource) ctx.lookup(
                    "java:comp/env/jdbc/EDMIS");

            conn = ds.getConnection();

        } catch (NamingException e) {
            logger.error(e);
        }

        catch (SQLException e) {
            logger.error(e);
            if (e.toString().indexOf("Unknown database 'edmis'") != -1) {
                conn = createDatabase();
                logger.debug("<<< End of MySQLController.getConnection()***");
                return conn;
            }
        }
        logger.debug(
                "<<< End of MySQLController.getConnection()***");
        return conn;
    }

    public Connection createDatabase() {
        logger.debug(
                ">>> Start of MySQLController.createDatabase()***");
            Connection conn = null;
            String sql = null;
            try{
                InitialContext ctx = new InitialContext();
                DataSource ds = (DataSource) ctx.lookup(
                        "java:comp/env/jdbc/test");
                conn = ds.getConnection();
                logger.info("Database EDMIS creation on server");
                sql = "CREATE DATABASE IF NOT EXISTS EDMIS";
                executeUpdate(conn, sql);
            }catch(NamingException e){
                logger.error(e);
            }catch(SQLException e){
                logger.error(e);
            }
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
        sql =
                "INSERT INTO DocumentDataTypes (DocumentDataTypeName) VALUES('Parsed')";
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

        Properties props = new Properties();
        String reppath = null;
        try {
            props.load(getClass().getResourceAsStream("/" + "db.properties"));
            reppath = props.getProperty("reppath", "C:\\EDMIS");
        } catch (IOException e) {
            logger.error(e);
        }

        // Create indexdir for all Documents
        File indexdir = new File(reppath + System.getProperty("file.separator") +
                                 "index", "all");
        indexdir.mkdir();
        CreateIndex.create(indexdir.getAbsolutePath());

        // Create indexdir for Administrator
        indexdir = new File(reppath + System.getProperty("file.separator") +
                            "index", "1");
        indexdir.mkdirs();
        CreateIndex.create(indexdir.getAbsolutePath());

        // Create zipdir for store Documents
        indexdir = new File(reppath, "zip");
        indexdir.mkdirs();

        // Create bodydir for store parsed documents
        indexdir = new File(reppath, "body");
        indexdir.mkdirs();

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
              "FOREIGN KEY (DocumentID) REFERENCES Documents(DocumentID) ON DELETE CASCADE,"
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
              +
              "FOREIGN KEY (DocumentID) REFERENCES Documents(DocumentID) ON DELETE CASCADE"
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
              "FOREIGN KEY (DocumentID) REFERENCES Documents(DocumentID) ON DELETE CASCADE,"
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
