package com.example.eshop.warehouse.domain;

import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Collection;
import java.util.List;

public interface StockItemRepository extends JpaRepository<StockItem, Long> {
    List<StockItem> findByEanIn(Collection<Ean> eanList);
}
