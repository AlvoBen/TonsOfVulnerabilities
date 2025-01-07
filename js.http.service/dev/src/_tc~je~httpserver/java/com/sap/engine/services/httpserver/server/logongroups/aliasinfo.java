/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.httpserver.server.logongroups;

/**
 *
 * version 1.0
 * PREFIX=/isolde~za/&GROUP=za&CASE=&VHOST=*.*;&STACK=J2EE
 * PREFIX=/isolde~zb/&GROUP=zb&CASE=&VHOST=*.*;&STACK=J2EE
 * PREFIX=/isolde~zall/&GROUP=zall&CASE=&VHOST=*.*;&STACK=J2EE
 * PREFIX=/isolde/&GROUP=zall&CASE=&VHOST=*.*;&STACK=J2EE
 * RN
 */
public class AliasInfo {
  public static final String separator = "&";
  public static final String PREFIX = "PREFIX";
  public static final String GROUP = "GROUP";
  public static final String CASE = "CASE";
  public static final String VHOST = "VHOST";
  public static final String STACK = "STACK";
  public static final String J2EE = "J2EE";

  private String aliasName = null;
  private String group = null;
  private boolean case_ = true;
  private String vhost = null;
  private String stack = null;
  private boolean isExact = false;

  public AliasInfo() {
    init();
  }

  public AliasInfo(String alias) {
    init();
    this.aliasName = alias;
  }

  private void init() {
    aliasName = null;
    group = null;
    case_ = true;
    vhost = null;
    stack = null;
  }

  public String getAliasName() {
    return aliasName;
  }

  public void setAliasName(String aliasName) {
    this.aliasName = aliasName;
  }
  
  public String getGroup() {
    return group;
  }

  public void setGroup(String group) {
    this.group = group;
  }

  public boolean getCase() {
    return case_;
  }

  public void setCase(boolean case_) {
    this.case_ = case_;
  }

  public String getVHost() {
    return vhost;
  }

  public void setVHost(String vhost) {
    this.vhost = vhost;
  }

  public String getStack() {
    return stack;
  }

  public void setStack(String stack) {
    this.stack = stack;
  }
  
  public void setIsExactAlias(boolean isExact) {
    this.isExact = isExact;
  }
  
