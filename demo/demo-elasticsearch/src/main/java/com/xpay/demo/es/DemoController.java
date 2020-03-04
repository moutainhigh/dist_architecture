package com.xpay.demo.es;

import com.xpay.common.statics.result.EsAggResult;
import com.xpay.common.statics.result.PageResult;
import com.xpay.common.util.params.EsQuery;
import com.xpay.starter.comp.component.ESClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("demo")
public class DemoController {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ESClient esClient;

    @ResponseBody
    @RequestMapping(value = "/getOne", method = RequestMethod.GET)
    public HashMap getOne(String index, Boolean camelCase) { //index可使用 tbl_account_detail
        try{
            if(camelCase == null) camelCase = false;
            EsQuery esQuery = null;
            if(camelCase){
                esQuery = EsQuery.buildCamelCase().from(index);
                esQuery.eq("id", 27496690);
            }else{
                esQuery = EsQuery.buildNoneCase().from(index);
                esQuery.eq("ID", 27496690);
            }

            HashMap result = esClient.getOne(esQuery);

            return result;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/listBy", method = RequestMethod.GET)
    public List<HashMap> listBy(String index, Integer pagSize, Boolean camelCase) {
        try{
            if(camelCase == null) camelCase = false;
            EsQuery esQuery = null;

            if(camelCase){
                esQuery = EsQuery.buildCamelCase().from(index);
                esQuery.eq("userNo", "888100000009003")
                        .size(pagSize);
            }else{
                esQuery = EsQuery.buildNoneCase().from(index);
                esQuery.eq("USER_NO", "888100000009003")
                        .size(pagSize);
            }

            List<HashMap> result = esClient.listBy(esQuery);

            return result;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/listPage", method = RequestMethod.GET)
    public PageResult<List<HashMap>> listPage(String index, Integer pageCurrent, Boolean camelCase) {
        try{
            if(camelCase == null) camelCase = false;
            EsQuery esQuery = null;

            if(camelCase){
                esQuery = EsQuery.buildCamelCase().from(index);
                esQuery
                    .eq("userNo", "888100000005252")
                        .neq("userNo", "888100000009003")
                        .notIn("alterType", "1,2,3".split(","))
                        .page(pageCurrent, 20)
                        .resultClass(HashMap.class);
            }else{
                esQuery = EsQuery.buildNoneCase().from(index);
                esQuery
                    .eq("USER_NO", "888100000005252")
                        .neq("USER_NO", "888100000009003")
                        .notIn("ALTER_TYPE", "1,2,3".split(","))
                        .page(pageCurrent, 20)
                        .resultClass(HashMap.class);
            }

            PageResult<List<HashMap>> result = esClient.listPage(esQuery);

            return result;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/scrollPage", method = RequestMethod.GET)
    public PageResult<List<HashMap>> scrollPage(String index, String scrollId, Integer pagSize, Boolean camelCase) {
        try{
            if(camelCase == null) camelCase = false;
            EsQuery esQuery = null;

            if(camelCase){
                esQuery = EsQuery.buildCamelCase().from(index);
                esQuery.eq("userNo", "888100000009003").scroll( 60, pagSize).scrollId(scrollId);
            }else{
                esQuery = EsQuery.buildNoneCase().from(index);
                esQuery.eq("USER_NO", "888100000009003").scroll( 60, pagSize).scrollId(scrollId);
            }

            PageResult<List<HashMap>> result = esClient.listPage(esQuery);

            return result;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/aggregation", method = RequestMethod.GET)
    public EsAggResult aggregation(String index, String groupBy, Boolean camelCase) {
        if(camelCase == null) camelCase = false;
        EsQuery esQuery = null;

        if(camelCase){
            esQuery = EsQuery.buildCamelCase().from(index);
            esQuery.count("id")
                    .sum("alterBalanceLong")
                    .avg("alterBalanceLong")
                    .min("alterBalanceLong")
                    .max("alterBalanceLong")
                    .eq("userNo", "888100000009003")
                    .groupBy(groupBy);
        }else{
            esQuery = EsQuery.buildNoneCase().from(index);
            esQuery.count("ID")
                    .sum("ALTER_BALANCE_LONG")
                    .avg("ALTER_BALANCE_LONG")
                    .min("ALTER_BALANCE_LONG")
                    .max("ALTER_BALANCE_LONG")
                    .eq("USER_NO", "888100000009003")
                    .groupBy(groupBy);
        }

        EsAggResult result = esClient.aggregation(esQuery);

        return result;
    }
}
