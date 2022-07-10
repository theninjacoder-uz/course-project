package com.itransition.courseproject.repository.collection;

import com.itransition.courseproject.model.entity.collection.Item;
import com.itransition.courseproject.model.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Transactional
    @Modifying
    @Query("delete from Item i where i.collection.id = ?1")
    int deleteAllByCollection_Id(Long id);

    @Query("select (count(i) > 0) from Item i inner join i.likedUsers likedUsers where i.id = ?1 and likedUsers.id = ?2")
    Boolean existsByIdAndLikedUsers_Id(Long itemId, Long userId);

    @Transactional
    @Modifying
    @Query(value = "insert into item_liked_users(item_id, liked_users_id) select :itemId, :userId where " +
            " not exists (select 1 from item_liked_users t where t.item_id = :itemId and t.liked_users_id = :userId)",
        nativeQuery = true
    )
    void insertItemLike(@Param("itemId") long itemId, @Param("userId") long userId);

    @Transactional
    @Modifying
    @Query(value = "delete from item_liked_users where " +
            " exists (select 1 from item_liked_users t where t.item_id = :itemId and t.liked_users_id = :userId)",
            nativeQuery = true
    )
    void deleteItemLike(@Param("itemId") long itemId, @Param("userId") long userId);

    @Query("select (count(i) > 0) from Item i " +
            "where i.id = ?1 and i.collection.user.email = ?2 and i.collection.user.status = ?3")
    boolean existsByIdAndCollection_UserEmailAndCollection_UserStatus(Long itemId, String email, Status status);

    @Query("select i from Item i where i.collection.id = ?1")
    List<Item> findAllByCollectionId(Long collectionId);
}
