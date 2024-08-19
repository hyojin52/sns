package com.fastcampus.sns.service;

import com.fastcampus.sns.exception.ErrorCode;
import com.fastcampus.sns.exception.SnsApplicationException;
import com.fastcampus.sns.model.AlarmArgs;
import com.fastcampus.sns.model.AlarmType;
import com.fastcampus.sns.model.Comment;
import com.fastcampus.sns.model.Post;
import com.fastcampus.sns.model.entity.*;
import com.fastcampus.sns.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class PostService {
  
  private final PostEntityRepository postEntityRepository;
  
  private final UserEntityRepository userEntityRepository;
  
  private final LikeEntityRepository likeEntityRepository;
  
  private final CommentEntityRepository commentEntityRepository;
  
  private final AlarmEntityRepository alarmEntityRepository;
  
  
  @Transactional
  public void create(String title, String body, String userName) {
    // user find
    UserEntity userEntity = getUserOrThrowException(userName);
    
    // post save
    PostEntity saved = postEntityRepository.save(PostEntity.of(title, body, userEntity));
  }
  
  @Transactional
  public Post modify(String title, String body, String userName, Integer postId) {
    UserEntity userEntity = getUserOrThrowException(userName);
    //post exist
    PostEntity postEntity = getPostOrThrowException(postId);
    
    // post permission
    if(postEntity.getUser() != userEntity) {
      throw new SnsApplicationException(ErrorCode.INVALID_PERMISSION, String.format("%s has no permission with %s", userName, postId));
    }
    postEntity.setTitle(title);
    postEntity.setBody(body);
    return Post.fromEntity(postEntityRepository.save(postEntity));
  }
  
  @Transactional
  public void delete(String userName, Integer postId) {
    UserEntity userEntity = getUserOrThrowException(userName);
    //post exist
    PostEntity postEntity = getPostOrThrowException(postId);
    
    // post permission
    if(postEntity.getUser() != userEntity) {
      throw new SnsApplicationException(ErrorCode.INVALID_PERMISSION, String.format("%s has no permission with %s", userName, postId));
    }
    likeEntityRepository.deleteAllByPost(postEntity);
    commentEntityRepository.deleteAllByPost(postEntity);
    postEntityRepository.delete(postEntity);
  }
  
  public Page<Post> list(Pageable pageable) {
    return postEntityRepository.findAll(pageable).map(Post::fromEntity);
  }
  
  public Page<Post> my(String username, Pageable pageable) {
    UserEntity userEntity = getUserOrThrowException(username);
    
    return postEntityRepository.findAllByUser(userEntity, pageable).map(Post::fromEntity);
  }
  
  @Transactional
  public void like(Integer postId, String userName) {
    //post exist
    PostEntity postEntity = getPostOrThrowException(postId);
    UserEntity userEntity = getUserOrThrowException(userName);
    
    // check liked -> throw
    likeEntityRepository.findByUserAndPost(userEntity, postEntity).ifPresent(it -> {
      throw new SnsApplicationException(ErrorCode.ALREADY_LIKED, String.format("userName %s already like post %d", userName, postId));
    });
    
    likeEntityRepository.save(LikeEntity.of(userEntity, postEntity));
    
    alarmEntityRepository.save(AlarmEntity.of(userEntity, AlarmType.NEW_LIKE_ON_POST, new AlarmArgs(userEntity.getId(), postEntity.getId())));
  }
  
  
  public int likeCount(Integer postId) {
    //post exist
    PostEntity postEntity = getPostOrThrowException(postId);
    
    // count like
    // List<LikeEntity> likeEntityList = likeEntityRepository.findByPost(postEntity);
    // return likeEntityList.size();
    return likeEntityRepository.countByPost(postEntity);
  }
  
  @Transactional
  public void comment(Integer postId, String userName, String comment) {
    //post exist
    PostEntity postEntity = getPostOrThrowException(postId);
    UserEntity userEntity = getUserOrThrowException(userName);
    
    // comment save
    commentEntityRepository.save(CommentEntity.of(userEntity, postEntity, comment));
    
    alarmEntityRepository.save(AlarmEntity.of(userEntity, AlarmType.NEW_COMMENT_ON_POST, new AlarmArgs(userEntity.getId(), postEntity.getId())));
  }
  
  public Page<Comment> getComment(Integer postId, Pageable pageable) {
    PostEntity postEntity = getPostOrThrowException(postId);
    return commentEntityRepository.findAllByPost(postEntity, pageable)
            .map(Comment::fromEntity);
  }
  
  private PostEntity getPostOrThrowException(Integer postId) {
    return postEntityRepository.findById(postId).orElseThrow(() ->
            new SnsApplicationException(ErrorCode.POST_NOT_FOUND, String.format("%s not founded", postId))
    );
  }
  
  private UserEntity getUserOrThrowException(String userName) {
    return userEntityRepository.findByUserName(userName).orElseThrow(() ->
            new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", userName))
    );
  }
  
  
}
