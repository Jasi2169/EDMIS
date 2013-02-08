package com.progdan.rtf2txt.rtf;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PushbackInputStream;


/**
 * Klasse zum Einlesen von RTF-Dokumenten. Hier werden auch die Sonderzeichen
 * umgesetzt und die einzelnen RTF-Token voneinander getrennt.
 *
 * @author Daniel Finger, Lars Raap, Martin Amelsberg <br>
 *         Based on Majix by Tetrasix
 * @version 1.0
 */
public class RtfReader {
  private PushbackInputStream _in;
  private StringBuffer _buf;
  private int _depth;
  private String _filename;
  private long _nbtoken;

  /**
   * Erzeugt ein neues RtfReader-Objekt
   *
   * @param file Byte-Array der Datei
   */
  public RtfReader(byte[] file) {
    open(file);
  }

  /**
   * Initialisiert die Eigenschaften des RtfReaders
   *
   * @param file Byte-Array der Datei
   */
  public void open(byte[] file) {
    _in = new PushbackInputStream(new ByteArrayInputStream(file));
    _buf = new StringBuffer();
    _depth = 0;
    _filename = "Out of DB";
    _nbtoken = 0;
  }

  /**
   * Gibt das nächste Token des RTF-Dokuments zurück und nimmt eine Einteilung
   * in die in RtfToken angegebenen Klassen (Data, ControlWord, ...) vor. Auch
   * Sonderzeichen werden hier erkannt und konvertiert.
   *
   * @return Ein RtfToken
   *
   * @throws IOException Falls ein Problem auftritt
   */
  public RtfToken getNextToken() throws IOException {
    RtfToken tok;

    //_buf.setLength(0);
    _buf = new StringBuffer();
    _nbtoken++;

    for (;;) {
      int ch = _in.read();

      if (ch == -1) {
        return null;
      } else if (ch == '\\') {
        if (_buf.length() > 0) {
          _in.unread(ch);

          String data = _buf.toString();
          _buf.setLength(0);

          return new RtfToken(data);
        }

        ch = _in.read();

        if (ch == '*') {
          return new RtfToken(RtfToken.ASTERISK);
        }

        // In diesem Block werden Sonderzeichen konvertiert. Zu den Sonder-
        // zeichen gehören auch die Umlaute.
        // Sonderzeichen beginnen mit \' und einer zweistelligen Hex-Zahl.
        if (ch == '\'') {
          char ch1 = (char) _in.read();
          char ch2 = (char) _in.read();
          ch = (Character.digit(ch1, 16) * 16) + Character.digit(ch2, 16);

          // Die typografischen Zeichen von Word in normale "Gänsefüßchen"
          // umwandeln. Es wird ASCII-Code genutzt.
          if ((ch == 132) || (ch == 147) || (ch == 148)) {
            ch = 34;
          }

          _buf.append((char) ch);

          // Hier wird das Eurozeichen umgesetzt.
          if (ch == 128) {
            _buf.deleteCharAt(_buf.length() - 1);
            _buf.append("Euro");
          }

          String data = _buf.toString();

          return new RtfToken(data);
        } else if (ch == '\\') {
          return new RtfToken("\\");
        } else if (ch == '~') {
          return new RtfToken(" ");
        } else if (ch == '{') {
          return new RtfToken("{");
        } else if (ch == '}') {
          return new RtfToken("}");
        }

        while (Character.isLetter((char) ch)) {
          _buf.append((char) ch);
          ch = _in.read();
        }

        String name = _buf.toString();
        _buf.setLength(0);

        if (ch == '-') {
          _buf.append((char) ch);
          ch = _in.read();
        }

        while (Character.isDigit((char) ch)) {
          _buf.append((char) ch);
          ch = _in.read();
        }

        if (!Character.isSpaceChar((char) ch)) {
          _in.unread(ch);
        }

        String data = _buf.toString();
        _buf.setLength(0);

        return new RtfToken(RtfToken.CONTROLWORD, name, data);
      } else if (ch == '{') {
        if (_buf.length() > 0) {
          _in.unread(ch);

          String data = _buf.toString();

          return new RtfToken(data);
        }

        _depth++;

        return new RtfToken(RtfToken.OPENGROUP);
      } else if (ch == '}') {
        if (_buf.length() > 0) {
          _in.unread(ch);

          String data = _buf.toString();

          return new RtfToken(data);
        }

        _depth--;

        return new RtfToken(RtfToken.CLOSEGROUP);
      } else if (ch == '\r') {
      } else if (ch == '\n') {
      } else {
        _buf.append((char) ch);
      }
    }
  }

  /**
   * Liefert die aktuelle Tiefe der RTF-Gruppen (eine Gruppe begint mit "{" und
   * endet mit "}"). Die Gruppen dürfen verschachtelt werden.
   *
   * @return Tiefe der Verschachtelung
   */
  public int getDepth() {
    return _depth;
  }

  /**
   * Gibt die Anzahl der Tokens zurück
   *
   * @return Anzahl der Tokens
   */
  public long getTokenCount() {
    return _nbtoken;
  }

  /**
   * Schließt den Eingabe-Stream
   */
  public void close() {
    if (_in != null) {
      try {
        _in.close();
      } catch (IOException e) {
        System.out.println("Exception when closing " + _filename);
      }

      _in = null;
      _buf = null;
      _filename = null;
    }
  }

  /**
   * Gibt den Dateinamen der RTF-Datei zurück
   *
   * @return Bisher immer nur "Out of DB"
   */
  public String getFileName() {
    return _filename;
  }
}
