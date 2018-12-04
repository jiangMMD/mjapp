package com.mmd.mjapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
public class Productsinfo {

    private Long pid;
    @JsonIgnore
    private Long merId;
    @JsonIgnore
    private Long state;
    private String pname;
    private String title;
    private String desc;
    @JsonIgnore
    private String categoryid;
    private Double shopprice; //rmb售价
    private Double shopmmdprice; //mmd售价
    private String homeimg; //产品首页照
    private String paramimg; //产品参数找
    @JsonIgnore
    private Double costprice;
    @JsonIgnore
    private Long isbest;
    @JsonIgnore
    private Long ishot;
    @JsonIgnore
    private Long isnew;
    private Long isfree; //是否免运费
    private Double weight;
    private Double volume; //尺寸
    private Double expressfee; //快递费用
    private Long inventory;
    private Long sell;
    private Long commentcount; //总评论数
    private Long bestcount; //好评数
    private Long mediumccount; //中等评价数
    private Long badcount; //差评数
    private Double average; //平均分

    private List<String> carousals = new ArrayList<>(); //轮播图地址集合
    @JsonIgnore
    private List<ProdCarousal> prodCarousals;  //对应的轮播图信息

    private List<String> services = new ArrayList<>(); //产品服务
    @JsonIgnore
    private List<ProdService> prodServices; //提供的服务

    @JsonIgnore
    private List<ProdDetailimg> prodDetailimgList; //对应的产品详情照片
    private List<String> detailimgs = new ArrayList<>();

    //产品的sku 信息
    private List<ProdSku> prodSkuList;

    //产品属性及其属性值信息
    private List<Map<String, Object>> propList;

    @JsonIgnore
    private Long isflashview;
    @JsonIgnore
    private String crtuser;
    @JsonIgnore
    private Date crtdate;
    @JsonIgnore
    private String recsts;


    public void dealServices() {
        if (this.prodServices != null) {
            for (ProdService prodServices : this.prodServices) {
                services.add(prodServices.getContent());
            }
        }
    }

    public void dealCarousals() {
        if (this.prodCarousals != null) {
            for (ProdCarousal prodCarousal : this.prodCarousals) {
                carousals.add(prodCarousal.getPdUrl());
            }
        }
    }

    public void dealDetailimgs() {
        if (this.prodDetailimgList != null) {
            for (ProdDetailimg prodDetailimg : this.prodDetailimgList) {
                detailimgs.add(prodDetailimg.getImg());
            }
        }
    }
}
