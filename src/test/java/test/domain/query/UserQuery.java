package test.domain.query;

import io.itit.smartjdbc.Query;
import io.itit.smartjdbc.annotations.QueryDefine;
import io.itit.smartjdbc.annotations.QueryField;
import test.domain.User;

/**
 * 
 * @author skydu
 *
 */
@QueryDefine(domainClass=User.class)
public class UserQuery extends Query{

	public String userName;
	
	public Integer gender;
	
	@QueryField(field="name,userName")
	public String nameOrUserName;
}
