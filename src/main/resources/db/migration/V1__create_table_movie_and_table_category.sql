-- -----------------------------
-- Table: category_model
-- -----------------------------
CREATE TABLE category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    score FLOAT
);

-- -----------------------------
-- Table: movie_model
-- -----------------------------
CREATE TABLE movie (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_date DATETIME NOT NULL,
    last_modified_date DATETIME,
    title VARCHAR(255),
    original_title VARCHAR(255),
    description TEXT,
    manufacturing_date DATETIME,
    rate_score FLOAT,
    review_quantity INT,
    duration INT,
    country VARCHAR(255),
    director VARCHAR(255),
    casts TEXT,
    episodes INT,
    status VARCHAR(255),
    release_date DATETIME
);

-- -----------------------------
-- Table: movie_category_mapping
-- -----------------------------
CREATE TABLE movie_category_mapping (
    movie_id     BIGINT NOT NULL,
    category_id  BIGINT NOT NULL,
    CONSTRAINT pk_movie_category PRIMARY KEY (movie_id, category_id),
    CONSTRAINT fk_movie_category_movie FOREIGN KEY (movie_id)
        REFERENCES movie(id) ON DELETE CASCADE,
    CONSTRAINT fk_movie_category_category FOREIGN KEY (category_id)
        REFERENCES category(id) ON DELETE CASCADE
);

-- -----------------------------
-- Table: movie_genre_mapping (for @ElementCollection)
-- -----------------------------
CREATE TABLE movie_genre_mapping (
    movie_id BIGINT NOT NULL,
    genre VARCHAR(255) NOT NULL,
    CONSTRAINT fk_movie_genre_movie
        FOREIGN KEY (movie_id)
            REFERENCES movie(id)
            ON DELETE CASCADE
);