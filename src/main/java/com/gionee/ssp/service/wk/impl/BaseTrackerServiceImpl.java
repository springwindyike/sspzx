package com.gionee.ssp.service.wk.impl;

import org.springframework.beans.factory.annotation.Value;

/**
 * @author dingyw
 *
 * 2017年9月11日
 */
public class BaseTrackerServiceImpl {
	
	/**
	 * ssp展示监播地址
	 */
	@Value("#{tracker_config.SSP_IMP_URL}")
	protected String imp_tracker_url;
	
	/**
	 * ssp点击监播地址
	 */
	@Value("#{tracker_config.SSP_CLKTRACKER}")
	protected String click_tracker_url;
	
	/**
	 * ssp下载完成监播地址
	 */
	@Value("#{tracker_config.SSP_DWNLTRACKERS}")
	protected String download_finished_tracker_url;
	
	/**
	 * ssp下载开始监播地址
	 */
	@Value("#{tracker_config.SSP_DWNLST}")
	protected String download_start_tracker_url;
	
	/**
	 * ssp安装完成监播地址
	 */
	@Value("#{tracker_config.SSP_INTLTRACKERS}")
	protected String install_finished_tracker_url;
	
	/**
	 * ssp激活监播地址
	 */
	@Value("#{tracker_config.SSP_ACTVTRACKERS}")
	protected String active_tracker_url;
	
    //为了实现直投监播转发，需要对监播地址进行处理,去掉url后面的1.gpg 1.gif
    protected String dealTrackerUrl(String url){
    	return url.substring(0, url.lastIndexOf("/"));
    }
}
