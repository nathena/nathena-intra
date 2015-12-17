package me.nathena.infra.wx;

import com.alibaba.fastjson.JSONObject;

import me.nathena.infra.utils.HttpUtil;
import me.nathena.infra.utils.LogHelper;
import me.nathena.infra.wx.beans.WxAccessToken;
import me.nathena.infra.wx.beans.WxTemplate;

public class WxSendTemplateMsgService {

	private static String send_api = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=%s";
	
	private WxAccessToken token;
	
	public WxSendTemplateMsgService(WxAccessToken token)
	{
		this.token = token;
	}
	
	public void send(WxTemplate template)
	{
		String api = String.format(send_api, token.getAccessToken());
		String data = JSONObject.toJSONString(template);
		byte[] result = HttpUtil.doRestPost(api, data);
		
		String res = new String(result);
		
		LogHelper.debug("发送微信文本消息完成 => "+res+" = "+data);
	}
}
