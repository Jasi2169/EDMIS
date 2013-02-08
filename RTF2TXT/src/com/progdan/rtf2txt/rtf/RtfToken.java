package com.progdan.rtf2txt.rtf;

/**
 * Die Klasse RtfToken stellt ein RTF-Token dar. Dies können Control-Words,
 * Gruppen oder Daten sein. <br>
 * Weitere Informationen sind der RTF-Spezifikation zu entnehmen.
 *
 * @author Daniel Finger, Lars Raap, Martin Amelsberg <br>
 *         Based on Majix by Tetrasix
 * @version 1.0
 */
public class RtfToken {
  public static final int BACKSLASH = 0;
  public static final int CONTROLWORD = 1;
  public static final int SPECIAL = 2;
  public static final int CONTROLSYMBOL = 3; //
  public static final int OPENGROUP = 4; // }
  public static final int CLOSEGROUP = 5; // {
  public static final int DATA = 6; //
  public static final int ASTERISK = 7; // \*
  private int _type; // token type (one of the constants before)
  private String _name; // name of the command, if any
  private String _data; // parameter or textual data

  /**
   * Erzeugt ein neues RtfToken-Objekt
   *
   * @param type Numerischer Typ des Tokens
   * @param name Name des Control-Words, wenn vorhanden
   * @param data Parameter des RTF-Control-Words
   */
  public RtfToken(int type, String name, String data) {
    _type = type;
    _name = name;
    _data = data;
  }

  /**
   * Erzeugt ein neues RtfToken-Objekt
   *
   * @param type Typ des Tokens
   */
  public RtfToken(int type) {
    _type = type;
  }

  /**
   * Erzeugt ein neues RtfToken-Objekt
   *
   * @param data Daten des Tokens
   */
  public RtfToken(String data) {
    _type = RtfToken.DATA;
    _data = data;
  }

  /**
   * Gibt den Typ des Tokens zurück
   *
   * @return Numerischer Typ des Tokens
   */
  public int getType() {
    return _type;
  }

  /**
   * Gibt den Namen des Control-Words zurück
   *
   * @return Namen des Control-Words
   */
  public String getName() {
    return _name;
  }

  /**
   * Gibt die Parameter des Control-Words zurück
   *
   * @return Daten oder Parameter
   */
  public String getData() {
    return _data;
  }

  /**
   * Gibt eine Darstellung des Tokens als String zurück
   *
   * @return String
   */
  public String toString() {
    switch (_type) {
      case BACKSLASH:
        return "\\";
      case CONTROLWORD:
        return "\\" + _name + _data;
      case SPECIAL:
        return "SPECIAL";
      case CONTROLSYMBOL:
        return "CONTROLSYMBOL";
      case OPENGROUP:
        return "{";
      case CLOSEGROUP:
        return "}";
      case DATA:
        return _data;
      default:
        return null;
    }
  }
}
