package com.oops.library.entity;

import java.time.LocalDate;

import jakarta.persistence.*;

@Entity
public class RestorationLog {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	private Book book;
	
	private LocalDate restorationDate;
	
	private String performedBy;
	
	private String notes;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Book getBook() {
		return book;
	}

	public void setBook(Book book) {
		this.book = book;
	}

	public LocalDate getRestorationDate() {
		return restorationDate;
	}

	public void setRestorationDate(LocalDate restorationDate) {
		this.restorationDate = restorationDate;
	}

	public String getPerformedBy() {
		return performedBy;
	}

	public void setPerformedBy(String performedBy) {
		this.performedBy = performedBy;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
	
	

}
