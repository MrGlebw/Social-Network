package com.gleb.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class TextMessage {

       @Id
        private Long id;
        private String sender;
        private String recipient;
        private String content;
        private LocalDateTime sentAt;

}
