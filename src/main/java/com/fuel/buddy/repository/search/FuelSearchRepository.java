package com.fuel.buddy.repository.search;

import com.fuel.buddy.domain.Fuel;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Fuel entity.
 */
public interface FuelSearchRepository extends ElasticsearchRepository<Fuel, Long> {
}
