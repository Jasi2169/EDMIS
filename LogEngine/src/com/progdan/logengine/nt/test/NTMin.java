/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.txt file.  */

package com.progdan.logengine.nt.test;


import com.progdan.logengine.Logger;
import com.progdan.logengine.BasicConfigurator;
import com.progdan.logengine.nt.NTEventLogAppender;
import com.progdan.logengine.Priority;
import com.progdan.logengine.NDC;


public class NTMin {

  static Logger cat = Logger.getLogger(NTMin.class.getName());

  public
  static
  void main(String argv[]) {

    //if(argv.length == 1) {
    init();
    //}
    //else {
    //Usage("Wrong number of arguments.");
    //}
      test("someHost");
  }


  static
  void Usage(String msg) {
    System.err.println(msg);
    System.err.println( "Usage: java " + NTMin.class + "");
    System.exit(1);
  }


  static
  void init() {

    BasicConfigurator.configure(new NTEventLogAppender());
  }

  static
  void test(String host) {
    NDC.push(host);
    int i  = 0;
    cat.debug( "Message " + i++);
    cat.info( "Message " + i++);
    cat.warn( "Message " + i++);
    cat.error( "Message " + i++);
    cat.log(Priority.FATAL, "Message " + i++);
    cat.debug("Message " + i++,  new Exception("Just testing."));
  }
}
