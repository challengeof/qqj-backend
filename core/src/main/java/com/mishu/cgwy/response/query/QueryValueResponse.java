package com.mishu.cgwy.response.query;

import com.mishu.cgwy.response.Response;
import lombok.Data;

/**
 * Created by king-ck on 2016/3/3.
 */
@Data
public class QueryValueResponse<T> extends Response<T> {
    public QueryValueResponse() {
    }

    public QueryValueResponse(T content) {
        this.content = content;
    }

    public QueryValueResponse(T content,boolean success) {
        this.content = content;
        this.success=success;
    }

    private T content;
}
