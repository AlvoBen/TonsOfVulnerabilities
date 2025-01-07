package com.sap.engine.services.rmi_p4;

import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;

public class P4URLLoader extends URLClassLoader {

  public P4URLLoader(URL[] urls){
    super(urls);
  }

  public P4URLLoader(URL[] urls, ClassLoader parent){
    super(urls, parent);
  }

  public P4URLLoader(URL[] urls, ClassLoader parent, URLStreamHandlerFactory factory) {
    super(urls, parent, factory);
  }

  public void addURL(URL url){
    super.addURL(url);
  }

  public void addURLs(URL[] urls){
    for(int i = 0; i < urls.length; i++){
      super.addURL(urls[i]);
    }
  }

  public boolean checkURL(URL url){
    URL[] uu = super.getURLs();
    boolean hasUrl = false;
    for (int i = 0; i < uu.length && !hasUrl; i++) {
      hasUrl = uu[i].sameFile(url);
    }
    return hasUrl;
  }

  public void addMissedURLs(URL[] urls){
    for (int i = 0; i < urls.length; i++) {
      if((urls[i] != null) && (!checkURL(urls[i]))){
        addURL(urls[i]);
      }

    }
  }



}
