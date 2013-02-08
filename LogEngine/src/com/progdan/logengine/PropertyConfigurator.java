/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.txt file.  */


// Contibutors: "Luke Blanshard" <Luke@quiq.com>
//              "Mark DONSZELMANN" <Mark.Donszelmann@cern.ch>
//               Anders Kristensen <akristensen@dynamicsoft.com>

package com.progdan.logengine;

import com.progdan.logengine.DefaultCategoryFactory;
import com.progdan.logengine.config.PropertySetter;
//import com.progdan.logengine.config.PropertySetterException;
import com.progdan.logengine.spi.OptionHandler;
import com.progdan.logengine.spi.Configurator;
import com.progdan.logengine.spi.LoggerFactory;
import com.progdan.logengine.spi.LoggerRepository;
import com.progdan.logengine.spi.RendererSupport;
import com.progdan.logengine.or.RendererMap;
import com.progdan.logengine.helpers.LogLog;
import com.progdan.logengine.helpers.OptionConverter;
import com.progdan.logengine.helpers.FileWatchdog;

import java.util.Enumeration;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Hashtable;

/**
   Allows the configuration of logengine from an external file.  See
   <b>{@link #doConfigure(String, LoggerRepository)}</b> for the
   expected format.

   <p>It is sometimes useful to see how logengine is reading configuration
   files. You can enable logengine internal logging by defining the
   <b>logengine.debug</b> variable.

   <P>As of logengine version 0.8.5, at class initialization time class,
   the file <b>logengine.properties</b> will be searched from the search
   path used to load classes. If the file can be found, then it will
   be fed to the {@link PropertyConfigurator#configure(java.net.URL)}
   method.

   <p>The <code>PropertyConfigurator</code> does not handle the
   advanced configuration features supported by the {@link
   com.progdan.logengine.xml.DOMConfigurator DOMConfigurator} such as
   support for {@link com.progdan.logengine.spi.Filter Filters}, custom
   {@link com.progdan.logengine.spi.ErrorHandler ErrorHandlers}, nested
   appenders such as the {@link com.progdan.logengine.AsyncAppender
   AsyncAppender}, etc.

   <p>All option <em>values</em> admit variable substitution. The
   syntax of variable substitution is similar to that of Unix
   shells. The string between an opening <b>&quot;${&quot;</b> and
   closing <b>&quot;}&quot;</b> is interpreted as a key. The value of
   the substituted variable can be defined as a system property or in
   the configuration file itself. The value of the key is first
   searched in the system properties, and if not found there, it is
   then searched in the configuration file being parsed.  The
   corresponding value replaces the ${variableName} sequence. For
   example, if <code>java.home</code> system property is set to
   <code>/home/xyz</code>, then every occurrence of the sequence
   <code>${java.home}</code> will be interpreted as
   <code>/home/xyz</code>.


   @author Ceki G&uuml;lc&uuml;
   @author Anders Kristensen
   @since 0.8.1 */
public class PropertyConfigurator implements Configurator {

  /**
     Used internally to keep track of configured appenders.
   */
  protected Hashtable registry = new Hashtable(11);
  protected LoggerFactory loggerFactory = new DefaultCategoryFactory();

  static final String      CATEGORY_PREFIX = "logengine.category.";
  static final String      LOGGER_PREFIX   = "logengine.logger.";
  static final String       FACTORY_PREFIX = "logengine.factory";
  static final String    ADDITIVITY_PREFIX = "logengine.additivity.";
  static final String ROOT_CATEGORY_PREFIX = "logengine.rootCategory";
  static final String ROOT_LOGGER_PREFIX   = "logengine.rootLogger";
  static final String      APPENDER_PREFIX = "logengine.appender.";
  static final String      RENDERER_PREFIX = "logengine.renderer.";
  static final String      THRESHOLD_PREFIX = "logengine.threshold";

