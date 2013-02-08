package com.progdan.rtf2txt.rtf;

import java.io.PrintWriter;
import java.util.Vector;


/**
 * Klasse zum Sammeln von externen Referenzen in einem Vektor.
 *
 * @author Daniel Finger, Lars Raap, Martin Amelsberg <br>
 *         Based on Majix by Tetrasix
 * @version 1.0
 */
public class RtfExternalEntities {
  Vector _entities;

  /**
   * Erzeugt ein neues RtfExternalEntities-Objekt
   */
  RtfExternalEntities() {
    _entities = new Vector();
  }

  /**
   * Liefert die Anzahl der eingelagerten Entities
   *
   * @return Anzahl der Entities
   */
  public int count() {
    return _entities.size();
  }

  /**
   * Fügt ein Entity der Liste hinzu
   *
   * @param entity Entity, das hinzugefügt werden soll
   */
  void addEntity(RtfExternalEntity entity) {
    _entities.addElement(entity);
  }

  /**
   * Liefert ein Entity aus der Liste
   *
   * @param idx Index des zu liefernden Entities
   *
   * @return Das Entity an der Position idx
   */
  public RtfExternalEntity getEntity(int idx) {
    return (RtfExternalEntity) _entities.elementAt(idx);
  }

  /**
   * Liefert eine Auflistung aller Entities
   *
   * @param out Stream, in den geschrieben werden soll
   */
  void Dump(PrintWriter out) {
    for (int ii = 0; ii < _entities.size(); ii++) {
      RtfExternalEntity entity = getEntity(ii);

      //  out.println("<entity name=\"" + entity.getName() + "\">");
    }
  }
}
