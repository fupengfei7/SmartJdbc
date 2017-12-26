package io.itit.smartjdbc;

/**
 * 
 * @author skydu
 *
 */
public abstract class DAOInterceptor {

	public void beforeInsert(Object bean,boolean withGenerateKey,String[] excludeProperties) {
	}
	
	public void afterInsert(int result,Object bean,boolean withGenerateKey,String[] excludeProperties) {
	}
	
	public void beforeUpdate(Object bean, boolean excludeNull, String[] excludeProperties) {
	}
	
	public void afterUpdate(int result, Object bean, boolean excludeNull, String[] excludeProperties) {
	}
	
	public void beforeDelete(Class<?> domainClass, QueryTerms qt) {
	}

	public void afterDelete(int result, Class<?> domainClass, QueryTerms qt) {
	}
}
