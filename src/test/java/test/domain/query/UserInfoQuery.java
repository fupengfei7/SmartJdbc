package test.domain.query;

import javax.management.relation.Role;

import io.itit.smartjdbc.Query;
import io.itit.smartjdbc.annotations.InnerJoin;
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
	
	@InnerJoin(table2=Role.class,table1Field="roleId")
	@QueryField(field="name")
	public String roleName;
}
