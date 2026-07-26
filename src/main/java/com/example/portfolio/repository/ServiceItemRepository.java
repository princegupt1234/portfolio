package com.example.portfolio.repository;

import com.example.portfolio.entity.ServiceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ServiceItemRepository extends JpaRepository<ServiceItem, Long> {
    List<ServiceItem> findAllByOrderBySortOrderAsc();
}
