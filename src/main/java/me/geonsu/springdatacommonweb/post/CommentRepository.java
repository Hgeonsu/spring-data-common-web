package me.geonsu.springdatacommonweb.post;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long>, JpaSpecificationExecutor<Comment> {
        <T> List<T> findByPost_Id(Long id, Class<T> type); //generic 활용
//    List<CommentSummary> findByPost_Id(Long id);
}
