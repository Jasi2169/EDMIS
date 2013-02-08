package com.progdan.parserserver.index.converters;

import java.io.*;

import com.progdan.logengine.*;
import com.progdan.doc2txt.WordExtractor;

public class DOC2Text extends Converter {
    private static Logger logger = Logger.getLogger(DOC2Text.class.getName());
    public DOC2Text(String reppath, String cmdpath) {
        super(reppath, cmdpath);
        logger.debug(">>> Start of DOC2Text.DOC2Text()***");
        logger.debug("<<< End of DOC2Text.DOC2Text()***");
    }

    public void convertFile(String id) {
        logger.debug(">>> Start of DOC2Text.convertFile()***");
        try {
            File tmp = new File(reppath, id + ".doc");
            FileInputStream is = new FileInputStream(tmp);
            WordExtractor extractor = new WordExtractor();
            String text = extractor.extractText(is);
            tmp = new File(reppath + System.getProperty("file.separator") + "body", id + ".txt");
            FileWriter out = new FileWriter(tmp);
            out.write(text);
            out.flush();
            out.close();
            is.close();
            tmp = new File(reppath, id + ".doc");
            tmp.delete();
        } catch (FileNotFoundException e) {
            logger.error(e);
        } catch (IOException e) {
            logger.error(e);
        } catch (Exception e) {
            logger.error(e);
        }
        logger.debug("<<< End of DOC2Text.convertFile()***");
    }
}
