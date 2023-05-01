CREATE TABLE books (
    id INTEGER AUTO_INCREMENT NOT NULL,
    author VARCHAR(64) NOT NULL,
    available_copies INTEGER NOT NULL,
    name VARCHAR(64) NOT NULL,
    num_of_copies INTEGER NOT NULL,
    publication_year YEAR NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE subscriptions (
    id INTEGER NOT NULL AUTO_INCREMENT,
    approved BIT NOT NULL,
    period INTEGER NOT NULL,
    start_date datetime(6) NOT NULL,
    book_id INTEGER NOT NULL,
    user_id INTEGER NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE users (
    id INTEGER NOT NULL AUTO_INCREMENT,
    email VARCHAR(255) NOT NULL,
    fine INTEGER NOT NULL,
    name VARCHAR(64) NOT NULL,
    password VARCHAR(45) NOT NULL,
    role ENUM('ADMIN', 'LIBRARIAN', 'READER'),
    PRIMARY KEY (id)
);