package test.domain;

import io.itit.smartjdbc.annotations.DomainDefine;

/**
 * 部门
 * @author skydu
 *
 */
@DomainDefine(domainClass=Department.class)
public class Department extends BaseDomain{

	public String name;
	
	public int status;
}