  /** Key for specifying the {@link com.progdan.logengine.spi.LoggerFactory
      LoggerFactory}.  Currently set to "<code>logengine.loggerFactory</code>".  */
  public static final String LOGGER_FACTORY_KEY = "logengine.loggerFactory";

  static final private String INTERNAL_ROOT_NAME = "root";

  /**
    Read configuration from a file. <b>The existing configuration is
    not cleared nor reset.</b> If you require a different behavior,
    then call {@link  LogManager#resetConfiguration
    resetConfiguration} method before calling
    <code>doConfigure</code>.

    <p>The configuration file consists of statements in the format
    <code>key=value</code>. The syntax of different configuration
    elements are discussed below.

    <h3>Repository-wide threshold</h3>

    <p>The repository-wide threshold filters logging requests by level
    regardless of logger. The syntax is:

    <pre>
    logengine.threshold=[level]
    </pre>

    <p>The level value can consist of the string values OFF, FATAL,
    ERROR, WARN, INFO, DEBUG, ALL or a <em>custom level</em> value. A
    custom level value can be specified in the form
    level#classname. By default the repository-wide threshold is set
    to the lowest possible value, namely the level <code>ALL</code>.
    </p>


    <h3>Appender configuration</h3>

    <p>Appender configuration syntax is:
    <pre>
    # For appender named <i>appenderName</i>, set its class.
    # Note: The appender name can contain dots.
    logengine.appender.appenderName=fully.qualified.name.of.appender.class

    # Set appender specific options.
    logengine.appender.appenderName.option1=value1
    ...
    logengine.appender.appenderName.optionN=valueN
    </pre>

    For each named appender you can configure its {@link Layout}. The
    syntax for configuring an appender's layout is:
    <pre>
    logengine.appender.appenderName.layout=fully.qualified.name.of.layout.class
    logengine.appender.appenderName.layout.option1=value1
    ....
    logengine.appender.appenderName.layout.optionN=valueN
    </pre>

    <h3>Configuring loggers</h3>

    <p>The syntax for configuring the root logger is:
    <pre>
      logengine.rootLogger=[level], appenderName, appenderName, ...
    </pre>

    <p>This syntax means that an optional <em>level</em> can be
    supplied followed by appender names separated by commas.

    <p>The level value can consist of the string values OFF, FATAL,
    ERROR, WARN, INFO, DEBUG, ALL or a <em>custom level</em> value. A
    custom level value can be specified in the form
    <code>level#classname</code>.

    <p>If a level value is specified, then the root level is set
    to the corresponding level.  If no level value is specified,
    then the root level remains untouched.

    <p>The root logger can be assigned multiple appenders.

    <p>Each <i>appenderName</i> (separated by commas) will be added to
    the root logger. The named appender is defined using the
    appender syntax defined above.

    <p>For non-root categories the syntax is almost the same:
    <pre>
    logengine.logger.logger_name=[level|INHERITED|NULL], appenderName, appenderName, ...
    </pre>

    <p>The meaning of the optional level value is discussed above
    in relation to the root logger. In addition however, the value
    INHERITED can be specified meaning that the named logger should
    inherit its level from the logger hierarchy.

    <p>If no level value is supplied, then the level of the
    named logger remains untouched.

    <p>By default categories inherit their level from the
    hierarchy. However, if you set the level of a logger and later
    decide that that logger should inherit its level, then you should
    specify INHERITED as the value for the level value. NULL is a
    synonym for INHERITED.

    <p>Similar to the root logger syntax, each <i>appenderName</i>
    (separated by commas) will be attached to the named logger.

    <p>See the <a href="../../../../manual.html#additivity">appender
    additivity rule</a> in the user manual for the meaning of the
    <code>additivity</code> flag.

    <h3>ObjectRenderers</h3>

    You can customize the way message objects of a given type are
    converted to String before being logged. This is done by
    specifying an {@link com.progdan.logengine.or.ObjectRenderer ObjectRenderer}
    for the object type would like to customize.

    <p>The syntax is:

    <pre>
    logengine.renderer.fully.qualified.name.of.rendered.class=fully.qualified.name.of.rendering.class
    </pre>

    As in,
    <pre>
    logengine.renderer.my.Fruit=my.FruitRenderer
    </pre>

    <h3>Logger Factories</h3>

    The usage of custom logger factories is discouraged and no longer
    documented.

    <h3>Example</h3>

    <p>An example configuration is given below. Other configuration
    file examples are given in the <code>examples</code> folder.

    <pre>

    # Set options for appender named "A1".
    # Appender "A1" will be a SyslogAppender
    logengine.appender.A1=com.progdan.logengine.net.SyslogAppender

    # The syslog daemon resides on www.abc.net
    logengine.appender.A1.SyslogHost=www.abc.net

    # A1's layout is a PatternLayout, using the conversion pattern
    # <b>%r %-5p %c{2} %M.%L %x - %m\n</b>. Thus, the log output will
    # include # the relative time since the start of the application in
    # milliseconds, followed by the level of the log request,
    # followed by the two rightmost components of the logger name,
    # followed by the callers method name, followed by the line number,
    # the nested disgnostic context and finally the message itself.
    # Refer to the documentation of {@link PatternLayout} for further information
    # on the syntax of the ConversionPattern key.
    logengine.appender.A1.layout=com.progdan.logengine.PatternLayout
    logengine.appender.A1.layout.ConversionPattern=%-4r %-5p %c{2} %M.%L %x - %m\n

    # Set options for appender named "A2"
    # A2 should be a RollingFileAppender, with maximum file size of 10 MB
    # using at most one backup file. A2's layout is TTCC, using the
    # ISO8061 date format with context printing enabled.
    logengine.appender.A2=com.progdan.logengine.RollingFileAppender
    logengine.appender.A2.MaxFileSize=10MB
    logengine.appender.A2.MaxBackupIndex=1
    logengine.appender.A2.layout=com.progdan.logengine.TTCCLayout
    logengine.appender.A2.layout.ContextPrinting=enabled
    logengine.appender.A2.layout.DateFormat=ISO8601

    # Root logger set to DEBUG using the A2 appender defined above.
    logengine.rootLogger=DEBUG, A2

    # Logger definitions:
    # The SECURITY logger inherits is level from root. However, it's output
    # will go to A1 appender defined above. It's additivity is non-cumulative.
    logengine.logger.SECURITY=INHERIT, A1
    logengine.additivity.SECURITY=false

    # Only warnings or above will be logged for the logger "SECURITY.access".
    # Output will go to A1.
    logengine.logger.SECURITY.access=WARN


    # The logger "class.of.the.day" inherits its level from the
    # logger hierarchy.  Output will go to the appender's of the root
    # logger, A2 in this case.
    logengine.logger.class.of.the.day=INHERIT
    </pre>

    <p>Refer to the <b>setOption</b> method in each Appender and
    Layout for class specific options.

    <p>Use the <code>#</code> or <code>!</code> characters at the
    beginning of a line for comments.

   <p>
   @param configFileName The name of the configuration file where the
   configuration information is stored.

  */
  public
  void doConfigure(String configFileName, LoggerRepository hierarchy) {
    Properties props = new Properties();
    try {
      FileInputStream istream = new FileInputStream(configFileName);
      props.load(istream);
      istream.close();
    }
    catch (IOException e) {
      LogLog.error("Could not read configuration file ["+configFileName+"].", e);
      LogLog.error("Ignoring configuration file [" + configFileName+"].");
      return;
    }
    // If we reach here, then the config file is alright.
    doConfigure(props, hierarchy);
  }

