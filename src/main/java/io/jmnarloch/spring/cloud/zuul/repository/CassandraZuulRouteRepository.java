/**
 * Copyright (c) 2015 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jmnarloch.spring.cloud.zuul.repository;

import com.datastax.driver.core.Row;
import com.datastax.driver.core.exceptions.DriverException;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import org.springframework.cassandra.core.RowMapper;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.data.cassandra.core.CassandraOperations;

import java.util.List;

/**
 *
 */
public class CassandraZuulRouteRepository implements ZuulRouteRepository {

    private static final ZuulRouteRowMapper ZUUL_ROUTE_MAPPER = new ZuulRouteRowMapper();

    private static final String DEFAULT_TABLE_NAME = "zuul_routes";

    private final CassandraOperations cassandraOperations;

    private final String keyspace;

    private final String table;

    public CassandraZuulRouteRepository(CassandraOperations cassandraOperations) {
        this(cassandraOperations, null, DEFAULT_TABLE_NAME);
    }

    public CassandraZuulRouteRepository(CassandraOperations cassandraOperations, String keyspace, String table) {
        this.cassandraOperations = cassandraOperations;
        this.keyspace = keyspace;
        this.table = table;
    }

    @Override
    public List<ZuulProperties.ZuulRoute> findAll() {

        final Select query = QueryBuilder.select().from(keyspace, table);
        return cassandraOperations.query(query, ZUUL_ROUTE_MAPPER);
    }

    private static class ZuulRouteRowMapper implements RowMapper<ZuulProperties.ZuulRoute> {

        @Override
        public ZuulProperties.ZuulRoute mapRow(Row row, int rowNum) throws DriverException {

            return new ZuulProperties.ZuulRoute(
                    row.getString("id"),
                    row.getString("path"),
                    row.getString("service_id"),
                    row.getString("url"),
                    row.getBool("strip_prefix"),
                    row.getBool("retryable")
            );
        }
    }
}
