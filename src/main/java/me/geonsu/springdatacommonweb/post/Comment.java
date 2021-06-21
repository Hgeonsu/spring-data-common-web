package me.geonsu.springdatacommonweb.post;

import javax.persistence.*;

@Entity
public class Comment {
    @Id @GeneratedValue
    private Long id;

    private String comment;

    /*
    fetch mode = eager (many to one의 기본값)인 경우, 특정 comment를 조회했을 때, 연관된 post의 데이터도 같이 조회한다.
    lazy인 경우, 그대로 코멘트만 가져온다.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    private Post post;

    private int up;

    private int down;

    private boolean best;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public int getUp() {
        return up;
    }

    public void setUp(int up) {
        this.up = up;
    }

    public int getDown() {
        return down;
    }

    public void setDown(int down) {
        this.down = down;
    }

    public boolean isBest() {
        return best;
    }

    public void setBest(boolean best) {
        this.best = best;
    }
}
