package com.progdan.edmis.control.relation;

import java.sql.*;
import java.util.Vector;

import com.progdan.logengine.*;
import com.progdan.edmis.model.user.User;
import com.progdan.edmis.control.database.*;
import com.progdan.edmis.model.documents.DocumentGroup;

public class RelationReader {
    private static Logger logger = Logger.getLogger(RelationReader.class.
            getName());
    private User user;
    private Connection conn;
    private DatabaseController bd;
    public RelationReader(User user) {
        logger.debug(">>> Start of RelationReader.RelationReader()***");
        this.user = user;
        this.bd = user.getBd();
        this.conn = user.getConn();
        logger.debug("<<< End of RelationReader.RelationReader()***");
    }

    public Vector getRelations(String doc) {
        logger.debug(">>> Start of RelationReader.getRelations()***");
        Vector result = new Vector();
        int i;
        String name;
        DocumentGroup docGrp;
        try {
            String sql = "SELECT DocumentGroups.DocumentGroupID, DocumentGroupName FROM Relations NATURAL JOIN DocumentGroups WHERE DocumentID='" +
                         doc + "' ORDER BY DocumentGroupName";
            ResultSet rs = bd.executeQuery(conn, sql);
            while (rs.next()) {
                docGrp = new DocumentGroup();
                i = rs.getInt("DocumentGroupID");
                name = rs.getString("DocumentGroupName");
                docGrp.setId(i);
                docGrp.setName(name);
                if (name.endsWith(" Favorites")) {
                    if (name.compareTo(user.getLogin() + " Favorites") == 0) {
                        result.add(0, docGrp);
                    }
                } else {
                    result.add(docGrp);
                }
            }
        } catch (SQLException e) {
            logger.error(e);
        }
        logger.debug("<<< End of RelationReader.getRelations()***");
        return result;
    }
}
