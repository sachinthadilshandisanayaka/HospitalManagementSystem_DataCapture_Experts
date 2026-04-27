package co.clinic.appointment.dto.response;

import org.springframework.data.domain.Page;

import java.util.List;

public class PageResponse<T> {

    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean last;

    public PageResponse() {}

    public static <T> PageResponse<T> of(Page<T> pageResult) {
        PageResponse<T> response = new PageResponse<>();
        response.content = pageResult.getContent();
        response.page = pageResult.getNumber();
        response.size = pageResult.getSize();
        response.totalElements = pageResult.getTotalElements();
        response.totalPages = pageResult.getTotalPages();
        response.last = pageResult.isLast();
        return response;
    }

    public List<T> getContent() { return content; }
    public int getPage() { return page; }
    public int getSize() { return size; }
    public long getTotalElements() { return totalElements; }
    public int getTotalPages() { return totalPages; }
    public boolean isLast() { return last; }
}
