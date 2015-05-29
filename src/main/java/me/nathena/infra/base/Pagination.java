package me.nathena.infra.base;

import java.io.Serializable;
import java.util.List;
public class Pagination<T> implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Integer total = 0;			//总数
	private List<T> rows;				//行信息
	public Integer getTotal() {
		return total;
	}
	public void setTotal(Integer total) {
		this.total = total;
	}
	public List<T> getRows() {
		return rows;
	}
	public void setRows(List<T> rows) {
		this.rows = rows;
	}
}
