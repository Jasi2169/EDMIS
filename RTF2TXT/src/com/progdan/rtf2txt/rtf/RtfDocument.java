package com.progdan.rtf2txt.rtf;

import java.io.File;
import java.io.PrintWriter;


/**
 * Repräsentation eines RTF-Dokuments.
 *
 * @author Daniel Finger, Lars Raap, Martin Amelsberg <br>
 *         Based on Majix by Tetrasix
 * @version 1.0
 */
public class RtfDocument extends RtfCompoundObject {
  RtfStyleSheet _theStyles = null;
  RtfExternalEntities _theEntities = null;
  RtfInfo _theInfo = null;
  String _filename;

  /**
   * Erzeugt ein neues RtfDocument-Objekt
   *
   * @param filename Dateiname des Dokuments
   */
  public RtfDocument(String filename) {
    _filename = filename;
  }

  /**
   * Weist dem Dokument ein RTF-Stylesheet zu
   *
   * @param styles Stylesheet vom Typ RtfStyleSheet
   */
  public void setStyleSheet(RtfStyleSheet styles) {
    _theStyles = styles;
  }

  /**
   * Liefert das Stylesheet des Dokuments
   *
   * @return Stylesheet des Dokuments
   */
  public RtfStyleSheet getStyleSheet() {
    return _theStyles;
  }

  /**
   * Weist dem Dokument die externen Verweise auf Dateien zu
   *
   * @param entities Die gesammelten externen Verweise
   */
  public void setExternalEntities(RtfExternalEntities entities) {
    _theEntities = entities;
  }

  /**
   * Liefert eine ContainerKlasse, die die externen Verweise enthält
   *
   * @return Die besagte Containerklasse
   */
  public RtfExternalEntities getExternalEntities() {
    return _theEntities;
  }

  /**
   * Liefert eine PlainText-Darstellung des Dokuments ohne Stylesheet, Entities
   * oder sonstige Informationen
   *
   * @param out Stream, in den die Information geschrieben werden soll
   */
  public void Dump(PrintWriter out) {
    //		out.println("<rtfdoc>");
    //		_theStyles.Dump(out);
    //		_theEntities.Dump(out);
    //		if (_theInfo != null) {
    //			_theInfo.Dump(out);
    //		}
    super.Dump(out);

    //		out.println("</rtfdoc>");
  }

  /**
   * Setzt zusätzliche Infos zum RTF-Dokument
   *
   * @param info Ein Objekt, das die Informationen enthält
   */
  public void setInfo(RtfInfo info) {
    _theInfo = info;
  }

  /**
   * Gibt zusätzliche Informationen zurück
   *
   * @return Ein Objekt, das die Informationen enthält
   */
  public RtfInfo getInfo() {
    return _theInfo;
  }

  /**
   * Liefert den Dateinamen des RTF-Dokuments
   *
   * @return Hier nur "Out of DB"
   */
  public String getFilePath() {
    return _filename;
  }

  /**
   * Liefert den Dateinamen ohne Endung zurück
   *
   * @return Dateiname ohne Endung
   */
  public String getFileName() {
    String filename = _filename;

    int pos = filename.lastIndexOf(File.separatorChar);

    if (pos >= 0) {
      return filename.substring(pos + 1);
    } else {
      return filename;
    }
  }
}
