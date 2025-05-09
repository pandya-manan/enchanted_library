package com.oops.library.observer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.oops.library.entity.Book;
import com.oops.library.entity.BookStatus;
import com.oops.library.entity.BorrowLog;
import com.oops.library.repository.BorrowLogRepository;

@Service
public class BookNotifierService {

    private final List<BookObserver> observers = new ArrayList<>();

    @Autowired
    private BorrowLogRepository borrowLogRepository;

    public void addObserver(BookObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(BookObserver observer) {
        observers.remove(observer);
    }

    public void notifyRestoration(Book book) {
        if (book.getStatus() == BookStatus.RESTORATION_NEEDED) {
            notifyAllObservers(book, "Book needs restoration");
        }
    }

    public void notifyOverdue(Book book, LocalDate returnDate) {
        if (returnDate != null && returnDate.isBefore(LocalDate.now())) {
            notifyAllObservers(book, "Book is overdue!");
        }
    }

    public void notifyAllOverdueBooks() {
        List<BorrowLog> overdueLogs = borrowLogRepository
                .findByReturnedFalseAndReturnDateBefore(LocalDate.now());

        for (BorrowLog log : overdueLogs) {
            Book book = log.getBook();
            String message = "Book '" + book.getTitle() + "' is overdue (due " + log.getReturnDate() + ") for user " + log.getBorrower().getName();
            notifyAllObservers(book, message);
        }
    }

    private void notifyAllObservers(Book book, String message) {
        for (BookObserver observer : observers) {
            observer.notify(book, message);
        }
    }
}
