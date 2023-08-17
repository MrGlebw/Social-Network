package com.gleb.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Ban {
    @Id
    private Long id;

    private String fromUser;
    private String toUser;
    private LocalDateTime bannedAt;

}
