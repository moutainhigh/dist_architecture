package com.xpay.starter.comp.component;

import com.google.common.cache.Cache;
import com.xpay.common.statics.dto.es.EsAggDto;
import com.xpay.common.statics.exceptions.BizException;
import com.xpay.common.statics.result.EsAggResult;
import com.xpay.common.statics.result.PageResult;
import com.xpay.common.util.params.EsQuery;
import com.xpay.common.util.utils.ClassUtil;
import com.xpay.common.util.utils.JsonUtil;
import com.xpay.common.util.utils.WordCaseUtil;
import com.xpay.common.util.utils.StringUtil;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.search.*;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetMappingsRequest;
import org.elasticsearch.client.indices.GetMappingsResponse;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.*;
import org.elasticsearch.search.aggregations.support.ValuesSourceAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;

/**
 * elasticsearch客户端，提供一些常规的方法，如果需要更复杂的操作，可通过 {@link #getRestEsClient()}方法取得ES的原生客户端来处理
 */
public class ESClient {
    public static final int MAX_GROUP_SIZE = 1000;//最大分组数量
    private static final String INNER_GROUP_VALUE = "__INNER_GROUP_VALUE__";
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private RestHighLevelClient restEsClient;
    private Cache<String, Map<String, String>> cache;

    public ESClient(RestHighLevelClient restEsClient){
        this(restEsClient, null);
    }

    public ESClient(RestHighLevelClient restEsClient, Cache<String, Map<String, String>> cache){
        this.restEsClient = restEsClient;
        this.cache = cache;
    }

    /**
     * 获取es原生客户端，用以处理比较复杂的需求
     * @return
     */
    public RestHighLevelClient getRestEsClient() {
        return restEsClient;
    }

    /**
     * 取得单个实体
     * @param esQuery
     * @param <T>
     * @return
     */
    public <T> T getOne(EsQuery esQuery){
        Class<T> clz = getReturnClass(esQuery);

        SearchResponse response = executeQuery(esQuery);
        if(response.getHits().getTotalHits().value > 0){
            if(esQuery.isWordCase()){
                Map<String, Object> resultMap = resultWordCase(response.getHits().getHits()[0].getSourceAsMap(), esQuery);
                if(isHashMap(clz)){
                    return (T) resultMap;
                }else if(isString(clz)){
                    return (T) JsonUtil.toString(resultMap);
                }else{
                    return JsonUtil.toBean(JsonUtil.toString(resultMap), clz);
                }
            }else{
                if(isHashMap(clz)){
                    return (T) response.getHits().getHits()[0].getSourceAsMap();
                }else if(isString(clz)){
                    return (T) response.getHits().getHits()[0].getSourceAsString();
                }else{
                    return JsonUtil.toBean(response.getHits().getHits()[0].getSourceAsString(), clz);
                }
            }
        }else{
            return null;
        }
    }

    /**
     * 列表查询
     * @param esQuery
     * @param <T>
     * @return
     */
    public <T> List<T> listBy(EsQuery esQuery){
        Class<T> clz = getReturnClass(esQuery);

        SearchResponse response = executeQuery(esQuery);
        if(response.getHits().getTotalHits().value <= 0){
            return new ArrayList<>();
        }

        List<T> entityList = getEntityList(response, clz, esQuery);
        if((entityList == null || entityList.isEmpty()) && StringUtil.isNotEmpty(response.getScrollId())){
            clearScroll(response.getScrollId());
        }
        return entityList;
    }

    /**
     * 分页查询，需要返回分页结果
     * @param esQuery
     * @param <T>
     * @return
     */
    public <T> PageResult<List<T>> listPage(EsQuery esQuery){
        Class<T> clz = getReturnClass(esQuery);
        SearchResponse response = executeQuery(esQuery);
        long totalRecord = response.getHits().getTotalHits().value;
        if(totalRecord <= 0){
            return PageResult.newInstance(new ArrayList<>(), esQuery.getPageCurrent(), esQuery.getPageSize());
        }

        String scrollId = response.getScrollId();
        List<T> entityList = getEntityList(response, clz, esQuery);
        if((entityList == null || entityList.isEmpty()) && StringUtil.isNotEmpty(scrollId)){
            clearScroll(scrollId);
            scrollId = null;//ES中的快照已被清除，故scrollId也不应该再返回
        }
        PageResult result = PageResult.newInstance(entityList, esQuery.getPageCurrent(), esQuery.getPageSize(), totalRecord);
        result.setScrollId(scrollId);
        return result;
    }