  /**
   */
  static
  public
  void configure(String configFilename) {
    new PropertyConfigurator().doConfigure(configFilename,
					   LogManager.getLoggerRepository());
  }

  /**
     Read configuration options from url <code>configURL</code>.

     @since 0.8.2
   */
  public
  static
  void configure(java.net.URL configURL) {
    new PropertyConfigurator().doConfigure(configURL,
					   LogManager.getLoggerRepository());
  }


  /**
     Read configuration options from <code>properties</code>.

     See {@link #doConfigure(String, LoggerRepository)} for the expected format.
  */
  static
  public
  void configure(Properties properties) {
    new PropertyConfigurator().doConfigure(properties,
					   LogManager.getLoggerRepository());
  }

  /**
     Like {@link #configureAndWatch(String, long)} except that the
     default delay as defined by {@link FileWatchdog#DEFAULT_DELAY} is
     used.

     @param configFilename A file in key=value format.

  */
  static
  public
  void configureAndWatch(String configFilename) {
    configureAndWatch(configFilename, FileWatchdog.DEFAULT_DELAY);
  }


  /**
     Read the configuration file <code>configFilename</code> if it
     exists. Moreover, a thread will be created that will periodically
     check if <code>configFilename</code> has been created or
     modified. The period is determined by the <code>delay</code>
     argument. If a change or file creation is detected, then
     <code>configFilename</code> is read to configure logengine.

      @param configFilename A file in key=value format.
      @param delay The delay in milliseconds to wait between each check.
  */
  static
  public
  void configureAndWatch(String configFilename, long delay) {
    PropertyWatchdog pdog = new PropertyWatchdog(configFilename);
    pdog.setDelay(delay);
    pdog.start();
  }


