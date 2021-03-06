package com.gionee.ssp.service.shield.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gionee.ssp.service.conf.ad.AdConfService;
import com.gionee.ssp.service.shield.ShieldService;
import com.gionee.ssp.vo.SSPAdShieldConfigVo;
import com.wk.exception.Errors;
import com.wk.ssp.mvc.Constant;
import com.wk.ssp.utils.local.ThreadLocalManager;
import com.wk.ssp.vo.FillingDataVO;
import com.wk.ssp.vo.sdk.SdkRequestVO;
import com.wk.ssp.vo.sdk.SdkResponseVO;

/**
 * @author dingyw
 *
 *         2017年9月5日
 */
@Service
public class ShieldServiceImpl implements ShieldService {

	/**
	 * 广告位配置服务层
	 */
	@Autowired
	AdConfService adConfService;

	/**
	 * @Title: isNeedShield
	 * @Desc {根据请求参数&广告屏蔽配置，判断该请求是否需要屏蔽}
	 * @param req
	 * @param p
	 * @retrun boolean
	 * @author zhengk
	 * @throws Exception
	 * @date 2017年8月22日 上午11:39:05
	 */
	public boolean isNeedShield(FillingDataVO fillingDataVO, SdkRequestVO req, SdkResponseVO rsp) throws Exception {
		
		String requestId = ThreadLocalManager.getLocal().getRequest_id();
        rsp.setRequest_id(requestId); // 填充requestID
		// 获取redis中存放的广告屏蔽配置信息,并判断该请求是否需要进行屏蔽
		SSPAdShieldConfigVo shieldCfg = adConfService.getAdShieldCfg(Constant.REDIS_KEY + Constant.SSP_AD_SHIELD_CFG);
		if (shieldCfg == null) {
			shieldCfg = new SSPAdShieldConfigVo();
		}
		List<String> imei_List = shieldCfg.getImei_list();
		List<String> model_list = shieldCfg.getModel_list();
		List<String> new_user_white_list = shieldCfg.getNew_user_white_list();
		List<String> adSheildModel = fillingDataVO.getbModels();// 广告位配置的需要屏蔽的机型

		boolean matched = false;
		// sdk上传数据is_new_user=1 同时设置的屏蔽天数>0,
		// update by qiancc 2017-9-23 同时不在新用户imei白名单列表中才进行新用户屏蔽
		if (req.getSdkInfoVO().getIs_new_user() == 1 && shieldCfg.getDays() > 0) {
			if (CollectionUtils.isEmpty(new_user_white_list)) {// 如果白名单为空,
				matched = true;
			} else {// 否则判断当前请求的设备的imei号,是否在白名单列表中,如果不在,则屏蔽广告;
				matched = !new_user_white_list.contains(req.getDevice().getImei_md5());
			}
			if (matched) {
				// 需要屏蔽广告的新用户，将屏蔽的天数返回给sdk
				rsp.setNo_ad_days(shieldCfg.getDays());
			}
		}
		if (!matched && CollectionUtils.isNotEmpty(imei_List)) {
			matched = imei_List.contains(req.getDevice().getImei_md5());
		}
		if (!matched && CollectionUtils.isNotEmpty(model_list)) {
			matched = model_list.contains(req.getDevice().getModel());
		}
		if (!matched && CollectionUtils.isNotEmpty(adSheildModel)) {
			matched = adSheildModel.contains(req.getDevice().getModel());
		}
		if (matched) {
			rsp.setError_code(Errors.NO_CONTENT);
			rsp.setAdms(new ArrayList<>());
			return true;
		} else {
			return false;
		}
	}

}
