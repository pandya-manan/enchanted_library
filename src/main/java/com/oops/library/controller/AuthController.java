package com.oops.library.controller;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.oops.library.command.ReturnBookCommand;
import com.oops.library.design.patterns.BookFactory;
import com.oops.library.design.patterns.CatalogManager;
import com.oops.library.design.patterns.FacadeDashboard;
import com.oops.library.dto.BookFormDto;
import com.oops.library.dto.RegistrationDto;
import com.oops.library.enchanted.exception.EnchantedLibraryException;
import com.oops.library.entity.AncientScript;
import com.oops.library.entity.Book;
import com.oops.library.entity.BookStatus;
import com.oops.library.entity.BorrowLog;
import com.oops.library.entity.GeneralBook;
import com.oops.library.entity.Notification;
import com.oops.library.entity.RareBook;
import com.oops.library.entity.User;
import com.oops.library.observer.BookNotifierService;
import com.oops.library.observer.LibrarianNotifier;
import com.oops.library.repository.BookRepository;
import com.oops.library.repository.BorrowLogRepository;
import com.oops.library.repository.NotificationRepository;
import com.oops.library.repository.UserRepository;
import com.oops.library.service.BookService;
import com.oops.library.service.BorrowLogService;
import com.oops.library.service.RegistrationService;
import com.oops.library.strategy.LendingStrategy;
import com.oops.library.command.*;

@Controller
public class AuthController {

    private final RegistrationService registrationService;
    private final CatalogManager catalog;  // our singleton book manager
    private final FacadeDashboard facadeDashboard;
    private final NotificationRepository notificationRepository;
    private final LibrarianNotifier librarianNotifier;
    
    @Autowired
    private BookService bookService;
    
    @Autowired
    private BorrowLogService borrowLogService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private Map<String,LendingStrategy> strategyMap;
    
   @Autowired
   private BorrowLogRepository borrowLogRepository;
   
   @Autowired
   private BookRepository bookRepository;

    public AuthController(RegistrationService registrationService,
                          BookRepository bookRepo,FacadeDashboard facadeDashboard,NotificationRepository notificationRepository,LibrarianNotifier librarianNotifier) {
        this.registrationService = registrationService;
        // bootstrap singleton with your JPA repository
        this.catalog = CatalogManager.getInstance(bookRepo);
        this.facadeDashboard=facadeDashboard;
        this.notificationRepository = notificationRepository;
        this.librarianNotifier=librarianNotifier;
    }

    //SIGN UP - show SIGN UP FORM//
    @GetMapping("/signup")
    public String showSignUpForm(Model model) {
        model.addAttribute("registrationDto", new RegistrationDto());
        return "signup";
    }

    //AFTER FILLING THE SIGN UP FORM, POST IT INTO THE DATABASE//
    @PostMapping("/signup")
    public String registerUser(@ModelAttribute RegistrationDto dto,
                               Model model,
                               RedirectAttributes flash) {
        try {
            registrationService.registerUser(dto);
            flash.addFlashAttribute("success", "Sign up completed");
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("error", "Registration failed: " + e.getMessage());
            return "signup";
        }
    }

    //SHOW LOGIN PAGE
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    // --- DASHBOARD & BOOK CRUD ---

    /**
     * Dashboard shows all books.
     * Librarians get Add/Edit/Delete buttons;
     * Scholars/Guests only see the list.
     */
    @GetMapping({"/home", "/dashboard"})
    public String dashboard(Model model, Authentication auth) {
        List<Book> books = catalog.getAllBooks();
        boolean isLibrarian = auth.getAuthorities()
                .contains(new SimpleGrantedAuthority("ROLE_LIBRARIAN"));
        boolean isScholar=auth.getAuthorities()
                .contains(new SimpleGrantedAuthority("ROLE_SCHOLAR"));
        boolean isGuest=auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_GUEST"));

        model.addAttribute("books", books);
        model.addAttribute("isLibrarian", isLibrarian);
        model.addAttribute("isScholar", isScholar);
        model.addAttribute("isGuest", isGuest);
        return "dashboard";
    }

    /**
     * Show "Add Book" form (only for librarians)
     */
    @GetMapping("/books/add")
    public String showAddBookForm(Model model) {
        model.addAttribute("bookForm", new BookFormDto());
        return "book-form";
    }