  /**
     Read configuration options from <code>properties</code>.

     See {@link #doConfigure(String, LoggerRepository)} for the expected format.
  */
  public
  void doConfigure(Properties properties, LoggerRepository hierarchy) {

    String value = properties.getProperty(LogLog.DEBUG_KEY);
    if(value == null) {
      value = properties.getProperty(LogLog.CONFIG_DEBUG_KEY);
      if(value != null)
	LogLog.warn("[logengine.configDebug] is deprecated. Use [logengine.debug] instead.");
    }

    if(value != null) {
      LogLog.setInternalDebugging(OptionConverter.toBoolean(value, true));
    }

    String thresholdStr = OptionConverter.findAndSubst(THRESHOLD_PREFIX,
						       properties);
    if(thresholdStr != null) {
      hierarchy.setThreshold(OptionConverter.toLevel(thresholdStr,
						     (Level) Level.ALL));
      LogLog.debug("Hierarchy threshold set to ["+hierarchy.getThreshold()+"].");
    }

    configureRootCategory(properties, hierarchy);
    configureLoggerFactory(properties);
    parseCatsAndRenderers(properties, hierarchy);

    LogLog.debug("Finished configuring.");
    // We don't want to hold references to appenders preventing their
    // garbage collection.
    registry.clear();
  }

  /**
     Read configuration options from url <code>configURL</code>.
   */
  public
  void doConfigure(java.net.URL configURL, LoggerRepository hierarchy) {
    Properties props = new Properties();
    LogLog.debug("Reading configuration from URL " + configURL);
    try {
      props.load(configURL.openStream());
    }
    catch (java.io.IOException e) {
      LogLog.error("Could not read configuration file from URL [" + configURL
		   + "].", e);
      LogLog.error("Ignoring configuration file [" + configURL +"].");
      return;
    }
    doConfigure(props, hierarchy);
  }


  // --------------------------------------------------------------------------
  // Internal stuff
  // --------------------------------------------------------------------------

  /**
     Check the provided <code>Properties</code> object for a
     {@link com.progdan.logengine.spi.LoggerFactory LoggerFactory}
     entry specified by {@link #LOGGER_FACTORY_KEY}.  If such an entry
     exists, an attempt is made to create an instance using the default
     constructor.  This instance is used for subsequent Category creations
     within this configurator.

     @see #parseCatsAndRenderers
   */
  protected void configureLoggerFactory(Properties props) {
    String factoryClassName = OptionConverter.findAndSubst(LOGGER_FACTORY_KEY,
							   props);
    if(factoryClassName != null) {
      LogLog.debug("Setting category factory to ["+factoryClassName+"].");
      loggerFactory = (LoggerFactory)
	          OptionConverter.instantiateByClassName(factoryClassName,
							 LoggerFactory.class,
							 loggerFactory);
      PropertySetter.setProperties(loggerFactory, props, FACTORY_PREFIX + ".");
    }
  }

