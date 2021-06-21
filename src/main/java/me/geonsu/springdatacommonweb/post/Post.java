package me.geonsu.springdatacommonweb.post;

import javax.persistence.*;
import java.util.Date;

@Entity
// 지저분, JPQL, SQL 많이 생김. 대신 repository method에 @Query 쓰는 게 나음
// title이 첫번째 인자와 같으면 찾아달라 (JPQL)
//@NamedQuery(name = "post.findByTitle", query = "SELECT p FROM Post AS p WHERE p.title = ?1")
public class Post {

    @Id @GeneratedValue
    private Long id;

    private String title;

    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
