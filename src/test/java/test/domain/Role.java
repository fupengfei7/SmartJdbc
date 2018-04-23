package test.domain;

import io.itit.smartjdbc.annotations.DomainDefine;

/**
 * 
 * @author skydu
 *
 */
@DomainDefine(domainClass=Role.class)
public class Role extends BaseDomain{

	/**角色名称*/
	public String name;
}