  /*
  void configureOptionHandler(OptionHandler oh, String prefix,
			      Properties props) {
    String[] options = oh.getOptionStrings();
    if(options == null)
      return;

    String value;
    for(int i = 0; i < options.length; i++) {
      value =  OptionConverter.findAndSubst(prefix + options[i], props);
      LogLog.debug(
         "Option " + options[i] + "=[" + (value == null? "N/A" : value)+"].");
      // Some option handlers assume that null value are not passed to them.
      // So don't remove this check
      if(value != null) {
	oh.setOption(options[i], value);
      }
    }
    oh.activateOptions();
  }
  */


  void configureRootCategory(Properties props, LoggerRepository hierarchy) {
    String effectiveFrefix = ROOT_LOGGER_PREFIX;
    String value = OptionConverter.findAndSubst(ROOT_LOGGER_PREFIX, props);

    if(value == null) {
      value = OptionConverter.findAndSubst(ROOT_CATEGORY_PREFIX, props);
      effectiveFrefix = ROOT_CATEGORY_PREFIX;
    }

    if(value == null)
      LogLog.debug("Could not find root logger information. Is this OK?");
    else {
      Logger root = hierarchy.getRootLogger();
      synchronized(root) {
	parseCategory(props, root, effectiveFrefix, INTERNAL_ROOT_NAME, value);
      }
    }
  }


  /**
     Parse non-root elements, such non-root categories and renderers.
  */
  protected
  void parseCatsAndRenderers(Properties props, LoggerRepository hierarchy) {
    Enumeration enumeration = props.propertyNames();
    while(enumeration.hasMoreElements()) {
      String key = (String) enumeration.nextElement();
      if(key.startsWith(CATEGORY_PREFIX) || key.startsWith(LOGGER_PREFIX)) {
	String loggerName = null;
	if(key.startsWith(CATEGORY_PREFIX)) {
	  loggerName = key.substring(CATEGORY_PREFIX.length());
	} else if(key.startsWith(LOGGER_PREFIX)) {
	  loggerName = key.substring(LOGGER_PREFIX.length());
	}
	String value =  OptionConverter.findAndSubst(key, props);
	Logger logger = hierarchy.getLogger(loggerName, loggerFactory);
	synchronized(logger) {
	  parseCategory(props, logger, key, loggerName, value);
	  parseAdditivityForLogger(props, logger, loggerName);
	}
      } else if(key.startsWith(RENDERER_PREFIX)) {
	String renderedClass = key.substring(RENDERER_PREFIX.length());
	String renderingClass = OptionConverter.findAndSubst(key, props);
	if(hierarchy instanceof RendererSupport) {
	  RendererMap.addRenderer((RendererSupport) hierarchy, renderedClass,
				  renderingClass);
	}
      }
    }
  }

  /**
     Parse the additivity option for a non-root category.
   */
  void parseAdditivityForLogger(Properties props, Logger cat,
				  String loggerName) {
    String value = OptionConverter.findAndSubst(ADDITIVITY_PREFIX + loggerName,
					     props);
    LogLog.debug("Handling "+ADDITIVITY_PREFIX + loggerName+"=["+value+"]");
    // touch additivity only if necessary
    if((value != null) && (!value.equals(""))) {
      boolean additivity = OptionConverter.toBoolean(value, true);
      LogLog.debug("Setting additivity for \""+loggerName+"\" to "+
		   additivity);
      cat.setAdditivity(additivity);
    }
  }

