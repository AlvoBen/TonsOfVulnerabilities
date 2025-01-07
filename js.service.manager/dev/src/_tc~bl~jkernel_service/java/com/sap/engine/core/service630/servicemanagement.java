package com.sap.engine.core.service630;

import com.sap.engine.frame.state.ManagementInterface;
import java.util.Properties;

/**
 * Provide information for all service container components.
 *
 * @author Dimitar Kostadinov
 * @version 7.10
 */
public interface ServiceManagement extends ManagementInterface {

  /**
   * Returns all supported by service container types.
   *
   * @return all valid component types.
   */
  String[] getTypes();

  /**
   * Returns the names of the components from specified type.
   *
   * @param type of the components - a valid entry from type array
   *
   * @return all valid component types.
   */
  String[] getNames(String type);

  /**
   * Returns a property object representing following structure:
   *
   * [name] -> [name of the component]
   * [type] -> [type of the component - valid entry from the types array]
   * [current_status] -> [current component status - started/stopped/error]
   * [default_status] -> [default component status - started/stopped]
   *
   * @param name of the component
   * @param type of the components - a valid entry from type array
   *
   * @return properties object representing component status
   */
  Properties getStatus(String name, String type);

  /**
   * Returns properties object array representing all components, the property object structure is:
   *
   * [name] -> [name of the component]
   * [type] -> [type of the component - valid entry from the types array]
   * [current_status] -> [current component status - started/stopped/error]
   * [default_status] -> [default component status - started/stopped]
   *
   * @return properties array representing all component statuses
   */
  Properties[] getStatuses();

  /**
   * Returns a property object representing following structure:
   *
   * [name] -> [name of the component]
   * [type] -> [type of the component - valid entry from the types array]
   * [hash] -> [String representing numeric hash code of the component]
   *
   * @param name of the component
   * @param type of the components - a valid entry from type array
   *
   * @return properties object representing component hash
   */
  Properties getHash(String name, String type);

  /**
   * Returns properties object array representing all component hashes, the property object structure is:
   *
   * [name] -> [name of the component]
   * [type] -> [type of the component - valid entry from the types array]
   * [hash] -> [String representing numeric hash code of the component]
   *
   * @return properties array representing all component hashes
   */
  Properties[] getHashes();

}
