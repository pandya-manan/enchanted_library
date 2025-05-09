package com.oops.library.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("GENERAL")
public class GeneralBook extends Book{

	private boolean digitalAccess;

	public boolean isDigitalAccess() {
		return digitalAccess;
	}

	public void setDigitalAccess(boolean digitalAccess) {
		this.digitalAccess = digitalAccess;
	}

	@Override
	public String getType() {
		return "GENERAL";
	}
	
	@Override
	public double getLateFeeRate() {
		return 1.0;
	}
}
