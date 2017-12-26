package test.domain;

import java.util.Date;

import io.itit.smartjdbc.annotations.ForeignKey;

/**
 * 
 * @author skydu
 *
 */
public class User extends BaseDomain{
	//
	public String name;
	
	public String userName;
	
	public String password;
	
	public boolean gender;
	
	public Date lastLoginTime;
	
	@ForeignKey(domainClass=Department.class)
	public int departmentId;
	
	@ForeignKey(domainClass=Role.class)
	public int roleId;
	
	public String description;
	
}