  public boolean isExactAlias() {
    return isExact;
  }

//  public void initAliasInfo(String aliasInfo) throws ParseException {
//    if (aliasInfo == null || aliasInfo.trim().length() == 0) {
//      throw new ParseException("Illegal alias info [" + aliasInfo + "].");
//    }
//    aliasInfo = aliasInfo.trim();
//    StringTokenizer tokenizer = new StringTokenizer(aliasInfo, separator);
//    if (!tokenizer.hasMoreTokens()) {
//      throw new ParseException("Illegal alias info [" + aliasInfo + "]. Alias name not found.");
//    }
//    parsePrefix(tokenizer.nextToken());
//    if (!tokenizer.hasMoreTokens()) {
//      return;
//    }
//    String next = tokenizer.nextToken();
//    if (parseGroup(next)) {
//      next = tokenizer.nextToken();
//    }
//    if (!tokenizer.hasMoreTokens()) {
//      return;
//    }
//    if (parseCase(next)) {
//      next = tokenizer.nextToken();
//    }s
//    if (!tokenizer.hasMoreTokens()) {
//      return;
//    }
//    if (parseVHost(next)) {
//      next = tokenizer.nextToken();
//    }
//    if (!tokenizer.hasMoreTokens()) {
//      return;
//    }
//    if (parseStack(next)) {
//      next = tokenizer.nextToken();
//    }
//    if (tokenizer.hasMoreTokens()) {
//      throw new ParseException("Illegal alias info [" + aliasInfo
//              + "]. Unknown attributes: [" + tokenizer.nextToken() + "].");
//    }
//  }
//
//  /**
//   * PREFIX=/isolde~za/&GROUP=za&CASE=&VHOST=*.*;&STACK=J2EE
//   * @return
//   */
//  public String toAliasInfoConfigLine() {
//    //alias
//    String res = PREFIX + "=" + "/";
//    if (aliasName != "/") {
//      res += aliasName + "/" + separator;
//    }
//    res += separator;
//    //group
//    res += GROUP + "=";
//    if (group != null) {
//      res += group;
//    }
//    res += separator;
//    //case
//    //todo ??
//    res += CASE + "=";
//    res += separator;
//    //stack
//    res += STACK + "=" + stack;
//    return res;
//  }
//
//  /**
//   * PREFIX=/isolde~zb/
//   */
//  private void parsePrefix(String prefix) throws ParseException {
//    if (prefix == null || prefix.trim().length() == 0) {
//      throw new ParseException("Illegal alias info prefix [" + prefix + "].");
//    }
//    prefix = prefix.trim();
//    int sep = prefix.indexOf('=');
//    if (sep == -1) {
//      throw new ParseException("Illegal alias info syntax [" + prefix + "]. Missing [=].");
//    }
//    aliasName = prefix.substring(sep + 1);
//    aliasName = aliasName.trim();
//    prefix = prefix.substring(0, sep);
//    prefix = prefix.trim();
//    if (!PREFIX.equals(prefix)) {
//      throw new ParseException("Illegal alias info syntax. Found [" + prefix + "]. Expected [" + PREFIX + "].");
//    }
//    aliasName = ParseUtils.convertAlias(aliasName);
//    aliasName = ParseUtils.canonicalize(aliasName);
//    if (aliasName.startsWith(ParseUtils.separator)) {
//      aliasName = aliasName.substring(1);
//    }
//    if (aliasName.endsWith(ParseUtils.separator)) {
//      aliasName = aliasName.substring(0, aliasName.length() - 2);
//    }
//  }
//
//  /**
//   * GROUP=za
//   * @param next
//   */
//  private boolean parseGroup(String next) {
//    if (next == null || next.trim().length() == 0) {
//      //todo - warning
//      return true;
//    }
//    next = next.trim();
//    int sep = next.indexOf('=');
//    if (sep == -1) {
//      return next.startsWith(GROUP);
//    }
//    group = next.substring(sep + 1);
//    group = group.trim();
//    next = next.substring(0, sep);
//    next = next.trim();
//    if (!GROUP.equals(next)) {
//      group = null;
//      return false;
//    }
//    return true;
//  }
//
//  /**
//   * CASE=
//   * @param next
//   * @return
//   */
//  private boolean parseCase(String next) {
//    if (next == null || next.trim().length() == 0) {
//      //todo - warning
//      return true;
//    }
//    next = next.trim();
//    int sep = next.indexOf('=');
//    if (sep == -1) {
//      return next.startsWith(CASE);
//    }
//    next = next.substring(0, sep);
//    next = next.trim();
//    if (!CASE.equals(next)) {
//      return false;
//    }
//    return true;
//  }
//
//  /**
//   * VHOST=*.*
//   * @param next
//   * @return
//   */
//  private boolean parseVHost(String next) {
//    if (next == null || next.trim().length() == 0) {
//      //todo - warning
//      return true;
//    }
//    next = next.trim();
//    int sep = next.indexOf('=');
//    if (sep == -1) {
//      return next.startsWith(VHOST);
//    }
//    next = next.substring(0, sep);
//    next = next.trim();
//    if (!VHOST.equals(next)) {
//      return false;
//    }
//    return true;
//  }
//
//  /**
//   * STACK=J2EE
//   * @param next
//   * @return
//   */
//  private boolean parseStack(String next) {
//    if (next == null || next.trim().length() == 0) {
//      //todo - warning
//      return true;
//    }
//    next = next.trim();
//    int sep = next.indexOf('=');
//    if (sep == -1) {
//      return next.startsWith(STACK);
//    }
//    stack = next.substring(sep + 1);
//    stack = stack.trim();
//    next = next.substring(0, sep);
//    next = next.trim();
//    if (!VHOST.equals(next)) {
//      stack = J2EE;
//      return false;
//    }
//    return true;
//  }
}