    /**
     * Handle submission of "Add Book"
     * Uses Factory to create the right subtype,
     * and Singleton catalog to save it.
     */
    @PostMapping("/books/add")
    public String addBook(@ModelAttribute BookFormDto form,
                          @RequestParam("manuscriptFile") MultipartFile file) throws IOException {

        // Only if type is ANCIENT and file is not empty
        String manuscriptPath = null;
        if ("ANCIENT".equalsIgnoreCase(form.getType()) && !file.isEmpty()) {
            String uploadDir = System.getProperty("user.dir") + File.separator + "uploads";

            File uploadFolder = new File(uploadDir);
            if (!uploadFolder.exists()) {
                uploadFolder.mkdirs();  // create if not exists
            }

            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            File dest = new File(uploadFolder, filename);
            file.transferTo(dest);

            manuscriptPath = "/uploads/" + filename;
        }

        // Create book using factory
        Book book = BookFactory.createBook(
            form.getType(),
            form.getTitle(),
            form.getAuthor(),
            form.isDigitalAccess(),
            form.getPreservationMethod(),
            form.getOriginalLanguage(),
            manuscriptPath,
            form.getStatus(),
            form.getSection(),
            form.getIsbn()
        );

        // Additional defaults for AncientScript
        if (book instanceof AncientScript ancientScript) {
            if (ancientScript.getTranslationNotes() == null || ancientScript.getTranslationNotes().isBlank()) {
                ancientScript.setTranslationNotes("Translation Pending");
            }
            ancientScript.setArchived(true);
        }

        // Add the book to the catalog
        catalog.addBook(book);

        // === Observer Pattern Integration ===
        BookNotifierService notifierService = new BookNotifierService();
        notifierService.addObserver(librarianNotifier);

        if (book.getStatus() == BookStatus.RESTORATION_NEEDED) {
            notifierService.notifyRestoration(book);
        }

        return "redirect:/dashboard";
    }



    // TODO: add edit/delete mappings here, following same pattern—use catalog.updateBook(…) and catalog.removeBook(…)
    @GetMapping("/books/view/{id}")
    public String viewBook(@PathVariable Long id, Model model) {
        Book book = catalog.getAllBooks().stream()
            .filter(b -> b.getId().equals(id))
            .findFirst()
            .orElse(null);

        model.addAttribute("book", book);

        // Add bookType to avoid using .class.simpleName in Thymeleaf
        String bookType = (book instanceof GeneralBook) ? "GeneralBook"
                        : (book instanceof RareBook) ? "RareBook"
                        : (book instanceof AncientScript) ? "AncientScript"
                        : "Unknown";

        model.addAttribute("bookType", bookType);

        return "book-view";
    }

    
    @GetMapping("/books/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Book book = catalog.getBookById(id);
        if (book == null) {
            return "redirect:/dashboard";
        }

        model.addAttribute("book", book);

        model.addAttribute("isGeneral", book instanceof GeneralBook);
        model.addAttribute("isRare", book instanceof RareBook);
        model.addAttribute("isAncient", book instanceof AncientScript);

        return "book-edit";
    }



    @PostMapping("/books/edit")
    public String editBook(@RequestParam("id") Long id,
                           @RequestParam("title") String title,
                           @RequestParam("author") String author,
                           @RequestParam("isbn") String isbn,
                           @RequestParam("section") String section,
                           @RequestParam("status") String status,
                           @RequestParam(value = "digitalAccess", required = false) Boolean digitalAccess,
                           @RequestParam(value = "preservationMethod", required = false) String preservationMethod,
                           @RequestParam(value = "inLibraryUseOnly", required = false) Boolean inLibraryUseOnly,
                           @RequestParam(value = "originalLanguage", required = false) String originalLanguage,
                           @RequestParam(value = "manuscriptFile", required = false) MultipartFile file
    ) throws IOException {

        Book existing = catalog.getBookById(id);
        if (existing == null) {
            return "redirect:/dashboard";
        }

        // Common fields
        existing.setTitle(title);
        existing.setAuthor(author);
        existing.setIsbn(isbn);
        existing.setSection(section);
        existing.setStatus(BookStatus.valueOf(status));

        // Specific subclass fields
        if (existing instanceof GeneralBook general) {
            general.setDigitalAccess(Boolean.TRUE.equals(digitalAccess));
        } else if (existing instanceof RareBook rare) {
            rare.setPreservationMethod(preservationMethod);
            rare.setInLibraryUseOnly(Boolean.TRUE.equals(inLibraryUseOnly));
        } else if (existing instanceof AncientScript ancient) {
            ancient.setOriginalLanguage(originalLanguage);

            if (file != null && !file.isEmpty()) {
                String uploadDir = "uploads/";
                String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
                File dest = new File(uploadDir + filename);
                dest.getParentFile().mkdirs();
                file.transferTo(dest);
                ancient.setManuscriptPath("/" + uploadDir + filename);
            }
        }

        catalog.updateBook(existing);
        return "redirect:/dashboard";
    }

    
    @PostMapping("/books/delete/{id}")
    public String deleteBook(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        catalog.removeBook(id); // implement this in your Catalog class
        redirectAttrs.addFlashAttribute("message", "Book deleted successfully.");
        return "redirect:/dashboard";
    }

