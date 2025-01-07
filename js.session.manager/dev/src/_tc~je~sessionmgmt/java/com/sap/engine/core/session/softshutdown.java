package com.sap.engine.core.session;

import java.util.Iterator;

import com.sap.engine.core.Names;
import com.sap.engine.boot.soft.FailoverListener;
import com.sap.engine.session.SessionContext;
import com.sap.engine.session.SessionContextFactory;
import com.sap.engine.session.SessionDomain;
import com.sap.engine.session.spi.persistent.Storage;
import com.sap.tc.logging.Location;

public class SoftShutdown implements FailoverListener{
	
	private static Location loc = Location.getLocation(SoftShutdown.class.getName(), Names.KERNEL_DC_NAME, Names.SESSION_MANAGER_CSN_COMPONENT);
	
	private boolean isOK;
	private int scope;
	private Storage storage;
	
	public boolean failoverState(int scope) {
		loc.pathT("failoverState() called");
		this.isOK = true;
		this.storage = ConfigurationEntryBuilder.buildStorageForType(ConfigurationEntryBuilder.STORAGE_DB);
		this.scope = scope;
		loc.pathT("storage is set to: " + storage);
		Iterator it = SessionContextFactory.getInstance().contexts();
		
		while(it.hasNext()){
			SessionContext context = (SessionContext) it.next();
			loc.errorT("context: " + context);
			traverseDomains(context.rootDomains());

		}
		return isOK;
	}
	
	private void traverseDomains(Iterator iterator) {
		SessionDomain domain;
		while(iterator.hasNext()){
			domain = (SessionDomain) iterator.next();
			traverseDomains(domain.subDomains());
			if(!domain.failoverState(scope, storage)){
				isOK = false;
			}
		}
	}
}
