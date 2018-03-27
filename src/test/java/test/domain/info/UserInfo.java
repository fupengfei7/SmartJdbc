package test.domain.info;

import io.itit.smartjdbc.annotations.DomainField;
import test.domain.User;

/**
 * 用户详情
 * @author skydu
 *
 */
public class UserInfo extends User{

	@DomainField(foreignKeyFields="departmentId",field="name")
	public String departmentName;
	
	@DomainField(foreignKeyFields="roleId",field="name")
	public String roleName;
}
