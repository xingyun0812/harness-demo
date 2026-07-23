package com.example.harnessdemo.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.harnessdemo.model.User;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

/**
 * UserRepository 集成测试.
 *
 * <p>注：{@code @Transactional} 保证每个测试方法结束后回滚，不污染其他测试。
 * 但 H2 内存库按名字共享（{@code jdbc:h2:mem:harness-demo}），跨 Spring 上下文的测试
 * （如 {@code HarnessDemoIntegrationTest} 通过 HTTP 提交的用户）可能残留数据。
 * 因此 {@code selectAll_returnsAllUsers} 先清空表再插入，不依赖空库。
 */
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
    // Clear any pre-existing rows (H2 in-memory DB is shared by name across Spring contexts;
    // @DirtiesContext on HarnessDemoIntegrationTest destroys its context but the named H2
    // instance may persist if a connection survives context recreation). With @Transactional
    // on the test class, this delete rolls back after the test — no side effects.
    userRepository.delete(new QueryWrapper<>());

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
