-- load 库存

if (redis.call('exists', KEYS[1]) == 0) then
    redis.call('set', KEYS[1], tonumber(ARGV[1]))
end
