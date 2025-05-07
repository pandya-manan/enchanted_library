package com.oops.library.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("LIBRARIAN")
public class Librarian extends User{

	@Override
	public String getType() {
		return "LIBRARIAN";
	}

}
