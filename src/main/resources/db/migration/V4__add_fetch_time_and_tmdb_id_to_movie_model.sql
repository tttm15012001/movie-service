ALTER TABLE movie
    ADD COLUMN fetch_time BIGINT DEFAULT 0,
    ADD COLUMN tmdb_id BIGINT;

ALTER TABLE movie
    ADD CONSTRAINT uk_movie_tmdb_id UNIQUE (tmdb_id),
    ADD CONSTRAINT uk_movie_search_title UNIQUE (search_title);