    /**
     * 统计，可统计多个字段的多个维度，每个字段都可有：count、sum、min、max、avg 等维度统计
     * @param esQuery
     * @return
     */
    public EsAggResult aggregation(EsQuery esQuery){
        SearchResponse response = executeAggregation(esQuery);

        AggResultTemp aggResultTemp = new AggResultTemp();
        if(response.getHits().getTotalHits().value > 0){
            if (isEmpty(esQuery.getGroupBy())){
                fillEsAggResult(aggResultTemp, INNER_GROUP_VALUE, response.getAggregations().iterator(), esQuery);
            }else{
                Iterator<Aggregation> iterator = response.getAggregations().iterator();
                while (iterator.hasNext()){
                    Aggregation agg = iterator.next();
                    ParsedTerms terms = (ParsedTerms) agg;

                    if(terms.getBuckets().isEmpty()){
                        continue;
                    }

                    for(Terms.Bucket bucket : terms.getBuckets()){
                        String groupValue = ((Terms.Bucket) bucket).getKeyAsString();
                        Aggregations bucketAgg = ((Terms.Bucket) bucket).getAggregations();
                        fillEsAggResult(aggResultTemp, groupValue, bucketAgg.iterator(), esQuery);
                    }
                }
            }
        }
        return aggResultTemp.toEsAggResult();
    }

    /**
     * 从查询结果中转换成List<T>返回
     * @param response
     * @return
     */
    public  <T> List<T> getEntityList(SearchResponse response, Class<T> clz, EsQuery esQuery){
        List<T> entityList = new ArrayList<>();
        boolean isHashMap = isHashMap(clz);
        boolean isString = isString(clz);
        if(response.getHits().getHits().length <= 0){
            return entityList;
        }

        SearchHit[] hits = response.getHits().getHits();
        for(int i=0; i<hits.length; i++){
            if(esQuery.isWordCase()){
                Map<String, Object> resultMap = resultWordCase(hits[i].getSourceAsMap(), esQuery);
                if(isHashMap){
                    entityList.add((T) resultMap);
                }else if(isString){
                    entityList.add((T)JsonUtil.toString(resultMap));
                }else{
                    entityList.add(JsonUtil.toBean(JsonUtil.toString(resultMap), clz));
                }
            }else{
                if(isHashMap){
                    entityList.add((T) hits[i].getSourceAsMap());
                }else if(isString){
                    entityList.add((T)hits[i].getSourceAsString());
                }else{
                    entityList.add(JsonUtil.toBean(hits[i].getSourceAsString(), clz));
                }
            }
        }
        return entityList;
    }

    private SearchResponse executeQuery(EsQuery esQuery){
        paramCheck(esQuery, false);

        esQuery.doQueryParamCase();//查询时参数名转换

        //如果是滚动查询，则查询后直接返回即可
        if(esQuery.getScrollMode() && StringUtil.isNotEmpty(esQuery.getScrollId())){
            SearchScrollRequest scrollRequest = new SearchScrollRequest(esQuery.getScrollId());
            scrollRequest.scroll(TimeValue.timeValueSeconds(esQuery.getScrollExpireSec()));
            try{
                return restEsClient.scroll(scrollRequest, RequestOptions.DEFAULT);
            }catch(IOException e){
                throw new BizException("IOException " + e.getMessage());
            }
        }

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //设置需要返回的字段
        if(esQuery.getSelectFields() != null && esQuery.getSelectFields().length > 0){
            sourceBuilder.fetchSource(esQuery.getSelectFields(), null);
        }
        //构造查询条件
        sourceBuilder.query(getQueryBuilder(esQuery));
        //增加排序字段
        addSort(sourceBuilder, esQuery.getOrderBy());
        //构建查询请求对象，并指定要查询的 index、type
        SearchRequest searchRequest = new SearchRequest(esQuery.getIndex());
        searchRequest.source(sourceBuilder);
        //处理分页查询
        if(esQuery.getScrollMode()){
            searchRequest.scroll(TimeValue.timeValueSeconds(esQuery.getScrollExpireSec()));
            sourceBuilder.size(esQuery.getPageSize());
        }else{
            int offset = (esQuery.getPageCurrent() - 1) * esQuery.getPageSize();
            sourceBuilder.from(offset).size(esQuery.getPageSize());
        }

        try{
            return restEsClient.search(searchRequest, RequestOptions.DEFAULT);
        }catch(IOException e){
            throw new BizException("IOException " + e.getMessage());
        }
    }

