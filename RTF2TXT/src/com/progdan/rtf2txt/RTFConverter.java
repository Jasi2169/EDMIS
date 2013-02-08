package com.progdan.rtf2txt;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.progdan.rtf2txt.rtf.RtfAnalyser;
import com.progdan.rtf2txt.rtf.RtfDocument;
import com.progdan.rtf2txt.rtf.RtfReader;


/**
 * Implementation eines RTF-nach-PlainText-Konverters.
 *
 * @author Daniel Finger, Lars Raap, Martin Amelsberg
 * @version 1.0
 */
public class RTFConverter extends Object implements TextConverter {
  /**
   * Erzeugt ein neues RTFConverter-Objekt
   */
  public RTFConverter() {
  }

  /**
   * Konvertiert eine RTF-Datei in ein PlainText-Dokument
   *
   * @param file Byte-Array der Datei
   *
   * @return PlainText-Fassung der RTF-Datei
   *
   * @throws IllegalArgumentException Falls die Datei nicht konvertiert werden
   *         konnte
   */
  public String convertToText(byte[] file) throws IllegalArgumentException {
    // Var Section
    RtfReader reader;
    RtfAnalyser ana;
    RtfDocument doc;
    PrintWriter swr = null;
    StringWriter output;

    //------------------------------------
    reader = new RtfReader(file);

    try {
      output = new StringWriter();
      swr = new PrintWriter(output);
      ana = new RtfAnalyser(reader, swr);
      doc = ana.parse();
    } catch (Exception e) {
      e.printStackTrace(System.out);
      throw new IllegalArgumentException("Fehler beim Lesen der RTF-Datei: " +
        e.getMessage());
    }

    doc.Dump(swr);
    swr.flush();
    reader.close();

    return output.toString();
  }
}
