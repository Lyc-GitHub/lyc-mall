-- 秒杀商品信息key
local secKillProductInfoKey = KEYS[1]

-- 秒杀商品库存信息key
local secKillProductStockKey = KEYS[2]

-- 已秒杀用户信息key
local secKilledUserKey = KEYS[3]

-- 秒杀商品id
local productId = ARGV[1]

-- 秒杀用户id
local userId = ARGV[2]

-- 验证用户是否已经秒杀成功
if redis.call('SISMEMBER', secKilledUserKey, userId) == 1 then
    return cjson.encode({code = 400, msg = "该用户已经秒杀过了"})
end

-- 验证商品是否已经开启秒杀
local productInfo = redis.call('HGET', secKillProductInfoKey, productId)
if not productInfo then
    return cjson.encode({code = 404, msg = "秒杀商品不存在"})
end

local json_productInfo = cjson.decode(productInfo)
local start_timestamp = json_productInfo.start_timestamp
local end_timestamp = json_productInfo.end_timestamp
local current_timestamp = tonumber(redis.call('TIME')[1])

if current_timestamp < start_timestamp then
    return cjson.encode({code = 401, msg = "还没有开始秒杀"})
end
if current_timestamp > end_timestamp then
    return cjson.encode({code = 401, msg = "秒杀活动已经结束"})
end

-- 秒杀
local productStock = redis.call('HGET', secKillProductStockKey, productId)
productStock = tonumber(productStock)
if productStock <= 0 then
    return cjson.encode({code = 402, msg = "库存为0，秒杀失败"})
end
local newProductStock = productStock - 1
redis.call('HSET', secKillProductStockKey, productId, newProductStock)

-- 秒杀成功，保存秒杀用户到已秒杀用户列表
redis.call('SADD', secKilledUserKey, userId)

-- 返回秒杀成功信息
return cjson.encode({code = 200, msg = "秒杀成功", productInfo = json_productInfo})

