package com.mmd.mjapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * 商品信息
 */
@Data
public class OfflineProdList {

    private Long pid;
    @JsonIgnore
    private Long merId;
    @JsonIgnore
    private Long state;
    private String pname;
    private String title;
    private String proDesc;
    private String homeimg;
    private String ativityTime;

    @JsonIgnore
    private String recsts;


}
