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
package io.jmnarloch.spring.cloud.zuul;

import com.datastax.driver.core.Cluster;
import io.jmnarloch.spring.cloud.zuul.api.EnableZuulProxyStore;
import io.jmnarloch.spring.cloud.zuul.route.StoreRefreshableRouteLocator;
import org.cassandraunit.spring.CassandraDataSet;
import org.cassandraunit.spring.CassandraUnitDependencyInjectionTestExecutionListener;
import org.cassandraunit.spring.EmbeddedCassandra;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.cloud.netflix.zuul.filters.Route;
import org.springframework.context.annotation.Bean;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.net.InetAddress;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.context.TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS;

/**
 * Tests this component.
 *
 * @author Jakub Narloch
 */
@RunWith(SpringJUnit4ClassRunner.class)
@EmbeddedCassandra
@CassandraDataSet(value = { "schema/cassandra.cql" })
@WebIntegrationTest({"zuul.store.cassandra.enabled=true"})
@TestExecutionListeners(
        value = { CassandraUnitDependencyInjectionTestExecutionListener.class},
        mergeMode = MERGE_WITH_DEFAULTS)
@SpringApplicationConfiguration(classes = CassandraZuulProxyStoreTest.Application.class)
public class CassandraZuulProxyStoreTest {

    @Autowired
    private StoreRefreshableRouteLocator refreshableRouteLocator;

    @Test
    public void shouldNotFindMatchingRoute() {

        // when
        Route route = refreshableRouteLocator.getMatchingRoute("/web/**");

        // then
        assertNull(route);
    }

    @Test
    public void shouldFindMatchingRoute() {

        // when
        Route route = refreshableRouteLocator.getMatchingRoute("/uaa/**");

        // then
        assertNotNull(route);
        assertEquals(1, route.getSensitiveHeaders().size());
        assertTrue(route.getSensitiveHeaders().contains("authorization"));
    }

    @EnableZuulProxyStore
    @SpringBootApplication
    public static class Application {

        @Bean
        public Cluster cluster() {
            return Cluster.builder()
                    .addContactPoints(InetAddress.getLoopbackAddress())
                    .withPort(9142)
                    .build();
        }

        @Bean
        public CassandraOperations cassandraTemplate(Cluster cluster) {
            return new CassandraTemplate(cluster.connect("zuul"));
        }
    }
}
