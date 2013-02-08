package com.progdan.rtf2txt.rtf;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Vector;


/**
 * Klasse zum Überführen des Dokuments in die interne Darstellung. In dieser
 * Klasse werden Control-Words erkannt und die Daten den entsprechenden
 * Klassen zugeführt.
 *
 * @author Daniel Finger, Lars Raap, Martin Amelsberg <br>
 *         Based on Majix by Tetrasix
 * @version 1.0
 */
public class RtfAnalyser {
  private RtfReader _reader;
  private RtfDocument _theDocument;
  private RtfInfo _theInfo = null;
  private RtfParagraph _theParagraph;
  private RtfStyleSheet _theStyles;
  private RtfParagraphProperties _theParagraphProperties;
  private RtfTextProperties _theTextProperties;
  private RtfTextPropertiesStack _theTextPropertiesStack;
  private RtfExternalEntities _theExternalEntities;
  private boolean _trace = false;
  private PrintWriter _trace_stream = null;
  private int _pictureCounter = 1;
  private boolean _no_field_result;
  private RtfCompoundObjectStack _theContainerStack;
  private RtfCompoundObject _theContainer;
  private RtfRow _theRow;
  private RtfCell _theCell;
  private Vector _theCellLimits;

  /**
   * Erzeugt ein neues RtfAnalyser-Objekt.
   *
   * @param reader Der Reader, der das Dokument enthält.
   * @param traceStream Ein Stream, der Tracing Informationen enthält. Falls
   *        keine Tracing Informationen gewünscht werden: null
   */
  public RtfAnalyser(RtfReader reader, PrintWriter traceStream) {
    _reader = reader;
    _theDocument = new RtfDocument(reader.getFileName());
    _theParagraph = null;
    _theStyles = new RtfStyleSheet();
    _theExternalEntities = new RtfExternalEntities();
    _theDocument.setStyleSheet(_theStyles);
    _theDocument.setExternalEntities(_theExternalEntities);
    _theParagraphProperties = new RtfParagraphProperties();
    _theTextProperties = new RtfTextProperties();
    _theCellLimits = new Vector();

    _theTextPropertiesStack = new RtfTextPropertiesStack();

    _theContainerStack = new RtfCompoundObjectStack();
    _theContainer = _theDocument;

    _theRow = null;
    _theCell = null;
    _theInfo = new RtfInfo();

    _trace_stream = traceStream;

    if (_trace_stream == null) {
      _trace = false;
    }
  }

  /**
   * Parst die einzelnen Token, die vom RtfReader bereitgestellt werden, und
   * überführt sie in das interne Format
   *
   * @return Ein im internen Format vorliegendes RtfDocument-Objekt
   *
   * @throws IOException Falls etwas schiefläuft
   */
  public RtfDocument parse() throws IOException {
    RtfToken tok;

    for (;;) {
      tok = _reader.getNextToken();

      if (tok == null) {
        if (_theParagraph != null) {
          _theContainer.add(_theParagraph);
        }

        return _theDocument;
      }

      switch (tok.getType()) {
        case RtfToken.ASTERISK: {
          tok = _reader.getNextToken();

          // Unbekannte
          if ((tok.getType() != RtfToken.CONTROLWORD) ||
                (!parseStarControlWord(tok))) {
            skipUntilEndOfGroup();
          }
        }

        break;
        case RtfToken.DATA:

          if (_theParagraph == null) {
            _theParagraph = new RtfParagraph();
          }

          _theParagraph.add(new RtfText(tok.getData(), _theTextProperties));

          break;
        case RtfToken.CONTROLWORD:
          parseControlWord(tok);
          break;
        case RtfToken.OPENGROUP:
          _theTextPropertiesStack.push(_theTextProperties);
          _theTextProperties = (RtfTextProperties) _theTextProperties.clone();

          break;
        case RtfToken.CLOSEGROUP:
          _theTextProperties = _theTextPropertiesStack.pop();
          break;
      }

      if (_trace) {
        _trace_stream.println(">>>" + tok.toString());

        String props = _theTextProperties.toString();
        String stack = _theTextPropertiesStack.toString();
        String data = props;

        if (!stack.equals("")) {
          data += (" / " + stack);
        }

        if (!data.equals("")) {
          _trace_stream.println("   " + data);
        }

        if (_theParagraph != null) {
          _trace_stream.print("===");
          _theParagraph.Dump(_trace_stream);
          _trace_stream.println("");
        }
      }
    }
  }

