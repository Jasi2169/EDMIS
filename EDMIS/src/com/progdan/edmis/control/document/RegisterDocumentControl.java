package com.progdan.edmis.control.document;

import java.io.*;
import java.sql.*;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

import com.progdan.checksum.*;
import com.progdan.fileupload.*;
import com.progdan.logengine.*;
import com.progdan.zipengine.ZipCreate;
import com.progdan.edmis.model.user.User;
import com.progdan.edmis.model.document.Document;

public class RegisterDocumentControl {
    private static Logger logger = Logger.getLogger(RegisterDocumentControl.class.
            getName());
    private User user;
    public RegisterDocumentControl(User user) {
        logger.debug(
                ">>> Start of RegisterDocumentControl.RegisterDocumentControl()***");
        this.user = user;
        logger.debug(
                "<<< End of RegisterDocumentControl.RegisterDocumentControl()***");
    }

    public boolean register(HttpSession session, HttpServletRequest request) {
        logger.debug(">>> Start of RegisterDocumentControl.register()***");
        DocumentReader read = new DocumentReader(user);
        boolean result = false;
        String temppath = null;
        String reppath = null;
        Properties props = new Properties();
        try {
            props.load(getClass().getResourceAsStream("/" + "db.properties"));
            temppath = props.getProperty("temppath", "C:\\Temp");
            reppath = props.getProperty("reppath", "C:\\EDMIS");
        } catch (IOException e) {
            logger.error(e);
        }
        DiskFileUpload fu = new DiskFileUpload();
        fu.setSizeMax( -1);
        fu.setRepositoryPath(temppath);
        try {
            List l = fu.parseRequest(request);
            Iterator i = l.iterator();
            while (i.hasNext()) {
                FileItem fi = (FileItem) i.next();
                if (fi.getName() != null) {
                    File tmp = new File(temppath, basename(fi.getName()));
                    fi.write(tmp);
                    Checksum cs = new Checksum(new String[] {"-a", "sha512",
                                               temppath +
                                               System.getProperty(
                            "file.separator") +
                                               basename(fi.getName())});
                    String checksum = cs.getChecksum().substring(0, 128);
                    if (read.exists(checksum)) {
                        exists(session, tmp, checksum, read);
                        logger.debug(
                                "<<< End of RegisterDocumentControl.register()***");
                        return false;
                    } else {
                        addDocument(session, tmp, checksum, reppath);
                    }
                }
            }
        } catch (FileUploadException e) {
            logger.error(e);
        } catch (Exception e) {
            logger.error(e);
        }
        logger.debug("<<< End of RegisterDocumentControl.register()***");
        return true;
    }

    public void addDocument(HttpSession session, File file, String checksum,
                            String path) {
        logger.debug(">>> Start of RegisterDocumentControl.addDocument()***");
        DocumentWriter write = new DocumentWriter(user);
        Document doc = new Document();
        logger.info("File " + file.getName() + " - sha-512: " + checksum +
                    " send by " + user.getLogin());
        doc.setId(checksum);
        doc.setName(file.getName().substring(0,
                                             file.getName().lastIndexOf(".")));
        String format = file.getName().substring(file.getName().lastIndexOf(".") +
                                                 1).toLowerCase();
        doc.setFormat(format);
        doc.setSize(file.length());
        doc.setLanguage("en");
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        String inDate = sdf.format(new java.util.Date());
        doc.setDate(inDate);
        doc.setPages(1);
        session.setAttribute("Document", doc);
        try {
            file.renameTo(new File(path, checksum + "." + format));
            ZipCreate.main(new String[] {path +
                           System.getProperty("file.separator") + "zip"
                           + System.getProperty("file.separator") + checksum +
                           ".zip",
                           path + System.getProperty("file.separator")
                           + checksum + "." + format});
        } catch (IOException e) {
            logger.error(e);
        }
        write.writeNew(doc);

        logger.debug("<<< End of RegisterDocumentControl.addDocument()***");
    }

    private void exists(HttpSession session, File file, String checksum,
                        DocumentReader read) {
        logger.debug(">>> Start of RegisterDocumentControl.exists()***");
        logger.info("File " + file.getName() + " - sha512: " + checksum +
                    " send by " + user.getLogin() +
                    " already exists on the system!");
        file.delete();
        Document doc = read.readDocument(checksum);
        session.setAttribute("Document", doc);
        logger.debug("<<< End of RegisterDocumentControl.exists()***");
    }

    private static String basename(String filename) {
        logger.debug(">>> Start of RegisterDocumentControl.basename()***");
        int slash = filename.lastIndexOf("\\");
        if (slash != -1) {
            filename = filename.substring(slash + 1);
            // I think Windows doesn't like /'s either
            slash = filename.lastIndexOf("/");
            if (slash != -1) {
                filename = filename.substring(slash + 1);
                // In case the name is C:foo.txt
            }
            slash = filename.lastIndexOf(":");
            if (slash != -1) {
                filename = filename.substring(slash + 1);
            }
        }
        logger.debug("<<< End of RegisterDocumentControl.basename()***");
        return filename;
    }

}
