package web.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import web.dao.OrdersDao;
import web.entity.Items;
import web.entity.Orders;
import web.entity.OrdersDetail;
import web.entity.ShippingAddress;
import web.service.OrdersService;
/**
 * @author 黄信胜
 * @date 2018年11月20日下午8:41:07
 * @version 版本号
 */
@Transactional
@Service("ordersService")
public class OrdersServiceImpl implements OrdersService {
	
	@Autowired
	private OrdersDao ordersDao;
	
	public void setOrdersDao(OrdersDao ordersDao) {
		this.ordersDao = ordersDao;
	}
	
	/* (non-Javadoc)
	 * @see web.service.OrdersService#addOrders(web.entity.Orders)
	 */
	public void addOrders(Orders orders) throws Exception {
		ordersDao.addOrders(orders);
	}

	/* (non-Javadoc)
	 * @see web.service.OrdersService#checkShippingAddress(java.lang.Integer)
	 */
	@Override
	public Orders checkShippingAddress(Integer uid) throws Exception {
		return ordersDao.checkShippingAddress(uid);
	}

	/* (non-Javadoc)
	 * @see web.service.OrdersService#lookOrders(java.lang.Integer)
	 */
	@Override
	public List<Orders> lookOrders(Integer uid) throws Exception {
		return ordersDao.lookOrders(uid);
	}

	/* (non-Javadoc)
	 * @see web.service.OrdersService#findItemsById(java.lang.Integer)
	 */
	@Override
	public Items findItemsById(Integer id) throws Exception {
		return ordersDao.findItemsById(id);
	}

	/* (non-Javadoc)
	 * @see web.service.OrdersService#lookOrdersDetail(java.lang.Integer)
	 */
	@Override
	public List<Items> lookOrdersDetail(Integer rid) throws Exception {
		return ordersDao.findOrdersDetailById(rid);
	}

	/* (non-Javadoc)
	 * @see web.service.OrdersService#saveOrdersDetailItems(java.lang.Integer, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public void saveOrdersDetailItems(OrdersDetail  ordersDetail) throws Exception {
		ordersDao.saveOrdersDetailItems(ordersDetail);
		
	}

	/* (non-Javadoc)
	 * @see web.service.OrdersService#savesaveShippingAddress(web.entity.ShippingAddress)
	 */
	@Override
	public void saveShippingAddress(ShippingAddress shippingAddress) throws Exception {
		ordersDao.saveShippingAddress(shippingAddress);
		
	}

}
