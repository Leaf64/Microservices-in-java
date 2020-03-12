package introtask.dto;

import java.util.List;

public class PaginationDto<T> {

    private PaginationDto.Pagination pagination;
    private List<T> results;

    public PaginationDto() {
    }

    public PaginationDto(PaginationDto.Pagination pagination, List<T> results) {
        this.pagination = pagination;
        this.results = results;
    }

    public PaginationDto.Pagination getPagination() {
        return this.pagination;
    }
    public List<T> getResults() {
        return this.results;
    }

    public static class Pagination {
        private long offset;
        private long count;
        private long totalCount;

        public Pagination() {
        }

        public Pagination(long offset, long count, long totalCount) {
            this.offset = offset;
            this.count = count;
            this.totalCount = totalCount;
        }

        public long getOffset() {
            return this.offset;
        }

        public long getCount() {
            return this.count;
        }

        public long getTotalCount() {
            return this.totalCount;
        }
    }

}
