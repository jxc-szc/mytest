package com.base.job;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SpringSimpleJob implements SimpleJob {

    @Override
    public void execute(ShardingContext shardingContext) {
        log.info(String.format("------Thread ID: %s, 任务总片数: %s, " + "当前分片项: %s.当前参数: %s," + "当前任务名称: %s.当前任务参数: %s",
                Thread.currentThread().getId(), shardingContext.getShardingTotalCount(),
                shardingContext.getShardingItem(), shardingContext.getShardingParameter(), shardingContext.getJobName(),
                shardingContext.getJobParameter()));
    }

}
