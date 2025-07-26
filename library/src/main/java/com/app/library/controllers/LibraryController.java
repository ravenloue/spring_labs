package com.app.library.controllers;

import java.time.LocalDate;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.library.models.Book;
import com.app.library.models.BorrowingRecord;
import com.app.library.models.Member;
import com.app.library.services.LibraryService;

@RestController
@RequestMapping("/api")
public class LibraryController {

    // Create a logger instance
    private static final Logger logger = LoggerFactory.getLogger(LibraryController.class);

    @Autowired
    private LibraryService libraryService;

    // ==================== Book Endpoints ====================

    // Get all books, filter by author and/or genre if provided
    @GetMapping("/books")
    public ResponseEntity<Collection<Book>> getBooks(
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String genre) {
        if (author!=null &&  genre!=null) {
            Collection<Book> books = libraryService.getBooksByAuthorAndGenre(author, genre);
            logger.info("The books retrieved for the author and genre "+author+" - " + genre);
            return new ResponseEntity<>(books, HttpStatus.OK);
        } else if (author!=null) {
            Collection<Book> books = libraryService.getBooksByAuthorAndGenre(author, null);
            logger.info("The books retrieved for the author "+author);
            return new ResponseEntity<>(books, HttpStatus.OK);
        } else {
            Collection<Book> books = libraryService.getAllBooks();
            logger.info("All the books retrieved");
            return new ResponseEntity<>(books, HttpStatus.OK);
        }
    }

    // Get a book by ID
    @GetMapping("/books/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        Book book = libraryService.getBookById(id);
        logger.info("The book returned"+book);

		if(book != null) {
			return new ResponseEntity<>(book, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
    }

    // Add a new book
    @PostMapping("/books")
    public ResponseEntity<Book> addBook(@RequestBody Book book) {
        libraryService.addBook(book);
        logger.info("The book was added");
        return new ResponseEntity<>(book, HttpStatus.CREATED);
    }

    // Update a book
    @PutMapping("/books/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable Long id, @RequestBody Book updatedBook) {
        if (libraryService.getBookById(id) == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        updatedBook.setId(id);
        libraryService.updateBook(updatedBook);
        logger.info("The book has been updated "+updatedBook);
        return new ResponseEntity<>(updatedBook, HttpStatus.OK);
    }

    // Delete a book
    @DeleteMapping("/books/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        if (libraryService.getBookById(id) == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        libraryService.deleteBook(id);
        logger.info("The book has been deleted ");
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Get books due on a specific date
    @GetMapping("/books/dueondate")
    public ResponseEntity<Collection<Book>> getBooksDueOnDate(
            @RequestParam("dueDate") @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate dueDate) {
        Collection<Book> books = libraryService.getBooksDueOnDate(dueDate);
        logger.info("The books retrieved by due date "+dueDate);
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    // Check availability of a book by ID
    @GetMapping("/books/{id}/availability")
    public ResponseEntity<LocalDate> checkAvailability(
            @PathVariable Long id) {
        LocalDate avlDate = libraryService.checkAvailability(id);
        if(avlDate == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(avlDate, HttpStatus.OK);
        }
    }

    // ==================== Member Endpoints ====================

    // Get all members
    @GetMapping("/members")
    public ResponseEntity<Collection<Member>> getAllMembers() {
        Collection<Member> members = libraryService.getAllMembers();
        logger.info("The members in the system " + members);
        return new ResponseEntity<>(members, HttpStatus.OK);
    }

    // Get a member by ID
    @GetMapping("/members/{id}")
    public ResponseEntity<Member> getMemberById(@PathVariable Long id) {
        Member member = libraryService.getMemberById(id);
        logger.info("The member you retrieved "+member);
		if(member != null) {
			return new ResponseEntity<>(member, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
    }

    // Add a new member
    @PostMapping("/members")
    public ResponseEntity<Member> addMember(@RequestBody Member member) {
        libraryService.addMember(member);
        logger.info("The member has been added ");
        return new ResponseEntity<>(member, HttpStatus.CREATED);
    }

    // Update a member
    @PutMapping("/members/{id}")
    public ResponseEntity<Member> updateMember(@PathVariable Long id, @RequestBody Member updatedMember) {
        if (libraryService.getMemberById(id) != null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        updatedMember.setId(id);
        libraryService.updateMember(updatedMember);
        logger.info("The member has been updated "+updatedMember);
        return new ResponseEntity<>(updatedMember, HttpStatus.OK);
    }

    // Delete a member
    @DeleteMapping("/members/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long id) {
        if (libraryService.getMemberById(id) == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        libraryService.deleteMember(id);
        logger.info("The member has been deleted "+id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // ==================== BorrowingRecord Endpoints ====================

    // Get all borrowing records
    @GetMapping("/borrowing-records")
    public ResponseEntity<Collection<BorrowingRecord>> getAllBorrowingRecords() {
        Collection<BorrowingRecord> records = libraryService.getAllBorrowingRecords();
        logger.info("The records has been retrieved "+records);
        return new ResponseEntity<>(records, HttpStatus.OK);
    }

    // Borrow a book
    @PostMapping("/borrow")
    public ResponseEntity<BorrowingRecord> borrowBook(@RequestBody BorrowingRecord record) {
        // Set borrow date and due date (e.g., due date = borrow date + 14 days)
        record.setBorrowDate(LocalDate.now());
        record.setDueDate(LocalDate.now().plusDays(14));
        libraryService.borrowBook(record);
        logger.info("The book has been borrowed "+record);
        return new ResponseEntity<>(record, HttpStatus.CREATED);
    }

    // Return a book
    @PutMapping("/return/{recordId}")
    public ResponseEntity<Void> returnBook(@PathVariable Long recordId) {
        libraryService.returnBook(recordId, LocalDate.now());
        logger.info("The book has been retrieved "+recordId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
