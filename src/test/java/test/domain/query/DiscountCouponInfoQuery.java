package test.domain.query;

import io.itit.smartjdbc.Query;
import io.itit.smartjdbc.annotations.InnerJoin;
import io.itit.smartjdbc.annotations.InnerJoins;
import io.itit.smartjdbc.annotations.QueryDefine;
import io.itit.smartjdbc.annotations.QueryField;
import test.domain.Department;
import test.domain.User;
import test.domain.info.DiscountCouponInfo;

/**
 * 
 * @author skydu
 *
 */
@QueryDefine(domainClass=DiscountCouponInfo.class)
public class DiscountCouponInfoQuery extends Query{

	public String name;
	
	@InnerJoin(table2=User.class,table1Field="createUserId")
	@QueryField(field="name")
	public String createUserName;


	@InnerJoin(table2=User.class,table1Field="updateUserId")
	@QueryField(field="name")
	public String updateUserName;
	
	@QueryField(field="status")
	public int[] statusList;
	
	@InnerJoins(innerJoins={
			@InnerJoin(table2=User.class,table1Field="updateUserId"),
			@InnerJoin(table2=Department.class,table1Field="departmentId")})
	@QueryField(field="name")
	public String updateUserDepartmentName;

	
	@InnerJoins(innerJoins={
			@InnerJoin(table2=User.class,table1Field="updateUserId"),
			@InnerJoin(table2=Department.class,table1Field="departmentId")})
	@QueryField(field="status")
	public Integer updateUserDepartmentStatus;
	
	@QueryField(field="name",foreignKeyFields="updateUserId,departmentId")
	public String updateUserDepartmentName2;

	@QueryField(field="status",foreignKeyFields="updateUserId,departmentId")
	public Integer updateUserDepartmentStatus2;
	
	@QueryField(field="name",foreignKeyFields="updateUserId,departmentId")
	public String updateUserDepartmentName3;
}
