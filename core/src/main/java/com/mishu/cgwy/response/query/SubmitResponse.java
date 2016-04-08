package com.mishu.cgwy.response.query;

import com.mishu.cgwy.response.Response;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SubmitResponse<T> extends QueryValueResponse<T> {

    public SubmitResponse() {
    }

    public SubmitResponse(T content) {
        super(content);
    }

}
