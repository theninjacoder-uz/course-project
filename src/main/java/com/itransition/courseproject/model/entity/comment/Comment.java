package com.itransition.courseproject.model.entity.comment;

import com.itransition.courseproject.model.entity.collection.Item;
import com.itransition.courseproject.model.entity.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String text;

    @Column(nullable = false)
    private Date creationDate = new Date();

    @ManyToOne
    private User user;

    @ManyToOne
    private Item item;

    public Comment(String text, User user, Item item) {
        this.text = text;
        this.user = user;
        this.item = item;
    }

}
