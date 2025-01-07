package com.sap.security.api.logon;

import java.util.Properties;

/**
 *  Interface to make external classes configurable.
 */
public interface IConfigurable
{
    /** Method that will be called prior to the first productive
     *  call of a class. All initialization work should be
     *  performed here.
     *  @param p Properties object that holds configuration data
     */
    public void initialize (Properties p);
}
