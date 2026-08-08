CREATE TABLE IF NOT EXISTS calculation_history (
    id          BIGINT       AUTO_INCREMENT PRIMARY KEY,
    algorithm   VARCHAR(16)  NOT NULL,
    input_json  CLOB         NOT NULL,
    result_json CLOB         NOT NULL,
    comment     VARCHAR(500),
    created_at  TIMESTAMP    NOT NULL
);
