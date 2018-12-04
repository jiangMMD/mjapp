package com.mmd.mjapp.model;


/**
 * 产品轮播图
 */
public class ProdCarousal {

  private Long id;
  private Long pid;
  private String pdUrl;
  private Long pdOrder;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getPid() {
    return pid;
  }

  public void setPid(Long pid) {
    this.pid = pid;
  }

  public String getPdUrl() {
    return pdUrl;
  }

  public void setPdUrl(String pdUrl) {
    this.pdUrl = pdUrl;
  }

  public Long getPdOrder() {
    return pdOrder;
  }

  public void setPdOrder(Long pdOrder) {
    this.pdOrder = pdOrder;
  }
}
