CREATE TABLE books (
    id INTEGER AUTO_INCREMENT NOT NULL,
    author VARCHAR(64) NOT NULL,
    available_copies INTEGER NOT NULL,
    title VARCHAR(64) NOT NULL,
    num_of_copies INTEGER NOT NULL,
    publication_year INTEGER NOT NULL,
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
    password VARCHAR(72) NOT NULL,
    role ENUM('ADMIN', 'LIBRARIAN', 'READER'),
    PRIMARY KEY (id)
);

INSERT INTO users VALUES ( 1, 'admin@gmail.com', 0, 'Illia Mykhailichenko',
                          '$2a$10$dWS/En0DU8bk38TCl48bfu9uLhv9hMV2tUcspE50.wCXNn7I9hkQ6', 'ADMIN');

INSERT INTO users VALUES ( 2, 'librarian@gmail.com', 0, 'Garcia Lorka',
                           '$2a$10$dWS/En0DU8bk38TCl48bfu9uLhv9hMV2tUcspE50.wCXNn7I9hkQ6', 'LIBRARIAN');

INSERT INTO users VALUES ( 3, 'reader@gmail.com', 0, 'Franz Kafka',
                           '$2a$10$dWS/En0DU8bk38TCl48bfu9uLhv9hMV2tUcspE50.wCXNn7I9hkQ6', 'READER');

INSERT INTO books VALUES ( 1, 'Erich Maria Remarque', 1, 'Three Comrades', 3, 1936);
INSERT INTO books VALUES ( 2, 'J. K. Rowling', 2, 'Harry Potter 1', 5, 1997);
INSERT INTO books VALUES ( 3, 'J. K. Rowling', 0, 'Harry Potter 2', 5, 1998);
INSERT INTO books VALUES ( 4, 'J. K. Rowling', 1, 'Harry Potter 3', 5, 1999);
INSERT INTO books VALUES ( 5, 'J. K. Rowling', 3, 'Harry Potter 4', 5, 2000);
INSERT INTO books VALUES ( 6, 'J. K. Rowling', 4, 'Harry Potter 5', 5, 2001);
INSERT INTO books VALUES ( 7, 'J. K. Rowling', 1, 'Harry Potter 6', 5, 2001);
INSERT INTO books VALUES ( 8, 'J. K. Rowling', 2, 'Harry Potter 7', 5, 2003);
INSERT INTO books VALUES ( 9, 'J. K. Rowling', 2, 'Harry Potter 8', 5, 2003);
INSERT INTO books VALUES ( 10, 'J. K. Rowling', 2, 'Harry Potter 9', 5, 2003);
INSERT INTO books VALUES ( 11, 'J. K. Rowling', 2, 'Harry Potter 10', 5, 2003);
INSERT INTO books VALUES ( 12, 'J. K. Rowling', 2, 'Harry Potter 11', 5, 2003);
INSERT INTO books VALUES ( 13, 'J. K. Rowling', 2, 'Harry Potter 12', 5, 2003);
INSERT INTO books VALUES ( 14, 'J. K. Rowling', 2, 'Harry Potter 13', 5, 2003);
INSERT INTO books VALUES ( 15, 'J. K. Rowling', 2, 'Harry Potter 14', 5, 2003);
INSERT INTO books VALUES ( 16, 'J. K. Rowling', 2, 'Harry Potter 15', 5, 2003);
INSERT INTO books VALUES ( 17, 'J. K. Rowling', 2, 'Harry Potter 16', 5, 2003);
INSERT INTO books VALUES ( 18, 'J. K. Rowling', 2, 'Harry Potter 17', 5, 2003);
INSERT INTO books VALUES ( 19, 'J. K. Rowling', 2, 'Harry Potter 18', 5, 2003);
INSERT INTO books VALUES ( 20, 'J. K. Rowling', 2, 'Harry Potter 19', 5, 2003);
INSERT INTO books VALUES ( 21, 'J. K. Rowling', 2, 'Harry Potter 20', 5, 2003);
INSERT INTO books VALUES ( 22, 'J. K. Rowling', 2, 'Harry Potter 21', 5, 2003);
INSERT INTO books VALUES ( 23, 'J. K. Rowling', 2, 'Harry Potter 22', 5, 2003);
INSERT INTO books VALUES ( 24, 'J. K. Rowling', 2, 'Harry Potter 23', 5, 2003);