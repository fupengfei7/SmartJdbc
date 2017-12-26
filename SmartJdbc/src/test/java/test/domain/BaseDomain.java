package test.domain;

import java.util.Date;

import io.itit.smartjdbc.annotations.PrimaryKey;

/**
 * 
 * @author skydu
 *
 */
public abstract class BaseDomain {
	
	@PrimaryKey
	public int id;
	
	public Date createTime;
	
	public Date updateTime;
}
