package com.oops.library.strategy;

import java.time.LocalDate;

public class RestrictedReadingRoomStrategy implements LendingStrategy {

	@Override
	public LocalDate calculateReturnDate(LocalDate borrowDate) {
		return borrowDate.plusDays(1);
	}

}
