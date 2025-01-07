package com.tssap.dtr.client.lib.protocol;

import java.util.Iterator;

import com.tssap.dtr.client.lib.protocol.entities.ResourceElement;


/**
 * This interface represents a MultiStatus response entity 
 * used to report the results of various
 * DAV and DeltaV requests.
 */
public interface IMultiStatusResponse extends IResponse {

 /**
   * Returns the number of resource elements stored in this entity.
   * @return The number of resources.
   */
  int size();
  
  /**
   * Returns the first resource of the response. The resources are provided
   * in the order they occured in the multistatus response.
   * @return The resource element from the first &lt;DAV:response&gt;
   * entry in the multistatus response, or null if no resource was
   * reveived.
   */
  ResourceElement first();  

  /**
   * Returns the resource specified by index. The resources are provided
   * in the order they occured in the multistatus response.
   * @param i index of a certain <response> tag.
   * @return The resource element that corresponds to the i-th <DAV:response>
   * entry in the multistatus response, or null if no resource was
   * reveived.
   */
  ResourceElement get(int i);

  /**
   * Returns an enumeration of ResourceElement objects that were retrieved
   * from the multistatus response. Each element contains the URL of
   * the corresponding resource, the set of retrieved properties and optionally
   * a human readable description of the response state.
   * @return An iterator of ResourceElement instances.
   */
  Iterator iterator(); 

}
