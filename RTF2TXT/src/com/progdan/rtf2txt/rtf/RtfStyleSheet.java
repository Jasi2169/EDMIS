package com.progdan.rtf2txt.rtf;

import java.io.PrintWriter;


/**
 * Repräsentiert ein RTF-Stylesheet. Ein Stylesheet in RTF besitzt die gleiche
 * Funktionalität wie ein HTML-Stylesheet.
 *
 * @author Daniel Finger, Lars Raap, Martin Amelsberg <br>
 *         Based on Majix by Tetrasix
 * @version 1.0
 */
public class RtfStyleSheet {
  static RtfStyleSheet _currentStyleSheet = null;
  private StyleDefinition[] _theStyles;

  /**
   * Erzeugt ein neues RtfStyleSheet-Objekt
   */
  public RtfStyleSheet() {
    _theStyles = new StyleDefinition[256];
    _currentStyleSheet = this;
  }

  /**
   * Definiert ein Stylesheet für einen RtfParagraph
   *
   * @param code Numerische Bezeichnung des Stylesheets
   * @param name Name des Stylesheets
   * @param textProperties Properties, die dem Stylesheet zugeordnet werden
   *        sollen
   */
  public void defineParagraphStyle(int code, String name,
    RtfTextProperties textProperties) {
    if ((code >= 0) && (code <= 255)) {
      _theStyles[code] = new ParagraphStyleDefinition(code, name, textProperties);
    } else {
      System.out.println("illegal paragraph style code : " + code + " : " +
        name);
    }
  }

  /**
   * Definiert ein Stylesheet für einzelne Zeichen
   *
   * @param code Numerische Bezeichnung des Stylesheets
   * @param name Name des Stylesheets
   * @param textProperties Properties, die dem Stylesheet zugeordnet werden
   *        sollen
   */
  public void defineCharacterStyle(int code, String name,
    RtfTextProperties textProperties) {
    if ((code >= 0) && (code <= 255)) {
      _theStyles[code] = new CharacterStyleDefinition(code, name, textProperties);
    } else {
      System.out.println("illegal character style code : " + code);
    }
  }

  /**
   * Liefert den Namen des Stylesheets mit der numerischen Bezeichnung "code"
   *
   * @param code Numerische Bezeichnung des Stylesheets
   *
   * @return Der gespeicherte Name des Stylesheets
   */
  public String getStyleName(int code) {
    if (code >= 0) {
      StyleDefinition style = _theStyles[code];

      if (style != null) {
        return _theStyles[code].getName();
      } else {
        return null;
      }
    } else {
      return null;
    }
  }

  /**
   * Prüft, ob das Stylesheet mit der numerischen Bezeichnung "code" ein
   * Stylesheet für einen Rtf-Paragraphen ist
   *
   * @param code Numerische Bezeichnung des Stylesheets
   *
   * @return True, wenn es ein Stylesheet für einen RTF-Paragraphen ist;
   *         ansonsten false
   */
  public boolean isParagraphStyle(int code) {
    if (code >= 0) {
      StyleDefinition style = _theStyles[code];

      if (style != null) {
        return style instanceof ParagraphStyleDefinition;
      }
    }

    return false;
  }

  /**
   * Prüft, ob das Stylesheet mit der numerischen Bezeichnung "code" ein
   * Stylesheet für ein Zeichen ist
   *
   * @param code Numerische Bezeichnung des Stylesheets
   *
   * @return True, wenn es ein Stylesheet für ein Zeichen ist; ansonsten false
   */
  public boolean isCharacterStyle(int code) {
    if (code >= 0) {
      StyleDefinition style = _theStyles[code];

      if (style != null) {
        return style instanceof CharacterStyleDefinition;
      }
    }

    return false;
  }

  /**
   * Liefert die mit dem Stylesheet "code" verbundenen Texteigenschaften
   *
   * @param code Numerische Bezeichnung des Stylesheets
   *
   * @return Ein Objekt vom Typ RtfTextProperties
   */
  public RtfTextProperties getTextProperties(int code) {
    if (code >= 0) {
      StyleDefinition style = _theStyles[code];

      if (style != null) {
        return style.getTextProperties();
      } else {
        return new RtfTextProperties();
      }
    } else {
      return new RtfTextProperties();
    }
  }

  /**
   * Schreibt die Stylesheet-Definitionen in einen Output-Stream. In ASCII
   * nicht  benutzt.
   *
   * @param out Stream, in den geschrieben werden soll
   */
  public void Dump(PrintWriter out) {
    out.println("<stylesheet>");

    for (int ii = 0; ii < 256; ii++) {
      if (_theStyles[ii] != null) {
        _theStyles[ii].Dump(out);
      }
    }

    out.println("</stylesheet>");
  }

  /**
   * Liefert das aktuellen Stylesheet <br> NOCH NICHT IMPLEMENTIERT
   *
   * @return null
   */
  public static RtfStyleSheet getCurrentStyleSheet() {
    return _currentStyleSheet;
  }

  abstract class StyleDefinition {
    String _styleName;
    RtfTextProperties _textProperties;
    int _code;

    StyleDefinition(int code, String name, RtfTextProperties textProperties) {
      _code = code;
      _styleName = name;
      _textProperties = textProperties;
    }

    String getName() {
      return _styleName;
    }

    RtfTextProperties getTextProperties() {
      return _textProperties;
    }

    abstract void Dump(PrintWriter out);
  }

  class ParagraphStyleDefinition extends StyleDefinition {
    ParagraphStyleDefinition(int code, String name,
      RtfTextProperties textProperties) {
      super(code, name, textProperties);
    }

    void Dump(PrintWriter out) {
      out.println("<style code=\"" + _code + "\"" +
        ((_textProperties.isBold() == true)
        ? " bold=\"1\""
        : "") + ((_textProperties.isItalic() == true)
        ? " italic=\"1\""
        : "") + ((_textProperties.isHidden() == true)
        ? " hidden=\"1\""
        : "") +
        ((_textProperties.isUnderlined() == true)
        ? " underlined=\"1\""
        : "") +
        (
          (_textProperties.getColor() == -1)
          ? (" color=\"" + _textProperties.getColorName() + "\"")
          : ""
        ) + ">" + _styleName + "</style>");
    }
  }

  class CharacterStyleDefinition extends StyleDefinition {
    CharacterStyleDefinition(int code, String name,
      RtfTextProperties textProperties) {
      super(code, name, textProperties);
    }

    void Dump(PrintWriter out) {
      out.println("<cstyle code=\"" + _code + "\"" +
        ((_textProperties.isBold() == true)
        ? " bold=\"1\""
        : "") + ((_textProperties.isItalic() == true)
        ? " italic=\"1\""
        : "") + ((_textProperties.isHidden() == true)
        ? " hidden=\"1\""
        : "") +
        ((_textProperties.isUnderlined() == true)
        ? " underlined=\"1\""
        : "") +
        (
          (_textProperties.getColor() != -1)
          ? (" color=\"" + _textProperties.getColorName() + "\"")
          : ""
        ) + ">" + _styleName + "</style>");
    }
  }
}
