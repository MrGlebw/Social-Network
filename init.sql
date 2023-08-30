CREATE TABLE IF NOT EXISTS users(
                      id SERIAL PRIMARY KEY,
                      username VARCHAR(64) UNIQUE NOT NULL,
                      first_name VARCHAR(64) NOT NULL,
                      last_name VARCHAR(64) NOT NULL,
                      birthdate DATE NOT NULL,
                      email VARCHAR(50) UNIQUE NOT NULL,
                      password VARCHAR(100) NOT NULL,
                      is_active BOOLEAN NOT NULL DEFAULT true,
                      is_deleted BOOLEAN NOT NULL DEFAULT false,
                      is_private BOOLEAN NOT NULL DEFAULT false,
                      created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                      updated TIMESTAMP NOT NULL DEFAULT NULL,
                      roles VARCHAR(20)[] NOT NULL DEFAULT '{}',
                      enabled BOOLEAN NOT NULL DEFAULT true,
                      posts_count INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS posts(
                      id SERIAL PRIMARY KEY,
                      title VARCHAR(150),
                      content Text NOT NULL,
                      author_name VARCHAR(64) NOT NULL REFERENCES users(username),
                      created_date TIMESTAMP DEFAULT NOW(),
                      last_modified_date TIMESTAMP,
                      status VARCHAR(20),
                      post_id_for_user integer DEFAULT 1,
                      published_date TIMESTAMP,
                      disapproved_date TIMESTAMP,
                      comments_count integer DEFAULT 0
);

CREATE TABLE IF NOT EXISTS comments (
                          id SERIAL PRIMARY KEY,
                          content TEXT NOT NULL,
                          post_id integer NOT NULL REFERENCES posts(id),
                          author_name VARCHAR(64) NOT NULL REFERENCES users(username),
                          created_date TIMESTAMP DEFAULT NOW(),
                          last_modified_date TIMESTAMP,
                          last_modified_by VARCHAR(64) NOT NULL REFERENCES users(username),
                          comment_id_for_post integer DEFAULT 1
);

CREATE TABLE IF NOT EXISTS messages (
                        id BIGSERIAL PRIMARY KEY,
                        sender VARCHAR(64) NOT NULL REFERENCES users(username),
                        recipient VARCHAR(64) NOT NULL REFERENCES users(username),
                        content TEXT NOT NULL,
                        sent_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE  IF NOT EXISTS subscriptions(
                     id BIGSERIAL PRIMARY KEY,
                     follower VARCHAR(64) NOT NULL REFERENCES users(username),
                     followed VARCHAR(64) NOT NULL REFERENCES users(username),
                     status VARCHAR(50),
                     request_date TIMESTAMP DEFAULT NOW(),
                     accept_date TIMESTAMP,
                     reject_date TIMESTAMP
);

CREATE TABLE IF NOT EXISTS bans(
                     id BIGSERIAL PRIMARY KEY,
                     from_user VARCHAR(64) NOT NULL REFERENCES users(username),
                     to_user VARCHAR(64) NOT NULL REFERENCES users(username),
                     banned_at TIMESTAMP DEFAULT NOW()
);