  /**
     This method must work for the root category as well.
   */
  void parseCategory(Properties props, Logger logger, String optionKey,
		     String loggerName, String value) {

    LogLog.debug("Parsing for [" +loggerName +"] with value=[" + value+"].");
    // We must skip over ',' but not white space
    StringTokenizer st = new StringTokenizer(value, ",");

    // If value is not in the form ", appender.." or "", then we should set
    // the level of the loggeregory.

    if(!(value.startsWith(",") || value.equals(""))) {

      // just to be on the safe side...
      if(!st.hasMoreTokens())
	return;

      String levelStr = st.nextToken();
      LogLog.debug("Level token is [" + levelStr + "].");

      // If the level value is inherited, set category level value to
      // null. We also check that the user has not specified inherited for the
      // root category.
      if(INHERITED.equalsIgnoreCase(levelStr) ||
 	                                  NULL.equalsIgnoreCase(levelStr)) {
	if(loggerName.equals(INTERNAL_ROOT_NAME)) {
	  LogLog.warn("The root logger cannot be set to null.");
	} else {
	  logger.setLevel(null);
	}
      } else {
	logger.setLevel(OptionConverter.toLevel(levelStr, (Level) Level.DEBUG));
      }
      LogLog.debug("Category " + loggerName + " set to " + logger.getLevel());
    }

    // Begin by removing all existing appenders.
    logger.removeAllAppenders();

    Appender appender;
    String appenderName;
    while(st.hasMoreTokens()) {
      appenderName = st.nextToken().trim();
      if(appenderName == null || appenderName.equals(","))
	continue;
      LogLog.debug("Parsing appender named \"" + appenderName +"\".");
      appender = parseAppender(props, appenderName);
      if(appender != null) {
	logger.addAppender(appender);
      }
    }
  }

  Appender parseAppender(Properties props, String appenderName) {
    Appender appender = registryGet(appenderName);
    if((appender != null)) {
      LogLog.debug("Appender \"" + appenderName + "\" was already parsed.");
      return appender;
    }
    // Appender was not previously initialized.
    String prefix = APPENDER_PREFIX + appenderName;
    String layoutPrefix = prefix + ".layout";

    appender = (Appender) OptionConverter.instantiateByKey(props, prefix,
					      com.progdan.logengine.Appender.class,
					      null);
    if(appender == null) {
      LogLog.error(
              "Could not instantiate appender named \"" + appenderName+"\".");
      return null;
    }
    appender.setName(appenderName);

    if(appender instanceof OptionHandler) {
      if(appender.requiresLayout()) {
	Layout layout = (Layout) OptionConverter.instantiateByKey(props,
								  layoutPrefix,
								  Layout.class,
								  null);
	if(layout != null) {
	  appender.setLayout(layout);
	  LogLog.debug("Parsing layout options for \"" + appenderName +"\".");
	  //configureOptionHandler(layout, layoutPrefix + ".", props);
          PropertySetter.setProperties(layout, props, layoutPrefix + ".");
	  LogLog.debug("End of parsing for \"" + appenderName +"\".");
	}
      }
      //configureOptionHandler((OptionHandler) appender, prefix + ".", props);
      PropertySetter.setProperties(appender, props, prefix + ".");
      LogLog.debug("Parsed \"" + appenderName +"\" options.");
    }
    registryPut(appender);
    return appender;
  }


  void  registryPut(Appender appender) {
    registry.put(appender.getName(), appender);
  }

  Appender registryGet(String name) {
    return (Appender) registry.get(name);
  }
}

class PropertyWatchdog extends FileWatchdog {

  PropertyWatchdog(String filename) {
    super(filename);
  }

  /**
     Call {@link PropertyConfigurator#configure(String)} with the
     <code>filename</code> to reconfigure logengine. */
  public
  void doOnChange() {
    new PropertyConfigurator().doConfigure(filename,
					   LogManager.getLoggerRepository());
  }
}
