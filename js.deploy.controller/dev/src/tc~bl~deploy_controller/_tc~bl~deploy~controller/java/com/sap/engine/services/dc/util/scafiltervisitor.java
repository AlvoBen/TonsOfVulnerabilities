package com.sap.engine.services.dc.util;

import java.util.HashSet;
import java.util.Set;

import com.sap.engine.services.dc.repo.Sca;
import com.sap.engine.services.dc.repo.Sda;
import com.sap.engine.services.dc.repo.SduVisitor;

public class ScaFilterVisitor implements SduVisitor {

	private Set<Sca> scas = new HashSet<Sca>();

	public void visit(Sda sda) {

		// filter out the SDAs
	}

	public void visit(Sca sca) {

		this.scas.add(sca);
	}

	public Set<Sca> getScas() {
		return this.scas;
	}

}