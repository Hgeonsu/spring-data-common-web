package me.geonsu.springdatacommonweb.post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long>, JpaSpecificationExecutor<Comment> {

        @Transactional(readOnly = true)
        <T> List<T> findByPost_Id(Long id, Class<T> type); //generic 활용
//    List<CommentSummary> findByPost_Id(Long id);
}
