package org.myproject.botcoreapp.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

@Data
@Entity
@Table(name = "user_tel_book")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserTelephoneBook {
    @Id
    @EqualsAndHashCode.Include
    @Column(name = "chat_id")
    private long chatId;
    @Column(name = "tel_book", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, String> telBook;
}
