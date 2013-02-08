package com.progdan.folderimport.document;

import java.sql.*;
import java.util.*;
import java.io.*;
import com.progdan.logengine.Logger;
import com.progdan.folderimport.database.*;
import com.progdan.folderimport.model.document.*;

import com.progdan.folderimport.index.*;

public class DocumentWriter {
    private static Logger logger = Logger.getLogger(DocumentWriter.class.
            getName());
    private Connection conn;
    private DatabaseController bd;
    public DocumentWriter() {
        bd = new MySQLController();
        bd.testDriver();
        conn = bd.getConnection();
    }


//    Original Function to Write a new Document...

        public void writeNew(Document doc) {
        logger.debug(">>> Start of DocumentWriter.writeNew()***");
        IndexFiles index = new IndexFiles();
        String sql = "INSERT INTO Documents (DocumentID, DocumentName, DocumentSize, DocumentFormat, LanguageID, DocumentDate, DocumentPages) VALUES('"
                     + doc.getId() + "','" + doc.getName() + "',"
                     + doc.getSize() + ",'"
                     + doc.getFormat() + "','" + doc.getLanguage() + "','" +
                     doc.getDate() + "'," + doc.getPages() + ")";
        bd.executeUpdate(conn, sql);

        //Administrator Favorites relation
        sql = "INSERT INTO Relations(DocumentID, DocumentGroupID) VALUES('" +
              doc.getId() + "',1)";
        bd.executeUpdate(conn, sql);
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        String lastUpdate = sdf.format(new java.util.Date());
        sql = "UPDATE DocumentGroups SET DocumentGroupUpdate='" + lastUpdate +
              "' WHERE DocumentGroupID=1";
        bd.executeUpdate(conn, sql);
        index.index("1", doc.getId(), doc.getFormat());

        //ProgDan Favorites relation
        sql = "INSERT INTO Relations(DocumentID, DocumentGroupID) VALUES('" +
              doc.getId() + "',2)";
        bd.executeUpdate(conn, sql);
        sql = "UPDATE DocumentGroups SET DocumentGroupUpdate='" + lastUpdate +
              "' WHERE DocumentGroupID=2";
        bd.executeUpdate(conn, sql);
        index.index("2", doc.getId(), doc.getFormat());

        index.index("all", doc.getId(), doc.getFormat());

        logger.debug("<<< End of DocumentWriter.writeNew()***");
    }


public void upadteCode(Document doc, String olddoc) {
    logger.debug(">>> Start of DocumentWriter.writeNew()***");
    String sql = "UPDATE Documents SET DocumentID='" + doc.getId() + "'  WHERE DocumentID='" + olddoc +
                     "'";

    bd.executeUpdate(conn, sql);

    logger.debug("<<< End of DocumentWriter.writeNew()***");
}

public void upadteName(String id, String name) {
    logger.debug(">>> Start of DocumentWriter.writeNew()***");
    String sql;
    int pages = 0;
    try{
        sql = "SELECT DocumentPages FROM Documents WHERE DocumentID='" + id +
              "'";
        ResultSet rs = bd.executeQuery(conn, sql);

        if (rs.next()) {
            pages = rs.getInt("DocumentPages");
        }

        if(pages == 0){
            sql = "UPDATE Documents SET DocumentName='" + name +
                  "'  WHERE DocumentID='" + id +
                  "'";
            bd.executeUpdate(conn, sql);
        }
    }catch(SQLException sqle){
        logger.error(sqle);
    }
    logger.debug("<<< End of DocumentWriter.writeNew()***");
}

}
