/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */

package com.progdan.logengine.varia;

import com.progdan.logengine.PropertyConfigurator;
import com.progdan.logengine.spi.Configurator;
import java.net.URL;
import  com.progdan.logengine.spi.LoggerRepository;

public class ReloadingPropertyConfigurator implements Configurator {


  PropertyConfigurator delegate = new PropertyConfigurator();


  public ReloadingPropertyConfigurator() {
  }

  public
  void doConfigure(URL url, LoggerRepository repository) {
  }

}
