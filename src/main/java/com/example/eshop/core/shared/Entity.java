package com.example.eshop.core.shared;

import org.springframework.lang.Nullable;
import java.io.Serializable;

/**
 * Entity in terms of DDD
 */
public interface Entity<ID extends Serializable> {
    @Nullable
    ID id();
}
