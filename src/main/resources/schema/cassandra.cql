CREATE TABLE zuul_routes (
    id text,
    path text,
    service_id text,
    url text,
    strip_prefix boolean,
    retryable boolean,
    PRIMARY KEY(id)
);