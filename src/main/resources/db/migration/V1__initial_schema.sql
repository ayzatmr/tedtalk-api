-- TED Talks table
CREATE TABLE ted_talks (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(500) NOT NULL,
    author VARCHAR(255) NOT NULL,
    year_value INT NOT NULL,
    month_value INT NOT NULL,
    views BIGINT NOT NULL,
    likes BIGINT NOT NULL,
    link VARCHAR(500) NOT NULL
);

CREATE INDEX idx_author ON ted_talks(author);
CREATE INDEX idx_year ON ted_talks(year_value);
CREATE INDEX idx_views ON ted_talks(views);
CREATE INDEX idx_likes ON ted_talks(likes);

-- Import Status table
CREATE TABLE import_status (
    import_id VARCHAR(255) PRIMARY KEY,
    status VARCHAR(50) NOT NULL,
    started_at TIMESTAMP NOT NULL,
    completed_at TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0
);
