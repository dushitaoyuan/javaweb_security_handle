-- unlock
local lock_key = KEYS[1]
local lock_value = ARGV[1]
--[[--redis.log(redis.LOG_WARNING, "key " .. KEYS[1])
--redis.log(redis.LOG_WARNING, "ARGV1 " .. ARGV[1])]]
local result = redis.call('get', lock_key)
--redis.log(redis.LOG_WARNING, "result " .. result)
if result == lock_value then
    redis.call('del', lock_key)
    return '1';
end
return '0'