    private SearchResponse executeAggregation(EsQuery esQuery){
        paramCheck(esQuery, true);

        esQuery.doQueryParamCase();//查询时参数名转换

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        appendAggregation(sourceBuilder, esQuery);//设置聚合维度
        sourceBuilder.query(getQueryBuilder(esQuery));//设置查询过滤条件

        SearchRequest searchRequest = new SearchRequest(esQuery.getIndex());
        searchRequest.source(sourceBuilder);

        try{
            return restEsClient.search(searchRequest, RequestOptions.DEFAULT);
        }catch(IOException e){
            throw new BizException("IOException " + e.getMessage());
        }
    }

    private QueryBuilder getQueryBuilder(EsQuery esQuery){
        Map<String, String> fieldMap = getESMappingFieldMap(esQuery.getIndex());
        if(fieldMap == null || fieldMap.isEmpty()){
            throw new BizException("es mapping not exist of index: " + esQuery.getIndex());
        }else if(! isEmpty(esQuery.getGroupBy()) && ! fieldMap.containsKey(esQuery.getGroupBy())){
            throw new BizException("cannot use an not exist field to group by : " + esQuery.getGroupBy());
        }

        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();//等同于sql中的 and 查询

        //精确匹配(等于)
        if(isNotBlank(esQuery.getEqMap())){
            for(Map.Entry<String, Object> entry : esQuery.getEqMap().entrySet()){
                if(isNotEmpty(entry.getKey(), entry.getValue()) && fieldMap.containsKey(entry.getKey())){
                    queryBuilder.filter(QueryBuilders.termQuery(entry.getKey(), entry.getValue()));
                }
            }
        }

        //精确匹配(不等于)
        if(isNotBlank(esQuery.getNeqMap())) {
            for (Map.Entry<String, Object> entry : esQuery.getNeqMap().entrySet()) {
                if (isNotEmpty(entry.getKey(), entry.getValue()) && fieldMap.containsKey(entry.getKey())) {
                    queryBuilder.mustNot(QueryBuilders.termQuery(entry.getKey(), entry.getValue()));
                }
            }
        }

        //范围列表查询(in 查询)
        if(isNotBlank(esQuery.getInMap())){
            for(Map.Entry<String, Object[]> entry : esQuery.getInMap().entrySet()){
                if(isNotEmpty(entry.getKey(), entry.getValue()) && fieldMap.containsKey(entry.getKey())){
                    queryBuilder.filter(QueryBuilders.termsQuery(entry.getKey(), entry.getValue()));
                }
            }
        }

        //精确匹配(not in 查询)
        if(isNotBlank(esQuery.getNotInMap())) {
            for (Map.Entry<String, Object[]> entry : esQuery.getNotInMap().entrySet()) {
                if (isNotEmpty(entry.getKey(), entry.getValue()) && fieldMap.containsKey(entry.getKey())) {
                    queryBuilder.mustNot(QueryBuilders.termsQuery(entry.getKey(), entry.getValue()));
                }
            }
        }

        //范围区间查询
        if(isNotBlank(esQuery.getGtMap()) || isNotBlank(esQuery.getGteMap())
                || isNotBlank(esQuery.getLtMap()) || isNotBlank(esQuery.getLteMap())){
            Set<String> keys = new HashSet<>();

            if(isNotBlank(esQuery.getGtMap())){
                keys.addAll(esQuery.getGtMap().keySet());
            }
            if(isNotBlank(esQuery.getGteMap())){
                keys.addAll(esQuery.getGteMap().keySet());
            }
            if(isNotBlank(esQuery.getLtMap())){
                keys.addAll(esQuery.getLtMap().keySet());
            }
            if(isNotBlank(esQuery.getLteMap())){
                keys.addAll(esQuery.getLteMap().keySet());
            }

            for(String key : keys){
                if(isEmpty(key) || ! fieldMap.containsKey(key)){
                    continue;
                }

                RangeQueryBuilder query = QueryBuilders.rangeQuery(key);
                Object valueGt = esQuery.getGtMap() == null ? null : esQuery.getGtMap().get(key);
                Object valueGte = esQuery.getGteMap() == null ? null : esQuery.getGteMap().get(key);
                Object valueLt = esQuery.getLtMap() == null ? null : esQuery.getLtMap().get(key);
                Object valueLte = esQuery.getLteMap() == null ? null : esQuery.getLteMap().get(key);

                if(valueGte != null && valueLte != null){
                    query.from(valueGte).to(valueLte);
                }else{
                    if(valueGt != null){
                        query.gt(valueGt);
                    }
                    if(valueGte != null){
                        query.gte(valueGte);
                    }
                    if(valueLt != null){
                        query.lt(valueLt);
                    }
                    if(valueLte != null){
                        query.lte(valueLte);
                    }
                }

                queryBuilder.filter(query);
            }
        }

        //全文搜索(ES服务端需安装有中文分词器)
        if(isNotBlank(esQuery.getLikeMap())){
            for(Map.Entry<String, Object> entry : esQuery.getLikeMap().entrySet()){
                if (isNotEmpty(entry.getKey(), entry.getValue()) && fieldMap.containsKey(entry.getKey())) {
                    queryBuilder.filter(QueryBuilders.matchQuery(entry.getKey(), entry.getValue()));
                }
            }
        }

        return queryBuilder;
    }

