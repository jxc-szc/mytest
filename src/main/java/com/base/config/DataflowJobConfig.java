package com.base.config;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.base.job.SpringDataflowJob;
import com.base.test.repository.AppOriginalRepository;
import com.dangdang.ddframe.job.api.dataflow.DataflowJob;
import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.dataflow.DataflowJobConfiguration;
import com.dangdang.ddframe.job.lite.api.JobScheduler;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;

@Configuration
public class DataflowJobConfig {

    // 注册中心配置
    @Resource
    private ZookeeperRegistryCenter regCenter;
    
    @Bean
    public AppOriginalRepository appOriginalRepository() {
        return new AppOriginalRepository();
    }

    @Bean
    public DataflowJob dataflowJob(AppOriginalRepository appOriginalRepository) {
        return new SpringDataflowJob(appOriginalRepository);
    }

    @Bean(initMethod = "init")
    public JobScheduler dataflowJobScheduler(final DataflowJob dataflowJob,
            @Value("${dataflowJob.cron}") final String cron,
            @Value("${dataflowJob.shardingTotalCount}") final int shardingTotalCount,
            @Value("${dataflowJob.shardingItemParameters}") final String shardingItemParameters) {
        return new SpringJobScheduler(dataflowJob, regCenter,
                getLiteJobConfiguration(dataflowJob.getClass(), cron, shardingTotalCount, shardingItemParameters));
    }

    /*
     * 作业配置
     * 作业配置分为3级，分别是JobCoreConfiguration，JobTypeConfiguration和LiteJobConfiguration。
     * LiteJobConfiguration使用JobTypeConfiguration，
     * JobTypeConfiguration使用JobCoreConfiguration，层层嵌套。
     * JobTypeConfiguration根据不同实现类型分为SimpleJobConfiguration，
     * DataflowJobConfiguration和ScriptJobConfiguration。
     */
    private LiteJobConfiguration getLiteJobConfiguration(final Class<? extends DataflowJob> jobClass, final String cron,
            final int shardingTotalCount, final String shardingItemParameters) {
        return LiteJobConfiguration
                .newBuilder(new DataflowJobConfiguration(
                        JobCoreConfiguration.newBuilder(jobClass.getName(), cron, shardingTotalCount)
                                .shardingItemParameters(shardingItemParameters).build(),
                        jobClass.getCanonicalName(), false))
                .overwrite(true).build();
    }
}
