package com.sap.engine.objectprofiler.view;

import java.awt.*;

/**
 * Copyright (c) 2001 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 * <p/>
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 * <p/>
 * User: Pavel Bonev
 * Date: 2005-6-14
 * Time: 16:53:43
 */
public class GlyphEvent {
  public static final String COMMAND_POINT = "COMMAND_POINT"; 

  private Glyph glyph = null;
  private Component source = null;
  private String command = null;

  public GlyphEvent(Glyph glyph) {
    this(glyph, COMMAND_POINT, null);
  }

  public GlyphEvent(Glyph glyph, String command) {
    this(glyph, command, null);
  }

  public GlyphEvent(Glyph glyph, String command, Component source) {
    this.glyph = glyph;
    this.source = source;
    this.command = command;
  }

  public Component getSource() {
    return source;
  }

  public String getCommand() {
    return command;
  }

  public Glyph getGlyph() {
    return glyph;
  }

}
