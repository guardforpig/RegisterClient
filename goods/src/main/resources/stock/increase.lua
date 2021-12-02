-- 加库存

local key =KEYS[1]
local quantity = tonumber(ARGV[1])

if (redis.call('exists',key)==1) then
    return redis.call('incrBy',key,quantity)
end

return -1
