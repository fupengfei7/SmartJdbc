package test.domain.info;

import io.itit.smartjdbc.annotations.DomainDefine;
import io.itit.smartjdbc.annotations.DomainField;

/**
 * 
 * @author skydu
 *
 */
@DomainDefine(tableName="User")
public class UserStat {

	public int gender;

	@DomainField(statFunc="count",field="id")
	public int num;
}
