package com.sap.sdm.api.remote;

/**
 * All possible <code>int</code> and <code>String</code> represantations
 * for <code>ComponentVersionHandlingRule</code>.
 * 
 * @author <A HREF="mailto:DL_011000358700005701181999E">Change Management Tools</a> - Martin Stahl
 * @version 1.0
 * @see com.sap.sdm.api.remote.ComponentVersionHandlingRule
 * 
 * @deprecated The SDM API is deprecated. From now on the <code>Deploy Controller API</code>
 * has to be used. The current type is replaced by <code>com.sap.engine.services.dc.api.deploy.ComponentVersionHandlingRule</code>.
 */
public interface ComponentVersionHandlingRules {

  /**
   * An <code>int</code> representation of the 
   * <code>ComponentVersionHandlingRule</code> UPDATE_ALL_VERSIONS.
   * This rule means that all versions of a DC are accepted
   * for deployment. In detail:
   * if a DC A is deployed with version x it is possible to 
   * deploy DC A with version y&lt;x, y=x and y&gt;x.
   */
  public final static int UPDATE_ALL_VERSIONS = 0;

  /**
   * An <code>int</code> representation of the 
   * <code>ComponentVersionHandlingRule</code> UPDATE_SAME_AND_LOWER_VERSIONS_ONLY.
   * This rule means that versions of a DC A are only accepted
   * for deployment if A was not deployed with a higher version before.
   * In detail:
   * if a DC A is deployed with version x it is possible to 
   * deploy DC A with version y=x and y&gt;x.
   * It is impossible to deploy DC with version y&lt;x.
   */
  public final static int UPDATE_SAME_AND_LOWER_VERSIONS_ONLY = 1;

  /**
   * An <code>int</code> representation of the 
   * <code>ComponentVersionHandlingRule</code> UPDATE_LOWER_VERSIONS_ONLY.
   * This rule means that versions of a DC A are only accepted
   * for deployment if A was not deployed with a higher or the same
   * version before.
   * In detail:
   * if a DC A is deployed with version x it is possible to 
   * deploy DC A with version y&gt;x.
   * It is impossible to deploy DC with version y&lt;x and y=x.
   */
  public final static int UPDATE_LOWER_VERSIONS_ONLY = 2;
  

  /**
   * A <code>int</code> representation of the 
   * <code>ComponentVersionHandlingRule</code>
   * UPDATE_LOWER_OR_CHANGED_VERSIONS_ONLY.
   * This rule means:
   * <ul><li>For SC - versions of a SC A are only accepted for deployment 
   * if A was not deployed with a higher or the same version before.</li>
   * <li>For top level DC - versions of a DC A are only accepted for deployment 
   * if A was not deployed with a higher or the same version before.</li>
   * <li>For DC contained by SC - SC contained DC A should be accepted for deployment 
   * (see the rule above for SC) and DC A are only accepted for deployment 
   * if A was not deployed with the same version before.</li></ul>  
   * 
   * This strategy is new with API client version 13     
   */
  public final static int UPDATE_LOWER_OR_CHANGED_VERSIONS_ONLY = 3;
  
  
  /**
   * An <code>int</code> representation of the 
   * <code>ComponentVersionHandlingRule</code> UPDATE_ALL_VERSIONS.
   * This rule means that all versions of a DC are accepted
   * for deployment. In detail:
   * if a DC A is deployed with version x it is possible to 
   * deploy DC A with version y&lt;x, y=x and y&gt;x.
   */
  public final static String UPDATE_ALL_VERSIONS_S = "UpdateAllVersions";

  /**
   * An <code>int</code> representation of the 
   * <code>ComponentVersionHandlingRule</code> UPDATE_SAME_AND_LOWER_VERSIONS_ONLY.
   * This rule means that versions of a DC A are only accepted
   * for deployment if A was not deployed with a higher version before.
   * In detail:
   * if a DC A is deployed with version x it is possible to 
   * deploy DC A with version y=x and y&gt;x.
   * It is impossible to deploy DC with version y&lt;x.
   */
  public final static String UPDATE_SAME_AND_LOWER_VERSIONS_ONLY_S = "UpdateSameAndLowerVersionsOnly";

  /**
   * A <code>String</code> representation of the 
   * <code>ComponentVersionHandlingRule</code> UPDATE_LOWER_VERSIONS_ONLY.
   * This rule means that versions of a DC A are only accepted
   * for deployment if A was not deployed with a higher or the same
   * version before.
   * In detail:
   * if a DC A is deployed with version x it is possible to 
   * deploy DC A with version y&gt;x.
   * It is impossible to deploy DC with version y&lt;x and y=x.
   */
  public final static String UPDATE_LOWER_VERSIONS_ONLY_S = "UpdateLowerVersionsOnly";
  
}
