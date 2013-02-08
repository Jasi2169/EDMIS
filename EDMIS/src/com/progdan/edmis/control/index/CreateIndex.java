package com.progdan.edmis.control.index;

import java.io.*;

import com.progdan.logengine.*;
import com.progdan.searchengine.index.IndexWriter;

public class CreateIndex {
    private static Logger logger = Logger.getLogger(CreateIndex.class.getName());
    public static void create(String indexPath){
        logger.debug(">>> Start of CreateIndex.create()***");
        IndexWriter writer;
        try{
            writer = new IndexWriter(indexPath, null, true);
            writer.close();
        }catch(IOException e){
            logger.error(e);
        }
        logger.debug("<<< End of CreateIndex.create()***");
    }
}
