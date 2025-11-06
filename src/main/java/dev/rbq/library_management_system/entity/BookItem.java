package dev.rbq.library_management_system.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 图书副本实体类（存储具体物理书籍信息）
 */
@Entity
@Table(name = "book_items")
public class BookItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id", nullable = false)
    private Integer itemId;

    @NotNull(message = "图书ID不能为空")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false, foreignKey = @ForeignKey(name = "fk_book_items_book_id"))
    private Book book;

    @NotBlank(message = "条码号不能为空")
    @Size(max = 50, message = "条码号长度不能超过50个字符")
    @Column(name = "barcode", length = 50, nullable = false, unique = true)
    private String barcode;

    @Size(max = 50, message = "馆藏位置长度不能超过50个字符")
    @Column(name = "location", length = 50)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private BookItemStatus status = BookItemStatus.Available;

    @Column(name = "acquisition_date")
    private LocalDate acquisitionDate;

    @Column(name = "acquisition_price", precision = 10, scale = 2)
    private BigDecimal acquisitionPrice;

    @Size(max = 500, message = "备注信息长度不能超过500个字符")
    @Column(name = "notes", length = 500)
    private String notes;

    // Constructors
    public BookItem() {
    }

    public BookItem(Book book, String barcode, String location, BookItemStatus status,
                    LocalDate acquisitionDate, BigDecimal acquisitionPrice, String notes) {
        this.book = book;
        this.barcode = barcode;
        this.location = location;
        this.status = status;
        this.acquisitionDate = acquisitionDate;
        this.acquisitionPrice = acquisitionPrice;
        this.notes = notes;
    }

    // Getters and Setters
    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public BookItemStatus getStatus() {
        return status;
    }

    public void setStatus(BookItemStatus status) {
        this.status = status;
    }

    public LocalDate getAcquisitionDate() {
        return acquisitionDate;
    }

    public void setAcquisitionDate(LocalDate acquisitionDate) {
        this.acquisitionDate = acquisitionDate;
    }

    public BigDecimal getAcquisitionPrice() {
        return acquisitionPrice;
    }

    public void setAcquisitionPrice(BigDecimal acquisitionPrice) {
        this.acquisitionPrice = acquisitionPrice;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "BookItem{" +
                "itemId=" + itemId +
                ", barcode='" + barcode + '\'' +
                ", location='" + location + '\'' +
                ", status=" + status +
                ", acquisitionDate=" + acquisitionDate +
                ", acquisitionPrice=" + acquisitionPrice +
                '}';
    }

    /**
     * 图书副本状态枚举
     */
    public enum BookItemStatus {
        Available("Available", "可借"),
        Checked_Out("Checked Out", "已借出"),
        Lost("Lost", "丢失"),
        Damaged("Damaged", "损坏"),
        Withdrawn("Withdrawn", "已下架");

        private final String value;
        private final String description;

        BookItemStatus(String value, String description) {
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

