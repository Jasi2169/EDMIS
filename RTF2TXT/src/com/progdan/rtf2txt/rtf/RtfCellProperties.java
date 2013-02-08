package com.progdan.rtf2txt.rtf;

import java.io.PrintWriter;


/**
 * Diese Klasse speichert die Eigenschaften einer Tabellenzelle. Dies ist
 * zurzeit die Breite.
 *
 * @author Daniel Finger, Lars Raap, Martin Amelsberg <br>
 *         Based on Majix by Tetrasix
 * @version 1.0
 */
public class RtfCellProperties implements Cloneable {
  int _width;

  /**
   * Erzeugt ein neues RtfCellProperties-Objekt
   */
  RtfCellProperties() {
    _width = -1;
  }

  /**
   * Kopiert die Eigenschaften der Zelle und liefert ein neues
   * RtfCellProperties- Objekt zurück
   *
   * @return Das neue Objekt
   */
  protected Object clone() {
    RtfCellProperties properties = new RtfCellProperties();

    properties._width = _width;

    return properties;
  }

  /**
   * Legt die Breite der aktuellen Zelle fest
   *
   * @param width Die Breite der Zelle
   */
  void setWidth(int width) {
    _width = width;
  }

  /**
   * Liefert die Breite der Zelle
   *
   * @return Die Breite de Zelle
   */
  public int getWidth() {
    return _width;
  }

  /**
   * Schreibt die Breite in den angegebene Output-Stream. Wird in der
   * PlainText- Version nicht genutzt.
   *
   * @param out Stream, in den geschrieben werden soll
   */
  void Dump(PrintWriter out) {
    if (_width >= 0) {
      out.print(" width=\"" + _width + "\"");
    }
  }
}
