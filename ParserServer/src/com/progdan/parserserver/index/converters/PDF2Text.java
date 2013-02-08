package com.progdan.parserserver.index.converters;

import java.io.*;

import com.progdan.logengine.*;
import com.progdan.pdf2txt.ExtractText;

public class PDF2Text extends Converter {
    private static Logger logger = Logger.getLogger(PDF2Text.class.getName());
    public PDF2Text(String reppath, String cmdpath) {
        super(reppath, cmdpath);
        logger.debug(">>> Start of PDF2Text.PDF2Text()***");
        logger.debug("<<< End of PDF2Text.PDF2Text()***");
    }

    public void convertFile(String id) {
        logger.debug(">>> Start of PDF2Text.convertFile()***");
        try {
            File source = new File(reppath, id + ".pdf");
            File target = new File(reppath + System.getProperty("file.separator") + "body", id + ".txt");
            ExtractText.main(new String[] {source.getAbsolutePath(),
                             target.getAbsolutePath()});
            String txt;
            String body = new String();
            BufferedReader is = new BufferedReader(new FileReader(
                    target));
            while (((txt = is.readLine()) != null) && (body.compareTo("") == 0)) {
                body += txt;
            }
            is.close();
            if (body.compareTo("") == 0) {
                System.out.println("Arquivo vazio!!");
                target.delete();
            } else {
                source.delete();
            }
        } catch (FileNotFoundException e) {
            logger.error(e);
        } catch (IOException e) {
            logger.error(e);
        } catch (Exception e) {
            logger.error(e);
        }
        logger.debug("<<< End of PDF2Text.convertFile()***");
    }
}
