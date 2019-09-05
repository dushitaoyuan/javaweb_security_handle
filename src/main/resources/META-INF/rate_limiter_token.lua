--令牌算法实现
--限流标识和时间戳key
local tokens_key = KEYS[1]
local timestamp_key = KEYS[2]

-- rate 令牌产生速率
local rate = tonumber(ARGV[1])
-- 令牌容量
local capacity = tonumber(ARGV[2])
--时间戳
local now = tonumber(ARGV[3])
--申请令牌数量
local requested = tonumber(ARGV[4])
--填满漏桶所需要的时间 容量除以速率
local fill_time = capacity/rate
--过期时间为填满漏桶时间的2倍
local ttl = math.floor(fill_time*2)

--redis.log(redis.LOG_WARNING, "rate " .. ARGV[1])
--redis.log(redis.LOG_WARNING, "capacity " .. ARGV[2])
--redis.log(redis.LOG_WARNING, "now " .. ARGV[3])
--redis.log(redis.LOG_WARNING, "requested " .. ARGV[4])
--redis.log(redis.LOG_WARNING, "filltime " .. fill_time)
--redis.log(redis.LOG_WARNING, "ttl " .. ttl)
--剩余令牌
local last_tokens = tonumber(redis.call("get", tokens_key))
if last_tokens == nil then
  last_tokens = capacity
end

--上次获取令牌的时间
local last_refreshed = tonumber(redis.call("get", timestamp_key))
if last_refreshed == nil then
  last_refreshed = 0
end

--重新计算令牌数量=当前时间与上次获取令牌的时间差值*令牌生产速率+剩余令牌数量
--申请令牌数量小于当前桶内数量时,请求被允许
local delta = math.max(0, now-last_refreshed)
local filled_tokens = math.min(capacity, last_tokens+(delta*rate))
local allowed = filled_tokens >= requested
local new_tokens = filled_tokens
local allowed_num = 0
if allowed then
  new_tokens = filled_tokens - requested
  allowed_num = 1
end


redis.call("setex", tokens_key, ttl, new_tokens)
redis.call("setex", timestamp_key, ttl, now)

return {allowed_num, new_tokens}
