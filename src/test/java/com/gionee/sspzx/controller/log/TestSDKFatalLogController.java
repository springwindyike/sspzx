package com.gionee.sspzx.controller.log;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.gionee.sspzx.controller.BaseTestSDKController;



/**自行构造SDK参数
 * @author dingyw
 *
 * 2017年3月22日
 */
public class TestSDKFatalLogController extends BaseTestSDKController{
	public void execute() throws IOException {
		//String url="http://localhost:8080/ssppb/RBI/error";
		String url="http://sspzx.ssptest.gionee.com/RBI/error";
		StringBuffer req_buf=new StringBuffer();
		
		CloseableHttpClient httpclient=null;
		CloseableHttpResponse  response=null;
		try {

			httpclient = HttpClients.createDefault();    
			req_buf.append("?svr=1.7.7&device=3434&cuid=3443&client_id=4334&device_id=4343&app_id=1234&error_info=34343");
            String uri =req_buf.toString();
            
            String url_enc=url+uri;
            System.out.println(url_enc);
            HttpGet httpGet = new HttpGet(url_enc); 
            response = httpclient.execute(httpGet); 
            // 获取响应实体      
            HttpEntity entity = response.getEntity();  
            // 打印响应状态      
            System.out.println(response.getStatusLine().getStatusCode());    
            if (entity != null) {    
                // 打印响应内容      
                System.out.println("Response content: " + EntityUtils.toString(entity));    
            }  
		} catch (Exception e) {
			e.printStackTrace();
		}finally{  
            httpclient.close();  
            response.close();  
        }  
	}
	public static void main(String[] args) {
		TestSDKFatalLogController t=new TestSDKFatalLogController();
		try {
			t.execute();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
