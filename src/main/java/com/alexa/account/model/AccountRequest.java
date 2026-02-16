package com.alexa.account.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "account_requests")
@Getter
@Setter
@NoArgsConstructor
public class AccountRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String requestId;

    private String name;

    private LocalDate dateOfBirth;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "fileName", column = @Column(name = "id_document_name")),
        @AttributeOverride(name = "fileType", column = @Column(name = "id_document_type"))
    })
    private IdDocument idDocument;

    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status;

    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    @Column(precision = 19, scale = 2)
    private BigDecimal startingBalance;

    @Column(precision = 19, scale = 2)
    private BigDecimal monthlySalary;

    private String email;

    @Convert(converter = YesNoConverter.class)
    @Column(length = 1)
    private Boolean interestedInOtherProducts;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
