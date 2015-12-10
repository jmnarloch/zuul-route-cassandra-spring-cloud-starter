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
package io.jmnarloch.spring.cloud.zuul.support;

import io.jmnarloch.spring.cloud.zuul.store.CassandraZuulRouteStore;
import io.jmnarloch.spring.cloud.zuul.store.ZuulRouteStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.core.CassandraOperations;

/**
 * Configures the Cassandra storage for Zuul routes.
 *
 * @author Jakub Narloch
 */
@Configuration
@ConditionalOnProperty(value = "zuul.store.cassandra.enabled", matchIfMissing = false)
public class CassandraZuulStoreAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ZuulRouteStore cassandraZuulRouteStore(CassandraOperations cassandraOperations) {

        return new CassandraZuulRouteStore(cassandraOperations);
    }
}
