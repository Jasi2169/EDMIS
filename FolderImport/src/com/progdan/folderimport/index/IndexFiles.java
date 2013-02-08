package com.progdan.folderimport.index;

import java.io.*;
import java.net.*;
import java.util.*;

import com.progdan.logengine.*;

public class IndexFiles {
    private static Logger logger = Logger.getLogger(IndexFiles.class.getName());
    private int port;
    private String host;
    private Socket kkSocket;
    private String reppath;
    private ParserController control;
    public IndexFiles() {
        control = new ParserController();
        try {
            Properties props = new Properties();
            props.load(getClass().getResourceAsStream("/" + "db.properties"));
            port = Integer.parseInt(props.getProperty("parsePort", "4444"));
            reppath = props.getProperty("reppath", "C:\\EDMIS");
            host = props.getProperty("parseHost", "localhost");
        } catch (FileNotFoundException e) {
            logger.error(e);
        } catch (IOException e) {
            logger.error(e);
        }
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void index(String indexPath, String id, String format) {
        logger.debug(">>> Start of Client.index()***");
        File test = new File(reppath + System.getProperty("file.separator") +
                             "body", id + ".txt");
        String file = control.fileParsed(id);
        if (file == null) {
/*@todo Envio de arquivo para extração de texto*/
//            sendFileToParser(indexPath, id, format);
        }
        control.addTask(indexPath, id);
        logger.debug("<<< End of Client.index()***");
    }

    public void sendFileToParser(String indexPath, String id, String format) {
        logger.debug(">>> Start of Client.sendFileToParser()***");
        String msg;
        Hashtable request = new Hashtable();
        try {
            kkSocket = new Socket(InetAddress.getByName(host), port);
            BufferedReader is = new BufferedReader(new InputStreamReader(
                    kkSocket.getInputStream()));
            PrintWriter os = new PrintWriter(kkSocket.getOutputStream());
            if (kkSocket != null && os != null && is != null) {
                msg = is.readLine();
                logger.info(msg);
                os.println("id: " + id);
                os.flush();
                os.println("format: " + format);
                os.flush();
                os.println("indexPath: " + indexPath);
                os.flush();
            }
            is.close();
            os.close();
            kkSocket.close();
        } catch (IOException e) {
            logger.error(e);
        }
        logger.debug("<<< End of Client.sendFileToParser()***");
    }

    private void jbInit() throws Exception {
    }

}
