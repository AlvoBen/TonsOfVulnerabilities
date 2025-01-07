
package com.sap.engine.services.security.remoteimpl.crypt;

import com.sap.engine.services.security.remote.crypt.RemoteCryptography;
import com.sap.engine.services.security.restriction.Restrictions;

import javax.rmi.PortableRemoteObject;
import java.util.Vector;
import java.rmi.RemoteException;
import java.lang.reflect.Modifier;
import java.security.Provider;
import java.security.Security;

public class RemoteCryptographyImpl extends PortableRemoteObject implements RemoteCryptography {

  public RemoteCryptographyImpl() throws RemoteException {
  }


  public Vector getInstalledProviders() throws RemoteException {
    try {
      Provider[] providers = Security.getProviders();
      Vector result = new Vector();

      for (int i = 0; i < providers.length; i++) {
        result.add(providers[i].getName());
      }

      return result;
    } catch (Exception e) {
      throw new RemoteException("Cannot list providers.", e);
    }
  }


  public String getProviderInfo(String provider) throws RemoteException {
    try {
      return Security.getProvider(provider).getInfo();
    } catch (Exception e) {
      Throwable error = null;

      try {
        Class providerClass = Class.forName(provider);
        return ((Provider) providerClass.newInstance()).getInfo();
      } catch (Exception exc) {
        error = exc;
      } catch (NoClassDefFoundError err) {
        error = err;
      }

      if (e instanceof NullPointerException) {
        return " Cannot add the provider due to " + error;
      } else {
        throw new RemoteException("Cannot get info of provider " + provider + ".", e);
      }
    }
  }


  public void setInstalledProviders(Vector providers) throws RemoteException {
    try {
      Restrictions.checkPermission(Restrictions.CRYPTOGRAPHY_PROVIDERS, Restrictions.RESTRICTION_CHANGE_PROVIDERS);

      for (int i = 0; i < providers.size(); i++) {
        String name = (String) providers.elementAt(i);
        Provider provider = null;

        try {
          provider = Security.getProvider(name);
          Security.removeProvider(name);
        } catch (Exception e) {
          // provider is not found
          provider = null;
        }

        if (provider == null) {
          Class providerClass = Class.forName(name);
          int modifiers = providerClass.getModifiers();

          if ((Modifier.isAbstract(modifiers)) || (Modifier.isInterface(modifiers)) || (Modifier.isPrivate(modifiers))) {
            throw new RemoteException("The provider cannot be instantiated.");
          }

          if (Provider.class.isAssignableFrom(providerClass)) {
            provider = (Provider) providerClass.newInstance();
          }
        }
        Security.insertProviderAt(provider, i + 1);
      }

      Provider[] list = Security.getProviders();
      for (int i = list.length - 1; i >= providers.size(); i--) {
        Security.removeProvider(list[i].getName());
      }
    } catch (RemoteException e) {
      throw e;
    } catch (Exception e) {
      throw new RemoteException("Cannot change providers.", e);
    } catch (NoClassDefFoundError cdnfe) {
      throw new RemoteException("Cannot load new provider.", cdnfe);
    }
  }

}