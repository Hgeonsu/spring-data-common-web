package me.geonsu.springdatacommonweb.post;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Comment {
    @Id @GeneratedValue
    private Long id;

    private String comment;

    @Enumerated(EnumType.STRING)
    private CommentState commentState;

    /*
        fetch mode = eager (many to one의 기본값)인 경우, 특정 comment를 조회했을 때, 연관된 post의 데이터도 같이 조회한다.
        lazy인 경우, 그대로 코멘트만 가져온다.
         */
    @ManyToOne(fetch = FetchType.EAGER)
    private Post post;

    private int up;

    private int down;

    private boolean best;

    @CreatedDate
    private Date created;

    @CreatedBy
    @ManyToOne
    private Account createdBy;

    @LastModifiedDate
    private Date updated;

    @LastModifiedBy
    @ManyToOne
    private Account updatedBy;

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

    public CommentState getCommentState() {
        return commentState;
    }

    public void setCommentState(CommentState commentState) {
        this.commentState = commentState;
    }

    @PrePersist // auditing 관련 설정이 빠지기 때문에 좀 더 general한 기능
    public void prePersist() {
        System.out.println("Pre Persist is called");
        this.created = new Date();
        // user도 security 적용한 이후에 사용 가능
    }
}
