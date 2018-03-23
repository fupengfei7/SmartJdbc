package test.domain.query;

import io.itit.smartjdbc.Query;
import io.itit.smartjdbc.annotations.QueryDefine;
import io.itit.smartjdbc.annotations.QueryField;
import test.domain.info.BugInfo;

/**
 * 
 * @author skydu
 *
 */
@QueryDefine(domainClass=BugInfo.class)
public class BugInfoQuery extends Query{

	public Integer id;
	
	public String name;
	
	public Integer releasePlanId;
	
	public Integer subSystemId;
	
	public Integer productId;
	
	public Integer subSystemVersionId;

	public Integer status;

	@QueryField(field="status")
	public int[] inStatusList;

	@QueryField(field="status",operator="not in")
	public int[] notInstatus;
	
	@QueryField(operator="!=",field="status")
	public Integer excludeStatus;

	public Integer type;
	
	public Integer level;

	public Integer version;
	
	public Integer ownerUserId;
	
	public Integer createUserId;
	
	@QueryField(whereSql=" and (fix_user_id=#{fixUserIdOrCloseUserId} or close_user_id=#{fixUserIdOrCloseUserId} ) ")
	public Integer fixUserIdOrCloseUserId;
	
	public Integer foundStep;

	@QueryField(foreignKeyFields="createUserId",field="name")
	public String createUserName;

	@QueryField(foreignKeyFields="fixUserId",field="name")
	public String fixUserName;
	
	//sort fields
	public int createUserNameSort;
	public int createUserDepartmentNameSort;
	public int createUserImageIdSort;
	public int updateUserNameSort;
	public int updateUserDepartmentNameSort;
	public int contentSort;
	public int ownerUserNameSort;
	public int fixUserNameSort;
	public int ownerUserDepartmentNameSort;
	public int ownerUserImageIdSort;
    public int foundStepNameSort;
    public int levelNameSort;
    public int statusNameSort;
    public int productNameSort;
    public int releasePlanNameSort;
	public int solutionTypeNameSort;
    public int releasePlanIdSort;
	public int statusSort;
    public int typeSort;
    public int levelSort;
    public int foundStepSort;
    public int confirmTimeSort;
    public int fixUserIdSort;
	public int fixedTimeSort;
    public int closeUserIdSort;
    public int closeTimeSort;
    public int testPlanIdSort;
    public int subSystemIdSort;
    public int subSystemVersionIdSort;
    public int productIdSort;
    public int solutionTypeSort;
    public int testPlanTestCaseIdSort;
	public int nameSort;
	public int createUserIdSort;
	public int updateUserIdSort;
	public int ownerUserIdSort;
	public int detailTextIdSort;
	public int isDeleteSort;
	public int idSort;
	public int createTimeSort;
	public int updateTimeSort;
}