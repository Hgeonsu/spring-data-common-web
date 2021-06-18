package me.geonsu.springdatacommonweb.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
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
    public String getPost(@PathVariable("id") Post post) { //Domain Class Converter 동작
        // 변수 이름이랑 path로 넘어오는 이름이랑 같으면 애노테이션에 명시 생략 가능

        return post.getTitle(); //null이 나올 수도 있음
    }

    @GetMapping("/posts")
    public PagedModel<EntityModel<Post>> getPosts(Pageable pageable, PagedResourcesAssembler<Post> assembler) {
        return assembler.toModel(postRepository.findAll(pageable));
    }
}
