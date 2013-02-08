package com.progdan.rtf2txt.rtf;

import java.io.PrintWriter;


/**
 * Oberklasse für Blockstrukturen des Dokuments.
 *
 * @author Daniel Finger, Lars Raap, Martin Amelsberg <br>
 *         Based on Majix by Tetrasix
 * @version 1.0
 */
public class RtfBlock extends RtfCompoundObject {
  /**
   * Erzeugt ein neues RtfBlock-Objekt.
   */
  RtfBlock() {
  }

  /**
   * Schreibt den RTF-Block in den Output-Stream
   *
   * @param out Stream, in den geschrieben wird.
   */
  void Dump(PrintWriter out) {
    super.Dump(out);
  }
}
