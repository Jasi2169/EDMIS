package com.progdan.rtf2txt.rtf;

import java.io.PrintWriter;


/**
 * Repräsentiert Sonderzeichen aus einer definierten Schriftart.
 *
 * @author Daniel Finger, Lars Raap, Martin Amelsberg <br>
 *         Based on Majix by Tetrasix
 * @version 1.0
 */
public class RtfSymbol extends RtfObject {
  int _code = 0;
  String _font = null;

  /**
   * Erzeugt ein neues RtfSymbol-Objekt
   *
   * @param data Das Sonderzeichen
   */
  RtfSymbol(String data) {
    data = data.substring(7);

    int pos = data.indexOf("\\f");

    if (pos != -1) {
      String scode = data.substring(0, pos);
      scode = scode.trim();
      _code = Integer.parseInt(scode);
      pos = data.indexOf("\"");

      if (pos != -1) {
        int pos2 = data.indexOf("\"", pos + 1);

        if (pos2 != -1) {
          _font = data.substring(pos + 1, pos2);
        }
      }
    }
  }

  /**
   * Liefert die Schriftart des Symbols
   *
   * @return Die Schriftart
   */
  public String getFont() {
    return _font;
  }

  /**
   * Liefert den Zeichencode des Symbols
   *
   * @return Der Zeichencode
   */
  public int getCode() {
    return _code;
  }

  /**
   * Schreibt das Symbol in den Output-Stream. In ASCII nicht benutzt.
   *
   * @param out Stream, in den geschrieben werden soll
   */
  void Dump(PrintWriter out) {
    //out.print("<symbol code=" + String.valueOf(_code) + " font='" + _font + "'>");
  }
}
