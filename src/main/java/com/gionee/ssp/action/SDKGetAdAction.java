package com.gionee.ssp.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gionee.ssp.service.ad.AdService;
import com.wk.ssp.mvc.exception.BusinessException;
import com.wk.ssp.utils.JsonUtils;
import com.wk.ssp.utils.log.LogInfo;
import com.wk.ssp.utils.log.WKLogManager;
import com.wk.ssp.vo.FillingDataVO;
import com.wk.ssp.vo.sdk.SdkRequestVO;
import com.wk.ssp.vo.sdk.SdkResponseVO;


/**查询广告的action
 * @author dingyw
 *
 * 2017年9月5日
 */
@Component
public class SDKGetAdAction extends BaseAction{
	/**
	 * 广告服务层
	 */
	@Autowired
	AdService adService;
	

	/**获取广告
	 * @param json
	 * @return
	 * @throws Exception
	 */
	public SdkResponseVO getAd(String json) throws Exception {

		LogInfo logInfo = WKLogManager.getLOG();
		SdkRequestVO req = null;

		SdkResponseVO rsp = new SdkResponseVO();
		try {
			req = JsonUtils.readJson2Object(json, SdkRequestVO.class);
		} catch (Exception e) {
			throw new BusinessException(10000);
		}
		//获取请求ID
		String req_id=this.getReqId(req, logInfo);

		// 请求参数校验
		validateService.validateVo(req);
		
		//根据不同的请求来源，获取不同的IP
		clientIpService.setIp(req);

		// 添加sdk相关数据
		sdkInfoService.setSDKInfo(req);
		
		// 获取取广告位信息和应用app信息
		FillingDataVO fillingDataVO = adConfService.getAdConfData(req);
		
		//判断是否需要屏蔽广告，如果需要，则返回无广告
		if(true==shieldService.isNeedShield(fillingDataVO,req, rsp)){
			logInfo.addReqAdLog("req_shield", "YES");
			return rsp;
		}

		// 日志中增加请求个数
		logInfo.addReqAdLog("req_ad_cnt", String.valueOf(fillingDataVO.getAd_cnt()));

		// 核心流程,获取广告
		rsp = adService.getAd(fillingDataVO, req, req_id);

		// 判断是否有广告返回, 没有的话, 直接返回null
		if (rsp == null) {
			return null;
		}

		//设置单个imei用户一天内看到的广告次数和请求广告的时间间隔.
		rsp.setShow_ad_times_one_day(fillingDataVO.getShow_ad_times_one_day());
		rsp.setShow_ad_interval(fillingDataVO.getShow_ad_interval());

		// 添加SSP监播信息和日志信息
		sdkTrackerService.insertTrackers(rsp, req, fillingDataVO);

		//额外的返回信息设置
		sdkRspService.setAdRspInfo(rsp, req);

		return rsp;
	}
	
}