  /**
   * Erkennt StarControlWords (in RTF eingeleitet mit \*) und verarbeitet sie
   * weiter, sofern sie bekannt sind. Sofern oben nach den \ ein Stern fehlt,
   * dann hat Jalopy den mal wieder entfernt. Die StarControlWord werden mit
   * "BackslashStern" eingeleitet.
   *
   * @param tok Das zu untersuchende Token
   *
   * @return True, falls das Token bekannt ist; ansonsten false
   *
   * @throws IOException Falls ein Fehler auftritt
   */
  private boolean parseStarControlWord(RtfToken tok) throws IOException {
    String name = tok.getName();

    if (name.equals("pn")) {
      processPn();

      return true;
    } else if (name.equals("shpinst")) {
      processShpinst();

      return true;
    } else {
      return false;
    }
  }

  /**
   * In dieser Funktion werden die Control-Words erkannt. Hier könnten neue
   * unbekannte Control-Words, die bisher ignoriert wurden, eingefügt oder
   * neue Aktionen auf Control-Words eingebaut werden.
   *
   * @param tok RtfToken vom Typ Control-Word
   *
   * @throws IOException Falls ein Fehler auftritt
   */
  private void parseControlWord(RtfToken tok) throws IOException {
    String name = tok.getName();

    //System.out.println("\\" + name);
    if (_trace) {
      _trace_stream.println(">>\\" + name);
    }

    // Falls das Control Word "par" (Enter) lautet
    if (name.equals("par")) {
      if (_theParagraph == null) {
        _theParagraph = new RtfParagraph();
      }

      _theParagraph.setProperties(_theParagraphProperties);
      _theContainer.add(_theParagraph);
      _theParagraph = null;
    } else if (name.equals("sect")) {
      // page break and section break are considered as paragraph break
      if (_theParagraph == null) {
        _theParagraph = new RtfParagraph();
      }

      _theParagraph.setProperties(_theParagraphProperties);
      _theContainer.add(_theParagraph);
      _theParagraph = null;
    } else if (name.equals("page")) {
      // page break and section break are considered as paragraph break
      if (_theParagraph == null) {
        _theParagraph = new RtfParagraph();
      }

      _theParagraphProperties.setPageBreak(true);
      _theParagraph.setProperties(_theParagraphProperties);
      _theContainer.add(_theParagraph);
      _theParagraph = null;
    } else if (name.equals("pard")) {
      _theParagraphProperties.reset();
    } else if (name.equals("pagebb")) {
      _theParagraphProperties.setPageBreak(true);
    } else if (name.equals("stylesheet")) {
      parseStyleSheet();
    } else if (name.equals("fonttbl")) {
      skipUntilEndOfGroup();
    } else if (name.equals("colortbl")) {
      skipUntilEndOfGroup();
    } else if (name.equals("pict")) {
      processPict();
    } else if (name.equals("s")) {
      int code = Integer.parseInt(tok.getData());
      _theParagraphProperties.setStyle(code);
    } else if (name.equals("pntext")) {
      String data = getDataOfGroup();
      _theParagraphProperties.setBullet(data);
    } else if (name.equals("b")) {
      _theTextProperties.setBold(true);
    } else if (name.equals("i")) {
      _theTextProperties.setItalic(true);
    } else if (name.equals("v")) {
      _theTextProperties.setHidden(true);
    } else if (name.equals("ul")) {
      _theTextProperties.setUnderlined(true);
    } else if (name.equals("cf")) {
      _theTextProperties.setColor(Integer.parseInt(tok.getData()));
    } else if (name.equals("cs")) {
      _theTextProperties.setStyle(Integer.parseInt(tok.getData()));
    } else if (name.equals("plain")) {
      _theTextProperties.setBold(false);
      _theTextProperties.setItalic(false);
      _theTextProperties.setUnderlined(false);
      _theTextProperties.setHidden(false);
      _theTextProperties.setColor(-1);
      _theTextProperties.setStyle(-1);
    } else if (name.equals("field")) {
      processField();
    } else if (name.equals("ldblquote")) {
      insertData("\"");
    } else if (name.equals("rdblquote")) {
      insertData("\"");
    } else if (name.equals("lquote")) {
      insertData("'");
    } else if (name.equals("rquote")) {
      insertData("'");
    } else if (name.equals("emdash")) {
      insertData("-");
    } else if (name.equals("endash")) {
      insertData("-");
    } else if (name.equals("tab")) {
      insertTab();
    } else if (name.equals("cell")) {
      processCell();
    } else if (name.equals("row")) {
      processRow();
    } else if (name.equals("intbl")) {
      processStartRow();
    } else if (name.equals("info")) {
      processInfo();
    } else if (name.equals("line")) {
      insertLineBreak();
    } else if (name.equals("footnote")) {
      parsefootnote();
    } else if (name.equals("header")) {
      skipUntilEndOfGroup();
    } else if (name.equals("footer")) {
      skipUntilEndOfGroup();
    } else if (name.equals("listtext")) {
      skipUntilEndOfGroup();
    } else if (name.equals("cellx")) {
      _theCellLimits.addElement(tok.getData());
    } else if (name.equals("trowd")) {
      _theCellLimits = new Vector();
    }
  }

