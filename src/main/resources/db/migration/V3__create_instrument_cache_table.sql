-- Create instrument_cache table for caching instrument details
CREATE TABLE instrument_cache (
    instrument_token BIGINT PRIMARY KEY,
    trading_symbol VARCHAR(100) NOT NULL,
    name VARCHAR(100),
    instrument_type VARCHAR(50),
    segment VARCHAR(50),
    exchange VARCHAR(50)
);

