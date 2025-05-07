package com.oops.library.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("GUEST")
public class Guest extends User{

	@Override
	public String getType() {
		return "GUEST";
	}

}
