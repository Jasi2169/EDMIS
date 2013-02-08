package com.progdan.parserserver.server;

import java.io.*;
import java.net.*;
import java.util.*;

import com.progdan.logengine.*;

public class ThreadClient extends Thread {
    private static Logger logger = Logger.getLogger(ThreadClient.class.getName());
    private Socket cSocket;
    private Server server;
    private BufferedReader is;
    private PrintWriter os;
    private IndexController control;
    public ThreadClient(Socket cSocket, Server server) {
        logger.debug(">>> Start of ThreadClient.ThreadClient()***");
        this.cSocket = cSocket;
        this.server = server;
        this.control = server.getControl();
        logger.debug("<<< End of ThreadClient.ThreadClient()***");
    }

    public void interrupt() {
        super.interrupt();
        logger.debug(">>> Start of ThreadClient.interrupt()***");
        try {
            os.flush();
            os.close();
            cSocket.close();
            server.removeClient(this);
        } catch (IOException e) {
            logger.error(e);
        }
        logger.debug("<<< End of ThreadClient.interrupt()***");
    }

    public void run() {
        logger.debug(">>> Start of ThreadClient.run()***");
        String msg;
        try {
            is = new BufferedReader(new InputStreamReader(cSocket.
                    getInputStream()));
            os = new PrintWriter(cSocket.getOutputStream());
            os.println("ProgDan® Codename Avalon - Index Server running on: " +
                       cSocket.getLocalAddress() + ":" +
                       cSocket.getLocalPort() + " connection accepted.");
            os.flush();
            Hashtable request = new Hashtable();
            while ((msg = is.readLine()) != null) {
                if (msg.startsWith("id: ")) {
                    request.put("id", msg.substring(4));
                }
                if (msg.startsWith("format: ")) {
                    request.put("format", msg.substring(8));
                }
                if (msg.startsWith("indexPath: ")) {
                    request.put("indexPath", msg.substring(11));
                }
            }
            is.close();
            os.close();
            server.addIndex(request);
        } catch (IOException e) {
            logger.error(e);
        }
        logger.info("Client disconected...");
        server.removeClient(this);
        logger.debug("<<< End of ThreadClient.run()***");
    }
}
