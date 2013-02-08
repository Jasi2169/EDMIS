package com.progdan.rtf2txt.rtf;

import java.io.PrintWriter;


/**
 * Diese Klasse repr�sentiert ein Bild, das in ein RTF-Dokument eingebettet
 * wurde.
 *
 * @author Daniel Finger, Lars Raap, Martin Amelsberg <br>
 *         Based on Majix by Tetrasix
 * @version 1.0
 */
public class RtfPicture extends RtfObject {
  String _data;
  int _counter;
  String _pictdata;
  int _picw;
  int _pich;
  int _picwg;
  int _pichg;

  /**
   * Erzeugt ein neues RtfPicture-Objekt
   *
   * @param counter Bild-Z�hler
   * @param pictdata RTF-codierte Daten des Bildes
   * @param picw Breite des Bildes
   * @param pich H�he des Bildes
   * @param picwg Breite des Bildes, in der es angezeigt werden soll
   * @param pichg H�he des Bildes, in der es angezeigt werden soll
   */
  RtfPicture(int counter, String pictdata, int picw, int pich, int picwg,
    int pichg) {
    _data = null;
    _counter = counter;

    _pictdata = pictdata;
    _picw = picw;
    _pich = pich;
    _picwg = picwg;
    _pichg = pichg;
  }

  /**
   * Erzeugt ein neues RtfPicture-Objekt
   *
   * @param data RTF-codierten Daten des Bildes
   */
  RtfPicture(String data) {
    _data = data;
    _counter = -1;

    _pictdata = null;
    _picw = 0;
    _pich = 0;
    _picwg = 0;
    _pichg = 0;
  }

  /**
   * Unbekannt, es wird nur benutzt, wenn in einem Rtf-Feld das Schl�sselwort
   * "INCLUDEPICTURE" folgt. Dies ist nicht in der RTF-Spezifikation erw�hnt
   * und konnte durch Tests auch nicht erzeugt werden.
   *
   * @return String, der die Daten enth�lt
   */
  public String getData() {
    return _data;
  }

  /**
   * Liefert den Bild-Z�hler
   *
   * @return Der Z�hler des Bildes
   */
  public int getCounter() {
    return _counter;
  }

  /**
   * Bisher nicht vorgesehen. Hier k�nnte ein PIC2ASCII-Konverter ansetzen ;-)
   *
   * @param out Der Output-Stream
   */
  void Dump(PrintWriter out) {
    //out.print("<pict count=" + String.valueOf(_counter) + ">");
  }

  /**
   * Liefert die RTF-codierten Daten des Bildes
   *
   * @return RTF-codierten Daten des Bildes
   */
  public String getPictdata() {
    return _pictdata;
  }

  /**
   * Liefert die Breite des Bildes
   *
   * @return Breite des Bildes
   */
  public int getPicw() {
    return _picw;
  }

  /**
   * Liefert die H�he des Bildes
   *
   * @return H�he des Bildes
   */
  public int getPich() {
    return _pich;
  }

  /**
   * Liefert die Breite, in der das Bild angezeigt werden soll
   *
   * @return Breite, in der das Bild angezeigt werden soll
   */
  public int getPicwg() {
    return _picwg;
  }

  /**
   * Liefert die H�he, in der das Bild angezeigt werden soll
   *
   * @return H�he, in der das Bild angezeigt werden soll
   */
  public int getPichg() {
    return _pichg;
  }
}
