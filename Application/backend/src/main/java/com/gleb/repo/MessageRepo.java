package com.gleb.repo;

import com.gleb.data.TextMessage;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface MessageRepo extends R2dbcRepository<TextMessage, Integer> {

}
