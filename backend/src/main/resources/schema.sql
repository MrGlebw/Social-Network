CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    surname VARCHAR(50) NOT NULL,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(50) NOT NULL,
    password VARCHAR(50) NOT NULL,
    birth_date DATE NOT NULL,
    created OFFSETDATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated OFFSETDATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_deleted BOOLEAN,
    is_private BOOLEAN,
    is_banned BOOLEAN,
    is_active BOOLEAN,



);

CREATE TABLE IF NOT EXISTS posts (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    text VARCHAR(50) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id)
    );



);