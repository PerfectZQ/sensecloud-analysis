package sensecloud.web.bean.common;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.util.List;

@Data
@Builder
public class PageResult<E> {

    // When use @Data @Builder at the same time, no args constructor couldn't be generate.
    // @Tolerate will let Lombok ignore this no args constructor if you must indicate one.
    @Tolerate
    PageResult() {}

    // 总页数
    private long totalPages;
    // 当前页面Id
    private int currentPageId;
    // 元素总数
    private long totalElems;
    // 当前页面元素
    private List<E> currentPageElems;

}
