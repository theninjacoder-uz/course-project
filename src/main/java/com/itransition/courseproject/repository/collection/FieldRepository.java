package com.itransition.courseproject.repository.collection;

import com.itransition.courseproject.model.entity.collection.Collection;
import com.itransition.courseproject.model.entity.collection.Field;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface FieldRepository extends JpaRepository<Field, Long> {
    @Query("select f from Field f where f.collection = ?1")
    List<Field> findAllByCollection(Collection collection);

    @Transactional
    @Modifying
    @Query("delete from Field f where f.collection.id = ?1")
    int deleteAllByCollection_Id(Long collectionId);

    @Query(value = "SELECT collection_id FROM field GROUP BY collection_id ORDER BY COUNT(collection_id) DESC LIMIT 5", nativeQuery = true)
    List<Long> findTopCollectionIds();

    @Query("select f from Field f where f.collection.id = ?1")
    List<Field> findAllByCollection_Id(Long id);

    @Query("select f from Field f where f.collection.id = ?1 order by f.id")
    List<Field> findAllByCollection_IdOrderByIdAsc(Long collectionId);
}
