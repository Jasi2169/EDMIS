package com.progdan.edmis.model.document;

import com.progdan.logengine.*;

public class Document {
    private static Logger logger = Logger.getLogger(Document.class.getName());
    private String id;
    private long size;
    private String name;
    private String date;
    private String format;
    private int pages;
    private String language;

    public String getId() {
        logger.debug(">>> Start of Document.getId()***");
        logger.debug("<<< End of Document.getId()***");
        return id;
    }

    public void setId(String id) {
        logger.debug(">>> Start of Document.setId()***");
        this.id = id;
        logger.debug("<<< End of Document.setId()***");
    }

    public long getSize() {
        logger.debug(">>> Start of Document.getSize()***");
        logger.debug("<<< End of Document.getSize()***");
        return size;
    }

    public void setSize(long size) {
        logger.debug(">>> Start of Document.setSize()***");
        this.size = size;
        logger.debug("<<< End of Document.setSize()***");
    }

    public String getName() {
        logger.debug(">>> Start of Document.getName()***");
        logger.debug("<<< End of Document.getName()***");
        return name;
    }

    public void setName(String name) {
        logger.debug(">>> Start of Document.setName()***");
        this.name = name;
        logger.debug("<<< End of Document.setName()***");
    }

    public String getDate() {
        logger.debug(">>> Start of Document.getDate()***");
        logger.debug("<<< End of Document.getDate()***");
        return date;
    }

    public void setDate(String date) {
        logger.debug(">>> Start of Document.setDate()***");
        this.date = date;
        logger.debug("<<< End of Document.setDate()***");
    }

    public String getFormat() {
        logger.debug(">>> Start of Document.getFormat()***");
        logger.debug("<<< End of Document.getFormat()***");
        return format;
    }

    public void setFormat(String format) {
        logger.debug(">>> Start of Document.setFormat()***");
        this.format = format;
        logger.debug("<<< End of Document.setFormat()***");
    }

    public int getPages() {
        logger.debug(">>> Start of Document.getPages()***");
        logger.debug("<<< End of Document.getPages()***");
        return pages;
    }

    public void setPages(int pages) {
        logger.debug(">>> Start of Document.setPages()***");
        this.pages = pages;
        logger.debug("<<< End of Document.setPages()***");
    }

    public String getLanguage(){ return language; }

    public void setLanguage(String language){ this.language = language; }
}
