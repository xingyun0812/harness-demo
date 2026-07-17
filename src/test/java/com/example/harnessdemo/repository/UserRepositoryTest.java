package com.example.harnessdemo.repository;

import com.example.harnessdemo.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  @Test
  void insertAndSelect() {
    User user = User.builder()
        .username("testuser")
        .email("test@example.com")
        .phone("13800138000")
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();

    int inserted = userRepository.insert(user);
    assertThat(inserted).isEqualTo(1);
    assertThat(user.getId()).isNotNull();

    User fetched = userRepository.selectById(user.getId());
    assertThat(fetched).isNotNull();
    assertThat(fetched.getUsername()).isEqualTo("testuser");
    assertThat(fetched.getEmail()).isEqualTo("test@example.com");
    assertThat(fetched.getPhone()).isEqualTo("13800138000");
  }

  @Test
  void selectAll_returnsAllUsers() {
    userRepository.insert(User.builder().username("u1").createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build());
    userRepository.insert(User.builder().username("u2").createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build());

    List<User> users = userRepository.selectList(null);

    assertThat(users).hasSize(2);
  }

  @Test
  void selectById_notFound_returnsNull() {
    User fetched = userRepository.selectById(999L);

    assertThat(fetched).isNull();
  }
}
