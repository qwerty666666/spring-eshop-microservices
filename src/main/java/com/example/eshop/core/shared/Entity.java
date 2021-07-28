package com.example.eshop.core.shared;

import java.io.Serializable;

/**
 * Entity in terms of DDD
 */
public interface Entity<ID extends Serializable> {
    ID id();
}
