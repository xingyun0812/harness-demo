package com.example.harnessdemo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.example.harnessdemo.dto.CreateUserRequest;
import com.example.harnessdemo.dto.UserResponse;
import com.example.harnessdemo.model.User;
import com.example.harnessdemo.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private UserService userService;

  @Test
  void createShouldInsertAndReturnResponse() {
    CreateUserRequest request = new CreateUserRequest("alice", "alice@example.com", "13800138000");

    ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
    when(userRepository.insert(captor.capture())).thenAnswer(invocation -> {
      User user = invocation.getArgument(0);
      user.setId(1L);
      user.setCreatedAt(LocalDateTime.now());
      user.setUpdatedAt(LocalDateTime.now());
      return 1;
    });

    UserResponse response = userService.create(request);

    assertThat(response.id()).isEqualTo(1L);
    assertThat(response.username()).isEqualTo("alice");
    assertThat(response.email()).isEqualTo("alice@example.com");

    User captured = captor.getValue();
    assertThat(captured.getUsername()).isEqualTo("alice");
    assertThat(captured.getEmail()).isEqualTo("alice@example.com");
    assertThat(captured.getPhone()).isEqualTo("13800138000");
  }

  @Test
  void listAllShouldReturnAllUsers() {
    User user = new User();
    user.setId(1L);
    user.setUsername("alice");
    user.setEmail("alice@example.com");
    user.setCreatedAt(LocalDateTime.now());

    when(userRepository.selectList(null)).thenReturn(List.of(user));

    List<UserResponse> responses = userService.listAll();

    assertThat(responses).hasSize(1);
    assertThat(responses.get(0).username()).isEqualTo("alice");
  }

  @Test
  void getByIdShouldReturnUser() {
    User user = new User();
    user.setId(1L);
    user.setUsername("alice");
    user.setEmail("alice@example.com");
    user.setCreatedAt(LocalDateTime.now());

    when(userRepository.selectById(1L)).thenReturn(user);

    UserResponse response = userService.getById(1L);

    assertThat(response.username()).isEqualTo("alice");
  }

  @Test
  void getByIdShouldThrowWhenNotFound() {
    when(userRepository.selectById(99L)).thenReturn(null);

    assertThatThrownBy(() -> userService.getById(99L))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("User not found");
  }
}
