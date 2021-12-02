-- 减库存

local key=KEYS[1]
local quantity=tonumber(ARGV[1])

if (redis.call('exists',key)==1) then
    local stock =tonumber(redis.call('get',key))
    if(stock>=quantity) then
        return redis.call('incrBy',key,0-quantity)
    end
end

return -1