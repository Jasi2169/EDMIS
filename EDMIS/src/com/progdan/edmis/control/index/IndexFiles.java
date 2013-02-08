package com.progdan.edmis.control.index;

import java.io.*;
import java.util.*;
import java.net.*;

import com.progdan.logengine.*;
import com.progdan.searchengine.index.IndexWriter;
import com.progdan.searchengine.analysis.SimpleAnalyzer;
import com.progdan.searchengine.document.*;
import com.progdan.edmis.model.user.User;


public class IndexFiles {
    private static Logger logger = Logger.getLogger(IndexFiles.class.getName());
    private int port;
    private String host;
    private Socket kkSocket;
    private String reppath;
    private ParserController control;
    public IndexFiles(User user) {
        control = new ParserController(user);
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
    }

    public void index(String indexPath, String id, String format) {
        logger.debug(">>> Start of Client.index()***");
        File test = new File(reppath + System.getProperty("file.separator") +
                             "body", id + ".txt");
        try {
            if (test.exists()) {
                IndexWriter writer = new IndexWriter(reppath +
                        System.getProperty("file.separator") + "index" +
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
            } else {
                String file = control.fileParsed(id);
                if (file == null) {
                    control.addTask(indexPath, id);
                    sendFileToParser(indexPath, id, format);
                }
            }
        } catch (FileNotFoundException e) {
            logger.error(e);
        } catch (IOException e) {
            logger.error(e);
        }
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

}
