package com.gionee.ssp.action;

import org.springframework.beans.factory.annotation.Autowired;

import com.gionee.ssp.service.ad.AdInfoService;
import com.gionee.ssp.service.conf.ad.AdConfService;
import com.gionee.ssp.service.ip.ClientIpService;
import com.gionee.ssp.service.sdk.rsp.SdkRspService;
import com.gionee.ssp.service.sdk.rsp.SdkTrackerService;
import com.gionee.ssp.service.shield.ShieldService;
import com.gionee.ssp.service.wk.SdkInfoService;
import com.gionee.ssp.service.wk.ValidateService;
import com.wk.ssp.utils.GeneratorUtils;
import com.wk.ssp.utils.local.LocalInfo;
import com.wk.ssp.utils.local.ThreadLocalManager;
import com.wk.ssp.utils.log.LogInfo;
import com.wk.ssp.utils.log.LogLevel;
import com.wk.ssp.vo.sdk.SdkRequestVO;

/**
 * @author dingyw
 *
 * 2017年9月5日
 */
public class BaseAction {
	
	/**
	 * 屏蔽广告服务层
	 */
	@Autowired
	ShieldService shieldService;
	
	/**
	 *校验服务层 
	 */
	@Autowired
	ValidateService validateService;
	/**
	 * redis信息层
	 */
	@Autowired
	AdConfService adConfService;
	
	/**
	 * 信息查询服务层
	 */
	@Autowired
	AdInfoService adInfoService;
	/**
	 * 获取SDK的信息
	 */
	@Autowired
	SdkInfoService sdkInfoService;
	
	/**
	 * 返回信息服务层
	 */
	@Autowired
	SdkRspService sdkRspService;
	
	/**
	 * 添加监播服务层
	 */
	@Autowired
	SdkTrackerService sdkTrackerService;
	
	/**
	 * IP服务层，根据不同的请求端，获取不同的IP
	 */
	@Autowired
	ClientIpService clientIpService;
	
	//获取req_id
	protected String getReqId(SdkRequestVO req,LogInfo logInfo) throws Exception{
		LocalInfo localInfo = ThreadLocalManager.getLocal();

		// 记录request_id
		String req_id = GeneratorUtils.generateRequestId(req);
		localInfo.setRequest_id(req_id);
		logInfo.addrequestId(req_id, LogLevel.INFO); // 记录requestId
		
		return req_id;
	}

}
