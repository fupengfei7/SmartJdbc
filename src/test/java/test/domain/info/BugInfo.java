package test.domain.info;

import io.itit.smartjdbc.annotations.DomainDefine;
import io.itit.smartjdbc.annotations.DomainField;
import test.domain.Bug;

/**
 * 
 * @author skydu
 *
 */
@DomainDefine(comment="Bug信息",domainClass=Bug.class)
public class BugInfo extends Bug{
	//
	@DomainField(foreignKeyFields="createUserId",field="name")
	public String createUserName;
	
	@DomainField(foreignKeyFields="createUserId",field="departmentName")
	public String createUserDepartmentName;
	
	@DomainField(foreignKeyFields="createUserId",field="imageId")
	public String createUserImageId;

	@DomainField(foreignKeyFields="updateUserId",field="name")
	public String updateUserName;
	
	@DomainField(foreignKeyFields="updateUserId",field="departmentName")
	public String updateUserDepartmentName;
	
	
	@DomainField(foreignKeyFields="ownerUserId",field="name")
	public String ownerUserName;
	
	@DomainField(foreignKeyFields="fixUserId",field="name")
	public String fixUserName;
	
	@DomainField(foreignKeyFields="ownerUserId",field="departmentName")
	public String ownerUserDepartmentName;
	
	@DomainField(foreignKeyFields="ownerUserId",field="imageId")
	public String ownerUserImageId;
}
