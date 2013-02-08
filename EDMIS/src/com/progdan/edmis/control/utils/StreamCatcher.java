package com.progdan.edmis.control.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;

/**
 * StreamCatcher writes information of inputstream to outputstream.
 *
 * @author Jan Löffler, mail@jlsoft.de
 * @version $Revision: 1.2 $
 */
public class StreamCatcher extends Thread {

    /** Member variables */
    private InputStream m_is = null;
    private OutputStream m_os = null;
    private Writer m_ow = null;

    /**
     * Constructor.
     * @param is InputStream
     * @param os OutputStream
     */
    public StreamCatcher(InputStream is, OutputStream os) {
        m_is = is;
        m_os = os;
        m_ow = null;
    }

    /**
     * Constructor.
     * @param is InputStream
     * @param ow Writer for output
     */
    public StreamCatcher(InputStream is, Writer ow) {
        m_is = is;
        m_os = null;
        m_ow = ow;
    }

    /**
     * Runs the thread.
     */
    public void run() {
        try {
            PrintWriter pw = null;

            if (m_os != null) {
                pw = new PrintWriter(m_os);
            } else if (m_ow != null) {
                pw = new PrintWriter(m_ow);
            }

            if (pw != null) {
                InputStreamReader isr = new InputStreamReader(m_is);
                BufferedReader br = new BufferedReader(isr);

                String strLine = null;

                while ((strLine = br.readLine()) != null) {
                    pw.println(strLine);
                }

                pw.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
