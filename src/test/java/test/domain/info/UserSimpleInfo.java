package test.domain.info;

import io.itit.smartjdbc.annotations.DomainDefine;

/**
 * 
 * @author skydu
 *
 */
@DomainDefine(tableName="User")
public class UserSimpleInfo {

	public int id;
	public String name;
}
