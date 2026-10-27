package com.courseshop.courseshop.model;

import java.util.List;

public class ResultPaginationDTO {
    
    private List<?> data;
    private int page;
    private int size;
    private int totalPages;
    private long totalElements;

    public ResultPaginationDTO(){}


    public ResultPaginationDTO(List<?> data, int page, int size, int totalPages, long totalElements) {
        this.data = data;
        this.page = page;
        this.size = size;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
    }

    public List<?> getData() {
        return data;
    }
    public void setData(List<?> data) {
        this.data = data;
    }
    public int getPage() {
        return page;
    }
    public void setPage(int page) {
        this.page = page;
    }
    public int getSize() {
        return size;
    }
    public void setSize(int size) {
        this.size = size;
    }
    public int getTotalPages() {
        return totalPages;
    }
    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
    public long getTotalElements() {
        return totalElements;
    }
    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    
}
