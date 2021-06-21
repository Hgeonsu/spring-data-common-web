package me.geonsu.springdatacommonweb.post;

import org.springframework.beans.factory.annotation.Value;

public interface CommentSummary {

    String getComment();

    int getUp();

    int getDown();

    // open projection
    @Value("#{target.up + ' ' + target.down}")
    String getVotes();

}
