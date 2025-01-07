package com.sap.engine.services.dc.cm.deploy.impl;

import com.sap.engine.services.dc.repo.Sca;
import com.sap.engine.services.dc.repo.Sda;
import com.sap.engine.services.dc.repo.SduVisitor;

public class SduGetType implements SduVisitor {

	private String description;

	public void visit(Sda sda) {

		this.description = "sda";

	}

	public void visit(Sca sca) {

		this.description = "sca";

	}

	public String getDescription() {
		return description;
	}

}
