package com.sap.engine.services.security.userstore.persistent;

import com.sap.engine.frame.core.configuration.ConfigurationChangedListener;
import com.sap.engine.frame.core.configuration.ChangeEvent;
import com.sap.engine.services.security.server.UserStoreFactoryCache;

/**
 * Handles the change events from the connector's userstores
 * in the userstores persistent storage.
 *
 * @version 6.30
 * @author  Ekaterina Zheleva
 */
public class ConnectorUserStoreConfigurationListener implements ConfigurationChangedListener {
  private UserStoreFactoryCache cache = null;
  private String userstoreName = null;

  public ConnectorUserStoreConfigurationListener(UserStoreFactoryCache cache, String userstoreName) {
    this.cache = cache;
    this.userstoreName = userstoreName;
  }

  /**
   * Configuration changed
   */
  public void configurationChanged(ChangeEvent e) {
    ChangeEvent[] events = e.getDetailedChangeEvents();
    //String path = events[0].getPath();
    if (events[0].getAction() == ChangeEvent.ACTION_DELETED) {
      if (events.length > 1 && events[1].getAction() == ChangeEvent.ACTION_CREATED) {
        return;
      }
      //String userstoreName = path.substring(path.lastIndexOf('/') + 1);
      cache.getOwner().unregisterUserStore(userstoreName);
    }
  }


}