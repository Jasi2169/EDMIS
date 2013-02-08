package com.progdan.rtf2txt.rtf;

import java.io.PrintWriter;


/**
 * Oberklasse für die einzelnen RTF-Objekte, aus denen ein RTF-Dokument
 * bestehen kann.
 *
 * @author Daniel Finger, Lars Raap, Martin Amelsberg <br>
 *         Based on Majix by Tetrasix
 * @version 1.0
 */
public class RtfObject {
  private int _type;

  /**
   * Liefert den Typ des Objektes
   *
   * @return Nummer, die dem Typ entspricht
   */
  int getType() {
    return _type;
  }

  /**
   * Gibt die Daten des Objekts aus
   *
   * @return null, da es nur eine Oberklasse für die eigentlichen Objekte ist
   */
  public String getData() {
    return null;
  }

  /**
   * Gibt den Inhalt des Objektes aus
   *
   * @param out Ausgabe-Stream
   */
  void Dump(PrintWriter out) {
    out.println(toString());
  }
}
