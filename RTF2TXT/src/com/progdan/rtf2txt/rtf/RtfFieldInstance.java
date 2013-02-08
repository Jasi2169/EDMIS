package com.progdan.rtf2txt.rtf;

import java.io.PrintWriter;


/**
 * Repräsentiert ein in das RTF-Dokument eingebettetes Feld, das
 * aus einem Namen, Parameter und einem Ergebnis besteht.
 * Beispiel: <br>
 * {\*\fldinst {TIME \\@ "dd.MM.yyyy" }}{\fldrslt {30.11.2002}}}
 * Erklärung:<br>
 * Der Name des Feldes ist TIME, der Parameter \@ "dd.MM.yyyy"
 * Das gespeicherte Ergebnis ist 30.11.2002.
 *
 * @author Daniel Finger, Lars Raap, Martin Amelsberg <br> Based on Majix by Tetrasix
 * @version 1.0
 */
public class RtfFieldInstance extends RtfObject {
  String _fieldname;
  String _fieldresult;
  String _parameters;
  int _counter;

  /**
   * Erzeugt ein neues RtfFieldInstance-Objekt
   *
   * @param data Daten des entsprechenden RTF-Feldes
   */
  RtfFieldInstance(String data) {
    int pos = data.indexOf(' ');

    if (pos <= 0) {
      _fieldname = data;
      _parameters = "";
    } else {
      _fieldname = data.substring(0, pos);
      _parameters = data.substring(pos + 1).trim();
    }

    _fieldresult = null;
  }

  /**
   * Erzeugt ein neues RtfFieldInstance-Objekt
   *
   * @param data Daten des RTF-Feldes (Name + Parameter)
   * @param result Das im Dokument gespeicherte Ergebnis des Feldes
   */
  RtfFieldInstance(String data, String result) {
    this(data);
    _fieldresult = result;
  }

  /**
   * Liefert den Namen des RTF-Feldes (der in dem Dokument gefundende Name)
   *
   * @return Name des RTF-Feldes
   */
  public String getFieldName() {
    return _fieldname;
  }

  /**
   * Liefert die Parameter des RTF-Feldes
   *
   * @return Die entsprechenden Parameter
   */
  public String getParameters() {
    return _parameters;
  }

  /**
   * Schreibt das RTF-Feld in den Output-Stream
   *
   * @param out Stream, in den geschrieben werden soll
   */
  void Dump(PrintWriter out) {
    //out.print("<field name='" + _fieldname + "' params='" + _parameters + "'");
    if (_fieldresult != null) {
            out.print(_fieldresult);
    }
    //out.print(">");

  }

  /**
   * Legt das Ergebnis des Feldes fest
   *
   * @param result Das gespeicherte Ergebnis
   */
  public void setResult(String result) {
    _fieldresult = result;
  }

  /**
   * Liefert das Ergebnis des Feldes (siehe Beschreibung der Klasse)
   *
   * @return Das Ergebnis als String
   */
  public String getResult() {
    return _fieldresult;
  }
}
