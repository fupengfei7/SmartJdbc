package test.domain;

import io.itit.smartjdbc.annotations.ForeignKey;

/**
 * 可追踪对象
 * @author skydu
 *
 */
public class TraceableDomain extends BaseDomain{
	//
	public String name;
	
	@ForeignKey(domainClass=User.class)
	public int createUserId;
	
	@ForeignKey(domainClass=User.class)
	public int updateUserId;
	
	@ForeignKey(domainClass=User.class)
	public int ownerUserId;
	
	public boolean isDelete;
}