  /**
   * Springt bis ans Ende einer Gruppe (}). Weitere darin enthaltene Gruppen
   * werden auch übersprungen.
   *
   * @throws IOException Falls ein Fehler auftritt
   */
  private void skipUntilEndOfGroup() throws IOException {
    int depth = 1;

    while (depth > 0) {
      RtfToken tok = _reader.getNextToken();

      if (tok == null) {
        break;
      }

      int type = tok.getType();

      switch (type) {
        case RtfToken.OPENGROUP:
          depth++;
          break;
        case RtfToken.CLOSEGROUP:
          depth--;
          break;
        case RtfToken.CONTROLWORD:

          if (_trace) {
            _trace_stream.println(">>>" + depth + " skipping " + tok.getName());
          }

          break;
      }
    }

    _theTextProperties = _theTextPropertiesStack.pop();
  }

  /**
   * Liefert die gesamten Daten der aktuellen Gruppe
   *
   * @return Daten der Gruppe
   *
   * @throws IOException Falls ein Fehler auftritt
   */
  private String getDataOfGroup() throws IOException {
    StringBuffer buf = new StringBuffer();
    int depth = 1;

    while (depth > 0) {
      RtfToken tok = _reader.getNextToken();

      if (tok == null) {
        break;
      }

      int type = tok.getType();

      switch (type) {
        case RtfToken.DATA:
          buf.append(tok.getData());
          break;
        case RtfToken.OPENGROUP:
          depth++;
          break;
        case RtfToken.CLOSEGROUP:
          depth--;
          break;
        case RtfToken.ASTERISK:
          skipUntilEndOfGroup();
          depth--;

          break;
      }
    }

    _theTextProperties = _theTextPropertiesStack.pop();

    return buf.toString();
  }

  /**
   * Verarbeitet die gefundene Fußnote. Der Fußnotentext wird in Klammern nach
   * der Fußnote eingefügt.
   *
   * @throws IOException Falls ein Fehler auftritt
   */
  private void parsefootnote() throws IOException {
    String data;
    data = getDataOfGroup();
    data = data.trim();
    insertData(" (" + data + ")");
  }

  /**
   * Verarbeitet die im Dokument gespeicherten Formatvorlagen
   *
   * @throws IOException Falls ein Fehler auftritt
   */
  private void parseStyleSheet() throws IOException {
    for (;;) {
      RtfToken tok = _reader.getNextToken();

      if (tok == null) {
        return;
      }

      if (tok.getType() == RtfToken.OPENGROUP) {
        parseStyleDefinition();
      } else if (tok.getType() == RtfToken.CLOSEGROUP) {
        return;
      }
    }
  }

