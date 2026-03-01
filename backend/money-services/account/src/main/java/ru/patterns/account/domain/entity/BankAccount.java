package ru.patterns.account.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import ru.patterns.account.application.common.enums.BankAccountType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class BankAccount {
    @Id
    private UUID id = UUID.randomUUID();

    private UUID userId;

    @Column(unique = true, nullable = false)
    private String accountNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BankAccountType accountType; // мастер-счёт или счёт клиента

    @Column(nullable = false, scale = 2)
    private BigDecimal balance;

    /*
    Активность счёта, данное поле может измениться только в одну сторону на false при закрытии счёта клиентом
     */
    private boolean active = true;

    /*
    Относится ли счёт к заблокированному клиенту. Может меняться в обе стороны
     */
    private boolean banned = false;

    @Column(nullable = false, updatable = false)
    private Instant createTime = Instant.now();

    private Instant updateTime = Instant.now();
}
