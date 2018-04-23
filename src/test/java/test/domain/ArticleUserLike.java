package test.domain;

import io.itit.smartjdbc.annotations.DomainDefine;
import io.itit.smartjdbc.annotations.ForeignKey;

/**
 * 用户喜爱文章
 * @author skydu
 *
 */
@DomainDefine(domainClass=ArticleUserLike.class)
public class ArticleUserLike extends BaseDomain{
	//
	@ForeignKey(domainClass=Article.class)
	public int articleId;
	
	@ForeignKey(domainClass=User.class)
	public int userId;
	
}
