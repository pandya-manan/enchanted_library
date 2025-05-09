package com.oops.library.strategy;

import java.time.LocalDate;

public interface LendingStrategy {

	LocalDate calculateReturnDate(LocalDate borrowDate);
}
