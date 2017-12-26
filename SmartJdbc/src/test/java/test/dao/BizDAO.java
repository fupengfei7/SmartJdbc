package test.dao;

import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.itit.smartjdbc.QueryTerms;
import io.itit.smartjdbc.SmartJdbcException;
import io.itit.smartjdbc.dao.SmartDAO;

/**
 * 
 * @author skydu
 *
 */
public class BizDAO extends SmartDAO{

	//
	private static final Logger logger=LoggerFactory.getLogger(BizDAO.class);
	/**
	 * 
	 * @param bean
	 * @return
	 */
	public int add(Object bean){
		return insert(bean, true, "id");
	}
	
	/**
	 * 
	 * @param bean
	 * @return
	 */
	public int update(Object bean){
		return update(bean,"id");
	}
	/**
	 * 
	 * @param id
	 */
	public void deleteById(Class<?> domainClass,int id){
		delete(domainClass,QueryTerms.create().where("id", id));
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	public <T> T getById(Class<T> domainClass,int id){
		return query(domainClass,QueryTerms.create().where("id",id));
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	public <T> T getExistedById(Class<T> domainClass,int id){
		T bean=getById(domainClass,id);
		if(bean==null) {;
			throw new SmartJdbcException("数据不存在");
		}
		return bean;
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	public <T> T getByIdForUpdate(Class<T> domainClass,int id){
		return query(domainClass,QueryTerms.create().where("id",id).whereSql(" for update"));
	}
	//
	/**
	 * 
	 * @param bean
	 * @param fieldName
	 * @param value
	 */
	public void setFieldValue(Object bean,String fieldName,Object value){
		try {
			Field field=bean.getClass().getField(fieldName);
			if(field!=null) {
				field.set(bean, value);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}
	
	/**
	 * 
	 * @param bean
	 * @param fieldName
	 * @return
	 */
	protected Object getFieldValue(Object bean,String fieldName){
		try {
			Field field=bean.getClass().getField(fieldName);
			if(field!=null) {
				return field.get(bean);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return null;
	}
	//
}