  /**
   * Verarbeitet eine einzelne Formatvorlage
   *
   * @throws IOException Falls ein Fehler auftritt
   * @throws NumberFormatException Falls beim Konvertieren einer Zahl ein
   *         Fehler auftritt
   */
  private void parseStyleDefinition() throws IOException, NumberFormatException {
    int code = 0;
    String name = null;
    RtfTextProperties textProperties = new RtfTextProperties();
    boolean isCharStyle = false;

    for (;;) {
      RtfToken tok = _reader.getNextToken();

      if (tok == null) {
        break;
      }

      if (tok.getType() == RtfToken.CONTROLWORD) {
        if (tok.getName().equals("s")) {
          code = Integer.parseInt(tok.getData());
          isCharStyle = false;
        } else if (tok.getName().equals("cs")) {
          code = Integer.parseInt(tok.getData());
          isCharStyle = true;
        } else if (tok.getName().equals("b")) {
          textProperties.setBold(true);
        } else if (tok.getName().equals("i")) {
          textProperties.setItalic(true);
        } else if (tok.getName().equals("v")) {
          textProperties.setHidden(true);
        } else if (tok.getName().equals("ul")) {
          textProperties.setUnderlined(true);
        } else if (tok.getName().equals("cf")) {
          int color = Integer.parseInt(tok.getData());
          textProperties.setColor(color);
        }
      } else if (tok.getType() == RtfToken.DATA) {
        if (name == null) {
          name = tok.getData();
        } else {
          name += tok.getData();
        }

        if (name.endsWith(";")) {
          name = name.substring(0, name.length() - 1);
        }
      } else if (tok.getType() == RtfToken.OPENGROUP) {
        skipUntilEndOfGroup();
      } else if (tok.getType() == RtfToken.CLOSEGROUP) {
        break;
      }
    }

    if (isCharStyle) {
      _theStyles.defineCharacterStyle(code, name, textProperties);
    } else {
      _theStyles.defineParagraphStyle(code, name, textProperties);
    }
  }

  /**
   * Fügt dem aktuellen RtfParagraph neue Daten (als Text) hinzu
   *
   * @param data Der hinzuzufügende String
   */
  private void insertData(String data) {
    if (_theParagraph == null) {
      _theParagraph = new RtfParagraph();
      _theParagraph.setProperties(_theParagraphProperties);
    }

    _theParagraph.add(new RtfText(data, _theTextProperties));
  }

  /**
   * Fügt ein Tabulator an der aktuellen Position ein.
   */
  private void insertTab() {
    if (_theParagraph == null) {
      _theParagraph = new RtfParagraph();
      _theParagraph.setProperties(_theParagraphProperties);
    }

    _theParagraph.add(new RtfTab());
  }

  /**
   * Fügt einen Seitenumbruch an der aktuellen Position ein.
   */
  private void insertLineBreak() {
    if (_theParagraph == null) {
      _theParagraph = new RtfParagraph();
      _theParagraph.setProperties(_theParagraphProperties);
    }

    _theParagraph.add(new RtfLineBreak());
  }

  /**
   * Verarbeitet das Control-Word \pict, das zum Einbinden von Grafiken in RTF
   * genutzt wird, und erkennt alle zum Bild gehörenden Daten.
   *
   * @throws IOException Falls ein Fehler auftritt
   */
  void processPict() throws IOException {
    StringBuffer buf = new StringBuffer();
    int depth = 1;
    int pich = 0;
    int picw = 0;
    int pichg = 0;
    int picwg = 0;

    while (depth > 0) {
      RtfToken tok = _reader.getNextToken();

      if (tok == null) {
        break;
      }

      int type = tok.getType();

      switch (type) {
        case RtfToken.DATA:

          if (depth == 1) {
            buf.append(tok.getData());
          }

          break;
        case RtfToken.OPENGROUP:
          depth++;
          break;
        case RtfToken.CLOSEGROUP:
          depth--;
          break;
        case RtfToken.CONTROLWORD: {
          String name = tok.getName();

          if (name.equals("picw")) {
            picw = Integer.parseInt(tok.getData());
          }

          if (name.equals("pich")) {
            pich = Integer.parseInt(tok.getData());
          }

          if (name.equals("picwgoal")) {
            picwg = Integer.parseInt(tok.getData());
          }

          if (name.equals("pichgoal")) {
            pichg = Integer.parseInt(tok.getData());
          }
        }
      }
    }

    if (_theParagraph == null) {
      _theParagraph = new RtfParagraph();
      _theParagraph.setProperties(_theParagraphProperties);
    }

    _theParagraph.add(new RtfPicture(_pictureCounter, buf.toString(), picw,
        pich, picwg, pichg));
    _theExternalEntities.addEntity(new RtfPictureExternalEntity(
        _pictureCounter, ""));
    _pictureCounter++;
  }

