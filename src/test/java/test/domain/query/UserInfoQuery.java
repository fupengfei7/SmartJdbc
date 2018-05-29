package test.domain.query;

import io.itit.smartjdbc.Query;
import io.itit.smartjdbc.annotations.QueryDefine;
import io.itit.smartjdbc.annotations.QueryField;
import io.itit.smartjdbc.annotations.QueryField.OrGroup;
import test.domain.info.UserInfo;

/**
 * 
 * @author skydu
 *
 */

@QueryDefine(domainClass = UserInfo.class)
public class UserInfoQuery extends Query {
	//
	public String userName;

	public Integer gender;

	@QueryField(foreignKeyFields = "roleId", field = "name")
	public String roleName;
	
	@QueryField(orGroup=@OrGroup(group="or"),field="name")
	public String orName;
	
	@QueryField(orGroup=@OrGroup(group="or"),field="userName")
	public String orUserName;
	//
	// sort fields
	public int nameSort;
	public int userNameSort;
	public int passwordSort;
	public int genderSort;
	public int lastLoginTimeSort;
	public int departmentIdSort;
	public int roleIdSort;
	public int descriptionSort;
}
