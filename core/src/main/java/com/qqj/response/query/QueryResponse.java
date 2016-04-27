package com.qqj.response.query;

import com.qqj.response.Response;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class QueryResponse<T> extends Response<T> {

    public QueryResponse() {
        super.setSuccess(Boolean.TRUE);
    }

    public QueryResponse(List<T> content) {
        super.setSuccess(Boolean.TRUE);
        this.content = content;
    }

    private List<T> content = new ArrayList<T>();

    private long total;

    private int page = 0;

    private int pageSize = 100;



}
