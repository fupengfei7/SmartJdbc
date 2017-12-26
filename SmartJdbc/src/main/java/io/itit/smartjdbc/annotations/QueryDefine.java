package io.itit.smartjdbc.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @author skydu
 *
 */
@Target(ElementType.TYPE)  
@Retention(RetentionPolicy.RUNTIME)  
@Documented
@Inherited  
public @interface QueryDefine {
	//
	public @interface OrderBy {
		  public int orderType();
		  public String sql();
	}
	public Class<?> domainClass() default Void.class;
	public OrderBy[] orderBys() default {};
}
