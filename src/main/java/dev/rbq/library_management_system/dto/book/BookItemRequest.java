package dev.rbq.library_management_system.dto.book;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 创建/更新图书副本请求 DTO
 */
public class BookItemRequest {

    @NotNull(message = "图书ID不能为空")
    private Integer bookId;

    @NotBlank(message = "条码号不能为空")
    @Size(max = 50, message = "条码号长度不能超过50个字符")
    private String barcode;

    @Size(max = 50, message = "馆藏位置长度不能超过50个字符")
    private String location;

    private String status; // Available, Checked Out, Lost, Damaged, Withdrawn

    private LocalDate acquisitionDate;

    private BigDecimal acquisitionPrice;

    @Size(max = 500, message = "备注信息长度不能超过500个字符")
    private String notes;

    // Constructors
    public BookItemRequest() {
    }

    public BookItemRequest(Integer bookId, String barcode, String location, String status,
                           LocalDate acquisitionDate, BigDecimal acquisitionPrice, String notes) {
        this.bookId = bookId;
        this.barcode = barcode;
        this.location = location;
        this.status = status;
        this.acquisitionDate = acquisitionDate;
        this.acquisitionPrice = acquisitionPrice;
        this.notes = notes;
    }

    // Getters and Setters
    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
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
        return "BookItemRequest{" +
                "bookId=" + bookId +
                ", barcode='" + barcode + '\'' +
                ", location='" + location + '\'' +
                ", status='" + status + '\'' +
                ", acquisitionDate=" + acquisitionDate +
                ", acquisitionPrice=" + acquisitionPrice +
                '}';
    }
}

