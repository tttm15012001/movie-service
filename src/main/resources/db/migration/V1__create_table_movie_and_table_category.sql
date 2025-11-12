-- -----------------------------
-- Table: category_model
-- -----------------------------
CREATE TABLE category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    code VARCHAR(100),
    score FLOAT
);

-- -----------------------------
-- Table: movie_model
-- -----------------------------
CREATE TABLE movie (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    search_title VARCHAR(100),
    release_year INT,
    metadata_id BIGINT,
    vote_average FLOAT,
    created_date DATETIME NOT NULL,
    last_modified_date DATETIME
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