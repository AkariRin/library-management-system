package dev.rbq.library_management_system.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 借阅记录实体类
 */
@Entity
@Table(name = "borrow_records")
public class BorrowRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "record_id", length = 36, nullable = false)
    private String recordId;

    @NotNull(message = "The user UUID cannot be empty")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_uuid", nullable = false, foreignKey = @ForeignKey(name = "fk_borrow_records_user_uuid"))
    private User user;

    @NotNull(message = "The book copy ID cannot be empty")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false, foreignKey = @ForeignKey(name = "fk_borrow_records_item_id"))
    private BookItem bookItem;

    @NotNull(message = "The loan date cannot be empty")
    @Column(name = "borrow_date", nullable = false, columnDefinition = "datetime default CURRENT_TIMESTAMP")
    private LocalDateTime borrowDate;

    @NotNull(message = "The due date cannot be empty")
    @Column(name = "due_date", nullable = false)
    private LocalDateTime dueDate;

    @Column(name = "return_date")
    private LocalDateTime returnDate;

    @NotNull(message = "The borrowing status cannot be empty")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private BorrowStatus status = BorrowStatus.Checked_Out;

    // Constructors
    public BorrowRecord() {
    }

    public BorrowRecord(String recordId, User user, BookItem bookItem, LocalDateTime borrowDate,
                        LocalDateTime dueDate, LocalDateTime returnDate, BorrowStatus status) {
        this.recordId = recordId;
        this.user = user;
        this.bookItem = bookItem;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.status = status;
    }

    // Getters and Setters
    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public BookItem getBookItem() {
        return bookItem;
    }

    public void setBookItem(BookItem bookItem) {
        this.bookItem = bookItem;
    }

    public LocalDateTime getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(LocalDateTime borrowDate) {
        this.borrowDate = borrowDate;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDateTime getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDateTime returnDate) {
        this.returnDate = returnDate;
    }

    public BorrowStatus getStatus() {
        return status;
    }

    public void setStatus(BorrowStatus status) {
        this.status = status;
    }

    /**
     * 借阅状态枚举
     */
    public enum BorrowStatus {
        Checked_Out("Checked Out", "Checked Out"),
        Returned("Returned", "Returned");

        private final String value;
        private final String description;

        BorrowStatus(String value, String description) {
            this.value = value;
            this.description = description;
        }

        public String getValue() {
            return value;
        }

        public String getDescription() {
            return description;
        }
    }
}
