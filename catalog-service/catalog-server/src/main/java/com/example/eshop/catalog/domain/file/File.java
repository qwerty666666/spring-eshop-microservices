package com.example.eshop.catalog.domain.file;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import java.util.Objects;

@Entity
@Table(name = "files")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class File {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "location", nullable = false, unique = true)
    @NotEmpty
    private String location;

    public File(String location) {
        this.location = location;
    }

    /**
     * @return if file is on external storage
     */
    public boolean isExternal() {
        return location.matches("^http[s]?://.*");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        File file = (File) o;
        return id != null && Objects.equals(id, file.id);
    }

    @Override
    public int hashCode() {
        return location.hashCode();
    }
}
