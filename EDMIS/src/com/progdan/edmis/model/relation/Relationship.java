package com.progdan.edmis.model.relation;

import com.progdan.logengine.*;

public class Relationship {
    private static Logger logger = Logger.getLogger(Relationship.class.getName());
    private String documentId;
    private int documentGroupId;
    private int relationA;
    private int relationB;
    private int relationEMin;
    private int relationEMax;

    public Relationship() {
        logger.debug(">>> Start of Relationship.Relationship()***");
        this.relationA = 75;
        this.relationB = 50;
        this.relationEMin = 50;
        this.relationEMax = 100;
        logger.debug("<<< End of Relationship.Relationship()***");
    }

    public Relationship(String documentId, int documentGroupId) {
        this();
        logger.debug(">>> Start of Relationship.Relationship()***");
        this.documentId = documentId;
        this.documentGroupId = documentGroupId;
        logger.debug("<<< End of Relationship.Relationship()***");
    }

    public String getDocumentId() {
        logger.debug(">>> Start of Relationship.getDocumentId()***");
        logger.debug("<<< End of Relationship.getDocumentId()***");
        return documentId;
    }

    public void setDocumentId(String documentId) {
        logger.debug(">>> Start of Relationship.setDocumentId()***");
        this.documentId = documentId;
        logger.debug("<<< End of Relationship.setDocumentId()***");
    }

    public int getDocumentGroupId() {
        logger.debug(">>> Start of Relationship.getDocumentGroupId()***");
        logger.debug("<<< End of Relationship.getDocumentGroupId()***");
        return documentGroupId;
    }

    public void setDocumentGroupId(int documentGroupId) {
        logger.debug(">>> Start of Relationship.setDocumentGroupId()***");
        this.documentGroupId = documentGroupId;
        logger.debug("<<< End of Relationship.setDocumentGroupId()***");
    }

    public int getRelationA() {
        logger.debug(">>> Start of Relationship.getRelationA()***");
        logger.debug("<<< End of Relationship.getRelationA()***");
        return relationA;
    }

    public void setRelationA(int relationA) {
        logger.debug(">>> Start of Relationship.setRelationA()***");
        this.relationA = relationA;
        logger.debug("<<< End of Relationship.setRelationA()***");
    }

    public int getRelationB() {
        logger.debug(">>> Start of Relationship.getRelationB()***");
        logger.debug("<<< End of Relationship.getRelationB()***");
        return relationB;
    }

    public void setRelationB(int relationB) {
        logger.debug(">>> Start of Relationship.setRelationB()***");
        this.relationB = relationB;
        logger.debug("<<< End of Relationship.setRelationB()***");
    }

    public int getRelationEMin() {
        logger.debug(">>> Start of Relationship.getRelationEMin()***");
        logger.debug("<<< End of Relationship.getRelationEMin()***");
        return relationEMin;
    }

    public void setRelationEMin(int relationEMin) {
        logger.debug(">>> Start of Relationship.setRelationEMin()***");
        this.relationEMin = relationEMin;
        logger.debug("<<< End of Relationship.setRelationEMin()***");
    }

    public int getRelationEMax() {
        logger.debug(">>> Start of Relationship.getRelationEMax()***");
        logger.debug("<<< End of Relationship.getRelationEMax()***");
        return relationEMax;
    }

    public void setRelationEMax(int relationEMax) {
        logger.debug(">>> Start of Relationship.setRelationEMax()***");
        this.relationEMax = relationEMax;
        logger.debug("<<< End of Relationship.setRelationEMax()***");
    }

}
