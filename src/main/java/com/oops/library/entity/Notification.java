package com.oops.library.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name="notification")
public class Notification {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;

    private String bookTitle;

    private LocalDateTime timestamp;

    private boolean isRead = false;

    // Constructors
    public Notification() {}

    public Notification(String message, String bookTitle) {
        this.message = message;
        this.bookTitle = bookTitle;
        this.timestamp = LocalDateTime.now();
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getBookTitle() {
		return bookTitle;
	}

	public void setBookTitle(String bookTitle) {
		this.bookTitle = bookTitle;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public boolean isRead() {
		return isRead;
	}

	public void setRead(boolean read) {
		this.isRead = read;
	}
    
    

}
