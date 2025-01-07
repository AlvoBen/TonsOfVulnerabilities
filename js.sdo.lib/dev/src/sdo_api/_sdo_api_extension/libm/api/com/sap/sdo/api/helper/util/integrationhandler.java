package com.sap.sdo.api.helper.util;

import java.util.List;

import commonj.sdo.DataObject;

/**
 * An IntegartionHandler is a special option for input that is processed by
 * {@link SDOContentHandler}, e.g. if a SAXSource is loaded.
 * The IntegrationHandler allows to integrate the incoming SAX events into an
 * existing SDO graph. The call-back method {@link #getIntegrationMode(List)}
 * let the client define the integration-strategy.
 * @author D042807
 *
 */
public interface IntegrationHandler {
    
    /**
     * The IntegrationHandler supports 3 integration-strategies: SET, MERGE and
     * APPEND.
     * <table>
     * <tr><th>Mode</th>
     * <th>simple single valued</th>
     * <th>simple many valued</th>
     * <th>complex single valued</th>
     * <th>complex many valued</th></tr>
     * <tr><td>SET</td>
     * <td>replace value</td>
     * <td>replace list</td>
     * <td>replace DataObject</td>
     * <td>replace list</td></tr>
     * <tr><td>MERGE</td>
     * <td>replace value</td>
     * <td>replace items of list by index, if index is too high APPEND</td>
     * <td>merge DataObject</td>
     * <td>merge DataObjects by index in list, if index is too high APPEND</td></tr>
     * <tr><td>APPEND</td>
     * <td>replace value</td>
     * <td>append items to list</td>
     * <td>replace DataObject</td>
     * <td>append DataObjects to list</td></tr>
     * </table>
     */
    public enum Mode {SET, MERGE, APPEND}
    
    /**
     * This call-back method must return the integration-strategy for an element.
     * path. The element is addressed by a path. The path is a list of
     * {@link Element Elements} (URI-name-pairs).
     * In case of many valued properties at the end of the path this method is
     * called just once.
     * @param path The path to the element.
     * @return The integration-strategy for the addressed element.
     */
    Mode getIntegrationMode(List<Element> path);
    
    /**
     * This method must return the root DataObject of the target SDO graph.
     * @return The root of the target structure.
     */
    DataObject getRootObject();
    
    /**
     * This interface represents the URI and name of an element.
     */
    public interface Element {

        /**
         * Returns the URI of the element.
         * @return The URI of the element.
         */
        String getUri();

        /**
         * Returns the name of the element.
         * @return The name of the element.
         */
        String getName();
    }

}
