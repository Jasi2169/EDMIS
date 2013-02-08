package com.progdan.rtf2txt.rtf;

import java.io.PrintWriter;


/**
 * Diese Klasse repr�sentiert einen Seitenumbruch.
 *
 * @author Daniel Finger, Lars Raap, Martin Amelsberg <br>
 *         Based on Majix by Tetrasix
 * @version 1.0
 */
public class RtfLineBreak extends RtfObject {
  /**
   * Erzeugt ein neues RtfLineBreak-Objekt
   */
  RtfLineBreak() {
  }

  /**
   * Schreibt die Daten eines Seitenumbruchs in einen Stream. Da ASCII keinen
   * Seitenumbruch unterst�tzt, wird hier nichts geschrieben.
   *
   * @param out Stream, in den die Daten des Seitenumbruchs geschrieben werden
   */
  void Dump(PrintWriter out) {
    //out.print("<page/>");
  }
}
