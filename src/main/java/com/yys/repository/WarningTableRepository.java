package com.yys.repository;

import com.yys.entity.WarningTable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface WarningTableRepository extends ElasticsearchRepository<WarningTable, String> {

}

