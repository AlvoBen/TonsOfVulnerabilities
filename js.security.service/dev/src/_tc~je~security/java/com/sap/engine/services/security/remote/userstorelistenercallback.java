package com.sap.engine.services.security.remote;

import com.sap.engine.interfaces.security.userstore.listener.UserStoreListener;
import java.rmi.*;

public interface UserStoreListenerCallback extends Remote, UserStoreListener {

}