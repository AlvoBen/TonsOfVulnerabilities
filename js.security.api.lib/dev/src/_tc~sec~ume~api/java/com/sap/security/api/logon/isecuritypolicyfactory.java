package com.sap.security.api.logon;

import com.sap.security.api.ISecurityPolicy;

/**
 * Security Policy Factory provides the retrieving of security policies.
 * @author Guenther Wannenmacher
 * @version 1.0
 */
public interface ISecurityPolicyFactory {
    /**
     * Gets the SecurityPolicy..
     *
     * @return a security policy object
     */
    public ISecurityPolicy getSecurityPolicy();
}