package me.geonsu.springdatacommonweb.post;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    //    <T> List<T> findByPost_Id(Long id, Class<T> type);
    List<CommentSummary> findByPost_Id(Long id);
}
