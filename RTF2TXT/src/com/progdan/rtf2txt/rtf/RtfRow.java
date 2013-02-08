package com.progdan.rtf2txt.rtf;

import java.io.PrintWriter;


/**
 * Repräsentiert eine Tabellenzeile in dem Dokument.
 *
 * @author Daniel Finger, Lars Raap, Martin Amelsberg <br>
 *         Based on Majix by Tetrasix
 * @version 1.0
 */
public class RtfRow extends RtfBlock {
  private RtfRowProperties _properties;

  /**
   * Erzeugt ein neues RtfRow-Objekt
   */
  RtfRow() {
    _properties = new RtfRowProperties();
  }

  /**
   * Legt die Eigenschaften der Tabellenzeile fest
   *
   * @param properties Objekt vom Typ RtfRowProperties
   */
  void setProperties(RtfRowProperties properties) {
    if (properties != null) {
      _properties = (RtfRowProperties) properties.clone();
    }
  }

  /**
   * Liefert die gespeicherten Eigenschaften der Tabellenzeile
   *
   * @return Ein Objekt vom Typ RtfRowProperties
   */
  public RtfRowProperties getProperties() {
    return _properties;
  }

  /**
   * Schreibt den Inhalt der Tabellenzeile in den Output-Stream
   *
   * @param out Stream, in den geschrieben werden soll
   */
  void Dump(PrintWriter out) {
    //	if (_properties != null) {
    //		_properties.Dump(out);
    //	}
    super.Dump(out);
  }
}
