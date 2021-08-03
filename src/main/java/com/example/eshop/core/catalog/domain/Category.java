package com.example.eshop.core.catalog.domain;

import com.example.eshop.core.shared.AggregateRoot;
import lombok.*;
import org.springframework.lang.Nullable;
import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "categories")
@NamedEntityGraphs({
        @NamedEntityGraph(
                name = "Category.parent",
                attributeNodes = { @NamedAttributeNode("parent") }
        )
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Builder
public class Category implements AggregateRoot<UUID> {
    @Id
    @Column(name = "id", nullable = false)
    @Getter(AccessLevel.NONE)
    private UUID id;

    @Column(name = "name", nullable = false)
    @lombok.NonNull
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @Nullable
    private Category parent;

    @OneToMany(mappedBy = "parent")
    @Builder.Default
    private Set<Category> children = new HashSet<>();

    @Override
    @Nullable
    public UUID id() {
        return id;
    }
}
