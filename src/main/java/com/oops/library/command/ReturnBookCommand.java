package com.oops.library.command;

import java.time.LocalDate;

import com.oops.library.design.patterns.CatalogManager;
import com.oops.library.entity.Book;
import com.oops.library.entity.BookStatus;
import com.oops.library.entity.BorrowLog;
import com.oops.library.repository.BookRepository;
import com.oops.library.repository.BorrowLogRepository;

public class ReturnBookCommand implements Command {

	private final BorrowLog borrowLog;
    private final Book book;
    private final BorrowLogRepository borrowLogRepository;
    private final BookRepository bookRepository;

    public ReturnBookCommand(BorrowLog borrowLog,
                              Book book,
                              BorrowLogRepository borrowLogRepository,
                              BookRepository bookRepository) {
        this.borrowLog = borrowLog;
        this.book = book;
        this.borrowLogRepository = borrowLogRepository;
        this.bookRepository = bookRepository;
    }

    @Override
    public void execute() {
        // Update borrow log
        borrowLog.setReturned(true);
        borrowLog.setReturnDate(LocalDate.now());
        borrowLogRepository.save(borrowLog);

        // Update book status
        book.setStatus(BookStatus.AVAILABLE);
        bookRepository.save(book);
    }

    @Override
    public void undo() {
        // Optional: reverse the return
        borrowLog.setReturned(false);
        borrowLog.setReturnDate(null);
        book.setStatus(BookStatus.BORROWED);
        borrowLogRepository.save(borrowLog);
        bookRepository.save(book);
    }

}
