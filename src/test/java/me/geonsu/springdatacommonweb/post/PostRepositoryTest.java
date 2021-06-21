package me.geonsu.springdatacommonweb.post;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class PostRepositoryTest {

    @Autowired
    PostRepository postRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Test
    public void crud() {
        Post post = new Post();
        post.setTitle("jpa");
        Post savedPost = postRepository.save(post);// persist (transient -> persistent 상태), 판단 근거는 entity의 id 존재 여부
        //반환되는 객체를 사용하는 게 best practice.

        assertThat(entityManager.contains(post)).isTrue();
        assertThat(entityManager.contains(savedPost)).isTrue();
        assertThat(savedPost == post).isTrue();

        Post postUpdate = new Post();
        postUpdate.setId(post.getId());
        postUpdate.setTitle("hibernate");
        Post updatedPost = postRepository.save(postUpdate);// merge (Detached-> Persistent 상태)
        // update 쿼리가 발생하고, jpa 대신 hibernate가 값을 update 한다

        assertThat(entityManager.contains(updatedPost)).isTrue();
        assertThat(entityManager.contains(postUpdate)).isFalse();
        assertThat(updatedPost == postUpdate).isFalse();

        List<Post> all = postRepository.findAll(); // 이 두 라인이 없었으면 insert를 하지 않음.
        assertThat(all.size()).isEqualTo(1);
    }
}