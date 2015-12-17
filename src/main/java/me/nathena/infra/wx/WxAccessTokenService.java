package me.nathena.infra.wx;

import me.nathena.infra.utils.DateTimeUtil;
import me.nathena.infra.utils.HttpUtil;
import me.nathena.infra.utils.NumberUtil;
import me.nathena.infra.utils.StringUtil;
import me.nathena.infra.wx.beans.WxAccessToken;
import me.nathena.infra.wx.exception.WxException;
import me.nathena.infra.wx.exception.WxExceptionMsg;

import com.alibaba.fastjson.JSONObject;

public class WxAccessTokenService {

	private static final String access_token_api = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";
	
	private String appid;
	private String appSercet;
	private WxAccessToken token;
	
	public WxAccessTokenService(String appid,String appSercet)
	{
		if( StringUtil.isEmpty(appid) || StringUtil.isEmpty(appSercet) )
		{
			throw new WxException(WxExceptionMsg.not_null);
		}
		
		this.appid = appid;
		this.appSercet = appSercet;
	}
	
	public WxAccessToken getAccessToken()
	{
		if( null != token && DateTimeUtil.getTimeStamp() - token.getCreateIn() <= token.getExpiresIn() )
		{
			return token;
		}
		
		String api = String.format(access_token_api, appid,appSercet);
		String result = HttpUtil.doGet(api, null);
		
		if( StringUtil.isEmpty(result) )
		{
			throw new WxException(WxExceptionMsg.wx_access_token_response_nothing);
		}
		
		JSONObject access_token_json = JSONObject.parseObject(result);
		String access_token = access_token_json.getString("access_token");
		
		if( StringUtil.isEmpty(access_token) )
		{
			throw new WxException(WxExceptionMsg.wx_access_token_response_nothing);
		}
		
		token = new WxAccessToken();
		token.setAccessToken(access_token);
		token.setExpiresIn(NumberUtil.parseInt(access_token_json.getString("expires_in")));
		token.setCreateIn(DateTimeUtil.getTimeStamp());
		token.setAppId(appid);
		token.setAppSercet(appSercet);
		
		return token;
	}
}
