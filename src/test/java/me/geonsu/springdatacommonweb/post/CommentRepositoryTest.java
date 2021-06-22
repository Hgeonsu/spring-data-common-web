package me.geonsu.springdatacommonweb.post;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

//@DataJpaTest // slicing test -> data 관련된 계층에 관련된 bean만 등록하고, @Service 붙은 bean 같은 건 등록이 안 됨
@SpringBootTest
public class CommentRepositoryTest {
    @Autowired
    CommentRepository commentRepository;

    @Autowired
    PostRepository postRepository;

    @Test
    public void getComment() {
        Post post = new Post();
        post.setTitle("jpa");
        Post savedPost = postRepository.save(post);

        Comment comment = new Comment();
        comment.setComment("spring data jpa projection");
        comment.setPost(savedPost);
        comment.setUp(10);
        comment.setDown(1);
        comment.setCommentState(CommentState.DRAFT);
        commentRepository.save(comment);

        commentRepository.findByPost_Id(savedPost.getId(), CommentOnly.class).forEach(c -> {
            System.out.println("============");
            System.out.println(c.getComment());
        });
    }

    @Test
    public void specs() {
        // 클라이언트 코드가 간단해진다는 장점
        Page<Comment> page = commentRepository.findAll(CommentSpecs.isBest().or(CommentSpecs.isGood()),
                PageRequest.of(0, 10));

    }
}