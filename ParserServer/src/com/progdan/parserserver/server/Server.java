package com.progdan.parserserver.server;

import java.io.*;
import java.net.*;
import java.util.*;

import com.progdan.logengine.*;
import com.progdan.parserserver.index.IndexFiles;
import com.progdan.parserserver.server.gui.FrameServer;


public class Server extends Thread {
    private static Logger logger = Logger.getLogger(Server.class.getName());
    private int port;
    private Vector clients;
    private LinkedList index;
    private FrameServer gui;
    private IndexFiles indexer;
    private IndexController control;
    public Server(FrameServer gui) {
        logger.debug(">>> Start of Server.Server()***");
        this.gui = gui;
        indexer = null;
        control = new IndexController();
        clients = new Vector();
        index = control.getIndexTasks();
        updateGUI();
        try {
            Properties props = new Properties();
            props.load(getClass().getResourceAsStream("/" + "db.properties"));
            port = Integer.parseInt(props.getProperty("parsePort", "4444"));
        } catch (FileNotFoundException e) {
            logger.error(e);
        } catch (IOException e) {
            logger.error(e);
        }
        logger.debug("<<< End of Server.Server()***");
    }

    public void interrupt() {
        super.interrupt();
        logger.debug(">>> Start of Server.interrupt()***");
        for (int i = 0; i < clients.size(); ) {
            ((ThreadClient) clients.get(i)).interrupt();
        }
        logger.debug("<<< End of Server.interrupt()***");
        System.exit(0);
    }

    public void run() {
        logger.debug(">>> Start of Server.run()***");
        ServerSocket sSocket = null;
        Socket cSocket = null;
        try {
            sSocket = new ServerSocket(port);
            while (true) {
                if ((indexer == null) && (index.size() != 0)) {
                    indexer = new IndexFiles(index, control, this);
                    indexer.start();
                }
                cSocket = sSocket.accept();
                logger.info("Connection accepted: " +
                            cSocket.getInetAddress() +
                            ":" + cSocket.getPort());
                ThreadClient tc = new ThreadClient(cSocket, this);
                clients.add(tc);
                tc.start();
                updateGUI();
            }
        } catch (IOException e) {
            logger.error("Nao consegui ouvir a porta: " + port);
            logger.error(e);
        }
        logger.debug("<<< End of Server.run()***");
    }

    public IndexController getControl() {
        logger.debug(">>> Start of Server.getControl()***");
        logger.debug("<<< End of Server.getControl()***");
        return control;
    }

    public void removeClient(ThreadClient tc) {
        logger.debug(">>> Start of Server.removeClient()***");
        clients.removeElement(tc);
        if ((indexer == null) && (index.size() != 0)) {
            indexer = new IndexFiles(index, control, this);
            indexer.start();
        }
        updateGUI();
        logger.debug("<<< End of Server.removeClient()***");
    }

    public void addIndex(Hashtable request) {
        logger.debug(">>> Start of Server.addIndex()***");
        index.add(request);
        logger.debug("<<< End of Server.addIndex()***");
    }

    public void removeIndexer() {
        logger.debug(">>> Start of Server.removeIndexer()***");
        indexer = null;
        updateGUI();
        logger.debug("<<< End of Server.removeIndexer()***");
    }

    public void updateGUI() {
        logger.debug(">>> Start of Server.updateGUI()***");
        gui.files(control.countFiles(), control.countParsedFiles(), control.countNoParsedFiles());
        gui.connections(clients.size());
        logger.debug("<<< End of Server.updateGUI()***");
    }
}
