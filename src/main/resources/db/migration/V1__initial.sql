CREATE TABLE books (
    id INTEGER AUTO_INCREMENT NOT NULL,
    title VARCHAR(64) NOT NULL,
    author VARCHAR(64) NOT NULL,
    publication_year INTEGER NOT NULL,
    num_of_copies INTEGER NOT NULL,
    available_copies INTEGER NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE subscriptions (
    id INTEGER NOT NULL AUTO_INCREMENT,
    book_id INTEGER NOT NULL,
    user_id INTEGER NOT NULL,
    approved BIT NOT NULL,
    start_date datetime(6),
    period INTEGER,
    fine LONG,
    PRIMARY KEY (id)
);

CREATE TABLE users (
    id INTEGER NOT NULL AUTO_INCREMENT,
    name VARCHAR(64) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(72) NOT NULL,
    role ENUM('ADMIN', 'LIBRARIAN', 'READER', 'USER'),
    fine INTEGER NOT NULL,
    blocked BOOLEAN NOT NULL,
    PRIMARY KEY (id)
);

INSERT INTO users VALUES (1, 'Illia Mykhailichenko', 'admin@gmail.com',
                          '$2a$10$dWS/En0DU8bk38TCl48bfu9uLhv9hMV2tUcspE50.wCXNn7I9hkQ6', 'ADMIN', 0, false);

INSERT INTO users VALUES (2, 'Garcia Lorka', 'librarian@gmail.com',
                           '$2a$10$dWS/En0DU8bk38TCl48bfu9uLhv9hMV2tUcspE50.wCXNn7I9hkQ6', 'LIBRARIAN',  0, false);

INSERT INTO users VALUES (3, 'Franz Kafka', 'reader@gmail.com',
                           '$2a$10$dWS/En0DU8bk38TCl48bfu9uLhv9hMV2tUcspE50.wCXNn7I9hkQ6', 'READER', 0, false);

INSERT INTO books VALUES ( 1, 'Three Comrades', 'Erich Maria Remarque', 1936, 3,  1);
INSERT INTO books VALUES ( 2, 'Harry Potter 1', 'J. K. Rowling', 1997, 5, 2);
INSERT INTO books VALUES ( 3, 'Harry Potter 2', 'J. K. Rowling', 1998, 3, 0);
INSERT INTO books VALUES ( 4, 'Harry Potter 3', 'J. K. Rowling', 1999, 4, 4);
INSERT INTO books VALUES ( 5, 'Harry Potter 4', 'J. K. Rowling', 2000, 2, 1);
INSERT INTO books VALUES ( 6, 'Harry Potter 5', 'J. K. Rowling', 2001, 2, 2);
INSERT INTO books VALUES ( 7, 'Harry Potter 6', 'J. K. Rowling', 2002, 3, 2);
INSERT INTO books VALUES ( 8, 'Harry Potter 7', 'J. K. Rowling', 2003, 3, 1);

INSERT INTO books VALUES ( 9, 'Harry Potter 8', 'J. K. Rowling', 2003, 3, 1);
INSERT INTO books VALUES ( 10, 'Harry Potter 9', 'J. K. Rowling', 2003, 3, 1);
INSERT INTO books VALUES ( 11, 'Harry Potter 10', 'J. K. Rowling', 2003, 3, 1);
INSERT INTO books VALUES ( 12, 'Harry Potter 11', 'J. K. Rowling', 2003, 3, 1);
INSERT INTO books VALUES ( 13, 'Harry Potter 12', 'J. K. Rowling', 2003, 3, 1);
INSERT INTO books VALUES ( 14, 'Harry Potter 13', 'J. K. Rowling', 2003, 3, 1);
INSERT INTO books VALUES ( 15, 'Harry Potter 14', 'J. K. Rowling', 2003, 3, 1);
INSERT INTO books VALUES ( 16, 'Harry Potter 15', 'J. K. Rowling', 2003, 3, 1);
INSERT INTO books VALUES ( 17, 'Harry Potter 16', 'J. K. Rowling', 2003, 3, 1);
INSERT INTO books VALUES ( 18, 'Harry Potter 17', 'J. K. Rowling', 2003, 3, 1);
INSERT INTO books VALUES ( 19, 'Harry Potter 18', 'J. K. Rowling', 2003, 3, 1);
INSERT INTO books VALUES ( 20, 'Harry Potter 19', 'J. K. Rowling', 2003, 3, 1);