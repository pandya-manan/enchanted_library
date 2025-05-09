package com.oops.library.strategy;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

@Component("academic")
public class AcademicStrategy implements LendingStrategy{

	@Override
	public LocalDate calculateReturnDate(LocalDate borrowDate) {
		return borrowDate.plusDays(7);
	}
	
	

}
