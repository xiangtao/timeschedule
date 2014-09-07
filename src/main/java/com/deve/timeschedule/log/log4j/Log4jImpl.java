/*
 *    Copyright 2009-2012 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.deve.timeschedule.log.log4j;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.deve.timeschedule.log.Log;

/**
 * @author Eduardo Macarron
 */
public class Log4jImpl implements Log {
  
  private static final String FQCN = Log4jImpl.class.getName();

  private Logger log;

  public Log4jImpl(String clazz) {
    log = Logger.getLogger(clazz);
  }

  public boolean isDebugEnabled() {
    return log.isDebugEnabled();
  }

  public boolean isTraceEnabled() {
    return log.isTraceEnabled();
  }
  
  public boolean isInfoEnabled() {
	    return log.isInfoEnabled();
}

  public void error(String s, Throwable e) {
    log.log(FQCN, Level.ERROR, s, e);
  }

  public void error(String s) {
    log.log(FQCN, Level.ERROR, s, null);
  }

  public void debug(String s) {
    log.log(FQCN, Level.DEBUG, s, null);
  }

  public void trace(String s) {
    log.log(FQCN, Level.TRACE, s, null);
  }
  
  public void info(String s) {
	  log.log(FQCN, Level.INFO, s, null);
 }

  public void warn(String s) {
    log.log(FQCN, Level.WARN, s, null);
  }

}
