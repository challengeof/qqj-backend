package com.mishu.cgwy.response.query;

import com.mishu.cgwy.response.Response;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
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
