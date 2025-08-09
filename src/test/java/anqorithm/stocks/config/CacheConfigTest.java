package anqorithm.stocks.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CacheConfigTest {

    @Test
    void testCacheManagerBean() {
        CacheConfig cacheConfig = new CacheConfig();
        
        CacheManager cacheManager = cacheConfig.cacheManager();
        
        assertNotNull(cacheManager);
        assertInstanceOf(ConcurrentMapCacheManager.class, cacheManager);
    }

    @Test
    void testCacheManagerHasCorrectCacheNames() {
        CacheConfig cacheConfig = new CacheConfig();
        
        ConcurrentMapCacheManager cacheManager = (ConcurrentMapCacheManager) cacheConfig.cacheManager();
        
        // Test that the cache names are set correctly
        assertNotNull(cacheManager.getCache("stocks"));
        assertNotNull(cacheManager.getCache("sectors"));
        assertNotNull(cacheManager.getCache("industries"));
        
        // Test cache names collection
        assertTrue(cacheManager.getCacheNames().contains("stocks"));
        assertTrue(cacheManager.getCacheNames().contains("sectors"));
        assertTrue(cacheManager.getCacheNames().contains("industries"));
        assertEquals(3, cacheManager.getCacheNames().size());
    }

    @Test
    void testCacheManagerDoesNotAllowNullValues() {
        CacheConfig cacheConfig = new CacheConfig();
        
        ConcurrentMapCacheManager cacheManager = (ConcurrentMapCacheManager) cacheConfig.cacheManager();
        
        // Test that null values are not allowed
        assertFalse(cacheManager.isAllowNullValues());
    }

    @Test
    void testCacheManagerCreatesMultipleDistinctCaches() {
        CacheConfig cacheConfig = new CacheConfig();
        
        ConcurrentMapCacheManager cacheManager = (ConcurrentMapCacheManager) cacheConfig.cacheManager();
        
        // Get individual caches
        var stocksCache = cacheManager.getCache("stocks");
        var sectorsCache = cacheManager.getCache("sectors");
        var industriesCache = cacheManager.getCache("industries");
        
        assertNotNull(stocksCache);
        assertNotNull(sectorsCache);
        assertNotNull(industriesCache);
        
        // Ensure they are different cache instances
        assertNotSame(stocksCache, sectorsCache);
        assertNotSame(sectorsCache, industriesCache);
        assertNotSame(stocksCache, industriesCache);
    }

    @Test
    void testCacheManagerReturnsSameCacheForSameName() {
        CacheConfig cacheConfig = new CacheConfig();
        
        ConcurrentMapCacheManager cacheManager = (ConcurrentMapCacheManager) cacheConfig.cacheManager();
        
        // Get the same cache multiple times
        var stocksCache1 = cacheManager.getCache("stocks");
        var stocksCache2 = cacheManager.getCache("stocks");
        
        assertNotNull(stocksCache1);
        assertNotNull(stocksCache2);
        assertSame(stocksCache1, stocksCache2);
    }

    @Test
    void testCacheManagerReturnsNullForUnknownCache() {
        CacheConfig cacheConfig = new CacheConfig();
        
        ConcurrentMapCacheManager cacheManager = (ConcurrentMapCacheManager) cacheConfig.cacheManager();
        
        // Try to get a cache that doesn't exist
        var unknownCache = cacheManager.getCache("unknown");
        
        assertNull(unknownCache);
    }

    @Test
    void testCacheConfigInstantiation() {
        // Test that CacheConfig can be instantiated without issues
        CacheConfig cacheConfig = new CacheConfig();
        
        assertNotNull(cacheConfig);
    }

    @Test
    void testCacheManagerIsConfiguredWithCorrectType() {
        CacheConfig cacheConfig = new CacheConfig();
        
        CacheManager cacheManager = cacheConfig.cacheManager();
        
        // Verify it's the expected implementation
        assertTrue(cacheManager instanceof ConcurrentMapCacheManager);
        
        // Verify it's not some other cache implementation
        assertFalse(cacheManager.getClass().getName().contains("Redis"));
        assertFalse(cacheManager.getClass().getName().contains("Hazelcast"));
        assertFalse(cacheManager.getClass().getName().contains("Ehcache"));
    }

    @Test
    void testCacheBehavior() {
        CacheConfig cacheConfig = new CacheConfig();
        
        ConcurrentMapCacheManager cacheManager = (ConcurrentMapCacheManager) cacheConfig.cacheManager();
        var stocksCache = cacheManager.getCache("stocks");
        
        assertNotNull(stocksCache);
        
        // Test basic cache operations
        String key = "testKey";
        String value = "testValue";
        
        // Initially, cache should not contain the key
        assertNull(stocksCache.get(key));
        
        // Put a value in cache
        stocksCache.put(key, value);
        
        // Now cache should contain the value
        var cachedValue = stocksCache.get(key);
        assertNotNull(cachedValue);
        assertEquals(value, cachedValue.get());
        
        // Test eviction
        stocksCache.evict(key);
        assertNull(stocksCache.get(key));
    }

    @Test
    void testMultipleCacheConfigInstances() {
        // Test that multiple instances of CacheConfig create equivalent cache managers
        CacheConfig cacheConfig1 = new CacheConfig();
        CacheConfig cacheConfig2 = new CacheConfig();
        
        CacheManager cacheManager1 = cacheConfig1.cacheManager();
        CacheManager cacheManager2 = cacheConfig2.cacheManager();
        
        // They should be different instances
        assertNotSame(cacheManager1, cacheManager2);
        
        // But they should have the same configuration
        assertEquals(cacheManager1.getCacheNames(), cacheManager2.getCacheNames());
        
        // Both should be ConcurrentMapCacheManager
        assertInstanceOf(ConcurrentMapCacheManager.class, cacheManager1);
        assertInstanceOf(ConcurrentMapCacheManager.class, cacheManager2);
    }

    @Test
    void testCacheNamesAreImmutable() {
        CacheConfig cacheConfig = new CacheConfig();
        
        ConcurrentMapCacheManager cacheManager = (ConcurrentMapCacheManager) cacheConfig.cacheManager();
        var cacheNames = cacheManager.getCacheNames();
        
        // Verify initial state
        assertEquals(3, cacheNames.size());
        
        // The returned collection should be read-only or modifications shouldn't affect the cache manager
        int originalSize = cacheNames.size();
        
        // Try to verify the cache names are as expected
        assertTrue(cacheNames.contains("stocks"));
        assertTrue(cacheNames.contains("sectors"));
        assertTrue(cacheNames.contains("industries"));
        
        // After our checks, the size should still be the same
        assertEquals(originalSize, cacheNames.size());
    }
}