    private void appendAggregation(SearchSourceBuilder sourceBuilder, EsQuery esQuery){
        Map<String, String> fieldMap = getESMappingFieldMap(esQuery.getIndex());
        boolean isNeedTerms = StringUtil.isNotEmpty(esQuery.getGroupBy());
        TermsAggregationBuilder termsAggBuilder = null;
        if (isNeedTerms) {
            termsAggBuilder = AggregationBuilders.terms(esQuery.getGroupBy()).field(esQuery.getGroupBy()).size(MAX_GROUP_SIZE);
        }

        Field[] fields = EsQuery.Aggregation.class.getDeclaredFields();
        for(Map.Entry<String, EsQuery.Aggregation> entry : esQuery.getAggMap().entrySet()){
            String aggField = entry.getKey();
            if(! fieldMap.containsKey(aggField)){ //ES中不存在的字段将直接忽略
                continue;
            }

            EsQuery.Aggregation agg = entry.getValue();
            for(Field field : fields){
                field.setAccessible(true);

                String name = field.getName();
                if(name.contains("this$") || "field".equals(name)){
                    continue;
                }

                Boolean value;
                try{
                    value = field.getBoolean(agg);
                }catch(Throwable e){
                    throw new BizException("EsQuery.Aggregation 获取"+name+"的属性值出现异常：", e);
                }
                if(value == null || value == false){
                    continue;
                }

                ValuesSourceAggregationBuilder aggBuilder;
                switch(name){
                    case "count":
                        aggBuilder = AggregationBuilders.count(fillFieldName(aggField, name)).field(aggField);
                        break;
                    case "sum":
                        aggBuilder = AggregationBuilders.sum(fillFieldName(aggField, name)).field(aggField);
                        break;
                    case "min":
                        aggBuilder = AggregationBuilders.min(fillFieldName(aggField, name)).field(aggField);
                        break;
                    case "max":
                        aggBuilder = AggregationBuilders.max(fillFieldName(aggField, name)).field(aggField);
                        break;
                    case "avg":
                        aggBuilder = AggregationBuilders.avg(fillFieldName(aggField, name)).field(aggField);
                        break;
                    default:
                        throw new BizException("EsQuery.Aggregation 未预期的属性名称：" + name);
                }

                if(isNeedTerms){
                    termsAggBuilder.subAggregation(aggBuilder);
                }else{
                    sourceBuilder.aggregation(aggBuilder);
                }
            }
        }

        if(isNeedTerms){
            sourceBuilder.aggregation(termsAggBuilder);
        }
    }

