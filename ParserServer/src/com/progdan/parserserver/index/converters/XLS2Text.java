package com.progdan.parserserver.index.converters;

import java.io.*;

import com.progdan.logengine.*;
import com.progdan.xls2txt.*;
import com.progdan.xls2txt.read.biff.BiffException;

public class XLS2Text extends Converter {
    private static Logger logger = Logger.getLogger(XLS2Text.class.getName());
    public XLS2Text(String reppath, String cmdpath) {
        super(reppath, cmdpath);
        logger.debug(">>> Start of XLS2Text.XLS2Text()***");
        logger.debug("<<< End of XLS2Text.XLS2Text()***");
    }

    public void convertFile(String id) {
        logger.debug(">>> Start of XLS2Text.convertFile()***");
        try {
            File tmp = new File(reppath, id + ".xls");
            String text = new String();
            Workbook workbook = Workbook.getWorkbook(tmp);
            for (int nSheet = 0; nSheet < workbook.getNumberOfSheets();
                              nSheet++) {
                Sheet sheet = workbook.getSheet(nSheet);
                text += sheet.getName() + "\n";
                Cell[] row = null;
                for (int i = 0; i < sheet.getRows(); i++) {
                    row = sheet.getRow(i);
                    // Find the last non-blank entry in the row
                    int nonblank = 0;
                    for (int j = row.length - 1; j >= 0; j--) {
                        if (row[j].getType() != CellType.EMPTY) {
                            nonblank = j;
                            break;
                        }
                    }
                    for (int j = 0; j < nonblank; j++) {
                        text += row[j].getContents() + " ";
                    }
                    text += "\n";
                }
            }
            workbook.close();
            tmp = new File(reppath + System.getProperty("file.separator") + "body", id + ".txt");
            FileWriter out = new FileWriter(tmp);
            out.write(text);
            out.flush();
            out.close();
            tmp = new File(reppath, id + ".xls");
            tmp.delete();
        } catch (IOException e) {
            logger.error(e);
        } catch (BiffException e) {
            logger.error(e);
        }
        logger.debug("<<< End of XLS2Text.convertFile()***");
    }
}
