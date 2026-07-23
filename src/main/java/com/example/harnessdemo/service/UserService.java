package com.example.harnessdemo.service;

import com.example.harnessdemo.dto.CreateUserRequest;
import com.example.harnessdemo.dto.UserResponse;
import com.example.harnessdemo.exception.ResourceNotFoundException;
import com.example.harnessdemo.model.User;
import com.example.harnessdemo.repository.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户业务服务.
 *
 * <p>事务约定（见 {@code docs/harness-standards.md §1.6}）：
 * <ul>
 *   <li>写操作（create/update/delete）必须 {@code @Transactional}（默认可写）</li>
 *   <li>只读操作标注 {@code @Transactional(readOnly = true)}，让 JPA/MyBatis 可走读优化路径</li>
 * </ul>
 *
 * @author xingyun0812
 */
@Service
public class UserService {

  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Transactional(rollbackFor = Exception.class)
  public UserResponse create(CreateUserRequest request) {
    User user = User.builder()
        .username(request.username())
        .email(request.email())
        .phone(request.phone())
        .build();
    userRepository.insert(user);
    return UserResponse.from(user);
  }

  @Transactional(readOnly = true)
  public List<UserResponse> listAll() {
    return userRepository.selectList(null)
        .stream()
        .map(UserResponse::from)
        .toList();
  }

  @Transactional(readOnly = true)
  public UserResponse getById(Long id) {
    User user = userRepository.selectById(id);
    if (user == null) {
      throw new ResourceNotFoundException("User", id);
    }
    return UserResponse.from(user);
  }
}
