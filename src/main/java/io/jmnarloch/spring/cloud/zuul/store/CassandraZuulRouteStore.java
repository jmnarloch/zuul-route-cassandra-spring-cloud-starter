/**
 * Copyright (c) 2015 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jmnarloch.spring.cloud.zuul.store;

import com.datastax.driver.core.Row;
import com.datastax.driver.core.exceptions.DriverException;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import org.springframework.cassandra.core.RowMapper;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.util.Assert;

import java.util.List;

/**
 * A basic Cassandra Zuul route storage. The implementation simply retrieves all entries from specific table.
 *
 * The table must confront fallowing schema:
 *
 * <pre>
 * <code>CREATE TABLE zuul_routes (
 *  id text,
 *  path text,
 *  service_id text,
 *  url text,
 *  strip_prefix boolean,
 *  retryable boolean,
 *  PRIMARY KEY(path)
 * );
 * </code>
 * </pre>
 *
 * @author Jakub Narloch
 */
public class CassandraZuulRouteStore implements ZuulRouteStore {

    /**
     * The shared instance of row mapper.
     */
    private static final ZuulRouteRowMapper ZUUL_ROUTE_MAPPER = new ZuulRouteRowMapper();

    /**
     * The default table name.
     */
    private static final String DEFAULT_TABLE_NAME = "zuul_routes";

    /**
     * Casandra template.
     */
    private final CassandraOperations cassandraOperations;

    /**
     * The optional keyspace.
     */
    private final String keyspace;

    /**
     * The table name.
     */
    private final String table;

    /**
     * Creates new instance of {@link CassandraZuulRouteStore}.
     *
     * @param cassandraOperations the cassandra template
     */
    public CassandraZuulRouteStore(CassandraOperations cassandraOperations) {
        this(cassandraOperations, null, DEFAULT_TABLE_NAME);
    }

    /**
     * Creates new instance of {@link CassandraZuulRouteStore}.
     *
     * @param cassandraOperations the cassandra template
     * @param keyspace            the optional keyspace
     * @param table               the table name
     * @throws IllegalArgumentException if {@code keyspace} is {@code null}
     *                                  or {@code table} is {@code null} or empty
     */
    public CassandraZuulRouteStore(CassandraOperations cassandraOperations, String keyspace, String table) {
        Assert.notNull(cassandraOperations, "Parameter 'cassandraOperations' can not be null.");
        Assert.hasLength(table, "Parameter 'table' can not be empty.");
        this.cassandraOperations = cassandraOperations;
        this.keyspace = keyspace;
        this.table = table;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ZuulProperties.ZuulRoute> findAll() {

        final Select query = QueryBuilder.select().from(keyspace, table);
        return cassandraOperations.query(query, ZUUL_ROUTE_MAPPER);
    }

    /**
     * Cassandra's {@link ZuulProperties.ZuulRoute} raw mapper.
     *
     * @author Jakub Narloch
     */
    private static class ZuulRouteRowMapper implements RowMapper<ZuulProperties.ZuulRoute> {

        /**
         * {@inheritDoc}
         */
        @Override
        public ZuulProperties.ZuulRoute mapRow(Row row, int rowNum) throws DriverException {

            return new ZuulProperties.ZuulRoute(
                    row.getString("id"),
                    row.getString("path"),
                    row.getString("service_id"),
                    row.getString("url"),
                    row.getBool("strip_prefix"),
                    row.getBool("retryable"),
                    row.getSet("sensitive_headers", String.class)
            );
        }
    }
}
