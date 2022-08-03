package com.example.eshop.sharedkernel.domain.base;

import org.springframework.lang.Nullable;
import java.io.Serializable;

/**
 * Entity in terms of DDD
 */
public interface Entity<ID extends Serializable> {
    @Nullable
    ID getId();
}
