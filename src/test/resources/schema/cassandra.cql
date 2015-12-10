DROP KEYSPACE IF EXISTS zuul;

CREATE KEYSPACE IF NOT EXISTS zuul WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 1 };

USE zuul;

CREATE TABLE zuul_routes (
    id text,
    path text,
    service_id text,
    url text,
    strip_prefix boolean,
    retryable boolean,
    PRIMARY KEY(id)
);

INSERT INTO zuul_routes(id, path, service_id) VALUES ('resource', '/api/**', 'rest-service');
INSERT INTO zuul_routes(id, path, service_id, strip_prefix) VALUES ('oauth2', '/uaa/**', 'oauth2-service', true);