package com.ram.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import com.ram.enums.AccountStatus;

import java.time.LocalDateTime;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "CUSTOMER_ACCOUNT")
@Audited(withModifiedFlag = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class CustomerAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ACCOUNT_ID")
    private Long id;

    @Column(name = "CUSTOMER_NAME", nullable = false, length = 100)
    private String customerName;

    @Column(name = "ACCOUNT_NUMBER", nullable = false,
            unique = true, length = 30)
    @Audited(withModifiedFlag = false)
    private String accountNumber;

    @Column(name = "ACCOUNT_TYPE", nullable = false, length = 20)
    private String accountType;

    @Column(name = "BALANCE", nullable = false)
    private Double balance;

    @Column(name = "BRANCH_CODE", nullable = false, length = 20)
    private String branchCode;

    @Column(name = "BRANCH_NAME", nullable = false, length = 100)
    private String branchName;

    @Column(name = "CITY", nullable = false, length = 60)
    private String city;

    @Column(name = "PHONE_NUMBER", nullable = false, length = 20)
    private String phoneNumber;

    @Column(name = "EMAIL", nullable = false, length = 100)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    private AccountStatus status;

    @CreatedDate
    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    @Audited(withModifiedFlag = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "LAST_MODIFIED_AT")
    private LocalDateTime lastModifiedAt;
}