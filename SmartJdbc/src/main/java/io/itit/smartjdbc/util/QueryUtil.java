package io.itit.smartjdbc.util;

import java.util.Collection;
import java.util.List;

import io.itit.smartjdbc.QueryTerms;

/**
 * 
 * @author skydu
 *
 */
public class QueryUtil {

	/**
	 * 
	 * @param queryTerms
	 * @param field
	 * @param values
	 * @return
	 */
	public static QueryTerms addInCondition(QueryTerms queryTerms, String field, List<? extends Object> values) {
		return addInCondition(queryTerms, field, true, values);
	}

	/**
	 * 
	 * @param queryTerms
	 * @param field
	 * @param withFieldEscape
	 * @param values
	 * @return
	 */
	public static QueryTerms addInCondition(QueryTerms queryTerms, String field, boolean withFieldEscape,
			List<? extends Object> values) {
		Object[] valueArray = null;
		if (values != null) {
			valueArray = values.toArray();
		}
		return addInCondition(queryTerms, field, withFieldEscape, valueArray);
	}

	/**
	 * 
	 * @param queryTerms
	 * @param field
	 * @param withFieldEscape
	 * @param values
	 * @return
	 */
	public static QueryTerms addInCondition(QueryTerms queryTerms, String field, boolean withFieldEscape,
			Object[] values) {
		if (values == null) {
			return queryTerms;
		}
		int len = values.length;
		if (len == 0) {
			return queryTerms;
		}
		StringBuffer buffer = new StringBuffer();
		buffer.append(" and ");
		if (withFieldEscape) {
			buffer.append("`");
			buffer.append(field);
			buffer.append("`");
		} else {
			buffer.append(field);
		}
		buffer.append(" in ( ");
		for (int i = 0; i < len; i++) {
			buffer.append(" ?,");
		}
		buffer.deleteCharAt(buffer.length() - 1);
		buffer.append(" ) ");
		return queryTerms.whereSql(buffer.toString(), values);
	}
	
	/**
	 * 
	 * @param queryTerms
	 * @param field
	 * @param withFieldEscape
	 * @param values
	 * @return
	 */
	public static QueryTerms addNotInCondition(QueryTerms queryTerms, String field, boolean withFieldEscape,
			List<? extends Object> values) {
		Object[] valueArray = null;
		if (values != null) {
			valueArray = values.toArray();
		}
		return addNotInCondition(queryTerms, field, withFieldEscape, valueArray);
	}
	
	public static QueryTerms addNotInCondition(QueryTerms queryTerms, String field, List<? extends Object> values) {
        return addNotInCondition(queryTerms, field, true, values);
    }
	
	/**
	 * 
	 * @param queryTerms
	 * @param field
	 * @param withFieldEscape
	 * @param values
	 * @return
	 */
	public static QueryTerms addNotInCondition(QueryTerms queryTerms, String field, boolean withFieldEscape,
			Object[] values) {
		if (values == null) {
			return queryTerms;
		}
		int len = values.length;
		if (len == 0) {
			return queryTerms;
		}
		StringBuffer buffer = new StringBuffer();
		buffer.append(" and ");
		if (withFieldEscape) {
			buffer.append("`");
			buffer.append(field);
			buffer.append("`");
		} else {
			buffer.append(field);
		}
		buffer.append(" not in ( ");
		for (int i = 0; i < len; i++) {
			buffer.append(" ?,");
		}
		buffer.deleteCharAt(buffer.length() - 1);
		buffer.append(" ) ");
		return queryTerms.whereSql(buffer.toString(), values);
	}

	/**
	 * 
	 * @param queryTerms
	 * @param field
	 * @param values
	 * @return
	 */
	public static QueryTerms addInCondition(QueryTerms queryTerms, String field, Collection<?> values) {
		return addInCondition(queryTerms, field, true, values);
	}
	
	/**
	 * 
	 * @param queryTerms
	 * @param field
	 * @param withFieldEscape
	 * @param values
	 * @return
	 */
	public static QueryTerms addInCondition(QueryTerms queryTerms, String field, boolean withFieldEscape,
			Collection<?> values) {
		Object[] valueArray = null;
		if (values != null) {
			valueArray = values.toArray();
		}
		return addInCondition(queryTerms, field, withFieldEscape, valueArray);
	}
}
