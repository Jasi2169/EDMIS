package com.progdan.rtf2txt.rtf;

import java.io.PrintWriter;


/**
 * Klasse, um die Eigenschaften eines Rtf-Paragraphs zu speichern. Dazu gehören
 * Informationen über die Nummerierung oder darüber, ob ein Seitenumbruch nach
 * dem Objekt folgen soll.
 *
 * @author Daniel Finger, Lars Raap, Martin Amelsberg <br>
 *         Based on Majix by Tetrasix
 * @version 1.0
 */
public class RtfParagraphProperties implements Cloneable {
  public static final int STYLE_SIMPLE = 0;
  public static final int STYLE_NUMBERED = 1;
  public static final int STYLE_BULLET = 2;
  private int _style = -1;
  private String _bullet = null;
  private int _numstyle = STYLE_SIMPLE;
  private boolean _pbreak = false;

  /**
   * Setzt den Stil des Paragraphs
   *
   * @param style Bisher nur 0 (STYLE_SIMPLE)
   */
  void setStyle(int style) {
    _style = style;
  }

  /**
   * Setzt den Art der Nummerierung (keine, mit Zahlen oder mit Kugeln
   * ("bullets")) des Paragraphs
   *
   * @param numstyle 0 (STYLE_SIMPLE) 1 (STYLE_NUMBERED)<br> 2 (STYLE_BULLET)
   */
  void setNumStyle(int numstyle) {
    _numstyle = numstyle;
  }

  /**
   * Liefert den Stil des aktuellen Dokuments
   *
   * @return 0 (STYLE_SIMPLE) oder -1, wenn Style nicht gesetzt ist
   */
  public int getStyle() {
    return _style;
  }

  /**
   * Liefert die Art der Nummerierung des aktuellen Paragraphs
   *
   * @return STYLE_NUMBERED ODER STYLE_BULLET. Falls keine Nummerierung
   *         erfolgt, wird STYLE_SIMPLE zurückgegeben.
   */
  public int getNumStyle() {
    return _numstyle;
  }

  /**
   * Setzt das "Bullet"-Zeichen einer Aufzählung
   *
   * @param bullet Das "Bullet"-Zeichen
   */
  void setBullet(String bullet) {
    _bullet = bullet;
  }

  /**
   * Liefert das "Bullet"-Zeichen
   *
   * @return Das "Bullet"-Zeichen als String
   */
  public String getBullet() {
    return _bullet;
  }

  /**
   * Setzt einen Seitenumbruch nach diesem Paragraph
   *
   * @param pbreak True oder False
   */
  void setPageBreak(boolean pbreak) {
    _pbreak = pbreak;
  }

  /**
   * Liefert den Status des Seitenumbruchs nach diesem Paragraph
   *
   * @return True, wenn nach diesem Paragraphen ein Seitenumbruch folgen soll
   *         ansonsten false
   */
  public boolean getPageBreak() {
    return _pbreak;
  }

  /**
   * Setzt den Paragraph auf Grundeinstellungen zurück
   */
  void reset() {
    _style = -1;

    //_bullet = null;
    _numstyle = STYLE_SIMPLE;
  }

  /**
   * Kopiert das Objekt und liefert das neue Objekt zurück
   *
   * @return Ein Objekt vom Typ RtfParagraphProperties
   */
  protected Object clone() {
    RtfParagraphProperties properties = new RtfParagraphProperties();

    properties.setStyle(getStyle());
    properties.setBullet(getBullet());
    properties.setNumStyle(getNumStyle());
    properties.setPageBreak(getPageBreak());

    return properties;
  }

  /**
   * Gibt die Properties des Paragraphs aus. Wird im PlainText-Konverter nicht
   * benutzt.
   *
   * @param out Stream, in den die Information geschrieben wird
   */
  void Dump(PrintWriter out) {
    if (_style > 0) {
      out.print(" style=\"" + _style + "\"");
    }

    if (_bullet != null) {
      out.print(" bullet=\"" + _bullet + "\"");
    }

    if (_numstyle > 0) {
      out.print(" numstyle=\"" + _numstyle + "\"");
    }

    if (_pbreak) {
      out.print(" pagebreak=\"1\"");
    }
  }
}