  /**
   * Verarbeitet ein Feld, das aus dem RTF-Feld selbst und seinem gespeicherten
   * Ergebnis besteht.
   *
   * @throws IOException Falls ein Fehler auftritt
   */
  void processField() throws IOException {
    RtfObject instance = null;
    int depth = 1;
loop:
    while (depth > 0) {
      RtfToken tok = _reader.getNextToken();

      if (tok == null) {
        break;
      }

      int type = tok.getType();

      switch (type) {
        case RtfToken.DATA:
          break;
        case RtfToken.OPENGROUP:
          depth++;
          break;
        case RtfToken.CLOSEGROUP:
          depth--;
          break;
        case RtfToken.CONTROLWORD:

          if (tok.getName().equals("fldinst")) {
            instance = processFieldInstance();

            if (_no_field_result) {
              skipUntilEndOfGroup();
            }

            break loop;
          } else if (tok.equals("fldrslt")) {
            //@@@TBC
          }

          break;
      }
    }

    if (instance != null) {
      if (_theParagraph == null) {
        _theParagraph = new RtfParagraph();
        _theParagraph.setProperties(_theParagraphProperties);
      }

      _theParagraph.add(instance);
    }
  }

  /**
   * Aufteilen eines Strings in einzelne gequotete Strings und deren
   * Trennzeichen.  Beispiel: "String1"\l"String2" erzeugt folgende<br>
   * <b>Rückgabe:</b> "String1""String2"<br>
   * Das Objekt commutators enthält jetzt ein Element, das \l beinhaltet.
   *
   * @param data String, der untersucht werden soll
   * @param commutators Objekt, das in dieser Funktion die Trennzeichen erhält
   *
   * @return String, der die einzelnen gequoteten Strings enthält
   */
  private static String getCommutators(String data, CommutatorList commutators) {
    char[] indata = data.trim().toCharArray();
    StringBuffer buf = new StringBuffer();
    StringBuffer bufcommutator = null;
    boolean inquotedstring = false;
    boolean incommutator = false;

    for (int ii = 0; ii < indata.length; ii++) {
      char ch = indata[ii];

      if (inquotedstring) {
        if (ch == '"') {
          inquotedstring = false;
        }

        buf.append(ch);
      } else if (incommutator) {
        if (Character.isSpaceChar(ch)) {
          commutators.addElement(bufcommutator.toString());
          bufcommutator = null;
          incommutator = false;
        } else if (ch == '"') {
          commutators.addElement(bufcommutator.toString());
          bufcommutator = null;
          inquotedstring = true;
          buf.append(ch);
          incommutator = false;
        } else {
          bufcommutator.append(ch);
        }
      } else {
        if (Character.isSpaceChar(ch)) {
          // nothing to do
        } else if (ch == '"') {
          inquotedstring = true;
          buf.append(ch);
        } else if (ch == '\\') {
          bufcommutator = new StringBuffer();
          bufcommutator.append(ch);
          incommutator = true;
        } else {
          // should not happen unless the user typed the
          // hyperlink target manually in the field
          buf.append(ch);
        }
      }
    }

    return buf.toString();
  }

