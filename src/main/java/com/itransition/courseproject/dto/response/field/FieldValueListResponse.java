package com.itransition.courseproject.dto.response.field;

import com.itransition.courseproject.dto.response.Response;
import com.itransition.courseproject.dto.response.collection.FieldResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class FieldValueListResponse implements Response {
    private List<FieldResponse> types;
    private Map<Long, List<FieldValueResponse>> values;
}
