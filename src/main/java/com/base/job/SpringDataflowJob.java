package com.base.job;

import java.util.ArrayList;
import java.util.List;

import com.base.test.entity.AppOriginal;
import com.base.test.repository.AppOriginalRepository;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.dataflow.DataflowJob;
import com.trs.search.builder.ITRSSearchBuilder;
import com.trs.search.builder.SearchBuilderFactory;
import com.trs.search.common.PagedList;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SpringDataflowJob implements DataflowJob<AppOriginal> {
    
    private AppOriginalRepository appOriginalRepository;

    public SpringDataflowJob() {
    }

    public SpringDataflowJob(AppOriginalRepository appOriginalRepository) {
        this.appOriginalRepository = appOriginalRepository;
    }

    @Override
    public List<AppOriginal> fetchData(ShardingContext shardingContext) {
        log.info(String.format("------Thread ID: %s, 任务总片数: %s, " + "当前分片项: %s.当前参数: %s," + "当前任务名称: %s.当前任务参数: %s",
                Thread.currentThread().getId(), shardingContext.getShardingTotalCount(),
                shardingContext.getShardingItem(), shardingContext.getShardingParameter(), shardingContext.getJobName(),
                shardingContext.getJobParameter()));
        ITRSSearchBuilder builder = SearchBuilderFactory.create(0, 1000);
        builder.filterChildField("SHARDING_FLAG", shardingContext.getShardingParameter(), ITRSSearchBuilder.Operator.Equal);
        log.info(String.format("fetch data process searchValue= msg:[%s]", "trsl:" + builder.asTRSL()));
        PagedList<AppOriginal> pagedList;
        try {
            pagedList = appOriginalRepository.pageList(builder);
            if (pagedList.size() > 0) {
                return pagedList.getPageItems();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void processData(ShardingContext shardingContext, List<AppOriginal> data) {
        System.out.println(data.size());
    }

}
