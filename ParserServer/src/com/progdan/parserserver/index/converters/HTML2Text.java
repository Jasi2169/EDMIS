package com.progdan.parserserver.index.converters;

import java.io.*;

import com.progdan.logengine.*;
import com.progdan.parserserver.util.StreamCatcher;

public class HTML2Text extends Converter {
    private static Logger logger = Logger.getLogger(HTML2Text.class.getName());
    /** Path to external HTML-Converter */
    private static String HTML2TXT_CONVERTER;
    public HTML2Text(String reppath, String cmdpath) {
        super(reppath, cmdpath);
        logger.debug(">>> Start of HTML2Text.HTML2Text()***");
        HTML2TXT_CONVERTER = cmdpath + System.getProperty("file.separator") +
                             "HTML2TXT.exe";
        logger.debug("<<< End of HTML2Text.HTML2Text()***");
    }

    public void convertFile(String id) {
        logger.debug(">>> Start of HTML2Text.convertFile()***");
        File tmp;
        String strFilename;
        try {
            tmp = new File(reppath, id + ".htm");
            if (!tmp.exists()) {
                tmp = new File(reppath, id + ".html");
            }
            strFilename = "\"" + HTML2TXT_CONVERTER + "\" \"" +
                          tmp.getAbsolutePath() + "\"";
            tmp = new File(reppath + System.getProperty("file.separator") + "body", id + ".txt");
            strFilename += " \"" + tmp.getAbsolutePath() + "\"";
            tmp = new File(reppath, "output.txt");
            FileOutputStream fos = new FileOutputStream(tmp.getAbsolutePath());
            Process proc = Runtime.getRuntime().exec(strFilename);
            StreamCatcher outputCatcher = new StreamCatcher(proc.getInputStream(),
                    fos);
            outputCatcher.start();
            proc.waitFor();
            fos.flush();
            fos.close();
            tmp.delete();
            tmp = new File(reppath, id + ".htm");
            if (!tmp.exists()) {
                tmp = new File(reppath, id + ".html");
            }
            tmp.delete();
        } catch (FileNotFoundException e) {
            logger.error(e);
        } catch (IOException e) {
            logger.error(e);
        } catch (InterruptedException e) {
            logger.error(e);
        }
        logger.debug("<<< End of HTML2Text.convertFile()***");
    }
}
