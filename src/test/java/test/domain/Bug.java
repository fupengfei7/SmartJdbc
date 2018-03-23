package test.domain;

import java.util.Date;

import io.itit.smartjdbc.annotations.ForeignKey;

/**
 * Created by yiys on 17/11/20.
 */
public class Bug extends TraceableDomain{

//    public static final int STATUS_新建 = 1;
    public static final int STATUS_已分配 = 2;
    public static final int STATUS_已确认 = 3;
    public static final int STATUS_已解决 = 4;
    public static final int STATUS_已关闭 = 5;
    public static final int STATUS_已拒绝 = 6;

    public static final int TYPE_代码问题 = 1;
    public static final int TYPE_环境问题 = 2;
    public static final int TYPE_配置问题 = 3;
    public static final int TYPE_疑问 = 4;
    public static final int TYPE_优化建议 = 5;

    public static final int FOUND_STEP_SIT= 1;
    public static final int FOUND_STEP_UAT = 2;
    public static final int FOUND_STEP_回归测试 = 3;
    public static final int FOUND_STEP_冒烟测试 = 4;
    public static final int FOUND_STEP_集成测试 = 5;
    
    public static final int SOLUTION_TYPE_已解决=1;
    public static final int SOLUTION_TYPE_重复bug=2;
    public static final int SOLUTION_TYPE_外部原因=3;
    public static final int SOLUTION_TYPE_设计如此=4;
    public static final int SOLUTION_TYPE_无法重现=5;
    public static final int SOLUTION_TYPE_延期处理=6;
    public static final int SOLUTION_TYPE_不予解决=7;

 	public int status;
    
    public int type;
    
    public int level;
    
    public int foundStep;
    
    public Date confirmTime;
    
    @ForeignKey(domainClass=User.class)
    public int fixUserId;
    
    public Date fixedTime;
    
    @ForeignKey(domainClass=User.class)
    public int closeUserId;
    
    public Date closeTime;
    
 }
