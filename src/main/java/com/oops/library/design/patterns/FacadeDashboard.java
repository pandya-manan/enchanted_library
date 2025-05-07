package com.oops.library.design.patterns;

import com.oops.library.enchanted.exception.EnchantedLibraryException;
import com.oops.library.entity.Book;
import com.oops.library.entity.User;
import com.oops.library.service.BookService;
import java.util.*;

import org.springframework.stereotype.Component;

import com.oops.library.service.UserInformationService;

@Component
public class FacadeDashboard {
	
	private BookService bookService;
	
	private UserInformationService userInfoService;
	
	public FacadeDashboard(BookService bookService, UserInformationService userInfoService)
	{
		this.bookService=bookService;
		this.userInfoService=userInfoService;
	}
	
	public Map<String,List<?>> getBooksAndUsers() throws EnchantedLibraryException
	{
		Map<String, List<?>> collections = new HashMap<>();
		List<Book> getBooks=bookService.getAllBooks();
		List<User> getUsers=userInfoService.getAllRegisteredUsers();
		collections.put("books", getBooks);
		collections.put("users", getUsers);
		return collections;
	}

}
