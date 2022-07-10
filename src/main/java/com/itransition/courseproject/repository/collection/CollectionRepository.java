package com.itransition.courseproject.repository.collection;

import com.itransition.courseproject.model.entity.collection.Collection;
import com.itransition.courseproject.model.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollectionRepository extends JpaRepository<Collection, Long> {
    @Query("select c from Collection c where c.user.id = ?1")
    List<Collection> findAllByUser_Id(Long userId);

    @Query("select (count(c) > 0) from Collection c where c.id = ?1 and c.user.email = ?2 and c.user.status = ?3")
    boolean existsByIdAndUserEmailAndUserStatus(Long id, String userEmail, Status userStatus);
}