  /**
   * Verarbeitung eines eingebetteten Feldes wie HYPERLINK, TOC o.ä.
   *
   * @return Ein dem gefundenem Typ entsprechendes, von RtfObject abgeleitetes
   *         Objekt
   *
   * @throws IOException Falls ein Fehler auftritt
   */
  RtfObject processFieldInstance() throws IOException {
    String data = getDataOfGroup().trim();
    int pos = data.indexOf("\\*");

    if (pos >= 0) {
      data = data.substring(0, pos).trim();
    }

    _no_field_result = false;

    if (data != null) {
      if (data.startsWith("SYMBOL")) {
        return new RtfSymbol(data);
      } else if (data.startsWith("TOC")) {
        _no_field_result = true;

        return new RtfFieldInstance(data);
      } else if (data.startsWith("INCLUDEPICTURE")) {
        data = data.substring(14).trim();

        CommutatorList commutators = new CommutatorList();
        data = getCommutators(data, commutators);

        if (data.startsWith("\"")) {
          data = data.substring(1);

          int pos2 = data.indexOf('"');

          if (pos2 != -1) {
            data = data.substring(0, pos2);
          }
        }

        return new RtfPicture(data);
      } else if (data.startsWith("HYPERLINK")) {
        {
          CommutatorList commutators = new CommutatorList();
          data = data.substring(9).trim();
          data = getCommutators(data, commutators);

          if (data.startsWith("\"")) {
            data = data.substring(1);
          }

          if (data.endsWith("\"")) {
            data = data.substring(0, data.length() - 1);
          }

          if (commutators.isCommutatorDefined("\\l")) {
            String newdata;
            pos = data.lastIndexOf("\"\"");

            if (pos > 0) {
              newdata = data.substring(0, pos);
              newdata = newdata + "#";
              data = newdata + data.substring(pos + 2);
            }
          }

          String result = getResultOfField();

          return new RtfHyperLink(data, result);
        }
      } else {
        return new RtfFieldInstance(data);
      }
    }

    return null;
  }

  /**
   * Liefert das im Dokument gespeicherte Ergebnis eines RTF-Feldes
   *
   * @return Das eingebettete Ergebnis
   *
   * @throws IOException Falls ein Fehler auftritt.
   */
  String getResultOfField() throws IOException {
    while (true) {
      RtfToken tok = _reader.getNextToken();

      if (tok == null) {
        return null;
      }

      int type = tok.getType();

      switch (type) {
        case RtfToken.CONTROLWORD: {
          String name = tok.getName();

          if (name.equals("fldrslt")) {
            return getDataOfGroup().trim();
          }
        }

        break;
        case RtfToken.OPENGROUP:

          String result = getResultOfField();

          if (result != null) {
            return result;
          }

          break;
        case RtfToken.CLOSEGROUP:
          return null;
        case RtfToken.ASTERISK:
          skipUntilEndOfGroup();
          return null;
      }
    }
  }

  /**
   * Verarbeitet die erste im Dokument gefundene Tabellenzeile
   */
  void processStartRow() {
    if (!(_theContainer instanceof RtfCell)) {
      if (_theParagraph != null) {
        _theParagraph.setProperties(_theParagraphProperties);
        _theContainer.add(_theParagraph);
        _theParagraph = null;
      }

      _theContainerStack.push(_theContainer);

      _theRow = new RtfRow();
      _theRow.getProperties().setCellWidths(_theCellLimits);
      _theCell = new RtfCell();
      _theCell.getProperties().setWidth(_theRow.getProperties().getCellWidth(0));
      _theContainerStack.push(_theContainer);
      _theContainer = _theCell;
    }
  }

  /**
   * Verarbeitet in dem Dokument gefundene Tabellenzeilen
   */
  void processRow() {
    _theContainer = _theContainerStack.pop();
    _theContainer.add(_theRow);
    _theRow = null;
  }

  /**
   * Verarbeitet eine im Dokument gefundene TabellenZelle und fügt sie der
   * aktuellen Tabellenzeile hinzu
   */
  void processCell() {
    if (_theParagraph != null) {
      _theParagraph.setProperties(_theParagraphProperties);
      _theContainer.add(_theParagraph);
      _theParagraph = null;
    }

    if (!(_theContainer instanceof RtfCell)) {
      System.out.println("bad \\cell, is a " + _theContainer.toString());
    }

    _theRow.add(_theCell);
    _theCell = new RtfCell();
    _theCell.getProperties().setWidth(_theRow.getProperties().getCellWidth(_theRow.size()));

    _theContainer = _theCell;
  }

