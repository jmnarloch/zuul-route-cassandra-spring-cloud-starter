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
package io.jmnarloch.spring.cloud.zuul.store;

import com.datastax.driver.core.Cluster;
import org.cassandraunit.spring.CassandraDataSet;
import org.cassandraunit.spring.CassandraUnitTestExecutionListener;
import org.cassandraunit.spring.EmbeddedCassandra;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.context.TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS;

/**
 * Tests the {@link CassandraZuulRouteStore} class.
 *
 * @author Jakub Narloch
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@TestExecutionListeners(
        value = { CassandraUnitTestExecutionListener.class },
        mergeMode = MERGE_WITH_DEFAULTS)
@CassandraDataSet(value = { "cassandra/data.cql" })
@EmbeddedCassandra
public class CassandraZuulRouteStoreTest {

    private CassandraZuulRouteStore instance;

    private CassandraTemplate cassandraTemplate;

    @Before
    public void setUp() throws Exception {

        final Cluster cluster = Cluster.builder()
                .addContactPoints("127.0.0.1")
                .withPort(9142)
                .build();
        cassandraTemplate = new CassandraTemplate(cluster.connect("zuul"));

        instance = new CassandraZuulRouteStore(cassandraTemplate);
    }

    @Test
    public void shouldRetrieveEmptyRouteList() {

        // given
        cassandraTemplate.truncate("zuul_routes");

        // when
        final List<ZuulProperties.ZuulRoute> routes = instance.findAll();

        // then
        assertTrue(routes.isEmpty());
    }

    @Test
    public void shouldRetrieveConfiguredRoutes() {

        // when
        final List<ZuulProperties.ZuulRoute> routes = instance.findAll();

        // then
        assertFalse(routes.isEmpty());
        assertEquals(2, routes.size());
        final Map<String, ZuulProperties.ZuulRoute> routeMap = asMap(routes);
        assertEquals("resource", routeMap.get("resource").getId());
        assertEquals("/api/**", routeMap.get("resource").getPath());
        assertEquals("rest-service", routeMap.get("resource").getServiceId());
    }

    private Map<String, ZuulProperties.ZuulRoute> asMap(List<ZuulProperties.ZuulRoute> routes) {
        final Map<String, ZuulProperties.ZuulRoute> map = new HashMap<String, ZuulProperties.ZuulRoute>();
        for(ZuulProperties.ZuulRoute route : routes) {
            map.put(route.getId(), route);
        }
        return map;
    }

    @Configuration
    public static class Config {

    }
}