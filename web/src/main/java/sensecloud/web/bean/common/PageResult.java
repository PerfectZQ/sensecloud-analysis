package sensecloud.web.bean.common;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PageResult {
    // 总页数
    private long totalPages;
    // 当前页面Id
    private int currentPageId;
    // 元素总数
    private long totalElems;
    // 当前页面元素
    private List<?> currentPageElems;
}
