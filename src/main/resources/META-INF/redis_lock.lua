-- get a lock
-- 获取锁成功，则返回 1

--redis.log(redis.LOG_WARNING, "key " .. KEYS[1])
--redis.log(redis.LOG_WARNING, "ARGV1 " .. ARGV[1])
--redis.log(redis.LOG_WARNING, "ARGV " .. ARGV[2])
local lock_key = KEYS[1]
local lock_value = ARGV[1]
local ttl = tonumber(ARGV[2])
local result = redis.call('setnx', lock_key, lock_value)
--redis.log(redis.LOG_WARNING, "result " .. result)
if result == 1 then
    redis.call('expire', lock_key, ttl)
    return '1'
else
    local value = redis.call('get', lock_key)
    -- lock_value 一致可重入
    if (value == lock_value) then
        result = '1';
        redis.call('expire', lock_key, ttl)
    end
end
return tostring(result)