  /**
   * Verarbeitet Metadaten aus dem RTF-Dokument
   *
   * @throws IOException Falls ein Fehler auftritt
   */
  void processInfo() throws IOException {
    _theInfo = new RtfInfo();
    _theDocument.setInfo(_theInfo);

    for (;;) {
      RtfToken tok = _reader.getNextToken();

      if (tok == null) {
        return;
      }

      if (tok.getType() == RtfToken.OPENGROUP) {
        processOneInfo();
      } else if (tok.getType() == RtfToken.CLOSEGROUP) {
        return;
      }
    }
  }

  /**
   * Verarbeitet eine einzelnes Metadatum aus dem RTF-Dokument
   *
   * @throws IOException Falls ein Fehler auftritt
   */
  void processOneInfo() throws IOException {
    int code = -1;
    String value = "";

    for (;;) {
      RtfToken tok = _reader.getNextToken();

      if (tok == null) {
        break;
      }

      if (tok.getType() == RtfToken.CONTROLWORD) {
        if (tok.getName().equals("title")) {
          code = RtfInfo.TITLE;
        }

        if (tok.getName().equals("subject")) {
          code = RtfInfo.SUBJECT;
        }

        if (tok.getName().equals("operator")) {
          code = RtfInfo.OPERATOR;
        }

        if (tok.getName().equals("author")) {
          code = RtfInfo.AUTHOR;
        }

        if (tok.getName().equals("manager")) {
          code = RtfInfo.MANAGER;
        }

        if (tok.getName().equals("company")) {
          code = RtfInfo.COMPANY;
        }
      } else if (tok.getType() == RtfToken.DATA) {
        value += tok.getData();
      } else if (tok.getType() == RtfToken.CLOSEGROUP) {
        break;
      }
    }

    if (code >= 0) {
      _theInfo.defineProperty(code, value);
    }
  }

  /**
   * Verarbeitet Aufzählungen, die durch \pn eingeleitet werden
   *
   * @throws IOException Falls ein Fehler auftritt
   */
  void processPn() throws IOException {
    for (;;) {
      RtfToken tok = _reader.getNextToken();

      if (tok == null) {
        return;
      }

      switch (tok.getType()) {
        case RtfToken.CONTROLWORD: {
          String name = tok.getName();

          if (name.equals("pnlvlblt")) {
            _theParagraphProperties.setNumStyle(RtfParagraphProperties.STYLE_BULLET);
          } else if (name.equals("pnlvlbody")) {
            _theParagraphProperties.setNumStyle(RtfParagraphProperties.STYLE_NUMBERED);
          }
        }

        break;
        case RtfToken.OPENGROUP:
          processPn();
          break;
        case RtfToken.CLOSEGROUP:
          return;
        default:
          break;
      }
    }
  }

  /**
   * Verarbeitet Shapes, also mit Word gezeichnete Objekte
   *
   * @throws IOException Falls ein Fehler auftritt
   */
  void processShpinst() throws IOException {
    int depth = 1;
    boolean done = false;

    while (depth > 0) {
      RtfToken tok = _reader.getNextToken();

      if (tok == null) {
        return;
      }

      switch (tok.getType()) {
        case RtfToken.CONTROLWORD: {
          String name = tok.getName();

          if (name.equals("pict") && !done) {
            processPict();
            done = true;
          }
        }

        break;
        case RtfToken.OPENGROUP:
          depth++;
          break;
        case RtfToken.CLOSEGROUP:
          depth--;
          break;
        default:
          break;
      }
    }
  }

  class CommutatorList extends Vector {
    boolean isCommutatorDefined(String commutator) {
      for (Enumeration enum = elements(); enum.hasMoreElements();) {
        if (enum.nextElement().equals(commutator)) {
          return true;
        }
      }

      return false;
    }
  }
}
