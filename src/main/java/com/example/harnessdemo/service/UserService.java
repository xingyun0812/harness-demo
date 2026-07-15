package com.example.harnessdemo.service;

import com.example.harnessdemo.dto.CreateUserRequest;
import com.example.harnessdemo.dto.UserResponse;
import com.example.harnessdemo.model.User;
import com.example.harnessdemo.repository.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public UserResponse create(CreateUserRequest request) {
    User user = User.builder()
        .username(request.username())
        .email(request.email())
        .phone(request.phone())
        .build();
    userRepository.insert(user);
    return UserResponse.from(user);
  }

  public List<UserResponse> listAll() {
    return userRepository.selectList(null)
        .stream()
        .map(UserResponse::from)
        .toList();
  }

  public UserResponse getById(Long id) {
    User user = userRepository.selectById(id);
    if (user == null) {
      throw new RuntimeException("User not found: " + id);
    }
    return UserResponse.from(user);
  }
}
