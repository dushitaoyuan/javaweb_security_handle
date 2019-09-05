--获取KEY
local key1 = KEYS[1]
local key2 = KEYS[2]
--输出参数
redis.log(redis.LOG_WARNING, "rate " .. ARGV[1])
redis.log(redis.LOG_WARNING, "capacity " .. ARGV[2])
redis.log(redis.LOG_WARNING, "now " .. ARGV[3])
redis.log(redis.LOG_WARNING, "requested " .. ARGV[4])

return {1,2}