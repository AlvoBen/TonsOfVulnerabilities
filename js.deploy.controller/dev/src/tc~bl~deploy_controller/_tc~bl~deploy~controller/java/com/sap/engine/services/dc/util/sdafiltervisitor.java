package com.sap.engine.services.dc.util;

import java.util.HashSet;
import java.util.Set;

import com.sap.engine.services.dc.repo.Sca;
import com.sap.engine.services.dc.repo.Sda;
import com.sap.engine.services.dc.repo.SduVisitor;

public class SdaFilterVisitor implements SduVisitor {

	private Set<Sda> sdas = new HashSet<Sda>();

	public void visit(Sda sda) {
		this.sdas.add(sda);

	}

	public void visit(Sca arg0) {
		// filter out the SCAs

	}

	public Set<Sda> getSdas() {
		return this.sdas;
	}

}