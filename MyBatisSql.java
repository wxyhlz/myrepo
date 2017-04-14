
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.shein.ims.core.resource.mapper.ResourceMapper;
import com.shein.ims.utils.DateUtils;

@Component
public class MyBatisSql {

	private final Logger logger = LoggerFactory.getLogger(MyBatisSql.class);
	
	private final String sqlId = ResourceMapper.class.getName() + ".selectExportResEntities";
	
	@Autowired
	private SqlSessionFactory sqlSessionFactory;
	
	public void execute() throws Exception {
		Map<String, Object> paramMap = getParamMap();
		MappedStatement ms = sqlSessionFactory.getConfiguration().getMappedStatement(sqlId);
		BoundSql boundSql = ms.getBoundSql(paramMap);
		List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
		List<Object> paramValues = getParamValues(paramMap, parameterMappings);
		String sql = boundSql.getSql();
		String execSql = getExecuteSql(sql, paramValues);
		logger.info("导出sql：{}\n", execSql);
	}

	private String getExecuteSql(String sql, List<Object> paramValues) {
		while(sql.indexOf("?") != -1 && paramValues.size() > 0) {
			Object paramValue = paramValues.get(0);
			String value = paramValue.toString();
			if (paramValue instanceof String) {
				value = "'" + paramValue.toString() + "'";
			}
			else if (paramValue instanceof Date || paramValue instanceof Timestamp) {
				value = DateUtils.format((Date) paramValue, DateUtils.yyyy_MM_dd_HH_mm_ss);
				value = "str_to_date('" + value + "','%Y-%m-%d %T')";
			}
			sql = sql.replaceFirst("\\?", value);
			paramValues.remove(0);
		}
		return sql;
	}

	private List<Object> getParamValues(Map<String, Object> paramMap, 
			List<ParameterMapping> parameterMappings) {
		if (parameterMappings == null) {
			return new ArrayList<Object>();
		}
		List<Object> paramValues = new ArrayList<Object>();
		for (ParameterMapping pm : parameterMappings) {
			if (pm.getMode() != ParameterMode.OUT) {
				String paramName = pm.getProperty();
				Object paramValue = paramMap.get(paramName);
				paramValues.add(paramValue);
			}
		}
		return paramValues;
	}

	private Map<String, Object> getParamMap() {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("resourceName", "用户管理");
		paramMap.put("parentName", "权限管理");
		paramMap.put("pageBegin", 0);
		paramMap.put("pageSize", 20);
		paramMap.put("addTime", new Date());
		paramMap.put("mark", 0);
		return paramMap;
	}

}
