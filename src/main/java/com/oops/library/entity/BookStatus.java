package com.oops.library.entity;

public enum BookStatus {

	AVAILABLE("This is the book available for borrowing"),
	BORROWED("This book is currently borrowed"),
	RESERVED("This book is reserved for another patron"),
	RESTORATION_NEEDED("This book needs restoration before use");
	
	private final String message;

    BookStatus(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
	
}
