package com.example.eshop.sales.application.services.queryorder;

import com.example.eshop.sales.domain.order.Order;
import com.example.eshop.sales.domain.order.OrderLine;
import com.example.eshop.sales.domain.order.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QueryOrderServiceImpl implements QueryOrderService {
    private final OrderRepository orderRepository;
    private final EntityManager em;

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("#customerId == authentication.getCustomerId()")
    public Page<Order> getForCustomer(String customerId, Pageable pageable) {
        var page = orderRepository.findOrdersWithLinesByCustomerId(customerId, pageable);

        fetchOrderLinesCollections(page.getContent());

        return page;
    }

    @Override
    @Transactional(readOnly = true)
    public Order getById(UUID orderId) {
        var order = orderRepository.findOrderWithLinesById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order " + orderId + " does not exist"));

        fetchOrderLinesCollections(Collections.singletonList(order));

        return order;
    }

    private void fetchOrderLinesCollections(List<Order> orders) {
        var lines = orders.stream()
                .flatMap(order -> order.getLines().stream())
                .toList();

        fetchImages(lines);
        fetchAttributes(lines);
    }

    private void fetchImages(List<OrderLine> orderLines) {
        if (orderLines.isEmpty()) {
            return;
        }

        em.createQuery("""
                select l
                    from  OrderLine l
                    left join fetch l.images
                    where l in :lines"""
        )
                .setParameter("lines", orderLines)
                .getResultList();
    }

    private void fetchAttributes(List<OrderLine> orderLines) {
        if (orderLines.isEmpty()) {
            return;
        }

        em.createQuery("""
                select l
                    from  OrderLine l
                    left join fetch l.attributes
                    where l in :lines"""
        )
                .setParameter("lines", orderLines)
                .getResultList();
    }
}
