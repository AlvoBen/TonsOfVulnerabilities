package com.sap.engine.services.dc.cm.undeploy;

import com.sap.engine.services.dc.repo.Sca;

public interface ScaUndeployItem extends GenericUndeployItem{

	public Sca getSca();

	public void setSca(Sca sca);

}
