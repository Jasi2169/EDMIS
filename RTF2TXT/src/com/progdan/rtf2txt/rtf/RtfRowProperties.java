package com.progdan.rtf2txt.rtf;

import java.io.PrintWriter;
import java.util.Vector;


/**
 * Speichert die Eigenschaften einer Tabellenzeile (bisher nur die Breite der
 * einzelnen Tabellenzellen).
 *
 * @author Daniel Finger, Lars Raap, Martin Amelsberg <br>
 *         Based on Majix by Tetrasix
 * @version 1.0
 */
public class RtfRowProperties implements Cloneable {
  private int[] _cellWidths;

  /**
   * Erzeugt ein neues RtfRowProperties-Objekt
   */
  RtfRowProperties() {
    _cellWidths = null;
  }

  /**
   * Kopiert das Objekt und alle seine Eigenschaften
   *
   * @return Ein Objekt, welches dem alten Objekt entspricht
   */
  protected Object clone() {
    RtfRowProperties properties = new RtfRowProperties();

    if (_cellWidths != null) {
      properties._cellWidths = new int[_cellWidths.length];

      for (int ii = 0; ii < _cellWidths.length; ii++) {
        properties._cellWidths[ii] = _cellWidths[ii];
      }
    }

    return properties;
  }

  /**
   * Setzt die Breite aller enthaltenen Zellen
   *
   * @param widths Array, das die Breite der einzelnen Zellen enthält
   */
  void setCellWidths(int[] widths) {
    _cellWidths = widths;
  }

  /**
   * Legt die Breite der Zellen fest
   *
   * @param widths Vektor, der die Breite der einzelnen Zellen enthält
   */
  void setCellWidths(Vector widths) {
    if (widths != null) {
      int length = widths.size();
      _cellWidths = new int[length];

      int current = 0;

      for (int ii = 0; ii < length; ii++) {
        int newcurrent = Integer.parseInt(widths.elementAt(ii).toString());
        _cellWidths[ii] = newcurrent - current;
        current = newcurrent;
      }
    } else {
      _cellWidths = null;
    }
  }

  /**
   * Liefert die Breite der Zelle an der Position pos
   *
   * @param pos Index der Tabellenzelle
   *
   * @return Die Breite der Zelle
   */
  int getCellWidth(int pos) {
    if (pos < _cellWidths.length) {
      return _cellWidths[pos];
    } else {
      return -1;
    }
  }

  /**
   * Schreibt die Breite der Zellen in einen Output-Stream
   *
   * @param out Stream, in den geschrieben werden soll
   */
  void Dump(PrintWriter out) {
    if (_cellWidths != null) {
      for (int ii = 0; ii < _cellWidths.length; ii++) {
        if (ii > 0) {
          out.print(" ");
        }

        out.print(_cellWidths[ii]);
      }
    }
  }
}
