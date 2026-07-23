package com.example.harnessdemo.model;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 系统用户实体.
 *
 * @author <your-name>
 */
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@TableName("sys_user")
public class User {
  private Long id;
  private String username;
  private String email;
  private String phone;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
