package com.oops.library.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("SCHOLAR")
public class Scholar extends User{

	@Override
	public String getType() {
		return "SCHOLAR";
	}

}
