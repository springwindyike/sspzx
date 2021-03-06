package com.gionee.ssp.service.ad.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gionee.ssp.common.CommonConstant;
import com.gionee.ssp.service.ad.AdService;
import com.gionee.ssp.service.adFlow.AdFlowModeSelectService;
import com.wk.exception.Errors;
import com.wk.ssp.mvc.Constant;
import com.wk.ssp.utils.log.WKLogManager;
import com.wk.ssp.vo.FillingDataVO;
import com.wk.ssp.vo.sdk.SdkRequestVO;
import com.wk.ssp.vo.sdk.SdkResponseVO;

/**
 * 广告服务层
 * 
 * @author dingyw
 *
 *         2017年4月19日
 *         
 * modified by zhengk增加对SDK版本控制逻辑
 */
@Service
public class AdServiceImpl extends BaseAdServiceImpl implements AdService {

	/**
	 * 广告流量控制服务层
	 */
	@Autowired
	AdFlowModeSelectService adFlowModeSelectService;

	@Override
	public SdkResponseVO getAd(FillingDataVO vo, SdkRequestVO req, String req_id) throws Exception {

		//TODO 由于目前是所有的广告请求都先查询直投，然后再根据配置是否开启竞价开关，决定是否请求竞价广告
		//TODO 下面代码的逻辑是可以优化的
		
		// 判断本次请求是否请求直投广告
		boolean isIpush = adInfoService.isIpush(vo); // 是否为直投广告

		// 若应用审核状态为未通过，返回测试广告
		if (Constant.APP_NOT_PASS_AUDIT == vo.getStatus()) {
			// 生成测试响应对象
			return debugService.getDebugAd(req, vo);
		}

		// 指定本次请求为正式请求
		WKLogManager.getLOG().addReqAdLog("is_debug", "0");

		SdkResponseVO rsp = null;
		if (isIpush) {
			// 只获取直投广告
			rsp = pushAdService.getPushAd(req, req_id, vo);
			// 设置返回信息中的adx字段
			pushRspService.setPushRsp(rsp);
			vo.setIs_ipush(CommonConstant.IS_PUSH.TRUE.getValue());
		} else {
			// 先获取直投广告, 直投广告获取不到, 再请求竞价广告
			rsp = pushAdService.getPushAd(req, req_id, vo);
			if (rsp == null || Errors.NO_CONTENT == rsp.getError_code()) {
				// 根据广告流量模式配置，到第三方或金立ADX获取广告
				rsp = adFlowModeSelectService.getAd(vo, req);
				vo.setIs_ipush(CommonConstant.IS_PUSH.FALSE.getValue());
			} else { //是push
				// 设置返回信息中的adx字段
				pushRspService.setPushRsp(rsp);
				vo.setIs_ipush(CommonConstant.IS_PUSH.TRUE.getValue());
			}
		}
		WKLogManager.getLOG().addReqAdLog("ipush", String.valueOf(vo.getIs_ipush()));

		//浮标广告处理
		floatAdService.domainFilter(vo, req, rsp);
		// 设置返回信息
		// 版本根据sdk版本控制clkurl，并且设置interaction_type
		// 如果是1.7.7以上版本，则当交互类型是应用分发（2）时，根据设置的子类型重新设定interaction_type
		adRspService.setAdRsp(vo,req, rsp);

		// 如果是锁屏,设置图片md5
		adMd5RspService.setImageMd5(req, rsp);

		return rsp;
	}

}
