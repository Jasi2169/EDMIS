package com.progdan.rtf2txt.rtf;

/**
 * Diese Klasse repräsentiert ein Bild, das nur in der RTF-Datei referenziert
 * und nicht eingebettet wurde.
 *
 * @author Daniel Finger, Lars Raap, Martin Amelsberg <br>
 *         Based on Majix by Tetrasix
 * @version 1.0
 */
class RtfPictureExternalEntity extends RtfExternalEntity {
  /**
   * Erzeugt ein neues RtfPictureExternalEntity-Objekt
   *
   * @param counter Übergabe der bisherigen Anzahl der Bilder in dem Dokument
   * @param sysid System-ID
   */
  RtfPictureExternalEntity(int counter, String sysid) {
    super(format_counter(counter), sysid + format_counter(counter));
  }

  /**
   * Formatiert eine Zahl. Die Zahl wird auf mindesten 3 Stellen formatiert:<br>
   * 2   -> 002<br>
   * 23  -> 023<br>
   * 234 -> 234<br>
   *
   * @param icounter Integer, der formatiert werden soll
   *
   * @return String, der die Zahl auf 3 Stellen konvertiert enthält
   */
  static String format_counter(int icounter) {
    String counter = String.valueOf(icounter);

    if (counter.length() < 3) {
      counter = "000" + counter;
      counter = counter.substring(counter.length() - 3, counter.length());
    }

    return counter;
  }
}
