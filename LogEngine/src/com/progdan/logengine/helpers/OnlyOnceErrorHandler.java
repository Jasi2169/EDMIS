/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */

package com.progdan.logengine.helpers;

import  com.progdan.logengine.spi.ErrorHandler;
import  com.progdan.logengine.spi.LoggingEvent;
import  com.progdan.logengine.Logger;
import  com.progdan.logengine.Appender;

/**

   The <code>OnlyOnceErrorHandler</code> implements logengine's default
   error handling policy which consists of emitting a message for the
   first error in an appender and ignoring all following errors.

   <p>The error message is printed on <code>System.err</code>.

   <p>This policy aims at protecting an otherwise working application
   from being flooded with error messages when logging fails.

   @author Ceki G&uuml;lc&uuml;
   @since 0.9.0 */
public class OnlyOnceErrorHandler implements ErrorHandler {


  final String WARN_PREFIX = "logengine warning: ";
  final String ERROR_PREFIX = "logengine error: ";

  boolean firstTime = true;


  /**
     Does not do anything.
   */
  public
  void setLogger(Logger logger) {
  }


  /**
     No options to activate.
  */
  public
  void activateOptions() {
  }


  /**
     Prints the message and the stack trace of the exception on
     <code>System.err</code>.  */
  public
  void error(String message, Exception e, int errorCode) {
    error(message, e, errorCode, null);
  }

  /**
     Prints the message and the stack trace of the exception on
     <code>System.err</code>.
   */
  public
  void error(String message, Exception e, int errorCode, LoggingEvent event) {
    if(firstTime) {
      LogLog.error(message, e);
      firstTime = false;
    }
  }


  /**
     Print a the error message passed as parameter on
     <code>System.err</code>.
  */
  public
  void error(String message) {
    if(firstTime) {
      LogLog.error(message);
      firstTime = false;
    }
  }

  /**
     Does not do anything.
   */
  public
  void setAppender(Appender appender) {
  }

  /**
     Does not do anything.
   */
  public
  void setBackupAppender(Appender appender) {
  }
}
