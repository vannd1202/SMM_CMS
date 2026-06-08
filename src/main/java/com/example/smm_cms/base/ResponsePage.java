package com.example.smm_cms.base;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.io.Serializable;
import java.util.List;

@Data
public class ResponsePage<T> implements Serializable {
    private final int pageNo;
    private final int pageSize;
    private final long totalCount;
    private final int totalPage;
    private final List<T> content;

    public ResponsePage(int pageNo, int pageSize, long totalCount, int totalPage, List<T> content) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.totalCount = totalCount;
        this.totalPage = totalPage;
        this.content = content;
    }

    public ResponsePage(Page<T> page) {
        this(page, page.getContent());
    }

    @SuppressWarnings("rawtypes")
    public ResponsePage(Page page, List<T> content) {
        this(page.getNumber() + 1, page.getSize(), page.getTotalElements(), page.getTotalPages(), content);
    }
}
