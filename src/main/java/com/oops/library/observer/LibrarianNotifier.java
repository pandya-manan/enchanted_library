package com.oops.library.observer;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.oops.library.entity.Book;
import com.oops.library.entity.Notification;
import com.oops.library.repository.NotificationRepository;

@Component
public class LibrarianNotifier implements BookObserver{

	private final NotificationRepository notificationRepository;
	
	 public LibrarianNotifier(NotificationRepository notificationRepository) {
	        this.notificationRepository = notificationRepository;
	    }


	@Override
	public void notify(Book book, String message) {
		System.out.println("🔔 Librarian Alert: " + message + " [Book: " + book.getTitle() + "]");
		Notification notification = new Notification(message, book.getTitle());
        notificationRepository.save(notification);
		
	}
	
	@Override
    public void update(String message) {
        Notification n = new Notification();
        n.setMessage(message);
        n.setTimestamp(LocalDateTime.now());
        notificationRepository.save(n);
    }
	

}
