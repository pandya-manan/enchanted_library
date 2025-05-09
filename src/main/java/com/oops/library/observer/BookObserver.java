package com.oops.library.observer;

import com.oops.library.entity.Book;

public interface BookObserver {

	void notify(Book book,String message);

	void update(String message);
}