    private void fillEsAggResult(AggResultTemp resultTemp, String groupValue, Iterator<Aggregation> iterator, EsQuery esQuery){
        while(iterator.hasNext()) {
            Aggregation aggEs = iterator.next();
            String fieldName = splitFieldName(aggEs.getName());
            if(esQuery.isWordCase()){
                fieldName = esQuery.isCamelCase() ? WordCaseUtil.toCamelCase(fieldName) : WordCaseUtil.toSnakeCase(fieldName);
            }

            EsAggDto agg;
            Map<String, EsAggDto> groupAggMap = resultTemp.getAggMap().get(fieldName);//分组后的统计结果
            if(groupAggMap == null){
                groupAggMap = new HashMap<>();
                agg = new EsAggDto();
                agg.setGroupValue(groupValue);
                groupAggMap.put(groupValue, agg);
                resultTemp.getAggMap().put(fieldName, groupAggMap);
            }else if((agg = groupAggMap.get(groupValue)) == null){
                agg = new EsAggDto();
                agg.setGroupValue(groupValue);
                groupAggMap.put(groupValue, agg);
            }

            switch(aggEs.getType()){
                case ValueCountAggregationBuilder.NAME:
                    agg.setCount(((ParsedValueCount) aggEs).getValue());
                    break;
                case MaxAggregationBuilder.NAME:
                    agg.setMax(BigDecimal.valueOf(((ParsedMax) aggEs).getValue()));
                    break;
                case MinAggregationBuilder.NAME:
                    agg.setMin(BigDecimal.valueOf(((ParsedMin) aggEs).getValue()));
                    break;
                case SumAggregationBuilder.NAME:
                    agg.setSum(BigDecimal.valueOf(((ParsedSum) aggEs).getValue()));
                    break;
                case AvgAggregationBuilder.NAME:
                    agg.setAvg(BigDecimal.valueOf(((ParsedAvg) aggEs).getValue()));
                    break;
                default:
                    throw new BizException("未支持的聚合类型：" + aggEs.getType());
            }
        }
    }

    /**
     * 取得查询结构返回的实体类
     * @param esQuery
     * @return
     */
    private Class getReturnClass(EsQuery esQuery){
        if(StringUtil.isEmpty(esQuery.getReturnClassName())){
            return HashMap.class;
        }else{
            try{
                return ClassUtil.getClass(esQuery.getReturnClassName());
            }catch (ClassNotFoundException e){
                throw new BizException("ClassNotFoundException " + e.getMessage());
            }
        }
    }

    /**
     * 添加排序字段
     * @param searchBuilder
     * @param sortColumns
     */
    protected void addSort(SearchSourceBuilder searchBuilder, String sortColumns){
        if(StringUtil.isEmpty(sortColumns)){
            return;
        }

        String[] sortColumnArray = sortColumns.split(",");
        for(int i=0; i<sortColumnArray.length; i++){
            String[] sortColumn = sortColumnArray[i].split(" ");
            if(sortColumn.length > 1){
                searchBuilder.sort(sortColumn[0], SortOrder.fromString(sortColumn[sortColumn.length - 1]));
            }else{
                searchBuilder.sort(sortColumn[0], SortOrder.DESC);
            }
        }
    }

    private void paramCheck(EsQuery esQuery, boolean aggMapMust){
        if(esQuery == null){
            throw new BizException(BizException.PARAM_INVALIDATE, "esQuery不能为空");
        }else if(StringUtil.isEmpty(esQuery.getIndex())){
            throw new BizException(BizException.PARAM_INVALIDATE, "index不能为空");
        }else if(esQuery.getPageSize() <= 0 || esQuery.getPageCurrent() <= 0){
            throw new BizException(BizException.PARAM_INVALIDATE, "pageCurrent和pageSize都需大于0");
        }else if(aggMapMust && (esQuery.getAggMap() == null || esQuery.getAggMap().isEmpty())){
            throw new BizException(BizException.PARAM_INVALIDATE, "aggMap不能为空");
        }
    }

    private boolean isNotEmpty(String key, Object value){
        return ! (isEmpty(key) || (value == null || value.toString().trim().length() <= 0));
    }
    private boolean isNotEmpty(String key, Object[] values){
        return ! (isEmpty(key) || (values == null || values.length <= 0));
    }
    private boolean isEmpty(String key){
        return (key == null || key.trim().length() <= 0);
    }

    private boolean isNotBlank(Map map){
        return map != null && ! map.isEmpty();
    }

    private boolean isHashMap(Class clz){
        return HashMap.class.getName().equals(clz.getName());
    }

    private boolean isString(Class clz){
        return String.class.getName().equals(clz.getName());
    }

    private String fillFieldName(String field, String suffix){
        return field + "|" + suffix;
    }

