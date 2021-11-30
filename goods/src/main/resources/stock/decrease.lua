-- 减库存

local key=KEYS[1]

if (redis.call('exists',key)==1) then
    local stock =tonumber(redis.call('get',key))
    local quantity = tonumber(KEYS[2])
    if(stock>=quantity) then
        return redis.call('incrBy',key,0-quantity)
    end
end

return -1