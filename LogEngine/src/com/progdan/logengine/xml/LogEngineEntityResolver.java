/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.txt file.  */

package com.progdan.logengine.xml;

import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

import com.progdan.logengine.helpers.LogLog;

/**
 * An {@link EntityResolver} specifically designed to return
 * <code>logengine.dtd</code> which is embedded within the logengine jar
 * file.
 *
 * @author Paul Austin
 * */
public class LogEngineEntityResolver implements EntityResolver {

  public InputSource resolveEntity (String publicId, String systemId) {
    if (systemId.endsWith("logengine.dtd")) {
      Class clazz = getClass();
      InputStream in = clazz.getResourceAsStream("/com/progdan/logengine/xml/logengine.dtd");
      if (in == null) {
	LogLog.error("Could not find [logengine.dtd]. Used [" + clazz.getClassLoader()
		     + "] class loader in the search.");
	return null;
      } else {
	return new InputSource(in);
      }
    } else {
      return null;
    }
  }
}
