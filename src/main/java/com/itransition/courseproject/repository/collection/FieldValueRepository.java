package com.itransition.courseproject.repository.collection;

import com.itransition.courseproject.model.entity.collection.FieldValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface FieldValueRepository extends JpaRepository<FieldValue, Long> {

    @Query("select f from FieldValue f where f.item.collection.id = ?1 order by f.item.id, f.item.name")
    List<FieldValue> findAllByItem_CollectionId(Long collection_id);

    @Query("select f from FieldValue f where f.item.id = ?1")
    List<FieldValue> findAllByItemId(Long item_id);


    @Query("select f from FieldValue f inner join f.item.tags tags where tags.id = ?1")
    List<FieldValue> findAllByItem_Tags_Id(Long item_tags_id);

    @Query("select f from FieldValue f where f.item.collection.id = ?1 order by f.item.id, f.field.id")
    List<FieldValue> findAllByItem_CollectionIdOrderByItem_IdAscField_IdAsc(Long collection_id);

    @Transactional
    @Modifying
    @Query("delete from FieldValue f where f.item.id = ?1")
    int deleteAllByItem_Id(Long itemId);
}
