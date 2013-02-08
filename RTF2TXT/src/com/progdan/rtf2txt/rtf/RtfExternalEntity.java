package com.progdan.rtf2txt.rtf;

/**
 * Oberklasse für externe Dateien, die in RTF referenziert werden. Bisher sind
 * nur Bilder vorgesehen.
 *
 * @author Daniel Finger, Lars Raap, Martin Amelsberg <br>
 *         Based on Majix by Tetrasix
 * @version 1.0
 */
public class RtfExternalEntity {
  String _name;
  String _pubid;
  String _sysid;

  /**
   * Erzeugt ein neues RtfExternalEntity-Objekt
   *
   * @param name Name des neuen Objektes
   * @param sysid System-ID des neuen Objektes
   */
  RtfExternalEntity(String name, String sysid) {
    _name = name;
    _sysid = sysid;
  }

  /**
   * Erzeugt ein neues RtfExternalEntity-Objekt
   *
   * @param name Name des neuen Objektes
   * @param pubid Public-ID des neuen Objektes
   * @param sysid System-ID des neuen Objektes
   */
  RtfExternalEntity(String name, String pubid, String sysid) {
    _name = name;
    _pubid = pubid;
    _sysid = sysid;
  }

  /**
   * Liefert den Namen des externen Objektes
   *
   * @return Namen des neuen Objektes
   */
  public String getName() {
    return _name;
  }

  /**
   * Liefert die System-ID des Objektes
   *
   * @return System-ID
   */
  public String getSystemID() {
    return _sysid;
  }

  /**
   * Liefert die Public-ID des Objektes
   *
   * @return Public-ID
   */
  public String getPublicID() {
    return _pubid;
  }
}
