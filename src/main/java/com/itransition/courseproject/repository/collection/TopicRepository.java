package com.itransition.courseproject.repository.collection;

import com.itransition.courseproject.model.entity.collection.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TopicRepository extends JpaRepository<Topic, Long> {
}
