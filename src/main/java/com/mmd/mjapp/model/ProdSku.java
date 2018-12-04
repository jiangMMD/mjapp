package com.mmd.mjapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class ProdSku {

  private Long skuId;
  @JsonIgnore
  private Long pid;
  private String skuVal;
  private Double price;
  private Double mmdprice;
  private Long repertory;
  private Long salenum;

}


