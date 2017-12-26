package test.domain.info;

import io.itit.smartjdbc.annotations.DomainDefine;
import io.itit.smartjdbc.annotations.DomainField;
import test.domain.User;

/**
 * 
 * @author skydu
 *
 */
@DomainDefine(tableClass=User.class)
public class UserInfo extends User{

	@DomainField(foreignKeyFields="departmentId",field="name")
	public String departmentName;
	
	@DomainField(foreignKeyFields="roleId",field="name")
	public String roleName;
}
