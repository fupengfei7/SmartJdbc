package test.domain;

import io.itit.smartjdbc.annotations.ForeignKey;

/**
 * 文章
 * @author skydu
 *
 */
public class Article extends BaseDomain{
	//
	public static final int STATUS_未发布=1;
	public static final int STATUS_已发布=2;
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
