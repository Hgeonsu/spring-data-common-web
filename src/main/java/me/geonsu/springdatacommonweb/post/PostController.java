package me.geonsu.springdatacommonweb.post;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class PostController {
    /*
    어떠한 한 bean의 생성자가 하나만 있고, 그 생성자가 받는 reference가 bean으로 등록되어있으면
    그 bean을 알아서 자동으로 주입을 해준다. (생성자가 하나만 있는 경우)
     */
    private final PostRepository postRepository;

    public PostController(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @GetMapping("/posts/{id}")
    public String getPost(@PathVariable Long id) {
        // 변수 이름이랑 path로 넘어오는 이름이랑 같으면 애노테이션에 명시 생략 가능
        // 바인딩을 Long으로 받았으니 스프링mvc 웹데이터바인더가 타입 바인딩을 해준다.(원래 문자열로 들어옴)

        Optional<Post> byId = postRepository.findById(id);
        Post post = byId.get();
        return post.getTitle(); //null이 나올 수도 있음
    }
}
