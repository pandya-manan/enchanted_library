package com.oops.library.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.oops.library.entity.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
	
	public Book findBookById(Long id);

}
