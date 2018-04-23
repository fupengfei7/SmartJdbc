package test.domain;

import io.itit.smartjdbc.annotations.DomainDefine;
import io.itit.smartjdbc.annotations.ForeignKey;

/**
 * 文章
 * @author skydu
 *
 */
@DomainDefine(domainClass=Article.class)
public class Article extends BaseDomain{
	//
	public static final int STATUS_待审核=1;
	public static final int STATUS_审核通过=2;
	public static final int STATUS_审核未通过=3;
	//
	/**标题*/
	public String title;
	
	/**内容*/
	public String content;
	/**状态*/
	public int status;

	@ForeignKey(domainClass = User.class)
	public int createUserId;
	
	@ForeignKey(domainClass = User.class)
	public int updateUserId;
	
}
