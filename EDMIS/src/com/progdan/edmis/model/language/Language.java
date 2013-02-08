package com.progdan.edmis.model.language;

import com.progdan.logengine.*;

public class Language {
    private static Logger logger = Logger.getLogger(Language.class.
            getName());
    private String id;
    private String name;
    public String getId() {
        logger.debug(">>> Start of Language.getId()***");
        logger.debug("<<< End of Language.getId()***");
        return id;
    }

    public void setId(String id) {
        logger.debug(">>> Start of Language.setId()***");
        this.id = id;
        logger.debug("<<< End of Language.setId()***");
    }

    public String getName() {
        logger.debug(">>> Start of Language.getName()***");
        logger.debug("<<< End of Language.getName()***");
        return name;
    }

    public void setName(String name) {
        logger.debug(">>> Start of Language.setName()***");
        this.name = name;
        logger.debug("<<< End of Language.setName()***");
    }
}
