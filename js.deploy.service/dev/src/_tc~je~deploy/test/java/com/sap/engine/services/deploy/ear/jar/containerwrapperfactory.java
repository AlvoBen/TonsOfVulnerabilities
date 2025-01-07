package com.sap.engine.services.deploy.ear.jar;

import com.sap.engine.services.deploy.container.ContainerInfo;
import com.sap.engine.services.deploy.ear.jar.moduledetect.ModuleDetectorWrapper;
import com.sap.engine.services.deploy.server.utils.container.ContainerWrapper;



public class ContainerWrapperFactory {
	
	public static ContainerWrapper buildContainerWrapper(ContainerInfo info, String name, int prio) {
		ContainerWrapper wrapper;
		ContainerInterfaceAdaptor cia = new ContainerInterfaceAdaptor();
		cia.setContaienrInfo(info);
		info.setName(name);
		info.setPriority(prio);
		wrapper = new ContainerWrapper(cia);
		if (info.getModuleDetector() != null){
			wrapper.setDetectorWrapper(new ModuleDetectorWrapper(info.getModuleDetector(), false));			
		}
		return wrapper;
	}
	
}
