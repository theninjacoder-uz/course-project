package com.itransition.courseproject.model.entity.collection;

import com.itransition.courseproject.model.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Getter
@Setter
@Entity
public class Field extends BaseEntity {

    @Column(nullable = false)
    private String name;

    private int type;

    @ManyToOne
    private Collection collection;

}
