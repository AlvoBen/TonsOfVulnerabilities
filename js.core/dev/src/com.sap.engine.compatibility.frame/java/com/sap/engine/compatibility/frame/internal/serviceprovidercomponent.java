package com.sap.engine.compatibility.frame.internal;

import java.util.List;
import java.util.Map;

import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentContext;

public class ServiceProviderComponent {
	ComponentContext cContext;
	Map<?, List<ServiceRegistration>> registrations;
	void activate(ComponentContext context) {
		cContext = context;
	}
	void deactivate(ComponentContext context) {
		cContext = null;
		for(List<ServiceRegistration> regList : registrations.values())
			for(ServiceRegistration sr : regList)
				try {
					sr.unregister();
				} catch (IllegalStateException ise) {
					// ok. already unregistered, ignore.
				}
	}
	
	void bindServiceFrame(ServiceReference csf) {
		
	}
	void unbindServiceFrame(ServiceReference csf) {
		String serviceId = (String) csf.getProperty("service.id");
		if (serviceId == null)
			return;
		List<ServiceRegistration> regList = registrations.remove(serviceId);
		if (regList == null)
			return;
		
		for (ServiceRegistration sr : regList)
			try {
				sr.unregister();
			} catch (IllegalStateException ise) {
				// ok. already unregistered, ignore.
			}
	}
}
