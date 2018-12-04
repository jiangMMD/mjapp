package com.mmd.mjapp.pjo;

import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * 分页返回结果集
 */
public class ResultPage<T> extends PageInfo<T>{
    private int page; //当前页
    private int total; //总页数
    private long records; //总记录数
    private List<T> rows; //数据集


    public ResultPage(List<T> list) {
        super(list);
        //减少数据量的传输
        super.setList(null);
        this.rows = list;
        this.page = super.getPageNum();
        this.total = super.getPages();
        this.records = super.getTotal();
    }


    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    @Override
    public long getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public long getRecords() {
        return records;
    }

    public void setRecords(long records) {
        this.records = records;
    }

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }

    @Override
    public String toString() {
        return "ResultPage{" +
                "page=" + page +
                ", total=" + total +
                ", records=" + records +
                ", rows=" + rows +
                '}';
    }
}
