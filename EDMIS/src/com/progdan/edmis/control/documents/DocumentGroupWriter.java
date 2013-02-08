package com.progdan.edmis.control.documents;

import java.sql.*;
import java.util.Properties;
import java.io.*;

import com.progdan.logengine.*;
import com.progdan.edmis.control.index.CreateIndex;
import com.progdan.edmis.control.database.*;
import com.progdan.edmis.model.user.User;
import com.progdan.edmis.model.documents.DocumentGroup;

public class DocumentGroupWriter {
    private static Logger logger = Logger.getLogger(DocumentGroupWriter.class.
            getName());
    private User user;
    private DatabaseController bd;
    private Connection conn;

    public DocumentGroupWriter(User user) {
        logger.debug(">>> Start of DocumentGroupWriter.DocumentGroupWriter()***");
        this.user = user;
        this.bd = user.getBd();
        this.conn = user.getConn();
        logger.debug("<<< End of DocumentGroupWriter.DocumentGroupWriter()***");
    }

    public void delete(int id) {
        logger.debug(">>> Start of DocumentGroupWriter.delete()***");
        String name = "", sql;
        try {
            sql =
                    "SELECT DocumentGroupName FROM DocumentGroups WHERE DocumentGroupID=" +
                    id;
            ResultSet rs = bd.executeQuery(conn, sql);
            while (rs.next()) {
                name = rs.getString("DocumentGroupName");
            }
            if (name.compareTo(user.getLogin() + " Favorites") != 0) {
                logger.info("DocumentGroup '" + name + "' removed by " +
                            user.getLogin() + ".");
                sql = "DELETE FROM DocumentGroups WHERE DocumentGroupID=" + id;
                bd.executeUpdate(conn, sql);
            }
        } catch (SQLException e) {
            logger.error(e);
        }
        logger.debug("<<< End of DocumentGroupWriter.delete()***");
    }

    public void update(DocumentGroup docgrp) {
        logger.debug(">>> Start of DocumentGroupWriter.update()***");
        logger.info("DocumentGroup '" + docgrp.getName() + "' updated by " +
                    user.getLogin() + ".");
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        String update = sdf.format(new java.util.Date());
        String sql = "UPDATE DocumentGroups SET DocumentGroupName='" +
                     docgrp.getName() +
                     "', DocumentGroupUpdate='" + update +
                     "' WHERE DocumentGroupID=" + docgrp.getId();
        bd.executeUpdate(conn, sql);
        logger.debug("<<< End of DocumentGroupWriter.update()***");
    }

    public void writeNew(DocumentGroup docgrp) {
        logger.debug(">>> Start of DocumentGroupWriter.writeNew()***");
        ResultSet rs;
        String sql;
        int docGrpID = 0, usrGrpID = 0;
        try {
            logger.info("DocumentGroup '" + docgrp.getName() + "' created by " +
                        user.getLogin() + ".");
            sql = "INSERT INTO DocumentGroups(DocumentGroupName, DocumentGroupDate, DocumentGroupUpdate) VALUES('" +
                  docgrp.getName() + "','" + docgrp.getDate() + "','" +
                  docgrp.getLastupdate() + "')";
            bd.executeUpdate(conn, sql);

            sql =
                    "SELECT DocumentGroupID FROM DocumentGroups WHERE DocumentGroupName='" +
                    docgrp.getName() + "'";
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
            File indexdir = new File(reppath + System.getProperty("file.separator") + "index", dir);
            indexdir.mkdir();
            CreateIndex.create(indexdir.getAbsolutePath());


            sql = "SELECT UserGroupID FROM UserGroups WHERE UserGroupName='" +
                  user.getLogin() + "'";
            rs = bd.executeQuery(conn, sql);
            while (rs.next()) {
                usrGrpID = rs.getInt("UserGroupID");
            }

            sql = "INSERT INTO Permissions (UserGroupID, DocumentGroupID, PermissionTypeID) VALUES(" +
                  usrGrpID + "," + docGrpID + ",1)";
            bd.executeUpdate(conn, sql);

            sql = "INSERT INTO Permissions (UserGroupID, DocumentGroupID, PermissionTypeID) VALUES(1," +
                  docGrpID + ",1)";
            bd.executeUpdate(conn, sql);

        } catch (SQLException e) {
            logger.error(e);
        }
        logger.debug("<<< End of DocumentGroupWriter.writeNew()***");
    }
}
