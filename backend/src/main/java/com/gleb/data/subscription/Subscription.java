package com.gleb.data.subscription;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("subscriptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Subscription {

    @Column("id")
    private Integer id;

    @Column("follower")
    private String follower;

    @Column("followed")
    private String followed;

    @Column("status")
    @Builder.Default
    private Status status = Status.REQUESTED;

    @Column("request_date")
    private LocalDateTime requestDate;

    @Column("accept_date")
    private LocalDateTime acceptDate;

    @Column("reject_date")
    private LocalDateTime rejectDate;
}
