package me.geonsu.springdatacommonweb.post;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/*
단위 테스트가 아니라 integration test (단위 테스트가 아님)
application 모든 bean이 다 등록된다.
application.properties에 들어와있는 속성들 전부 적용이 된다. 여기 설정한 datasource를 사용하게 된다.
이걸 원하지 않으면, @ActiveProfiles("test") 라고 주고
application-test.properties 파일 생성해서 test/resources 에 넣어주면된다.
 */

@SpringBootTest
@AutoConfigureMockMvc
public class PostControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    PostRepository postRepository;

    @Test
    public void getPost() throws Exception {
        Post post = new Post();
        post.setTitle("jpa");
        postRepository.save(post);

        mockMvc.perform(get("/posts/" + post.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("jpa"));
    }
}