    private String splitFieldName(String field){
        return field.split("\\|")[0];
    }

    private Map<String, Object> resultWordCase(Map<String, Object> entryMap, EsQuery esQuery){
        Map<String, Object> resultMap = new HashMap<>();
        for(Map.Entry<String, Object> entry : entryMap.entrySet()) {
            String key = esQuery.isCamelCase() ? WordCaseUtil.toCamelCase(entry.getKey()) : WordCaseUtil.toSnakeCase(entry.getKey());
            resultMap.put(key, entry.getValue());
        }
        return resultMap;
    }

    /**
     * 返回Mapping，其中key为字段名，value为字段的数据类型
     * @param index
     * @return
     */
    private Map<String, String> getESMappingFieldMap(String index){
        if(cache != null && cache.getIfPresent(index) != null){
            return cache.getIfPresent(index);
        }

        Map<String, String> fieldMap = new HashMap<>();
        Map<String, MappingMetaData> mappings;
        try{
            GetMappingsResponse mapping = getRestEsClient().indices().getMapping(new GetMappingsRequest().indices(index), RequestOptions.DEFAULT);
            mappings = mapping.mappings();
            if (mappings == null){
                return new HashMap<>();
            }
        }catch(IOException e){
            throw new BizException(e);
        }

        for(Map.Entry<String, MappingMetaData> entry : mappings.entrySet()){
            Map<String, Object> res = (Map<String, Object>) entry.getValue().sourceAsMap().get("properties");
            if(res == null) {
                continue;
            }

            for(Map.Entry<String, Object> entry1 : res.entrySet()){
                LinkedHashMap<String, Object> map = (LinkedHashMap) entry1.getValue();
                if(StringUtil.isNotEmpty(entry1.getKey())){
                    fieldMap.put(entry1.getKey(), (String) map.get("type"));
                }
            }
        }
        cache.put(index, fieldMap);
        return fieldMap;
    }

    private void clearScroll(String scrollId){
        ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
        clearScrollRequest.addScrollId(scrollId);
        getRestEsClient().clearScrollAsync(clearScrollRequest, RequestOptions.DEFAULT, new ActionListener<ClearScrollResponse>() {
            @Override
            public void onResponse(ClearScrollResponse clearScrollResponse) {
                if(clearScrollResponse.isSucceeded()){
                    logger.info("scrollId={} 清除成功", scrollId);
                }else{
                    logger.info("scrollId={} status={} 清除失败", scrollId, clearScrollResponse.status().name());
                }
            }
            @Override
            public void onFailure(Exception e) {
                logger.info("scrollId={} 清除异常 Exception={} ", scrollId, e.getMessage());
            }
        });
    }

    public void destroy(){
        try{
            this.getRestEsClient().close();
        }catch(Throwable e){
        }
    }

    /**
     * 统计结果的临时存放对象
     */
    private class AggResultTemp {
        /**
         * 统计结果，第一层key为字段名，第二层key为group by之后的具体分组值，第二层value为统计结果
         */
        Map<String, Map<String, EsAggDto>> aggMap = new HashMap<>();

        public Map<String, Map<String, EsAggDto>> getAggMap() {
            return aggMap;
        }

        public void setAggMap(Map<String, Map<String, EsAggDto>> aggMap) {
            this.aggMap = aggMap;
        }

        public EsAggResult toEsAggResult(){
            EsAggResult result = new EsAggResult();
            for(Map.Entry<String, Map<String, EsAggDto>> entry : aggMap.entrySet()){
                String key = entry.getKey();//字段名
                Map<String, EsAggDto> valueMap = entry.getValue();//字段的分组统计结果

                if(valueMap.size() == 1 && valueMap.containsKey(INNER_GROUP_VALUE)){//说明参数中没有说明要使用group by进行分组
                    EsAggDto aggDto = valueMap.get(INNER_GROUP_VALUE);
                    aggDto.setGroupValue(null);
                    result.getAggMap().put(key, aggDto);
                }else{
                    List<EsAggDto> aggList = new ArrayList<>();
                    for(Map.Entry<String, EsAggDto> aggEntry : valueMap.entrySet()){
                        aggList.add(aggEntry.getValue());
                    }
                    result.getAggListMap().put(key, aggList);
                }
            }
            return result;
        }
    }
}

