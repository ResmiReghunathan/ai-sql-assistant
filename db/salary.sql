CREATE TABLE IF NOT EXISTS salary (
    id SERIAL PRIMARY KEY,
    salary_month DATE NOT NULL UNIQUE,          -- first day of the month, e.g. 2026-03-01
    salary NUMERIC(12,2) NOT NULL,
    gst_percentage NUMERIC(5,2) NOT NULL -- 18 means 18%
);

-- Let the AI's read-only user see this table (and any future ones)
GRANT SELECT ON salary TO readonly_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT ON TABLES TO readonly_user;