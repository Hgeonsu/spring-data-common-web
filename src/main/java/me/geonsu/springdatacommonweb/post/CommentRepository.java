package me.geonsu.springdatacommonweb.post;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<CommentSummary> findByPost_Id(Long id); // type에 projection의 대상이 되는 컬럼의 getter를 담은 interface를 넣어준다.
}
