// Title:        Ossivista 2
// Version:      1.02
// Copyright:    Copyright (c) 2002
// Author:       Christian Abt
// Company:      Labor für parallele Prozesse, Fachhochschule OOW
// Description:  Interface für einen binären Textkonverter
package com.progdan.rtf2txt;

/**
 * Interface für einen binären Textkonverter An dieser Stelle werden externe
 * Konverter für weitere Dateitypen eingebunden. Derzeit sind noch keine
 * implementiert.
 */
public interface TextConverter {
  /**
   * Konvertiert eine binäre Datei in ein Textdokument Parameter: - file:
   * Binäre Textdatei Rückgabewert: Text Ausnahmebehandlung:
   * IllegalArgumentException, falls Binärdatei ungültig
   *
   * @return DOCUMENT ME!
   */
  public String convertToText(byte[] file) throws IllegalArgumentException;
}
