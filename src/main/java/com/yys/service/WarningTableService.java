package com.yys.service;

import com.yys.entity.GetWarningSearch;
import com.yys.entity.WarningTable;
import com.yys.repository.WarningTableRepository;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class WarningTableService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Autowired
    private WarningTableRepository warningTableRepository;


    /**
     * 插入一条警告记录到Elasticsearch
     *
     * @param warningTable 要插入的警告记录
     */
    public void saveWarningTable(WarningTable warningTable) {
        warningTable.setAlertId(generateCameraId());
        warningTableRepository.save(warningTable);
    }

    public WarningTable searchByAlertId(String alertId) {
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery("alertId", alertId))
                .build();
        SearchHit<WarningTable> searchHit = elasticsearchRestTemplate.searchOne(searchQuery, WarningTable.class);
        return searchHit != null ? searchHit.getContent() : null;
    }

    public List<WarningTable> searchWithSort() {

        Pageable pageable = PageRequest.of(0, 5);
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withSourceFilter(new FetchSourceFilter(new String[]{"alertId","alertTime", "cameraPosition", "alertType", "capturedImage","capturedVideo"}, null))
                .withSort(SortBuilders.fieldSort("alertTime").order(SortOrder.DESC))
                .withPageable(pageable)
                .build();

        SearchHits<WarningTable> searchHits = elasticsearchRestTemplate.search(searchQuery, WarningTable.class);

        return searchHits.getSearchHits().stream()
                .map(hit -> hit.getContent())
                .collect(Collectors.toList());
    }

    public WarningTable searchLatest() {
        Pageable pageable = PageRequest.of(0, 1);
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withSourceFilter(new FetchSourceFilter(new String[]{"alertId", "alertTime", "cameraPosition", "alertType", "capturedImage", "capturedVideo"}, null))
                .withSort(SortBuilders.fieldSort("alertTime").order(SortOrder.DESC))
                .withPageable(pageable)
                .build();

        SearchHits<WarningTable> searchHits = elasticsearchRestTemplate.search(searchQuery, WarningTable.class);

        if (searchHits.getTotalHits() > 0) {
            return searchHits.getSearchHits().get(0).getContent();
        } else {
            return null;
        }
    }

    /**
     * 根据告警类型搜索警告信息
     *
     * @param getWarningSearch 包含搜索条件和分页信息的对象
     * @return 返回满足条件的警告信息分页结果
     */
    public Page<WarningTable> searchByAlertTypes(GetWarningSearch getWarningSearch) {
        // 初始化Bool查询构建器
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

        // 提取搜索条件参数
        String startTime =getWarningSearch.getStartTime();
        String endTime =getWarningSearch.getEndTime();
        String searchText =getWarningSearch.getSearchText();
        List<String> alertTypes =getWarningSearch.getAlertTypes();
        List<String> cameraPosition =getWarningSearch.getCameraPosition();

        int pageNum=getWarningSearch.getPageNum();
        int pageSize=getWarningSearch.getPageSize();

        // 如果开始时间和结束时间不为空，则添加时间范围查询
        if (startTime != null && !startTime.isEmpty() && endTime != null && !endTime.isEmpty()) {
            queryBuilder.must(QueryBuilders.rangeQuery("alertTime").from(startTime).to(endTime));
        }

        // 如果搜索文本不为空，则添加多字段匹配查询
        if (searchText != null && !searchText.isEmpty()) {
            MultiMatchQueryBuilder multiMatchQuery = QueryBuilders.multiMatchQuery(searchText, "cameraPosition", "monitoringTask", "alertType","videoTags");
            queryBuilder.must(multiMatchQuery);
        }

        // 如果告警类型列表不为空，则添加术语查询
        if (alertTypes != null && !alertTypes.isEmpty()) {
            TermsQueryBuilder termsQuery = QueryBuilders.termsQuery("alertType.keyword", alertTypes);
            queryBuilder.must(termsQuery);
        }

        // 如果摄像机点位列表不为空，则添加术语查询
        if (cameraPosition != null && !cameraPosition.isEmpty()) {
            TermsQueryBuilder termsQuery = QueryBuilders.termsQuery("cameraPosition.keyword", cameraPosition);
            queryBuilder.must(termsQuery);
        }

        // 设置分页信息和排序规则，准备执行查询
        Pageable pageable = PageRequest.of(pageNum, pageSize);
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(queryBuilder)
                .withPageable(pageable)
                .withSort(SortBuilders.fieldSort("alertTime").order(SortOrder.DESC))
                .build();

        // 执行Elasticsearch查询并处理结果
        SearchHits<WarningTable> searchHits = elasticsearchRestTemplate.search(searchQuery, WarningTable.class);
        List<WarningTable> warningList = searchHits.getSearchHits().stream()
                .map(hit -> hit.getContent())
                .collect(Collectors.toList());

        // 构建并返回分页结果
        return new PageImpl<>(warningList, pageable, searchHits.getTotalHits());
    }


    //获取七天的数据
    public Map<String, Map<String, Long>> getSevenTopAlertTypes() {
        // 构建查询
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.rangeQuery("alertTime")
                        .gte("now-6d/d")
                        .lt("now+1d/d"))
                .addAggregation(
                        AggregationBuilders.terms("top_alert_types").field("alertType.keyword").size(3)
                                .subAggregation(
                                        AggregationBuilders.dateHistogram("daily_distribution")
                                                .field("alertTime")
                                                .calendarInterval(DateHistogramInterval.DAY)
                                                .format("yyyy-MM-dd")
                                                .minDocCount(0)  // 补全没有值的日期
                                )
                )
                .build();

        // 执行查询
        SearchHits<WarningTable> searchHits = elasticsearchRestTemplate.search(searchQuery, WarningTable.class);

        // 解析聚合结果
        Map<String, Map<String, Long>> result = new HashMap<>();

        // 检查是否有聚合结果
        if (searchHits.hasAggregations()) {
            // 获取 Aggregations 对象
            Aggregations aggregations = (Aggregations) searchHits.getAggregations().aggregations();

            // 获取 top_alert_types 聚合结果
            Terms topAlertTypes = aggregations.get("top_alert_types");

            // 获取过去七天的日期列表
            List<String> lastSevenDays = getLastSevenDays();

            // 处理每个 alertType
            for (Terms.Bucket bucket : topAlertTypes.getBuckets()) {
                String alertType = bucket.getKeyAsString();
                Histogram dailyDistribution = bucket.getAggregations().get("daily_distribution");

                // 使用 LinkedHashMap 保持日期顺序
                Map<String, Long> dailyCounts = new LinkedHashMap<>();
                for (String day : lastSevenDays) {
                    dailyCounts.put(day, 0L);  // 预先将每一天的值设置为0
                }

                for (Histogram.Bucket dayBucket : dailyDistribution.getBuckets()) {
                    String day = dayBucket.getKeyAsString();
                    long count = dayBucket.getDocCount();
                    dailyCounts.put(day, count);
                }

                result.put(alertType, dailyCounts);
            }
        }

        return result;
    }

    //查询30天，每三天一查
    public Map<String, Map<String, Long>> getThreeDayTopAlertTypes() {
        // 构建查询
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.rangeQuery("alertTime")
                        .gte("now-29d/d")
                        .lt("now+1d/d"))
                .addAggregation(
                        AggregationBuilders.terms("top_alert_types").field("alertType.keyword").size(3)
                                .subAggregation(
                                        AggregationBuilders.dateHistogram("three_day_distribution")
                                                .field("alertTime")
                                                .fixedInterval(DateHistogramInterval.days(3))
                                                .format("yyyy-MM-dd")
                                )
                )
                .build();

        // 执行查询
        SearchHits<WarningTable> searchHits = elasticsearchRestTemplate.search(searchQuery, WarningTable.class);

        // 初始化日期列表
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<String> dateList = new ArrayList<>();
        LocalDate startDate = LocalDate.now().minusDays(30);
        LocalDate endDate = LocalDate.now();

        while (!startDate.isAfter(endDate)) {
            dateList.add(startDate.format(formatter));
            startDate = startDate.plusDays(3);
        }

        // 解析聚合结果
        Map<String, Map<String, Long>> result = new HashMap<>();

        // 检查是否有聚合结果
        if (searchHits.hasAggregations()) {
            // 获取 Aggregations 对象
            Aggregations aggregations = (Aggregations) searchHits.getAggregations().aggregations();

            // 获取 top_alert_types 聚合结果
            Terms topAlertTypes = aggregations.get("top_alert_types");

            // 处理每个 alertType
            for (Terms.Bucket bucket : topAlertTypes.getBuckets()) {
                String alertType = bucket.getKeyAsString();
                Histogram threeDayDistribution = bucket.getAggregations().get("three_day_distribution");

                Map<String, Long> threeDayCounts = new LinkedHashMap<>();
                for (String date : dateList) {
                    threeDayCounts.put(date, 0L);
                }

                for (Histogram.Bucket dayBucket : threeDayDistribution.getBuckets()) {
                    String day = dayBucket.getKeyAsString();
                    long count = dayBucket.getDocCount();

                    // 计算当前日期所属的3天区间
                    LocalDate bucketDate = LocalDate.parse(day, formatter);
                    String threeDayKey = dateList.stream()
                            .filter(date -> !LocalDate.parse(date, formatter).isAfter(bucketDate))
                            .max(Comparator.naturalOrder())
                            .orElse(day);

                    // 更新三天计数，如果日期在列表中则覆盖
                    if (threeDayCounts.containsKey(threeDayKey)) {
                        threeDayCounts.put(threeDayKey, threeDayCounts.get(threeDayKey) + count);
                    }
                }

                result.put(alertType, threeDayCounts);
            }
        }

        return result;
    }

    //获取今天的数据
    public Map<String, Map<String, Long>> getTodayTopAlertTypes() {
        // 构建查询
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.rangeQuery("alertTime")
                        .gte("now/d")
                        .lt("now+1d/d"))
                .addAggregation(
                        AggregationBuilders.terms("top_alert_types").field("alertType.keyword").size(3)
                                .subAggregation(
                                        AggregationBuilders.dateHistogram("three_hour_distribution")
                                                .field("alertTime")
                                                .fixedInterval(DateHistogramInterval.hours(3))
                                                .format("HH:mm")
                                )
                )
                .build();

        // 执行查询
        SearchHits<WarningTable> searchHits = elasticsearchRestTemplate.search(searchQuery, WarningTable.class);

        // 初始化 3 小时间隔列表
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        List<String> timeList = new ArrayList<>();
        LocalTime startTime = LocalTime.MIDNIGHT;
        LocalTime endTime = LocalTime.of(21, 00);

        while (!startTime.equals(endTime)) {
            timeList.add(startTime.format(formatter));
            startTime = startTime.plusHours(3);
        }

        // 解析聚合结果
        Map<String, Map<String, Long>> result = new HashMap<>();

        // 检查是否有聚合结果
        if (searchHits.hasAggregations()) {
            // 获取 Aggregations 对象
            Aggregations aggregations = (Aggregations) searchHits.getAggregations().aggregations();

            // 获取 top_alert_types 聚合结果
            Terms topAlertTypes = aggregations.get("top_alert_types");

            // 处理每个 alertType
            for (Terms.Bucket bucket : topAlertTypes.getBuckets()) {
                String alertType = bucket.getKeyAsString();
                Histogram threeHourDistribution = bucket.getAggregations().get("three_hour_distribution");

                Map<String, Long> threeHourCounts = new LinkedHashMap<>();
                for (String time : timeList) {
                    threeHourCounts.put(time, 0L);
                }

                for (Histogram.Bucket hourBucket : threeHourDistribution.getBuckets()) {
                    String time = hourBucket.getKeyAsString();
                    long count = hourBucket.getDocCount();

                    // 计算当前时间所属的3小时区间
                    LocalTime bucketTime = LocalTime.parse(time, formatter);
                    String threeHourKey = timeList.stream()
                            .filter(t -> !LocalTime.parse(t, formatter).isAfter(bucketTime))
                            .max(Comparator.naturalOrder())
                            .orElse(time);

                    // 更新三小时计数，如果时间在列表中则覆盖
                    if (threeHourCounts.containsKey(threeHourKey)) {
                        threeHourCounts.put(threeHourKey, threeHourCounts.get(threeHourKey) + count);
                    }
                }

                result.put(alertType, threeHourCounts);
            }
        }

        return result;
    }


    public String generateCameraId() {
        WarningTable warningTable = searchLatest();
        SimpleDateFormat sdf = new SimpleDateFormat("MMdd");
        String datePart = sdf.format(new Date());
        String oldId="";
        if (warningTable == null){
            oldId=null;
        }else {
            oldId = warningTable.getAlertId();
        }

        if (oldId == null || oldId.isEmpty()) {
            return "JWD-"+datePart+"-000001";
        }
        int numericPart = Integer.parseInt(oldId.substring(9)) + 1;
        return String.format("JWD-%s-%06d", datePart, numericPart);
    }


    // 获取过去七天的日期列表，格式为 yyyy-MM-dd
    private List<String> getLastSevenDays() {
        List<String> days = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate today = LocalDate.now();

        for (int i = 0; i < 7; i++) {
            days.add(today.minusDays(i).format(formatter));
        }

        Collections.reverse(days);  // 确保日期从早到晚排列
        return days;
    }

}
