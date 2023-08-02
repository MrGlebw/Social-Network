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