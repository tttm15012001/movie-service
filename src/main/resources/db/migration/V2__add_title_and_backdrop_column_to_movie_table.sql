ALTER TABLE movie
    ADD COLUMN title VARCHAR(255) AFTER search_title,
    ADD COLUMN backdrop VARCHAR(255) AFTER title;