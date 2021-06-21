package me.geonsu.springdatacommonweb.post;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.JpaSort;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

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

    @Test
    public void findByTitleStartsWith() {
        savePost("Spring Data Jpa");

        List<Post> all = postRepository.findByTitleStartsWith("Spring");
        assertThat(all.size()).isEqualTo(1);
    }

    @Test
    public void findByTitle() {
        savePost("Spring");

        List<Post> all = postRepository.findByTitle("Spring", Sort.by("title"));
        // 정렬 조건은 프로퍼티 또는 사용한 alias로만 가능. LENGTH(title) 이런거 불가

        all = postRepository.findByTitle("Spring", JpaSort.unsafe("LENGTH(title)"));
        assertThat(all.size()).isEqualTo(1);
    }

    private Post savePost(String spring) {
        Post post = new Post();
        post.setTitle(spring);
        return postRepository.save(post);
    }

    @Test
    public void updateTitle() {
        Post spring = savePost("Spring");
        int update = postRepository.updateTitle("hibernate", spring.getId());
        assertThat(update).isEqualTo(1);

        /*
        update 쿼리는 날아갔지만, spring 이라는 post가 여전히 persistent 상태로 캐시가 되어있기 때문에,
        아래의 조회에서 title이 "Spring"으로 찍힌다. select 쿼리가 발생한 적이 없기때문에 캐시에 변화가 없다.
        @Modifying 애너테이션에 옵션을 통해 persistent context 를 clear 해주면, persistent 상태가 아니게 되기 때문에
        findById 할 때 다시 DB에서 select 쿼리가 발생하기 때문에 갱신된 값을 읽어올 수 있다.
        권장하는 방법은 아님
         */
        Optional<Post> byId = postRepository.findById(spring.getId());
        assertThat(byId.get().getTitle()).isEqualTo("hibernate");
    }

    @Test
    public void updateTitle2() {
        Post spring = savePost("Spring");
        spring.setTitle("hibernate");

        List<Post> all = postRepository.findAll();
        assertThat(all.get(0).getTitle()).isEqualTo("hibernate");
    }
}















