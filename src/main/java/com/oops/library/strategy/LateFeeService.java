package com.oops.library.strategy;

import com.oops.library.entity.BorrowLog;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class LateFeeService {
    public double calculateLateFee(BorrowLog borrowLog) {
        LocalDate today = LocalDate.now();
        LocalDate returnDate = borrowLog.getReturnDate();

        if (today.isAfter(returnDate)) {
            long overdueDays = ChronoUnit.DAYS.between(returnDate, today);
            double lateFeeRate = borrowLog.getBook().getLateFeeRate();
            return overdueDays * lateFeeRate;
        }
        return 0.0;
    }
}