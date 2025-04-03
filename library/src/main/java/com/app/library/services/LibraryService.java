package com.app.library.services;

import com.app.library.models.Book;
import com.app.library.models.Member;
import com.app.library.models.BorrowingRecord;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
public class LibraryService {

    private Map<Long, Book> books = new HashMap<Long, Book>();
    private Map<Long, Member> members = new HashMap<Long, Member>();
    private Map<Long, BorrowingRecord> borrowingRecords = new HashMap<Long, BorrowingRecord>();

    // ==================== Book Methods ====================

    // Get all books
    public Collection<Book> getAllBooks() {
        return books.values();
    }

    // Get a book by ID
    public Book getBookById(Long id) {
        return books.get(id);
    }

    // Add a new book
    public void addBook(Book book) {
        books.put(book.getId(), book);
    }

    // Update a book
    public void updateBook(Book updatedBook) {
        books.put(updatedBook.getId(), updatedBook);
    }

    // Delete a book by ID
    public void deleteBook(Long id) {
        books.remove(id);
    }

    // ==================== Member Methods ====================

    // Get all members
    public Collection<Member> getAllMembers() {
        return members.values();
    }

    // Get a member by ID
    public Member getMemberById(Long id) {
        return members.get(id);
    }

    // Add a new member
    public void addMember(Member member) {
        members.put(member.getId(), member);
    }

    // Update a member
    public void updateMember(Member updatedMember) {
        members.put(updatedMember.getId(), updatedMember);
    }

    // Delete a member by ID
    public void deleteMember(Long id) {
        members.remove(id);
    }

    // ==================== BorrowingRecord Methods ====================

    // Get all borrowing records
    public Collection<BorrowingRecord> getAllBorrowingRecords() {
        return borrowingRecords.values();
    }

    // Borrow a book (create a new borrowing record)
    public void borrowBook(BorrowingRecord record) {
        System.out.println(record);
        // Set borrow date and due date (e.g., due date = borrow date + 14 days)
        record.setBorrowDate(LocalDate.now());
        record.setDueDate(LocalDate.now().plusDays(14));
        borrowingRecords.put(record.getId(), record);
        // Decrease the available copies of the book
        System.out.println("record.getBookId() " + record.getBookId());
        System.out.println("book " + books.get(record.getBookId()));
        Book book = books.get(record.getBookId());
        book.setAvailableCopies(book.getAvailableCopies() - 1);
    }

    // Return a book (update the borrowing record with the return date)
    public void returnBook(Long recordId, LocalDate returnDate) {
        BorrowingRecord record = borrowingRecords.get(recordId);
        // Increase the available copies of the book
        Book book = books.get(record.getBookId());
        book.setAvailableCopies(book.getAvailableCopies() +1 );
    }
}
