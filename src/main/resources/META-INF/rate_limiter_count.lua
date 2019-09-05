--如果等于0说明超时,其他则是当前资源的访问数量

--申请资源数量
local count= tonumber(ARGV[1])

if count == nil then
    count = 1
end


-- 获取剩余资源数量
local last_count = tonumber(redis.call("get", KEYS[1]))
if last_count == nil then
    last_count = ARGV[2];
end

--计数减少
if last_count > count then
    redis.call("DECRBY", KEYS[1], count)
    return 1
else
    return 0
end
--todo bitmap 标记 资源为0