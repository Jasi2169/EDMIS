/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.txt file.  */

package com.progdan.logengine.performance;

import com.progdan.logengine.Layout;
import com.progdan.logengine.spi.LoggingEvent;
import com.progdan.logengine.AppenderSkeleton;

/**
 * A bogus appender which calls the format method of its layout object
 * but does not write the result anywhere.
 *
 * <p><b> <font color="#FF2222">The
 * <code>com.progdan.logengine.performance.NullAppender</code> class is
 * intended for internal use only.</font> Consequently, it is not
 * included in the <em>logengine.jar</em> file.</b> </p>
 * */
public class NullAppender extends AppenderSkeleton {

  public static String s;
  public String t;

  public
  NullAppender() {}

  public
  NullAppender(Layout layout) {
    this.layout = layout;
  }

  public
  void close() {}

  public
  void doAppend(LoggingEvent event) {
    if(layout != null) {
      t = layout.format(event);
      s = t;
    }
  }

  public
  void append(LoggingEvent event) {
  }

  /**
     This is a bogus appender but it still uses a layout.
  */
  public
  boolean requiresLayout() {
    return true;
  }
}
