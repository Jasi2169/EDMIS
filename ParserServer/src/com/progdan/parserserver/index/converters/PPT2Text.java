package com.progdan.parserserver.index.converters;

import java.io.*;

import com.progdan.logengine.*;
import com.progdan.parserserver.util.StreamCatcher;

public class PPT2Text extends Converter {
    private static Logger logger = Logger.getLogger(PPT2Text.class.getName());
    /** Path to external PPT-Converter */
    private static String PPT2HTML_CONVERTER;
    /** Path to external HTML-Converter */
    private static String HTML2TXT_CONVERTER;
    public PPT2Text(String reppath, String cmdpath) {
        super(reppath, cmdpath);
        logger.debug(">>> Start of PPT2Text.PPT2Text()***");
        PPT2HTML_CONVERTER = cmdpath + System.getProperty("file.separator") +
                             "PPT2HTML.exe";
        HTML2TXT_CONVERTER = cmdpath + System.getProperty("file.separator") +
                             "HTML2TXT.exe";
        logger.debug("<<< End of PPT2Text.PPT2Text()***");
    }

    public void convertFile(String id) {
        logger.debug(">>> Start of PPT2Text.convertFile()***");
        try {
            File tmp = new File(reppath, id + ".ppt");
            String strFilename = "\"" + PPT2HTML_CONVERTER + "\" \"" +
                                 tmp.getAbsolutePath() + "\"";
            tmp = new File(reppath, id + ".htm");
            FileOutputStream fos = new FileOutputStream(tmp.getAbsolutePath());
            Process proc = Runtime.getRuntime().exec(strFilename);
            StreamCatcher outputCatcher = new StreamCatcher(proc.getInputStream(),
                    fos);
            outputCatcher.start();
            proc.waitFor();
            fos.flush();
            fos.close();

            strFilename = "\"" + HTML2TXT_CONVERTER + "\" \"" +
                          tmp.getAbsolutePath() + "\"";
            tmp = new File(reppath + System.getProperty("file.separator") + "body", id + ".txt");
            strFilename += " \"" + tmp.getAbsolutePath() + "\"";
            tmp = new File(reppath, "output.txt");
            fos = new FileOutputStream(tmp.getAbsolutePath());
            proc = Runtime.getRuntime().exec(strFilename);
            outputCatcher = new StreamCatcher(proc.getInputStream(), fos);
            outputCatcher.start();
            proc.waitFor();
            fos.flush();
            fos.close();
            tmp.delete();
            tmp = new File(reppath, id + ".htm");
            tmp.delete();
            tmp = new File(reppath, id + ".ppt");
            tmp.delete();
        } catch (FileNotFoundException e) {
            logger.error(e);
        } catch (IOException e) {
            logger.error(e);
        } catch (InterruptedException e) {
            logger.error(e);
        }
        logger.debug("<<< End of PPT2Text.convertFile()***");
    }
}
