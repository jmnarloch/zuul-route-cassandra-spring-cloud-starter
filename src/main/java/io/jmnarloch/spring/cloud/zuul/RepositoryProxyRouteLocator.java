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

import io.jmnarloch.spring.cloud.zuul.repository.ZuulRouteRepository;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.zuul.filters.ProxyRouteLocator;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 */
@SuppressWarnings("PMD.UselessOverridingMethod")
public class RepositoryProxyRouteLocator extends ProxyRouteLocator {

    private final ZuulRouteRepository repository;

    public RepositoryProxyRouteLocator(String servletPath, DiscoveryClient discovery, ZuulRouteRepository repository) {
        super(servletPath, discovery, null);
        this.repository = repository;
    }

    @Override
    public void addRoute(String path, String location) {
        super.addRoute(path, location);
    }

    @Override
    public void addRoute(ZuulProperties.ZuulRoute route) {
        super.addRoute(route);
    }

    @Override
    public Collection<String> getRoutePaths() {
        return super.getRoutePaths();
    }

    @Override
    public Map<String, String> getRoutes() {
        return super.getRoutes();
    }

    @Override
    public ProxyRouteSpec getMatchingRoute(String path) {
        return super.getMatchingRoute(path);
    }

    @Override
    public String getTargetPath(String matchingRoute, String requestURI) {
        return super.getTargetPath(matchingRoute, requestURI);
    }

    @Override
    protected LinkedHashMap<String, ZuulProperties.ZuulRoute> locateRoutes() {
        return super.locateRoutes();
    }
}
