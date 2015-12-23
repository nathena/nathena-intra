package me.nathena.infra.wx;

import me.nathena.infra.utils.HttpUtil;
import me.nathena.infra.utils.LogHelper;
import me.nathena.infra.wx.beans.WxTemplate;

import com.alibaba.fastjson.JSONObject;

public class WxSendTemplateMsgService {

	private static String send_api = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=%s";
	
	private WxAccessTokenService wxAccessTokenService;
	
	public WxSendTemplateMsgService(String appid,String appSercet)
	{
		wxAccessTokenService = new WxAccessTokenService(appid,appSercet);
	}
	
	public void send(WxTemplate template)
	{
		String api = String.format(send_api, wxAccessTokenService.getToken().getAccessToken());
		String data = JSONObject.toJSONString(template);
		byte[] result = HttpUtil.doRestPost(api, data);
		
		String res = new String(result);
		
		LogHelper.debug("发送微信文本消息完成 => "+res+" = "+data);
	}
}
