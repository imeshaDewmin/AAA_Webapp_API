package com.aaa.service.AAAService.dtos;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * A Data Transfer Object for pagination.
 *
 * @param <T> the type of the data being paginated
 */

@Data
@Builder
public class PaginationDto<T> implements Serializable {
    private int page = 0;
    private int size = 10;
    private T content;
    private int totalElements;

}
