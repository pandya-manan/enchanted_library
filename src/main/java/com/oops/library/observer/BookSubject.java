package com.oops.library.observer;

public interface BookSubject {
	
	void addObserver(BookObserver observer);
	void removeObserver(BookObserver observer);
	void notifyObservers(String message);

}
