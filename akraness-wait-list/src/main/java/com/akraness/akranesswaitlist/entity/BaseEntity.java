package com.akraness.akranesswaitlist.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Calendar;

@Getter
@Setter
@MappedSuperclass
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Timestamp createdOn;

    private Timestamp lastModifiedOn;

    @PrePersist
    public void prePersist() {
        createdOn = Timestamp.from(Calendar.getInstance().toInstant());
        lastModifiedOn = Timestamp.from(Calendar.getInstance().toInstant());
    }

    @PreUpdate
    public void preUpdate() {
        lastModifiedOn = Timestamp.from(Calendar.getInstance().toInstant());
    }
}
