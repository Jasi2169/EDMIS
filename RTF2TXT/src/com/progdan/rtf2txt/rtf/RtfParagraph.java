package com.progdan.rtf2txt.rtf;

import java.io.PrintWriter;


/**
 * Diese Klasse repräsentiert einen RTF-Paragraphen. Dies entspricht ungefähr
 * einem Absatz.
 *
 * @author Daniel Finger, Lars Raap, Martin Amelsberg <br>
 *         Based on Majix by Tetrasix
 * @version 1.0
 */
public class RtfParagraph extends RtfBlock {
  private RtfParagraphProperties _properties;

  /**
   * Erzeugt ein neues RtfParagraph-Objekt
   */
  RtfParagraph() {
    _properties = new RtfParagraphProperties();
  }

  /**
   * Setzt die Properties des Paragraphen
   *
   * @param properties Ein Objekt vom Typ RtfParagraphProperties<br>
   *        <b>Hinweis: </b>Darf <b>null</b> sein.
   */
  void setProperties(RtfParagraphProperties properties) {
    if (properties != null) {
      _properties = (RtfParagraphProperties) properties.clone();
    }
  }

  /**
   * Liefert die diesem Paragraphen zugeordneten Properties
   *
   * @return Ein Objekt vom Typ RtfParagraphProperties<br>
   *         <b>Hinweis: </b>Kann <b>null</b> sein.
   */
  public RtfParagraphProperties getProperties() {
    return _properties;
  }

  /**
   * Liefert die Text-Properties des Paragraphen
   *
   * @return Ein neues Objekt mit den Text-Properties des Paragraphen
   */
  public RtfTextProperties getTextProperties() {
    int code = _properties.getStyle();

    if (code >= 0) {
      RtfStyleSheet styleSheet = RtfStyleSheet.getCurrentStyleSheet();

      return styleSheet.getTextProperties(code);
    } else {
      return new RtfTextProperties();
    }
  }

  /**
   * Gibt den Inhalt des RtfParagraph aus, die Formatierungsinformationen
   * werden dabei ignoriert
   *
   * @param out Stream, in den die Information geschrieben werden soll
   */
  void Dump(PrintWriter out) {
    super.Dump(out);

    //Zeilenumbruch erzeugen
    out.println("");
  }
}
