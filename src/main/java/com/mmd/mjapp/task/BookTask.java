package com.mmd.mjapp.task;

import com.mmd.mjapp.config.RedisUtils;
import com.mmd.mjapp.constant.RedisKey;
import com.mmd.mjapp.dao.BookDao;
import com.mmd.mjapp.pjo.Result;
import com.mmd.mjapp.utils.HttpClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Component
@Transactional
public class BookTask {

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private BookDao bookDao;

    /**
     * 处理超时未支付订单
     */
    @Scheduled(cron = "0/10 * * * * *")
    public void dealBook() throws Exception {
        //开启事物
        Set<String> ids = redisTemplate.opsForSet().members("tobepay");
        Map<String, Object> param = new HashMap<>();
        param.put("cancelreason", "超时系统自动取消！");
        for (String id : ids) {
            //在redis中有该未支付订单信息
            if (redisUtils.getObj(RedisKey.TOBEPAY + id) != null) {
                continue;
            }
            //如果已经没有；表明已经超时，那么就执行取消订单操作
            param.put("bid", id);
            dealBook(param);
        }
    }

    private void dealBook(Map<String, Object> param) throws Exception {
        int num = bookDao.cancelBook(param);
        System.out.println("本次取消执行num值：" + num);
        if (num == 0) {
            //表明已经没有改订单信息，或者该订单的状态已经改变
            return;
        }
        //查询产品购买数量
        List<Map<String, Object>> prodSkuInfos = bookDao.queryBuyProdSkuNum(param);
        //释放产品SKU库存,
        for (Map<String, Object> map : prodSkuInfos) {
            bookDao.releaseProdSkuStock(map);
        }
        //重新计算产品库存
        bookDao.resetProdStock(param);
        //添加订单表操作记录
        bookDao.insertBookOperLog(String.valueOf(param.get("bid")), 5, String.valueOf(param.get("cancelreason")));

        redisUtils.deleteByKey(RedisKey.TOBEPAY + param.get("bid"));
        redisUtils.deleteWithSet(RedisKey.TOBEPAY, param.get("bid"));
    }

}
