Objects

This section will list down all the objects which are being used in the project

1. Book 
2. BorrowLog
3. User 
4. Notification
5. RestorationLog

Context

This section will list down the context of all the objects used in this project

1. Book is used in the context of showing available books in dashboard which supports crud operations, borrow book functionality and return book functionality.
2. BorrowLog is used in the context of the showing the log for the borrowed books
3. User is used in the context of sign up, login , register and to browse through available books and do the return and borrow. Special kind of user like Librarian will be managing the books in the library.
4. Notification is used to notify the user of the type librarian for the borrowing status.
5. RestorationLog will be used to track which books need restoration.

Information
1. Book will have vital details such as title, author, isbn ,section, status(borrowed or available or reserved or restoration_needed) .
2. BorrowLog will have vital details such as the user which has borrowed the particular book, borrowed date, returned date, and if it is returned or still available.
3. User will have information such as name, email, password, and the role which will specify if the user is librarian or guest.
4. Notification will have information such as message, bookTitle, timestamp.
5. RestorationLog will have information such as which book needs to be restored, restorationDate, performedBy, and the notes regarding restoration. 