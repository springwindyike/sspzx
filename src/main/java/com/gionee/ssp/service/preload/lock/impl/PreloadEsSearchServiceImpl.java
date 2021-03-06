package com.gionee.ssp.service.preload.lock.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gionee.ssp.common.ipush.IpushConstant;
import com.gionee.ssp.service.es.esclient.ESClient;
import com.gionee.ssp.service.preload.lock.PreloadEsSearchService;
import com.wk.ssp.mvc.ipush.es.vo.CampaignVO;
import com.wk.ssp.utils.DateTimeUtils;
import com.wk.ssp.utils.JsonUtils;

/**
 *
 * 锁屏预加载ES搜索
 *
 */
@Service
public class PreloadEsSearchServiceImpl implements PreloadEsSearchService {

	Logger logger = LogManager.getLogger(getClass());
	/**
	 * es client服务层
	 */
	@Autowired
	ESClient esClient;
	/**
	 * 查询锁屏开屏素材
	 * 
	 * @param adslots
	 *            锁屏广告位
	 * @return 返回查询结果
	 * @throws Exception
	 */
	@Override
	public List<CampaignVO> search(String adId) throws Exception {
		BoolQueryBuilder queryBuilder = new BoolQueryBuilder();

		queryBuilder.must(QueryBuilders.termQuery("status", 1)); // 活动状态
																	// 1-暂停使用
																	// 0-正在使用
		queryBuilder.must(QueryBuilders.rangeQuery("start_time").lte(DateTimeUtils.getCurrentSecond())); // 大于等于活动开始日期
		queryBuilder.must(QueryBuilders.rangeQuery("end_time").gt(DateTimeUtils.getCurrentSecond())); // 小于活动结束日期
		queryBuilder.must(QueryBuilders.termQuery("adslots", adId)); // 广告位ID

//		// 锁屏预加载只是给开屏素材
//		queryBuilder.must(QueryBuilders.termQuery("ad_type", 3)); // 创意形式：1：横幅，2：插屏,3:开屏,

		Client client = esClient.getClient();
		logger.info(client.prepareSearch(IpushConstant.ES_INDEX)
					.setTypes(IpushConstant.ES_TYPE)
					.setPostFilter(queryBuilder));
		
		SearchResponse response = client.prepareSearch(IpushConstant.ES_INDEX)
				.setTypes(IpushConstant.ES_TYPE)
				.setPostFilter(queryBuilder)//
				.setFrom(0).setSize(100)//最多100个直投素材
				.execute().actionGet();

		SearchHit[] searchHits = response.getHits().getHits();
		List<CampaignVO> result = new ArrayList<CampaignVO>();

		for (SearchHit searchHit : searchHits) {
			String source = searchHit.getSourceAsString();
			if (!StringUtils.isBlank(source)) {
				result.add(JsonUtils.readJson2Object(source, CampaignVO.class));
			}
		}
		return result;
	}

}
