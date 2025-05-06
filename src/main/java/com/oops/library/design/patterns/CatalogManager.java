package com.oops.library.design.patterns;

import java.util.List;

import com.oops.library.entity.Book;
import com.oops.library.entity.BorrowLog;
import com.oops.library.entity.RestorationLog;
import com.oops.library.repository.BookRepository;

public class CatalogManager {
	
	private static CatalogManager instance;
    private final BookRepository bookRepo;
    private CatalogManager(BookRepository bookRepo) {
        this.bookRepo = bookRepo;
    }
    public static synchronized CatalogManager getInstance(BookRepository bookRepo) {
        if (instance == null) {
            instance = new CatalogManager(bookRepo);
        }
        return instance;
    }

    public Book addBook(Book book) {
        return bookRepo.save(book);
    }
    public Book updateBook(Book book) {
        return bookRepo.save(book);
    }
    public void removeBook(Long id) {
        bookRepo.deleteById(id);
    }
    public List<Book> getAllBooks() {
        return bookRepo.findAll();
    }
	public Book getBookById(Long id) {
		return bookRepo.findBookById(id);
	}
	

//    // hooks for logs:
//    public List<BorrowLog> getBorrowLogs(Book b) { … }
//    public List<RestorationLog> getRestorationLogs(Book b) { … }

}
