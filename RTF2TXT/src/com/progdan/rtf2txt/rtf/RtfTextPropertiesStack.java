package com.progdan.rtf2txt.rtf;

/**
 * Repr�sentiert ein einzelnes Objekt auf dem RtfTextPropertiesStack.
 *
 * @author Daniel Finger, Lars Raap, Martin Amelsberg <br>
 *         Based on Majix by Tetrasix
 * @version 1.0
 */
class RtfTextPropertiesStackCell {
  private RtfTextProperties _properties;
  private RtfTextPropertiesStackCell _nextcell;

  /**
   * Erzeugt ein neues RtfTextPropertiesStackCell-Objekt
   *
   * @param cell Der Stack, dem das Objekt hinzugef�gt werden soll
   * @param properties Das Objekt, was eingelagert werden soll
   */
  RtfTextPropertiesStackCell(RtfTextPropertiesStackCell cell,
    RtfTextProperties properties) {
    _properties = properties;
    _nextcell = cell;
  }

  /**
   * Liefert das n�chste Objekt der verketteten Liste
   *
   * @return Die n�chste Zelle auf dem Stack
   */
  RtfTextPropertiesStackCell getNextCell() {
    return _nextcell;
  }

  /**
   * Liefert das in diesem Objekt eingebettete RtfTextProperties-Objekt
   *
   * @return Das eingelagerte Objekt
   */
  public RtfTextProperties getProperties() {
    return _properties;
  }

  /**
   * Ruft die Methode "toString" des eingelagerten RtfTextProperties auf
   *
   * @return Der R�ckgabe-String des eingelagerten Objektes
   */
  public String toString() {
    return _properties.toString();
  }
}


/**
 * Implementiert einen Stack, auf dem Objekte vom Typ RtfTextProperties
 * abgelegt werden k�nnen. Stack-Operationen wie "push" und "pop" werden
 * unterst�tzt. Ein einzelnes Objekt wird durch ein RtfTextPropertiesStackCell
 * repr�sentiert. Der Stack ist als verkettete Liste implementiert.
 *
 * @author Daniel Finger, Lars Raap, Martin Amelsberg <br>
 *         Based on Majix by Tetrasix
 * @version 1.0
 */
class RtfTextPropertiesStack {
  RtfTextPropertiesStackCell _top;

  /**
   * Erzeugt ein neues RtfTextPropertiesStack-Objekt
   */
  RtfTextPropertiesStack() {
    _top = null;
  }

  /**
   * Legt ein neues RtfTextProperties-Objekt auf dem Stack ab
   *
   * @param properties Das abzulegende Objekt
   */
  void push(RtfTextProperties properties) {
    _top = new RtfTextPropertiesStackCell(_top, properties);
  }

  /**
   * Gibt das "oberste" Element vom Stack zur�ck und entfernt es vom Stack.
   * Wenn der Stack leer ist, dann wird ein neues RtfTextProperties-Objekt
   * (mit StandardFormatierung) zur�ckgegeben.
   *
   * @return Das "oberste" Objekt des Stacks
   */
  RtfTextProperties pop() {
    RtfTextPropertiesStackCell save = _top;

    if (_top != null) {
      _top = _top.getNextCell();
    }

    return (save != null)
    ? save.getProperties()
    : new RtfTextProperties();
  }

  /**
   * Pr�ft, ob der Stack leer ist.
   *
   * @return True, wenn der Stack leer ist; ansonsten false
   */
  boolean empty() {
    return _top == null;
  }

  /**
   * Liefert das "oberste" Element des Stacks, ohne es zu l�schen
   *
   * @return Das "oberste" Objekt.<br>Wenn die Liste leer ist, dann "null"
   */
  RtfTextProperties top() {
    if (_top != null) {
      return _top.getProperties();
    } else {
      return null;
    }
  }

  /**
   * Ruft f�r jedes Objekt in dem Stack die Methode "toString" auf und sammelt
   * deren Ergebnisse
   *
   * @return Die gesammelten Ergebnisse der einzelnen Objekte auf dem Stack
   */
  public String toString() {
    StringBuffer buf = new StringBuffer();
    RtfTextPropertiesStackCell cell = _top;

    while (cell != null) {
      buf.append(cell.toString());
      cell = cell.getNextCell();

      if (cell != null) {
        buf.append(" : ");
      }
    }

    return buf.toString();
  }
}
