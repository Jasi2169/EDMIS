package com.progdan.folderimport.document;

import java.sql.*;
import com.progdan.logengine.Logger;
import com.progdan.folderimport.database.*;

public class DocumentReader {
    private static Logger logger = Logger.getLogger(DocumentReader.class.
            getName());
    private Connection conn;
    private DatabaseController bd;
    public DocumentReader() {
        bd = new MySQLController();
        bd.testDriver();
        conn = bd.getConnection();
    }
    public boolean exists(String id) {
        logger.debug(">>> Start of DocumentReader.exists()***");
        boolean result = false;
        try {
            String sql = "SELECT * FROM Documents WHERE DocumentID='" + id +
                         "'";
            ResultSet rs = bd.executeQuery(conn, sql);
            result = rs.next();
        } catch (SQLException e) {
            logger.error(e);
        }
        logger.debug("<<< End of DocumentReader.exists()***");
        return result;
    }

}
