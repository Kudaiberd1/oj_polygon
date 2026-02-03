package com.polygon.onlinejudge.dto.pagination;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Getter
@Setter
public class PaginationParams {
    @Min(value = 0, message = "validation.page.min")
    private int page;
    @Min(value = 1, message = "validation.size.min")
    @Max(value = 100, message = "validation.size.max")
    private int size = 10;


    public Pageable toPageable() {
        return PageRequest.of(
                page, size
        );
    }
}
