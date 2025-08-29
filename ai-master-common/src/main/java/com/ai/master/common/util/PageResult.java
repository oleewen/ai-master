package com.ai.master.common.util;

import lombok.Data;

import java.util.List;

/**
 * 分页结果
 */
@Data
public class PageResult<T> {
    
    private List<T> content;
    private int page;
    private int size;
    private long total;
    private int totalPages;
    
    public static <T> PageResult<T> of(List<T> content, int page, int size) {
        PageResult<T> result = new PageResult<>();
        result.setContent(content);
        result.setPage(page);
        result.setSize(size);
        result.setTotal(content.size());
        result.setTotalPages((int) Math.ceil((double) content.size() / size));
        return result;
    }
    
    public static <T> PageResult<T> of(List<T> content, int page, int size, long total) {
        PageResult<T> result = new PageResult<>();
        result.setContent(content);
        result.setPage(page);
        result.setSize(size);
        result.setTotal(total);
        result.setTotalPages((int) Math.ceil((double) total / size));
        return result;
    }
}