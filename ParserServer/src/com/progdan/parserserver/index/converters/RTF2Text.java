package com.progdan.parserserver.index.converters;

import java.io.*;

import com.progdan.logengine.*;
import com.progdan.rtf2txt.RTFConverter;

public class RTF2Text extends Converter {
    private static Logger logger = Logger.getLogger(RTF2Text.class.getName());
    public RTF2Text(String reppath, String cmdpath) {
        super(reppath, cmdpath);
        logger.debug(">>> Start of RTF2Text.RTF2Text()***");
        logger.debug("<<< End of RTF2Text.RTF2Text()***");
    }

    public void convertFile(String id) {
        logger.debug(">>> Start of RTF2Text.convertFile()***");
        try{
            RTFConverter extractor = new RTFConverter();
            File tmp = new File(reppath, id + ".rtf");
            FileInputStream is = new FileInputStream(tmp);
            byte[] bytearray = new byte[(int) tmp.length()];
            is.read(bytearray);
            String text = extractor.convertToText(bytearray);
            is.close();
            tmp = new File(reppath + System.getProperty("file.separator") + "body", id + ".txt");
            FileWriter out = new FileWriter(tmp);
            out.write(text);
            out.flush();
            out.close();
            tmp = new File(reppath, id + ".rtf");
            tmp.delete();
        }catch(FileNotFoundException e){
            logger.error(e);
        }catch(IOException e){
            logger.error(e);
        }
        logger.debug("<<< End of RTF2Text.convertFile()***");
    }
}
