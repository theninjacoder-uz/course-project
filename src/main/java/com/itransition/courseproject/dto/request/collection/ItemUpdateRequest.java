package com.itransition.courseproject.dto.request.collection;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.itransition.courseproject.dto.request.Request;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;
@Getter
@Setter
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemUpdateRequest implements Request {
    private String name;
    private List<Long> tags;
    @JsonProperty("field_values")
    private List<FieldValueUpdateRequest> fieldValues;
}
