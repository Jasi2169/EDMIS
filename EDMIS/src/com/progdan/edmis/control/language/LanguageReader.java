package com.progdan.edmis.control.language;

import java.sql.*;
import java.util.Vector;

import com.progdan.logengine.*;
import com.progdan.edmis.model.user.User;
import com.progdan.edmis.control.database.*;
import com.progdan.edmis.model.language.Language;


public class LanguageReader {
    private static Logger logger = Logger.getLogger(LanguageReader.class.
            getName());
    private User user;
    private Connection conn;
    private DatabaseController bd;
    public LanguageReader(User user) {
        logger.debug(">>> Start of LanguageReader.LanguageReader()***");
        this.user = user;
        this.bd = user.getBd();
        this.conn = user.getConn();
        logger.debug("<<< End of LanguageReader.LanguageReader()***");
    }

    public Vector getLanguages(){
        logger.debug(">>> Start of LanguageReader.getLanguages()***");
        Vector result = new Vector();
        Language lang;
        try{
            String sql = "SELECT LanguageID,LanguageName FROM Languages ORDER BY LanguageName";
            ResultSet rs = bd.executeQuery(conn, sql);
            while(rs.next()){
                lang = new Language();
                lang.setId(rs.getString("LanguageID"));
                lang.setName(rs.getString("LanguageName"));
                result.add(lang);
            }
        }catch(SQLException e){
            logger.error(e);
        }
        logger.debug("<<< End of LanguageReader.getLanguages()***");
        return result;
    }
}
