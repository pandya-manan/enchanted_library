package com.oops.library.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;
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

import com.oops.library.design.patterns.BookFactory;
import com.oops.library.design.patterns.CatalogManager;
import com.oops.library.dto.BookFormDto;
import com.oops.library.dto.RegistrationDto;
import com.oops.library.entity.AncientScript;
import com.oops.library.entity.Book;
import com.oops.library.entity.BookStatus;
import com.oops.library.entity.GeneralBook;
import com.oops.library.entity.RareBook;
import com.oops.library.repository.BookRepository;
import com.oops.library.service.RegistrationService;

@Controller
public class AuthController {

    private final RegistrationService registrationService;
    private final CatalogManager catalog;  // our singleton book manager

    @Autowired
    public AuthController(RegistrationService registrationService,
                          BookRepository bookRepo) {
        this.registrationService = registrationService;
        // bootstrap singleton with your JPA repository
        this.catalog = CatalogManager.getInstance(bookRepo);
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

        model.addAttribute("books", books);
        model.addAttribute("isLibrarian", isLibrarian);
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
    	    // Use absolute path relative to user.dir (the working directory)
    	    String uploadDir = System.getProperty("user.dir") + File.separator + "uploads";

    	    // Ensure directory exists
    	    File uploadFolder = new File(uploadDir);
    	    if (!uploadFolder.exists()) {
    	        uploadFolder.mkdirs();  // create if not exists
    	    }

    	    // Unique filename
    	    String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
    	    File dest = new File(uploadFolder, filename);
    	    
    	    // Save file
    	    file.transferTo(dest);

    	    // Save relative path for URL access
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

        // Set additional defaults for AncientScript
        if (book instanceof AncientScript ancientScript) {
            if (ancientScript.getTranslationNotes() == null || ancientScript.getTranslationNotes().isBlank()) {
                ancientScript.setTranslationNotes("Translation Pending");
            }
            ancientScript.setArchived(true); // Set explicitly, even if default
        }

        catalog.addBook(book);
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


    

}
