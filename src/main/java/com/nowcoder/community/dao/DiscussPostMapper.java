package com.nowcoder.community.dao;

import com.nowcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zrxggg
 * @create 2022-04-15-9:28
 */
@Mapper
public interface DiscussPostMapper {
    List<DiscussPost> selectDiscussPosts(int userId,int offset,int limit);
    int selectDiscussPostRows(@Param("userId") int userId);
    int insertDiscussPost(DiscussPost discussPost);
    DiscussPost selectDiscussPostById(int id);
    int updateCommentCount(int id, int commentCount);

    int updateType(int id, int type);

    int updateStatus(int id, int status);

    int updateScore(int id, double score);
}
