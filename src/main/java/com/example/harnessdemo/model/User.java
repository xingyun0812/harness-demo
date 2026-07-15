package com.example.harnessdemo.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class User {
  private Long id;
  private String username;
  private String email;
  private String phone;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
