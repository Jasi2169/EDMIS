package com.progdan.rtf2txt.rtf;

import java.io.PrintWriter;


/**
 * Repr�sentiert normalen Text aus einem RTF-Dokument.
 *
 * @author Daniel Finger, Lars Raap, Martin Amelsberg <br>
 *         Based on Majix by Tetrasix
 * @version 1.0
 */
public class RtfText extends RtfObject {
  private String _data;
  private RtfTextProperties _properties;

  /**
   * Erzeugt ein neues RtfText-Objekt
   *
   * @param data Normaler Text
   */
  RtfText(String data) {
    _data = data;
  }

  /**
   * Erzeugt ein neues RtfText-Objekt
   *
   * @param data Normaler Text
   * @param properties Hier sind Eigenschaften wie fett, kursiv u.�. enthalten
   */
  RtfText(String data, RtfTextProperties properties) {
    _data = data;
    _properties = properties;
  }

  /**
   * Gibt den RTF-Text zur�ck
   *
   * @return Der Text als String
   */
  public String getData() {
    return _data;
  }

  /**
   * Gibt die Texteigenschaften zur�ck
   *
   * @return Ein Objekt, das die Texteigenschaften enth�lt
   */
  public RtfTextProperties getProperties() {
    return _properties;
  }

  /**
   * Schreibt die Daten ohne Formatierung in einen Stream
   *
   * @param out Stream, in den geschrieben werden soll
   */
  void Dump(PrintWriter out) {
    /*
                    if (_properties.isBold()) {
                            out.print("<b>");
                    }
                    if (_properties.isItalic()) {
                            out.print("<i>");
                    }
                    if (_properties.isHidden()) {
                            out.print("<h>");
                    }
                    if (_properties.isUnderlined()) {
                            out.print("<ul>");
                    }
    */
    out.print(_data);

    /*
                    if (_properties.isUnderlined()) {
                            out.print("</ul>");
                    }
                    if (_properties.isHidden()) {
                            out.print("</h>");
                    }
                    if (_properties.isItalic()) {
                            out.print("</i>");
                    }
                    if (_properties.isBold()) {
                            out.print("</b>");
                    }
    */
  }
}
