package com.itransition.courseproject.model.entity.collection;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.itransition.courseproject.model.entity.BaseEntity;
import com.itransition.courseproject.model.entity.tag.Tag;
import com.itransition.courseproject.model.entity.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Setter
@Getter
@Entity
@NoArgsConstructor
public class Item extends BaseEntity {
    @Column(nullable = false)
    private String name;
    @Column(columnDefinition = "int default 0")
    private long likes;

    @ManyToOne
    private Collection collection;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    private List<Tag> tags;

    public Item(String name, Collection collection, List<Tag> tagList) {
        this.name = name;
        this.collection = collection;
        this.tags = tagList;
    }

    @JsonIgnore
    @ManyToMany
    private Set<User> likedUsers = new HashSet<>();

    public long getLikes() {
        return likedUsers.size();
    }

}
