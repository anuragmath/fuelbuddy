package com.fuel.buddy.repository.search;

import com.fuel.buddy.domain.Assets;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Assets entity.
 */
public interface AssetsSearchRepository extends ElasticsearchRepository<Assets, Long> {
}
