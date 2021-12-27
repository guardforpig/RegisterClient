-- load 库存
-- @author GXC
if (redis.call('exists', KEYS[1]) == 0) then
    redis.call('set', KEYS[1], ARGV[1])
end