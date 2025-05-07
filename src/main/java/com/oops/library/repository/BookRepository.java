package com.oops.library.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.oops.library.entity.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
	
	public Book findBookById(Long id);

	@Query(value="SELECT * FROM enchanted_library.book",nativeQuery=true)
	public List<Book> findAllBooks();
}
