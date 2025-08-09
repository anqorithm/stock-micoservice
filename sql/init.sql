-- Create stocks table
CREATE TABLE IF NOT EXISTS stocks (
    id BIGSERIAL PRIMARY KEY,
    symbol VARCHAR(10) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    current_price DECIMAL(12, 4) NOT NULL,
    market_cap BIGINT,
    sector VARCHAR(100),
    industry VARCHAR(100),
    dividend_yield DECIMAL(5, 4),
    pe_ratio DECIMAL(8, 2),
    eps DECIMAL(8, 2),
    fifty_two_week_high DECIMAL(12, 4),
    fifty_two_week_low DECIMAL(12, 4),
    volume BIGINT DEFAULT 0,
    average_volume BIGINT DEFAULT 0,
    beta DECIMAL(6, 4),
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_stocks_symbol ON stocks(symbol);
CREATE INDEX IF NOT EXISTS idx_stocks_sector ON stocks(sector);
CREATE INDEX IF NOT EXISTS idx_stocks_market_cap ON stocks(market_cap);
CREATE INDEX IF NOT EXISTS idx_stocks_current_price ON stocks(current_price);

-- Insert some sample data
INSERT INTO stocks (symbol, name, current_price, market_cap, sector, industry, dividend_yield, pe_ratio, eps, fifty_two_week_high, fifty_two_week_low, volume, average_volume, beta, version) VALUES
('AAPL', 'Apple Inc.', 175.43, 2750000000000, 'Technology', 'Consumer Electronics', 0.0050, 28.50, 6.15, 182.94, 124.17, 85000000, 75000000, 1.20, 0),
('GOOGL', 'Alphabet Inc.', 138.21, 1650000000000, 'Technology', 'Internet Content & Information', 0.0000, 25.30, 5.46, 151.55, 83.34, 42000000, 35000000, 1.05, 0),
('MSFT', 'Microsoft Corporation', 378.85, 2820000000000, 'Technology', 'Software-Infrastructure', 0.0072, 32.10, 11.80, 384.30, 213.43, 38000000, 33000000, 0.90, 0),
('TSLA', 'Tesla, Inc.', 248.42, 790000000000, 'Consumer Cyclical', 'Auto Manufacturers', 0.0000, 62.50, 3.98, 414.50, 138.80, 95000000, 85000000, 2.09, 0),
('NVDA', 'NVIDIA Corporation', 875.28, 2150000000000, 'Technology', 'Semiconductors', 0.0035, 65.80, 13.31, 974.00, 180.96, 55000000, 48000000, 1.68, 0)
ON CONFLICT (symbol) DO NOTHING;