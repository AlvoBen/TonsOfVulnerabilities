package com.sap.engine.compatibility.frame;

import java.util.Dictionary;

import org.osgi.service.component.ComponentContext;

import com.sap.engine.frame.ApplicationServiceContext;
import com.sap.engine.frame.ApplicationServiceFrame;
import com.sap.engine.frame.ServiceException;

public class CompatibilityServiceFrame {
	ApplicationServiceContext asContext;
	ComponentContext cContext;
	Class<? extends ApplicationServiceFrame> frameClass;
	ApplicationServiceFrame frame;

	@SuppressWarnings("unchecked")
	void activate(ComponentContext context) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException, ServiceException {
		cContext = context;
		Dictionary bundleHeaders = cContext.getBundleContext().getBundle()
				.getHeaders();
		if (frameClass == null)
		{
			String frameClassName = null;
			frameClass = cContext.getBundleContext().getBundle().loadClass(
					frameClassName);
		}
		if (frame == null)
			frame = (ApplicationServiceFrame) frameClass.newInstance();
		if (frame != null)
			frame.start(asContext);
	}

	void deactivate(ComponentContext context) {
		frame.stop();
	}

	void bindApplicationServiceContext(ApplicationServiceContext asc) {
		asContext = asc;
	}

	void unbindApplicationServiceContext(ApplicationServiceContext asc) {
		if (asContext == asc)
			asContext = null;
	}
}
