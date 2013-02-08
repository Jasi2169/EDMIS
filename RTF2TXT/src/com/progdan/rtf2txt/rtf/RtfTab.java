package com.progdan.rtf2txt.rtf;

import java.io.PrintWriter;


/**
 * Die Repräsentation eines Tabulators in RTF (Control Word: \tab).
 *
 * @author Daniel Finger, Lars Raap, Martin Amelsberg <br>
 *         Based on Majix by Tetrasix
 * @version 1.0
 */
public class RtfTab extends RtfObject {
  /**
   * Erzeugt ein neues RtfTab-Objekt
   */
  RtfTab() {
  }

  /**
   * Gibt einen Tabulator aus. Bisher durch 2 Leerzeichen simuliert.
   *
   * @param out Stream, in den der Output geschrieben wird
   */
  void Dump(PrintWriter out) {
    // out.print("&nbsp;&nbsp;&nbsp;&nbsp;");
    out.print("  ");
  }
}
