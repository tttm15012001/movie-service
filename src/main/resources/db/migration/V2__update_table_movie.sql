-- ================================================
-- v2__update_movie_model.sql
-- Update schema for MovieModel (based on new entity)
-- ================================================

-- Drop obsolete columns if they exist
ALTER TABLE movie
DROP COLUMN manufacturing_date,
    DROP COLUMN rate_score,
    DROP COLUMN review_quantity,
    DROP COLUMN duration,
    DROP COLUMN episodes;

-- Add new columns if they don't exist
ALTER TABLE movie
    ADD COLUMN tmdb_id INT,
    ADD COLUMN for_adult BOOLEAN,
    ADD COLUMN number_of_episodes INT,
    ADD COLUMN vote_average DOUBLE,
    ADD COLUMN vote_count INT,
    ADD COLUMN popularity DOUBLE,
    ADD COLUMN poster_path VARCHAR(500),
    ADD COLUMN backdrop_path VARCHAR(500),
    ADD COLUMN original_language VARCHAR(50);
