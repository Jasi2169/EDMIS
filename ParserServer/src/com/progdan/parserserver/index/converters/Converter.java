package com.progdan.parserserver.index.converters;

import java.util.Hashtable;
import java.io.*;

import com.progdan.logengine.*;
import com.progdan.searchengine.index.IndexWriter;
import com.progdan.searchengine.analysis.SimpleAnalyzer;
import com.progdan.searchengine.document.*;

public abstract class Converter {
    private static Logger logger = Logger.getLogger(Converter.class.getName());
    protected static String reppath;
    protected static String cmdpath;
    private IndexWriter writer;
    public Converter(String reppath, String cmdpath) {
        logger.debug(">>> Start of Converter.Converter()***");
        this.reppath = reppath;
        this.cmdpath = cmdpath;
        logger.debug("<<< End of Converter.Converter()***");
    }

    public boolean index(Hashtable task) {
        logger.debug(">>> Start of Converter.index()***");
        boolean result = false;
        String indexPath = (String) task.get("indexPath");
        String id = (String) task.get("id");
        String format = (String) task.get("format");
        try {
            File test = new File(reppath + System.getProperty("file.separator") +
                                 "body", id + ".txt");
            if (!test.exists()) {
                convertFile(id);
            }
            if (test.exists()) {
                writer = new IndexWriter(reppath +
                                         System.getProperty("file.separator") +
                                         "index" +
                                         System.getProperty("file.separator") +
                                         indexPath,
                                         new SimpleAnalyzer(), false);
                logger.info("Indexing file " + id + "." + format +
                            " on the group " + indexPath);
                Document doc = new Document();
                //We create a Document with two Fields, one wich contains
                //the file path, and one the file's contents
                InputStream is = new FileInputStream(test.getAbsolutePath());
                doc.add(Field.UnIndexed("path", id));
                doc.add(Field.Text("body", (Reader)new InputStreamReader(is)));
                writer.addDocument(doc);
                is.close();
                writer.close();
                result = true;
            }
        } catch (IOException e) {
            logger.error(e);
        }
        logger.debug("<<< End of Converter.index()***");
        return result;
    }

    abstract public void convertFile(String id);
}
