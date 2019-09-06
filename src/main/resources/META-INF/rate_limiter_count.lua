--判断资源归0标记是否存在
--标记实现,可通过 布隆过滤和bitmap实现 注意redis支持情况
local bit_key_offset=tonumber(ARGV[3])
redis.log(redis.LOG_WARNING, "bit_key_offset " .. ARGV[3])
local zero_flag =redis.call("GETBIT", KEYS[2],bit_key_offset)
if zero_flag == 1 then
    return -1
end
--申请资源数量
local count= tonumber(ARGV[1])
--redis.log(redis.LOG_WARNING, "key " .. KEYS[1])

--redis.log(redis.LOG_WARNING, "count " .. count)
--redis.log(redis.LOG_WARNING, "total_count " .. ARGV[2])

if count == nil then
    count = 1
end

-- 获取剩余资源数量
local last_count = tonumber(redis.call("get", KEYS[1]))
if last_count == nil then
    last_count = tonumber(ARGV[2]);
    redis.call("set", KEYS[1],last_count);
end

--计数减少,资源归0时,标记
if last_count >= count then
    redis.call("DECRBY", KEYS[1], count)
    last_count=tonumber(redis.call("get", KEYS[1]))
--  redis.log(redis.LOG_WARNING, "last_count " .. last_count)
    return     last_count
else
    redis.call("SETBIT", KEYS[2],bit_key_offset,1)
    redis.call("DEL", KEYS[2])
    return -1
end

