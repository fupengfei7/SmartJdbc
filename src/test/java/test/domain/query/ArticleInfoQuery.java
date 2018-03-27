package test.domain.query;

import io.itit.smartjdbc.Query;
import io.itit.smartjdbc.annotations.InnerJoin;
import io.itit.smartjdbc.annotations.QueryDefine;
import io.itit.smartjdbc.annotations.QueryField;
import test.domain.ArticleUserLike;
import test.domain.User;
import test.domain.info.ArticleInfo;

/**
 * 
 * @author skydu
 *
 */
@QueryDefine(domainClass=ArticleInfo.class)
public class ArticleInfoQuery extends Query{

	public String title;
	
	public Integer status;
	
	@InnerJoin(table2=User.class,table1Field="createUserId")
	@QueryField(field="name")
	public String createUserName;


	@InnerJoin(table2=User.class,table1Field="updateUserId")
	@QueryField(field="name")
	public String updateUserName;
	
	@QueryField(field="status")
	public int[] statusList;
	
	@QueryField(field="name",foreignKeyFields="createUserId,departmentId")
	public String createUserDepartmentName;
	
	/**likeUserId喜爱的文章*/
	@InnerJoin(table2=ArticleUserLike.class,table2Field="articleId")
	@QueryField(field="userId")
	public Integer likeUserId;
}
