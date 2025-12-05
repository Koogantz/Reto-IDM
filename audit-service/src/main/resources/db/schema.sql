CREATE TABLE IF NOT EXISTS audit_records (
    id UUID PRIMARY KEY,
    target_resource_id UUID NOT NULL,
    action VARCHAR(64) NOT NULL,
    payload CLOB NOT NULL,
    timestamp TIMESTAMP WITH TIME ZONE NOT NULL
);
