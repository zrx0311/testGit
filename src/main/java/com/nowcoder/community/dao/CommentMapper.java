package com.nowcoder.community.dao;

import com.nowcoder.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author zrxggg
 * @create 2022-04-17-14:00
 */
@Mapper
public interface CommentMapper {
    //根据实体查询，帖子的评论还是评论的评论
    List<Comment> selectCommentsByEntity(int entityType, int entityId, int offset, int limit);

    int selectCountByEntity(int entityType, int entityId);
    //添加评论
    int insertComment(Comment comment);

    Comment selectCommentById(int id);
}
