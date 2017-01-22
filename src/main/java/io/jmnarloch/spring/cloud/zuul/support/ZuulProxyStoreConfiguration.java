/**
 * Copyright (c) 2015 the original author or authors
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jmnarloch.spring.cloud.zuul.support;

import io.jmnarloch.spring.cloud.zuul.route.StoreRefreshableRouteLocator;
import io.jmnarloch.spring.cloud.zuul.store.ZuulRouteStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.zuul.ZuulProxyConfiguration;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.cloud.netflix.zuul.filters.discovery.DiscoveryClientRouteLocator;
import org.springframework.context.annotation.Configuration;

/**
 * Registers a {@link org.springframework.cloud.netflix.zuul.filters.RouteLocator} that is being populated through
 * external store.
 *
 * @author Jakub Narloch
 */
@Configuration
public class ZuulProxyStoreConfiguration extends ZuulProxyConfiguration {

    @Autowired
    private ZuulRouteStore zuulRouteStore;

    @Autowired
    private DiscoveryClient discovery;

    @Autowired
    private ZuulProperties zuulProperties;

    @Autowired
    private ServerProperties server;

    @Override
    public DiscoveryClientRouteLocator routeLocator() {
        return new StoreRefreshableRouteLocator(server.getServletPath(), discovery, zuulProperties, zuulRouteStore);
    }
}
