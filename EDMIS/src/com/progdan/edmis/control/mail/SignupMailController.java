package com.progdan.edmis.control.mail;

import java.io.*;
import java.util.*;

import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

import com.progdan.logengine.*;
import com.progdan.edmis.control.crypt.JCrypt;

public class SignupMailController {
    protected SignupMailController() {}

    private static Logger logger = Logger.getLogger(SignupMailController.class.
            getName());

    /**
     * @link
     * @shapeType PatternLink
     * @pattern Singleton
     * @supplierRole Singleton factory
     */
    /*# private SignupMailController _signupMailController; */
    private static SignupMailController instance = null;

    public void confirmSignup(String email, String login) {
        logger.debug(">>> Start of SignupMailController.confirmSignup()***");
        // E-Mail address to send message
        String to = email;
        try {
            Properties props = new Properties();
            props.load(getClass().getResourceAsStream("/" + "mail.properties"));
            // E-Mail address from sending a Message
            String from = props.getProperty("from", "edmis@localhost");
            // SMTP server
            String host = props.getProperty("host", "smtpserver.yourisp.net");
            String user = props.getProperty("user", "");
            String passwd = props.getProperty("passwd", "");

            // Create properties for the Session
            props = new Properties();

            // If using static Transport.send(),
            // need to specify the mail server here
            props.put("mail.smtp.host", host);

            // To see what is going on behind the scene
            // props.put("mail.debug", "true");

            // Get a session
            Session session = Session.getInstance(props);

            try {
                // Get a Transport object to send e-mail
                Transport bus = session.getTransport("smtp");

                // Connect only once here
                // Transport.send() disconnects after each send
                // Usually, no username and password is required for SMTP
                // bus.connect();
                bus.connect(host, user, passwd);

                // Instantiate a message
                Message msg = new MimeMessage(session);

                // Set message attributes
                msg.setFrom(new InternetAddress(from));
                InternetAddress[] address = {
                                            new InternetAddress(to)};
                msg.setRecipients(Message.RecipientType.TO, address);
                // Parse a comma-separated list of email addresses. Be strict.
                msg.setRecipients(Message.RecipientType.CC,
                                  InternetAddress.parse(to, true));
                // Parse comma/space-separated list. Cut some slack.
                msg.setRecipients(Message.RecipientType.BCC,
                                  InternetAddress.parse(to, false));

                msg.setSubject("Progdan® Codename Avalon - Signup Confirmation");
                msg.setSentDate(new Date());

                setHTMLContent(msg, login);
                msg.saveChanges();
                bus.sendMessage(msg, address);

                bus.close();
            } catch (NoSuchProviderException e) {
                logger.error(e);
            } catch (MessagingException e) {
                logger.error(e);
            }

        } catch (FileNotFoundException e) {
            logger.error(e);
        } catch (IOException e) {
            logger.error(e);
        }
        logger.debug("<<< End of SignupMailController.confirmSignup()***");
    }

    // Set a single part html content.
    // Sending data of any type is similar.
    public void setHTMLContent(Message msg, String login) throws
            MessagingException {
        logger.debug(">>> Start of SignupMailController.setHTMLContent()***");
        try {
            Properties props = new Properties();
            props.load(getClass().getResourceAsStream("/" + "mail.properties"));
            String urlbase = props.getProperty("urlbase",
                                               "http://localhost:8080/EDMIS/");
            String url = urlbase + "account-confirm.jsp?login=" + login +
                         "&secret=" +
                         JCrypt.crypt("any string", login);

            String html = "<html>"
                          + "  <head>"
                          + "    <title>" + msg.getSubject() + "</title>"
                          + "  </head>"
                          + "  <body>"
                          + "    <h1>" + msg.getSubject() + "</h1>"
                          +
                          "      <p>Welcome to <b>ProgDan® Codename Avalon - Beta 1 Release.</b></p>"
                          +
                          "      <p>To confirm your use registration, you have to follow this link:</p>"
                          + "      <p><a href=\"" + url + "\">" + url +
                          "</a></p>"
                          + "      <p>After you do this, you will be able to use your new account. If you fail to do this, your account will be deleted within a few days.</p>"
                          + "      <br><br>"
                          + "      <p>Best regards,</p>"
                          +
                          "      <p>ProgDan® Codename Avalon Admin.</p>"
                          + "  </body>"
                          + "</html>";

            // HTMLDataSource is an inner class
            msg.setDataHandler(new DataHandler(new HTMLDataSource(html)));
        } catch (FileNotFoundException e) {
            logger.error(e);
        } catch (IOException e) {
            logger.error(e);
        }
        logger.debug("<<< End of SignupMailController.setHTMLContent()***");
    }

    public static SignupMailController getInstance() {
        if (instance == null) {
            instance = new com.progdan.edmis.control.mail.SignupMailController();
        }
        return instance;
    }

    /*
     * Inner class to act as a JAF datasource to send HTML e-mail content
     */
    static class HTMLDataSource implements DataSource {
        private String html;

        public HTMLDataSource(String htmlString) {
            html = htmlString;
        }

        // Return html string in an InputStream.
        // A new stream must be returned each time.
        public InputStream getInputStream() throws IOException {
            if (html == null) {
                throw new IOException("Null HTML");
            }
            return new ByteArrayInputStream(html.getBytes());
        }

        public OutputStream getOutputStream() throws IOException {
            throw new IOException("This DataHandler cannot write HTML");
        }

        public String getContentType() {
            return "text/html";
        }

        public String getName() {
            return "JAF text/html dataSource to send e-mail only";
        }
    }
}
