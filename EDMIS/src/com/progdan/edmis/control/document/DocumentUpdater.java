package com.progdan.edmis.control.document;

import java.sql.*;
import javax.servlet.http.*;

import com.progdan.logengine.*;
import com.progdan.edmis.model.user.User;
import com.progdan.edmis.control.database.*;
import com.progdan.edmis.model.document.Document;

public class DocumentUpdater {
    private static Logger logger = Logger.getLogger(DocumentUpdater.class.
            getName());
    private User user;
    private Connection conn;
    private DatabaseController bd;
    public DocumentUpdater(User user) {
        logger.debug(">>> Start of DocumentUpdater.DocumentUpdater()***");
        this.user = user;
        this.bd = user.getBd();
        this.conn = user.getConn();
        logger.debug("<<< End of DocumentUpdater.DocumentUpdater()***");
    }

    public void update(HttpSession session, HttpServletRequest request) {
        logger.debug(">>> Start of DocumentUpdater.update()***");
        DocumentWriter write = new DocumentWriter(user);
        Document doc = (Document) session.getAttribute("Document");
//    doc.setId(request.getParameter("id"));
        doc.setName(request.getParameter("name"));
        doc.setLanguage(request.getParameter("language"));
        doc.setPages(Integer.parseInt(request.getParameter("pages")));
//    doc.setSize(Long.parseLong(request.getParameter("size")));
//    doc.setFormat(request.getParameter("format"));
//    doc.setOwer(Integer.parseInt(request.getParameter("ower")));
//    doc.setDate(request.getParameter("date"));
        write.updateDocument(doc);
        logger.debug("<<< End of DocumentUpdater.update()***");
    }
}
