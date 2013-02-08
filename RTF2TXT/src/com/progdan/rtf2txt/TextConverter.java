// Title:        Ossivista 2
// Version:      1.02
// Copyright:    Copyright (c) 2002
// Author:       Christian Abt
// Company:      Labor f�r parallele Prozesse, Fachhochschule OOW
// Description:  Interface f�r einen bin�ren Textkonverter
package com.progdan.rtf2txt;

/**
 * Interface f�r einen bin�ren Textkonverter An dieser Stelle werden externe
 * Konverter f�r weitere Dateitypen eingebunden. Derzeit sind noch keine
 * implementiert.
 */
public interface TextConverter {
  /**
   * Konvertiert eine bin�re Datei in ein Textdokument Parameter: - file:
   * Bin�re Textdatei R�ckgabewert: Text Ausnahmebehandlung:
   * IllegalArgumentException, falls Bin�rdatei ung�ltig
   *
   * @return DOCUMENT ME!
   */
  public String convertToText(byte[] file) throws IllegalArgumentException;
}
