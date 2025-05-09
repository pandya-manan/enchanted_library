package com.oops.library.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.oops.library.repository.BookRepository;
import com.oops.library.enchanted.exception.EnchantedLibraryException;
import com.oops.library.entity.*;
import java.util.*;
@Service
public class BookService {
	
	@Autowired
	private BookRepository bookRepository;
	
	public List<Book> getAllBooks() throws EnchantedLibraryException
	{
		List<Book> booksAvailableCurrently=new ArrayList<>();
		booksAvailableCurrently=bookRepository.findAllBooks();
		if(booksAvailableCurrently.isEmpty() || booksAvailableCurrently==null)
		{
			throw new EnchantedLibraryException("There are no books at this point of time");
		}
		return booksAvailableCurrently;
	}

	public Book getBookById(Long bookId) {
		Book book=bookRepository.findBookById(bookId);
		return book;
	}

	public void saveBook(Book book) {
		bookRepository.save(book);
		
	}

}
