package sensecloud.web.bean.vo;

import lombok.Data;

@Data
public class DagFileVO {

    private String fileLoc;
    private long fileLocHash;
    private String fileName;
    private String groupName;
    private String sourceCode;

}
