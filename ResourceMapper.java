
public interface ResourceMapper extends IBaseMapper<ResourceEntity> {
	
	public List<Map<String, Object>> selectExportResEntities(Map<String, Object> paramMap);

}
