package test.domain.info;

import io.itit.smartjdbc.annotations.DomainDefine;
import io.itit.smartjdbc.annotations.DomainField;
import test.domain.Article;

/**
 * 
 * @author skydu
 *
 */
@DomainDefine(domainClass=Article.class)
public class ArticleInfo extends Article{

	/**创建人名称*/
	@DomainField(foreignKeyFields="createUserId",field="name")
	public String createUserName;
	
	/**创建人所在部门名称*/
	@DomainField(foreignKeyFields="createUserId,departmentId",field="name")
	public String createUserDepartmentName;
	
	/**最后编辑人名称*/
	@DomainField(foreignKeyFields="updateUserId",field="name")
	public String updateUserName;
	
	/**最后编辑人所在部门名称*/
	@DomainField(foreignKeyFields="updateUserId,departmentId",field="name")
	public String updateUserDepartmentName;
	
	@DomainField(ignoreWhenSelect=true)
	public String test;
	
}
