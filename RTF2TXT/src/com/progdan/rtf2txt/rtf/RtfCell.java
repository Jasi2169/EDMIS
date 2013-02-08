package com.progdan.rtf2txt.rtf;

import java.io.PrintWriter;


/**
 * Diese Klasse repräsentiert eine Tabellenzelle.
 *
 * @author Daniel Finger, Lars Raap, Martin Amelsberg <br>
 *         Based on Majix by Tetrasix
 * @version 1.0
 */
public class RtfCell extends RtfCompoundObject {
  private RtfCellProperties _properties;

  /**
   * Erzeugt ein neues RtfCell-Objekt
   */
  RtfCell() {
    _properties = new RtfCellProperties();
  }

  /**
   * Weist der Zelle die Eigenschaften zu
   *
   * @param properties Objekt vom Typ RtfCellProperties, das die Eigenschaften
   *        enthält
   */
  void setProperties(RtfCellProperties properties) {
    if (properties != null) {
      _properties = (RtfCellProperties) properties.clone();
    }
  }

  /**
   * Liefert das Objekt, das die aktuellen Eigenschaften speichert
   *
   * @return Objekt vom Typ RtfCellProperties
   */
  public RtfCellProperties getProperties() {
    return _properties;
  }

  /**
   * Schreibt den Inhalt der Zelle in den angegeben Output-Stream. Die
   * Eigenschaften der Zelle werden dabei ignoriert.
   *
   * @param out Stream, in den geschrieben werden soll
   */
  void Dump(PrintWriter out) {
    /*                out.print("<cell");
                    if (_properties != null) {
                            _properties.Dump(out);
                    }
                    out.println(">");
        */
    super.Dump(out);

    //		out.println("</cell>");
  }
}
