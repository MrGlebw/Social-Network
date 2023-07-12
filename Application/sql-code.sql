#Reset the queue ID of the primary key

ALTER SEQUENCE users_id_seq RESTART WITH 1;
UPDATE users SET id=nextval('users_id_seq');
