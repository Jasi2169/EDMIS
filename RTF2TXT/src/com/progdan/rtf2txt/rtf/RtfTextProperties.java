package com.progdan.rtf2txt.rtf;

/**
 * Klasse zum Speichern von Texteigenschaften. Diese sind fett, kursiv,
 * unterstrichen. Auch die Textfarbe wird hier gespeichert.
 *
 * @author Daniel Finger, Lars Raap, Martin Amelsberg >br> Based on Majix by
 *         Tetrasix
 * @version 1.0
 */
public class RtfTextProperties implements Cloneable {
  boolean _bold = false;
  boolean _italic = false;
  boolean _underlined = false;
  boolean _hidden = false;
  int _color = -1;
  int _charStyle = -1;

  /**
   * Kopiert das Objekt und liefert das neue zurück
   *
   * @return Das neue Objekt vom Typ RtfTextProperties
   */
  protected Object clone() {
    RtfTextProperties properties = new RtfTextProperties();

    properties.setBold(isBold());
    properties.setItalic(isItalic());
    properties.setHidden(isHidden());
    properties.setUnderlined(isUnderlined());
    properties.setColor(getColor());
    properties.setStyle(getStyle());

    return properties;
  }

  /**
   * Setzt die Eigenschaft fett
   *
   * @param bold True, wenn der Text fett sein soll; ansonsten false
   */
  void setBold(boolean bold) {
    _bold = bold;
  }

  /**
   * Setzt die Eigenschaft kursiv
   *
   * @param italic True, wenn der Text kursiv sein soll; ansonsten false
   */
  void setItalic(boolean italic) {
    _italic = italic;
  }

  /**
   * Setzt die Eigenschaft versteckt
   *
   * @param hidden True, wenn der Text versteckt sein soll; ansonsten false
   */
  void setHidden(boolean hidden) {
    _hidden = hidden;
  }

  /**
   * Setzt die Eigenschaft unterstrichen
   *
   * @param underlined True, wenn der Text unterstrichen sein soll; ansonsten
   *        false
   */
  void setUnderlined(boolean underlined) {
    _underlined = underlined;
  }

  /**
   * Prüft, ob der Text fett ist
   *
   * @return True, wenn der Text fett ist; ansonsten false
   */
  public boolean isBold() {
    return _bold;
  }

  /**
   * Prüft, ob der Text fett ist
   *
   * @return True, wenn der Text kursiv ist; ansonsten false
   */
  public boolean isItalic() {
    return _italic;
  }

  /**
   * Prüft, ob der Text versteckt ist
   *
   * @return True, wenn der Text versteckt ist; ansonsten false
   */
  public boolean isHidden() {
    return _hidden;
  }

  /**
   * Prüft, ob der Text unterstrichen ist
   *
   * @return True, wenn der Text unterstrichen ist; ansonsten false
   */
  public boolean isUnderlined() {
    return _underlined;
  }

  /**
   * Weist der Property eine Identifikationsnummer zu (siehe \s in der
   * RTF-Spezifikation)
   *
   * @param style Nummer des Styles
   */
  void setStyle(int style) {
    _charStyle = style;
  }

  /**
   * Liefert die Nummer des Styles
   *
   * @return Nummer des Styles
   */
  public int getStyle() {
    return _charStyle;
  }

  /**
   * Setzt die Farbe des Textes
   *
   * @param color Folgende Farben sind definiert:
   *         <table>
   *         <tr><th>Nummer</th><th>Farbe</th></tr>
   *         <tr><td>1</td><td>Black</td></tr>
   *         <tr><td>2</td><td>Blue</td></tr>
   *         <tr><td>3</td><td>Aqua</td></tr>
   *         <tr><td>4</td><td>Lime</td></tr>
   *         <tr><td>5</td><td>Fuschia</td></tr>
   *         <tr><td>6</td><td>Red</td></tr>
   *         <tr><td>7</td><td>Yellow</td></tr>
   *         <tr><td>8</td><td>White</td></tr>
   *         <tr><td>9</td><td>Navy</td></tr>
   *         <tr><td>10</td><td>Teal</td></tr>
   *         <tr><td>11</td><td>Green</td></tr>
   *         <tr><td>12</td><td>Purple</td></tr>
   *         <tr><td>13</td><td>Maroon</td></tr>
   *         <tr><td>14</td><td>Olive</td></tr>
   *         <tr><td>15</td><td>Gray</td></tr>
   *         <tr><td>16</td><td>Silver</td></tr>
   *         </table>
   */
  void setColor(int color) {
    _color = color;
  }

  /**
   * Liefert die Farbe des Textes
   *
   * @return Farbe; Tabelle s. setColor()
   */
  public int getColor() {
    return _color;
  }

  /**
   * Liefert den Namen zu der gespeicherten Farbe
   *
   * @return s. Tabelle setColor()
   */
  String getColorName() {
    return getColorNamefromCode(_color);
  }
  /**
   * Liefert den Namen zu der angegebenen Farbe
   *
   * @return s. Tabelle setColor()
   */
  static String getColorNamefromCode(int color) {
    if (color == 1) {
      return "Black";
    } else if (color == 2) {
      return "Blue";
    } else if (color == 3) {
      return "Aqua";
    } else if (color == 4) {
      return "Lime";
    } else if (color == 5) {
      return "Fuschia";
    } else if (color == 6) {
      return "Red";
    } else if (color == 7) {
      return "Yellow";
    } else if (color == 8) {
      return "White";
    } else if (color == 9) {
      return "Navy";
    } else if (color == 10) {
      return "Teal";
    } else if (color == 11) {
      return "Green";
    } else if (color == 12) {
      return "Purple";
    } else if (color == 13) {
      return "Maroon";
    } else if (color == 14) {
      return "Olive";
    } else if (color == 15) {
      return "Gray";
    } else if (color == 16) {
      return "Silver";
    } else {
      return "";
    }
  }

  /**
   * Liefert den Farbcode zu der gespeicherten Farbe
   *
   * @return cA für Black, cB für Blue, cC für Aqua usw. -> s. Tabelle
   *         setColor()
   */
  public String getColorCode() {
    if (_color == 1) {
      return "cA";
    } else if (_color == 2) {
      return "cB";
    } else if (_color == 3) {
      return "cC";
    } else if (_color == 4) {
      return "cD";
    } else if (_color == 5) {
      return "cE";
    } else if (_color == 6) {
      return "cF";
    } else if (_color == 7) {
      return "cG";
    } else if (_color == 8) {
      return "cH";
    } else if (_color == 9) {
      return "cI";
    } else if (_color == 10) {
      return "cJ";
    } else if (_color == 11) {
      return "cK";
    } else if (_color == 12) {
      return "cL";
    } else if (_color == 13) {
      return "cM";
    } else if (_color == 14) {
      return "cN";
    } else if (_color == 15) {
      return "cO";
    } else if (_color == 16) {
      return "cP";
    } else {
      return "";
    }
  }

  /**
   * Liefert eine String-Repräsentation des Inhalts
   *
   * @return String mit Aufzählung der Formate oder - falls keine definiert
   *         sind - "plain"
   */
  public String toString() {
    StringBuffer buf = new StringBuffer();
    boolean empty = true;

    if (_bold) {
      buf.append("bold ");
      empty = false;
    }

    if (_italic) {
      buf.append("italic ");
      empty = false;
    }

    if (_underlined) {
      buf.append("underlined ");
      empty = false;
    }

    if (_color != -1) {
      buf.append(getColorName());
      empty = false;
    }

    if (empty) {
      return "plain";
    } else {
      return buf.toString();
    }
  }
}
