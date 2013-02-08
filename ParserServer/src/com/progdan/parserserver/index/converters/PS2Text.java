package com.progdan.parserserver.index.converters;

import java.io.*;

import com.progdan.logengine.*;
import com.progdan.parserserver.util.StreamCatcher;

public class PS2Text extends Converter {
    private static Logger logger = Logger.getLogger(PS2Text.class.getName());
    /** Path to external HTML-Converter */
    private static String PS2TXT_CONVERTER;
    public PS2Text(String reppath, String cmdpath) {
        super(reppath, cmdpath);
        logger.debug(">>> Start of PS2Text.PS2Text()***");
        PS2TXT_CONVERTER = cmdpath + System.getProperty("file.separator") +
                           "PS2TXT.exe";
        logger.debug("<<< End of PS2Text.PS2Text()***");
    }

    public void convertFile(String id) {
        logger.debug(">>> Start of PS2Text.convertFile()***");
        File tmp;
        String strFilename;
        try {
            tmp = new File(reppath, id + ".ps");
            strFilename = "\"" + PS2TXT_CONVERTER + "\" \"" +
                          tmp.getAbsolutePath() + "\"";
            tmp = new File(reppath + System.getProperty("file.separator") + "body", id + ".txt");
            FileOutputStream fos = new FileOutputStream(tmp.getAbsolutePath());
            Process proc = Runtime.getRuntime().exec(strFilename);
            StreamCatcher outputCatcher = new StreamCatcher(proc.getInputStream(),
                    fos);
            outputCatcher.start();
            proc.waitFor();
            fos.flush();
            fos.close();
            tmp = new File(reppath, id + ".ps");
            tmp.delete();
        } catch (FileNotFoundException e) {
            logger.error(e);
        } catch (IOException e) {
            logger.error(e);
        } catch (InterruptedException e) {
            logger.error(e);
        }
        logger.debug("<<< End of PS2Text.convertFile()***");
    }
}
