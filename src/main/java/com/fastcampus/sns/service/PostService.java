package com.fastcampus.sns.service;

import com.fastcampus.sns.exception.ErrorCode;
import com.fastcampus.sns.exception.SnsApplicationException;
import com.fastcampus.sns.model.Post;
import com.fastcampus.sns.model.entity.LikeEntity;
import com.fastcampus.sns.model.entity.PostEntity;
import com.fastcampus.sns.model.entity.UserEntity;
import com.fastcampus.sns.repository.LikeEntityRepository;
import com.fastcampus.sns.repository.PostEntityRepository;
import com.fastcampus.sns.repository.UserEntityRepository;
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
  
  
  @Transactional
  public void create(String title, String body, String userName) {
    // user find
    UserEntity userEntity = userEntityRepository.findByUserName(userName).orElseThrow(() ->
            new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("user name is %s", userName))
    );
    
    // post save
    PostEntity saved = postEntityRepository.save(PostEntity.of(title, body, userEntity));
  }
  
  @Transactional
  public Post modify(String title, String body, String userName, Integer postId) {
    UserEntity userEntity = userEntityRepository.findByUserName(userName).orElseThrow(() ->
            new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", userName))
    );
    //post exist
    PostEntity postEntity = postEntityRepository.findById(postId).orElseThrow(() ->
            new SnsApplicationException(ErrorCode.POST_NOT_FOUND, String.format("%s not founded", postId))
    );
    
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
    UserEntity userEntity = userEntityRepository.findByUserName(userName).orElseThrow(() ->
            new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", userName))
    );
    //post exist
    PostEntity postEntity = postEntityRepository.findById(postId).orElseThrow(() ->
            new SnsApplicationException(ErrorCode.POST_NOT_FOUND, String.format("%s not founded", postId))
    );
    
    // post permission
    if(postEntity.getUser() != userEntity) {
      throw new SnsApplicationException(ErrorCode.INVALID_PERMISSION, String.format("%s has no permission with %s", userName, postId));
    }
    
    postEntityRepository.delete(postEntity);
  }
  
  public Page<Post> list(Pageable pageable) {
    return postEntityRepository.findAll(pageable).map(Post::fromEntity);
  }
  
  public Page<Post> my(String username, Pageable pageable) {
    UserEntity userEntity = userEntityRepository.findByUserName(username).orElseThrow(() ->
            new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", username))
    );
    
    return postEntityRepository.findAllByUser(userEntity, pageable).map(Post::fromEntity);
  }
  
  @Transactional
  public void like(Integer postId, String userName) {
    //post exist
    PostEntity postEntity = postEntityRepository.findById(postId).orElseThrow(() ->
            new SnsApplicationException(ErrorCode.POST_NOT_FOUND, String.format("%s not founded", postId))
    );
    UserEntity userEntity = userEntityRepository.findByUserName(userName).orElseThrow(() ->
            new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", userName))
    );
    
    // check liked -> throw
    likeEntityRepository.findByUserAndPost(userEntity, postEntity).ifPresent(it -> {
      throw new SnsApplicationException(ErrorCode.ALREADY_LIKED, String.format("userName %s already like post %d", userName, postId));
    });
    
    likeEntityRepository.save(LikeEntity.of(userEntity, postEntity));
  }
  
  
  public int likeCount(Integer postId) {
    //post exist
    PostEntity postEntity = postEntityRepository.findById(postId).orElseThrow(() ->
            new SnsApplicationException(ErrorCode.POST_NOT_FOUND, String.format("%s not founded", postId))
    );
    
    // count like
    // List<LikeEntity> likeEntityList = likeEntityRepository.findByPost(postEntity);
    // return likeEntityList.size();
    return likeEntityRepository.countByPost(postEntity);
  }
}
