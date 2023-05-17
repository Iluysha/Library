CREATE TABLE books (
                       id INTEGER AUTO_INCREMENT NOT NULL,
                       title VARCHAR(64) NOT NULL,
                       author VARCHAR(64) NOT NULL,
                       publication_year INTEGER NOT NULL,
                       num_of_copies INTEGER NOT NULL,
                       available_copies INTEGER NOT NULL,
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

CREATE TABLE subscriptions (
                               id INTEGER NOT NULL AUTO_INCREMENT,
                               book_id INTEGER NOT NULL,
                               user_id INTEGER NOT NULL,
                               approved BIT NOT NULL,
                               start_date datetime(6),
                               period INTEGER,
                               fine LONG,
                               PRIMARY KEY (id),
                               FOREIGN KEY (book_id) REFERENCES books(id),
                               FOREIGN KEY (user_id) REFERENCES users(id)
);