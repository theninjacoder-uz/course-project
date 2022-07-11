package com.itransition.courseproject.repository.item;

import com.itransition.courseproject.model.entity.item.Item;
import com.itransition.courseproject.model.entity.item.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment,Long> {

    List<Comment> findAllByItemIdOrderByCreationDateDesc(Long item_id);

    void deleteAllByItemId(Long item_id);

    void deleteAllByItemIn(Collection<Item> item);

    void deleteCommentsByItem_Collection_Id(Long collectionId);
}