    @GetMapping("/facade")
    public String showDashboard(Model model)
    {
    	try
    	{
    		model.addAttribute("books", facadeDashboard.getBooksAndUsers().get("books"));
    		model.addAttribute("users",facadeDashboard.getBooksAndUsers().get("users"));
    	}
    	catch(EnchantedLibraryException e)
    	{
    		model.addAttribute("errorMessage", "Error fetching either books or users");
    	}
    	return "facade-dashboard";
    }
    
    @PostMapping("/books/borrow/{id}")
    public String borrowBook(@PathVariable("id") Long bookId,@RequestParam("borrowType") String borrowType, Authentication authentication, Model model,RedirectAttributes redirectAttrs) {
        // Extract email from Spring Security's authentication object
        String email = authentication.getName();

        // Fetch the User entity using the email
        User loggedInUser = userRepository.findByEmail(email)
                .orElse(null);

        if (loggedInUser == null) {
            model.addAttribute("error", "You must be logged in to borrow a book.");
            return "login";
        }

        // Fetch the book using the provided id
        Book book = bookService.getBookById(bookId);
        if (book == null || book.getStatus() == BookStatus.BORROWED) {
            redirectAttrs.addFlashAttribute("error", "Book unavailable.");
            return "redirect:/dashboard";
        }
        
        LendingStrategy strategy = strategyMap.get(borrowType);
        if (strategy == null) {
            redirectAttrs.addFlashAttribute("error", "Invalid borrow type.");
            return "redirect:/dashboard";
        }

        // Change book status to BORROWED
        book.setStatus(BookStatus.BORROWED);
        bookService.saveBook(book);

        // Create a new BorrowLog entry
        BorrowLog borrowLog = new BorrowLog();
        borrowLog.setBorrower(loggedInUser);
        borrowLog.setBook(book);
        borrowLog.setBorrowDate(LocalDate.now());
        borrowLog.setReturnDate(strategy.calculateReturnDate(LocalDate.now()));
        borrowLog.setReturned(false);
        borrowLogService.saveBorrowLog(borrowLog);

        redirectAttrs.addFlashAttribute("message", "You have successfully borrowed the book: " + book.getTitle());
        redirectAttrs.addFlashAttribute("borrowMessage", 
                "Borrowed on: " + LocalDate.now() + " | Return by: " + strategy.calculateReturnDate(LocalDate.now()));
        return "redirect:/dashboard";

    }

    @GetMapping("/books/status")
    public String showBookStatuses(Model model, Authentication auth) throws EnchantedLibraryException {
        boolean isLibrarian = auth.getAuthorities()
            .contains(new SimpleGrantedAuthority("ROLE_LIBRARIAN"));
        if (!isLibrarian) {
            return "redirect:/dashboard";  // or an access-denied page
        }
        try
        {
        	List<Book> books = bookService.getAllBooks(); // throws if empty
            model.addAttribute("books", books);
            return "book-status";  // new Thymeleaf template
        }
        catch(EnchantedLibraryException e)
        {
        	model.addAttribute("error", e.getMessage());
        	return "book-status";

        }
        
    }
    
    @GetMapping("/notifications")
    public String viewNotifications(Model model) {
        List<Notification> notifications = notificationRepository.findAllByOrderByTimestampDesc();
        model.addAttribute("notifications", notifications);
        return "notifications";
    }
    
    @GetMapping("/user/borrowed-books")
    public String getBorrowedBooks(Model model, Principal principal) {
        List<BorrowLog> logs = borrowLogRepository.findByBorrowerEmailAndReturnedFalse(principal.getName());
        model.addAttribute("borrowedLogs", logs);
        return "user-borrowed-books";
    }

    
    @PostMapping("/books/return/{borrowLogId}")
    public String returnBook(@PathVariable Long borrowLogId, Principal principal) {
        Optional<BorrowLog> optionalLog = borrowLogRepository.findById(borrowLogId);
        if (optionalLog.isPresent()) {
            BorrowLog log = optionalLog.get();

            if (!log.getBorrower().getEmail().equals(principal.getName())) {
                return "redirect:/unauthorized"; // or return 403
            }

            Command command = new ReturnBookCommand(log, log.getBook(), borrowLogRepository, bookRepository);
            command.execute();

            return "redirect:/user/borrowed-books?success";
        }

        return "redirect:/user/borrowed-books?error";
    }

    

}
