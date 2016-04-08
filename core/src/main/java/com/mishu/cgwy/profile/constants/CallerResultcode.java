package com.mishu.cgwy.profile.constants;

/**
 * Created by king-ck on 2015/10/13.
 */
public enum CallerResultcode {

    success(0,"成功"),
    existsCaller(101,"此手机号客户已存在，保存失败"),
    errorSave(102,"保存失败"),

    delFailure(201,"删除失败");

    private CallerResultcode(int no, String detail) {
        this.no = no;
        this.detail = detail;
    }

    private int no;
    private String detail;

    public int getNo() {
        return no;
    }

    public String getDetail() {
        return detail;
    }
}
