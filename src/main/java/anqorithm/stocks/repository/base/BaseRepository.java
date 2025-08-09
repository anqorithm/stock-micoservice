package anqorithm.stocks.repository.base;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Base repository interface defining common CRUD operations
 * @param <T> Entity type
 * @param <ID> Primary key type
 */
public interface BaseRepository<T, ID> {
    
    /**
     * Find entity by ID
     */
    Optional<T> findById(ID id);
    
    /**
     * Find all entities
     */
    List<T> findAll();
    
    /**
     * Find all entities with pagination
     */
    Page<T> findAll(Pageable pageable);
    
    /**
     * Save entity
     */
    T save(T entity);
    
    /**
     * Delete entity by ID
     */
    void deleteById(ID id);
    
    /**
     * Check if entity exists by ID
     */
    boolean existsById(ID id);
    
    /**
     * Count all entities
     */
    long count();
}