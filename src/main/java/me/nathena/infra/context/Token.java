package me.nathena.infra.context;



/**
 * The persistent class for the way_sys_dtssession database table.
 * 
 */
public abstract class Token {

	private String ip;
	private long   id;
	private long activetime;
	
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getActivetime() {
		return activetime;
	}
	public void setActivetime(long activetime) {
		this.activetime = activetime;
	}
	
	
}