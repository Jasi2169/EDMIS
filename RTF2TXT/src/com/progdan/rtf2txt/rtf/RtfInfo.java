package com.progdan.rtf2txt.rtf;

import java.io.PrintWriter;


/**
 * Repräsentiert Meta-Informationen des RTF-Dokuments. Meta-Informationen sind
 * der Titel des Dokuments, der Autor sowie die Firma. Diese Informationen
 * werden  gespeichert, aber nicht angezeigt.
 *
 * @author Daniel Finger, Lars Raap, Martin Amelsberg <br>
 *         Based on Majix by Tetrasix
 * @version 1.0
 */
public class RtfInfo {
  static public final int TITLE = 0;
  static public final int SUBJECT = 1;
  static public final int AUTHOR = 2;
  static public final int OPERATOR = 3;
  static public final int MANAGER = 4;
  static public final int COMPANY = 5;
  private String[] _theProperties = new String[6];

  /**
   * Erzeugt ein neues RtfInfo-Objekt
   */
  RtfInfo() {
  }

  /**
   * Speichert die Meta-Information in dem Objekt der Klasse
   *
   * @param code Typ der Information als numerischer Wert
   * @param value Inhalt der Information
   */
  void defineProperty(int code, String value) {
    if ((code >= 0) && (code <= 5)) {
      _theProperties[code] = value;
    }
  }

  /**
   * Liefert den Inhalt einer Information
   *
   * @param code Typ der gewünschten Information
   *
   * @return Inhalt der Information
   */
  public String getProperty(int code) {
    if ((code >= 0) && (code <= 255)) {
      return _theProperties[code];
    }

    return null;
  }

  /*
    private void Dump(PrintWriter out, String name, int code) {
      if (_theProperties[code] != null) {
        out.println("<" + name + ">" + _theProperties[code] + "</" + name + ">");
      }
    }
  */

  /**
   * Liefert Informationen über das Dokument. Aktuell schreibt die Funktion
   * nichts, da in Textdateien Meta-Informationen nicht versteckt werden
   * können
   *
   * @param out Stream, in den die Informationen geschrieben werden sollen
   */
  void Dump(PrintWriter out) {
    /*
        out.println("<info>");
        Dump(out, "title", TITLE);
        Dump(out, "subject", SUBJECT);
        Dump(out, "author", AUTHOR);
        Dump(out, "operator", OPERATOR);
        Dump(out, "manager", MANAGER);
        Dump(out, "company", COMPANY);
        out.println("</info>");
    */
  }
}
