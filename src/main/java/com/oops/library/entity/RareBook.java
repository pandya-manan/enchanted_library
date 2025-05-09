package com.oops.library.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("RARE")
public class RareBook extends Book{

	private String preservationMethod;
	private boolean inLibraryUseOnly=true;
	public String getPreservationMethod() {
		return preservationMethod;
	}
	public void setPreservationMethod(String preservationMethod) {
		this.preservationMethod = preservationMethod;
	}
	public boolean isInLibraryUseOnly() {
		return inLibraryUseOnly;
	}
	public void setInLibraryUseOnly(boolean inLibraryUseOnly) {
		this.inLibraryUseOnly = inLibraryUseOnly;
	}
	@Override
	public String getType() {
		return "RARE";
	}
	
	@Override
	public double getLateFeeRate() {
		return 5.0;
	}
}
