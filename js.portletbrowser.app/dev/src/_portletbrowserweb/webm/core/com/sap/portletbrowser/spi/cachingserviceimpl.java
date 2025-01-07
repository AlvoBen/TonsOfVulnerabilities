package com.sap.portletbrowser.spi;

import com.sap.engine.services.portletcontainer.spi.CachingService;

public class CachingServiceImpl implements CachingService {

  private String etag;
  
	public String getETag() {
		return this.etag;
	}

	public void setCacheScope(String arg0) {
		// TODO Auto-generated method stub
		
	}

	public void setETag(String etag) {
		this.etag = etag;
	}

	public void setExpirationCache(Integer arg0) {
		// TODO Auto-generated method stub
	  
		
	}

	public void setUseCachedContent(boolean arg0) {
		// TODO Auto-generated method stub
		
	}

}
