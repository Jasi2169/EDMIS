package com.progdan.edmis.model.documents;

import java.util.*;

import com.progdan.logengine.*;

public class DocumentGroup {
    public int getId() {
        return id;
    }

    public void setId(String id){
        this.id = Integer.parseInt(id);
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLastupdate() {
        return lastupdate;
    }

    public void setLastupdate(String lastupdate) {
        this.lastupdate = lastupdate;
    }

    private static Logger logger = Logger.getLogger(DocumentGroup.class.getName());
    Vector admins;
    private int id;
    private String name;
    private String date;
    private String lastupdate;

    public DocumentGroup() {
        logger.debug(">>> Start of DocumentGroup.DocumentGroup()***");
        admins = new Vector();
        logger.debug("<<< End of DocumentGroup.DocumentGroup()***");
    }

    public DocumentGroup(String name) {
        this();
        logger.debug(">>> Start of DocumentGroup.DocumentGroup()***");
        this.name = name;
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        String date = sdf.format(new java.util.Date());
        this.date = date;
        this.lastupdate = date;
        logger.debug("<<< End of DocumentGroup.DocumentGroup()***");
    }
}
