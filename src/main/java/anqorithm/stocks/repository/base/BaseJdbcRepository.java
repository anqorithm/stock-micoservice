package anqorithm.stocks.repository.base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;
import java.util.Optional;

/**
 * Base JDBC repository implementation providing common database operations
 * @param <T> Entity type
 * @param <ID> Primary key type
 */
public abstract class BaseJdbcRepository<T, ID> {

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    /**
     * Get the table name for this entity
     */
    protected abstract String getTableName();

    /**
     * Get the primary key column name
     */
    protected abstract String getIdColumnName();

    /**
     * Get the row mapper for this entity
     */
    protected abstract RowMapper<T> getRowMapper();

    /**
     * Get all columns for select queries
     */
    protected abstract String getAllColumns();

    /**
     * Find entity by ID
     */
    public Optional<T> findById(ID id) {
        String sql = String.format("SELECT %s FROM %s WHERE %s = ?", 
                getAllColumns(), getTableName(), getIdColumnName());
        List<T> results = jdbcTemplate.query(sql, getRowMapper(), id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    /**
     * Find all entities
     */
    public List<T> findAll() {
        String sql = String.format("SELECT %s FROM %s", getAllColumns(), getTableName());
        return jdbcTemplate.query(sql, getRowMapper());
    }

    /**
     * Find all entities with pagination
     */
    public Page<T> findAll(Pageable pageable) {
        String sql = String.format("SELECT %s FROM %s LIMIT ? OFFSET ?", 
                getAllColumns(), getTableName());
        String countSql = String.format("SELECT COUNT(*) FROM %s", getTableName());
        
        List<T> results = jdbcTemplate.query(sql, getRowMapper(), 
                pageable.getPageSize(), pageable.getOffset());
        
        Long total = jdbcTemplate.queryForObject(countSql, Long.class);
        
        return new PageImpl<>(results, pageable, total != null ? total : 0);
    }

    /**
     * Check if entity exists by ID
     */
    public boolean existsById(ID id) {
        String sql = String.format("SELECT COUNT(*) FROM %s WHERE %s = ?", 
                getTableName(), getIdColumnName());
        Long count = jdbcTemplate.queryForObject(sql, Long.class, id);
        return count != null && count > 0;
    }

    /**
     * Count all entities
     */
    public long count() {
        String sql = String.format("SELECT COUNT(*) FROM %s", getTableName());
        Long count = jdbcTemplate.queryForObject(sql, Long.class);
        return count != null ? count : 0;
    }

    /**
     * Update entity field
     */
    protected int updateField(ID id, String fieldName, Object value) {
        String sql = String.format("UPDATE %s SET %s = ? WHERE %s = ?", 
                getTableName(), fieldName, getIdColumnName());
        return jdbcTemplate.update(sql, value, id);
    }

    /**
     * Delete entity by ID
     */
    public int deleteById(ID id) {
        String sql = String.format("DELETE FROM %s WHERE %s = ?", 
                getTableName(), getIdColumnName());
        return jdbcTemplate.update(sql, id);
    }

    /**
     * Execute paginated query with custom SQL
     */
    protected Page<T> executePagedQuery(String sql, String countSql, Pageable pageable, Object... params) {
        String pagedSql = sql + " LIMIT ? OFFSET ?";
        Object[] pagedParams = new Object[params.length + 2];
        System.arraycopy(params, 0, pagedParams, 0, params.length);
        pagedParams[params.length] = pageable.getPageSize();
        pagedParams[params.length + 1] = pageable.getOffset();
        
        List<T> results = jdbcTemplate.query(pagedSql, getRowMapper(), pagedParams);
        Long total = jdbcTemplate.queryForObject(countSql, Long.class, params);
        
        return new PageImpl<>(results, pageable, total != null ? total : 0);
    }
}