package com.progdan.rtf2txt.rtf;

import java.io.PrintWriter;
import java.util.Vector;


/**
 * Implementiert eine Containerklasse, die weitere RTF-Objekte enthalten kann.
 * Bsp: Ein RtfRow stellt eine Tabellenzeile dar, diese kann aus mehreren
 * Zellen bestehen.
 *
 * @author Daniel Finger, Lars Raap, Martin Amelsberg based on Majix by
 *         Tetrasix
 * @version 1.0
 */
public class RtfCompoundObject extends RtfObject {
  protected Vector _content;

  /**
   * Erzeugt ein neues RtfCompoundObject-Objekt
   */
  RtfCompoundObject() {
    _content = new Vector();
  }

  /**
   * Fügt ein neues Element dem Inhalt der Klasse hinzu
   *
   * @param object Das hinzuzufügende RtfObject
   */
  void add(RtfObject object) {
    _content.addElement(object);
  }

  /**
   * Liefert die Anzahl der eingelagerten Objekte
   *
   * @return Anzahl
   */
  public int size() {
    return _content.size();
  }

  /**
   * Liefert das eingelagerte Objekt an der Position pos
   *
   * @param pos Index des eingelagerten Objektes
   *
   * @return Das Objekt an der Position pos
   */
  public RtfObject getObject(int pos) {
    return (RtfObject) _content.elementAt(pos);
  }

  /**
   * Ruft die Methode "dump" für jedes eingelagerte Objekt auf.
   *
   * @param out Outputstream, in den die einzelnen "Dumps" gespeichert werden
   */
  void Dump(PrintWriter out) {
    for (int ii = 0; ii < _content.size(); ii++) {
      ((RtfObject) _content.elementAt(ii)).Dump(out);
    }
  }

  /**
   * Ruft die Methode "getdata" für jedes eingelagerte Objekt auf. Die
   * einzelnen Ausgaben werden gesammelt und als String zurückgegeben.
   *
   * @return String, der die gesammelten Ausgaben enthält
   */
  public String getData() {
    StringBuffer buf = new StringBuffer();

    for (int ii = 0; ii < _content.size(); ii++) {
      buf.append(((RtfObject) _content.elementAt(ii)).getData());
    }

    return buf.toString();
  }
}
