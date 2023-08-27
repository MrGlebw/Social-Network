/* create table USERS
   CREATE TABLE users(
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
*/





/* Reset the query ID of the primary key */

/*ALTER SEQUENCE users_id_seq RESTART WITH 1;
UPDATE users SET id=nextval('users_id_seq');


DELETE FROM comments;
ALTER SEQUENCE comments_id_seq RESTART WITH 1;
UPDATE comments SET id=nextval('comments_id_seq');

DELETE FROM posts;
ALTER SEQUENCE posts_id_seq RESTART WITH 1;
UPDATE posts SET id=nextval('posts_id_seq');

DELETE FROM users

ALTER SEQUENCE users_id_seq RESTART WITH 1;
UPDATE users SET id=nextval('users_id_seq');/*