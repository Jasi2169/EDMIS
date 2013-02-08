package com.progdan.rtf2txt.rtf;

/**
 * Implementiert einen Stack, auf dem Objekte vom Typ RtfCompoundObject
 * abgelegt werden k�nnen. Standard Stack Operationen wie "push" und "pop"
 * werden unterst�tzt. Ein einzelnes Objekt wird durch ein
 * RtfCompoundObjectCell repr�sentiert. Der Stack ist als verkettete Liste
 * implementiert.
 *
 * @author Daniel Finger, Lars Raap, Martin Amelsberg based on Majix by
 *         Tetrasix
 * @version 1.0
 */
class RtfCompoundObjectStack {
  RtfCompoundObjectStackCell _top;

  /**
   * Erzeugt ein neues RtfCompoundObjectStack-Objekt
   */
  RtfCompoundObjectStack() {
    _top = null;
  }

  /**
   * Legt ein neues RtfCompoundObject auf dem Stack ab.
   *
   * @param state Das abzulegende Objekt
   */
  void push(RtfCompoundObject state) {
    _top = new RtfCompoundObjectStackCell(_top, state);
  }

  /**
   * Gibts das oberste Element vom Stack zur�ck und entfernt es vom Stack.
   *
   * @return Das "oberste" Objekt des Stacks
   */
  RtfCompoundObject pop() {
    RtfCompoundObjectStackCell save = _top;

    if (_top != null) {
      _top = _top.getNextCell();
    }

    if (save != null) {
      return save.getRtfObject();
    } else {
      return null;
    }
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
   * Liefert das oberste Element des Stacks, ohne es zu l�schen.
   *
   * @return Das oberste Objekt.<br>Wenn die Liste leer ist, dann "null"
   */
  RtfCompoundObject top() {
    if (_top != null) {
      return _top.getRtfObject();
    } else {
      return null;
    }
  }

  /**
   * Ruft f�r jedes Objekt in dem Stack die Methode "toString" auf und sammelt
   * deren Ergebnisse.
   *
   * @return Die gesammelten Ergebnisse der einzelnen Objekte auf dem Stack
   */
  public String toString() {
    StringBuffer buf = new StringBuffer();
    RtfCompoundObjectStackCell cell = _top;

    while (cell != null) {
      buf.append(cell.toString());
      cell = cell.getNextCell();
    }

    return buf.toString();
  }
}


/**
 * Repr�sentiert ein einzelnes Objekt auf dem RtfCompoundObjectStack.
 *
 * @author Daniel Finger, Lars Raap, Martin Amelsberg based on Majix by
 *         Tetrasix
 * @version 1.0
 */
class RtfCompoundObjectStackCell {
  private RtfCompoundObject _rtfobject;
  private RtfCompoundObjectStackCell _nextcell;

  /**
   * Erzeugt ein neues RtfCompoundObjectStackCell-Objekt
   *
   * @param cell Der Stack, dem das Objekt hinzugef�gt werden soll
   * @param rtfobject Das Objekt, was eingelagert werden soll
   */
  RtfCompoundObjectStackCell(RtfCompoundObjectStackCell cell,
    RtfCompoundObject rtfobject) {
    _rtfobject = rtfobject;
    _nextcell = cell;
  }

  /**
   * Liefert das n�chste Objekt der verketteten Liste
   *
   * @return Die n�chste Zelle auf dem Stack
   */
  RtfCompoundObjectStackCell getNextCell() {
    return _nextcell;
  }

  /**
   * Liefert das in der Zelle gespeicherte Objekt
   *
   * @return Das eigelagerte RtfCompoundObject
   */
  RtfCompoundObject getRtfObject() {
    return _rtfobject;
  }

  /**
   * Ruft die Methode "toString" des eingelagerten RtfCompoundObject auf
   *
   * @return Der R�ckgabeString des eingelagerten Objektes
   */
  public String toString() {
    return _rtfobject.toString();
  }
}
