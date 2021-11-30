-- 加库存

local key =KEYS[1]

if (redis.call('exists',key)==1) then
    local quantity = tonumber(KEYS[2])
    return redis.call('incrBy',key,quantity)
end

return -1
