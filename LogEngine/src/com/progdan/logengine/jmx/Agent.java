
package com.progdan.logengine.jmx;

import javax.management.ObjectName;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import com.sun.jdmk.comm.HtmlAdaptorServer;

import com.progdan.logengine.Logger;


public class Agent {

  static Logger log = Logger.getLogger(Agent.class);

  public Agent() {
  }


  public
  void start() {

    MBeanServer server = MBeanServerFactory.createMBeanServer();
    HtmlAdaptorServer html = new HtmlAdaptorServer();

    try {
      log.info("Registering HtmlAdaptorServer instance.");
      server.registerMBean(html, new ObjectName("Adaptor:name=html,port=8082"));
      log.info("Registering HierarchyDynamicMBean instance.");
      HierarchyDynamicMBean hdm = new HierarchyDynamicMBean();
      server.registerMBean(hdm, new ObjectName("logengine:hiearchy=default"));

    } catch(Exception e) {
      log.error("Problem while regitering MBeans instances.", e);
      return;
    }
    html.start();
  }
}
