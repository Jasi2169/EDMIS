package com.progdan.rtf2txt.rtf;

import java.io.PrintWriter;


/**
 * Repräsentiert einen in RTF eingebetteten Hyperlink.
 *
 * @author Daniel Finger, Lars Raap, Martin Amelsberg <br>
 *         Based on Majix by Tetrasix
 * @version 1.0
 */
public class RtfHyperLink extends RtfObject {
  String _url;
  String _refid;
  String _text;

  /**
   * Erzeugt ein neues RtfHyperLink-Objekt
   *
   * @param url URL des Hyperlinks
   * @param text Text, der anstelle des Hyperlinks angezeigt wird
   */
  RtfHyperLink(String url, String text) {
    _url = url;
    _text = text;

    int pos = _url.lastIndexOf('#');
    String id = "";

    if (pos >= 0) {
      _refid = _url.substring(pos + 1);
      _url = _url.substring(0, pos);
    } else {
      _refid = "";
    }
  }

  /**
   * Liefert die URL des Hyperlinks
   *
   * @return Die URL
   */
  public String getUrl() {
    return _url;
  }

  /**
   * Liefert den "Anchor" des HTML-Dokuments. Beispiel: http://here.com#where
   *
   * @return "Anchor" (für unser Beispiel: "where")
   */
  public String getRefid() {
    return _refid;
  }

  /**
   * Liefert den Text, der anstelle des Hyperlinks angezeigt wird
   *
   * @return Der "Alternativtext"
   */
  public String getText() {
    return _text;
  }

  /**
   * Schreibt den Hyperlink als "reinen" Text in den Stream. Geschrieben wird,<br>
   * falls Alternativtext vorhanden ist, der Alternativtext (URL),<br>
   * ansonsten wird einfach die URL augegeben
   *
   * @param out Stream, in den geschrieben werden soll
   */
  void Dump(PrintWriter out) {
    if (_refid.equals("") == false) {
      if (((_text != null) && (_text.equals(_url + "#" + _refid) == true)) ||
            (_text == null)) {
        out.print(_url + "#" + _refid);
      } else if (_text != null) {
        out.print(_text + " (" + _url + "#" + _refid + ")");
      }
    } else if (((_text != null) && (_url.equals(_text) == true)) ||
          (_text == null)) {
      out.print(_url);
    } else if (_text != null) {
      out.print(_text + " (" + _url + ")");
    }
  }
}
