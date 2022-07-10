package com.itransition.courseproject.repository;

import com.itransition.courseproject.model.entity.collection.Item;
import com.itransition.courseproject.model.entity.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;


public interface CommentRepository extends JpaRepository<Comment,Long> {

    @Query("select c from Comment c where c.item.id = ?1 order by c.creationDate DESC")
    List<Comment> findAllByItemIdOrderByCreationDateDesc(Long item_id);

    @Query(value = "select * from comment c where c.doc @@ plainto_tsquery(:text)", nativeQuery = true)
    List<Comment> fullTextSearch(String text);

    @Transactional
    @Modifying
    @Query("delete from Comment c where c.item.id = ?1")
    int deleteAllByItem_Id(Long item_id);


    @Transactional
    @Modifying
    @Query("delete from Comment c where c.item in ?1")
    int deleteAllByItemIn(Collection<Item> item);
}
