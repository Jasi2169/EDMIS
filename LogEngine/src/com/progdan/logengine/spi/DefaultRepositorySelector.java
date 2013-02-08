


package com.progdan.logengine.spi;


public class DefaultRepositorySelector implements RepositorySelector {

  final LoggerRepository repository;

  public
  DefaultRepositorySelector(LoggerRepository repository) {
    this.repository = repository;
  }

  public
  LoggerRepository getLoggerRepository() {
    return repository;
  }
}

