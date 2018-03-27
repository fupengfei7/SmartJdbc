package test.domain.query;

import io.itit.smartjdbc.Query;
import io.itit.smartjdbc.annotations.QueryDefine;
import io.itit.smartjdbc.annotations.QueryField;
import test.domain.info.UserInfo;

/**
 * 
 * @author skydu
 *
 */

@QueryDefine(domainClass=UserInfo.class)
public class UserInfoQuery extends Query{
	//
	public String userName;
	
	public Integer gender;
	
	@QueryField(foreignKeyFields="roleId",field="name")
	public String roleName;
}
