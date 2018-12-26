package web.action;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;

import web.Intermediate.ShowPage;
import web.entity.Orders;
import web.entity.OrdersDetail;
import web.entity.ShippingAddress;
import web.entity.User;
import web.service.OrdersService;

/**
 *<p> Title:  OrdersAction</p>
 *<p> Description:  对客户订单信息的各种操作</p>
 * @package   web.action
 * @author    黄信胜
 * @date      2018年12月9日下午8:45:00
 * @version 1.0
 */
@Controller("ordersAction")
@Scope("prototype")
@SuppressWarnings("all")
public class OrdersAction extends ActionSupport implements ModelDriven<Orders>{
	
  private String itemsId;//商品id序列
  
  private String itemsNum;//商品id对应的商品数量
  
  @Resource(name="showPage")
  private ShowPage showPage;//分页实体类
  
  @Resource(name="ordersService")
  private OrdersService ordersService;
  
  @Resource(name="ordersDetail")
  private OrdersDetail ordersDetail;
  
  @Resource(name="shippingAddress")
  private ShippingAddress shippingAddress;
  
  private Orders orders = new Orders();
  
  private ActionContext con = ActionContext.getContext();
  
  public void setItemsId(String itemsId) {
	this.itemsId = itemsId;
  }
  
  public void setItemsNum(String itemsNum) {
	this.itemsNum = itemsNum;
  }
  
  public void setShippingAddress(ShippingAddress shippingAddress) {
	this.shippingAddress = shippingAddress;
  }

  public void setOrdersService(OrdersService ordersService) {
	this.ordersService = ordersService;
  }

  public void setShowPage(ShowPage showPage) {
	this.showPage = showPage;
  }
  
  public Orders getModel() {
  	return orders;
  }
  
  /**
   * 查看订单
   * @return 返回字符串”lookorders“
 * @throws Exception 
   */
  public String lookOrders() throws Exception {
	 con.getSession().put("ordersList", ordersService.lookOrders(((User)(con.getSession().get("user"))).getUid()));
	return "lookOrders";
  }
  
  /**
   *添加订单
   * 1.下单时间，商品总数量，商品总金额，用户id，订单id
   * 2.确定下单时间
   * 3.检查是否填写收货信息,没有填写转到收货信息页面再提交，已填，直接保存
   * 4.选择是否马上付款（付款：1，未付:0） 
   * 将订单详情的商品id与对应的商品数量存入map中
   * 通过商品id查询商品，得到商品单价
   * 通过商品id，得到对应的商品数量，再把所有的商品数量相加。
   * 商品数量乘以商品单价得到商品总价，再把所有的商品总价相加；
   * @return 返回字符串"lookOrders"
   */
  public String addOrders(){
	 try {
		 Integer itemsAllNum = 0;/*订单中商品总数量*/
		 
		 Double itemsAllPrice = 0.0;/*订单中商品总价格*/
		 
		 String[] itemsIdList = this.itemsId.split(",");/*接收下单的商品id总字符串*/
		 
		 String[] itemsNumList =this.itemsNum.split(",");/*接收下单的对应商品id的商品数量总字符串*/
		 
		 Map<Integer,Integer> itemsDetailMap = new HashMap<Integer,Integer>();
		 
		 /*得到对应的商品id与数量存入map*/
	     for (int i = 0; i < itemsIdList.length; i++) {
	    	 if(!itemsIdList[i].equals("") && !itemsNumList[i].equals("")) {//去掉非空
	    		 itemsDetailMap.put(Integer.parseInt(itemsIdList[i]), Integer.parseInt(itemsNumList[i]));
	    	 }
	     }
	     
	    /*计算商品总价格与总数量*/
	    Set<Integer> itemsIdMap =  itemsDetailMap.keySet();//得到商品id的数组
	    
	    for (Integer itemsidmap : itemsIdMap) {/*计算商品总价格与总数量*/
	    	
	    	Integer itemsnum = itemsDetailMap.get(itemsidmap);
	    	
	    	Double itemsprice =  (ordersService.findItemsById(itemsidmap)).getGprice();
	    	
	    	itemsAllPrice += (itemsprice*itemsnum);
	    	
	    	itemsAllNum += itemsnum;
		}
	    
	 /*  将订单中的各个属性添加*/
		  orders.setAllMoney(itemsAllPrice);
		  orders.setGnum(itemsAllNum);
		  orders.setDate(new Date());
		  orders.setUid(((User)(con.getSession().get("user"))).getUid());
		  orders.setUser((User)(con.getSession().get("user")));
		  orders.setOrderStatus(0);
		  
		  ordersService.addOrders(orders);
		  
		 /*将订单中的商品信息存入详细订单详情中*/ 
		for (Integer itemsidmap : itemsIdMap) {
		    this.saveOrdersDetailItems(orders.getRid(), itemsDetailMap.get(itemsidmap), itemsidmap);
		}
		  
		/*  先保存订单，再判断是否填写地址，无，则跳转到填写地址页面，否则就跳转到我的订单页面*/
		  if (!this.checkShippingAddress(((User)(con.getSession().get("user"))).getUid())) {
			  con.getSession().put("orders", orders);
			  return "writeaddress";
		  }
		  
	 }catch(Exception e) {
		 System.out.println("保存商品id与对应数量到Map失败"+e);
	 }
	 return "tryLookOrders";
	
  }
  
  /**
   * 保存订单详细信息
 * @param rid 订单号
 * @param number 商品数量
 * @param gid 商品id
 * @throws Exception 
 */
public void saveOrdersDetailItems(Integer rid, Integer num, Integer gid) throws Exception {
	OrdersDetail ordersDetail = new OrdersDetail ();//实例化多个对象
	ordersDetail.setItems(ordersService.findItemsById(gid));
	ordersDetail.setItemsId(gid);
	ordersDetail.setOrders(orders);
	ordersDetail.setItemsNum(num);
	ordersDetail.setOrdersId(rid);
	ordersService.saveOrdersDetailItems(ordersDetail);
	
  }
  
  /**
   * 查看订单中的详细商品
   * @return
   * @throws Exception 
   */
  public String lookItemsDetail() throws Exception {
	con.getSession().put("ordersDetailList", ordersService.lookOrdersDetail(orders.getRid()))  ;
	return "lookItemsDetail";
  }
  
  
  /**
   * 检查是否填写收货地址
   * @param uid 用户id
   * @return true:存在该用户；false:不存在该用户
   * @throws Exception 
   */
  public Boolean checkShippingAddress(Integer uid) throws Exception {
	if (ordersService.checkShippingAddress(uid) != null) return true;
	return false;
  }
  
 
 /**
  * 保存地址
  * 1.保存地址后，提示是否选择付款
  * 付款：1,修改订单信息中的状态
  * 未付款:0，
  * 2.回到查看订单页面 
  * @return 
 * @throws Exception 
  */
  public String saveShippingAddress() throws Exception {
	  ordersService.saveShippingAddress(shippingAddress);
	return 	"tryLookOrders";
  }
  
  /**
   * 更新收货信息
   * @return
   */
  public String updateShippingAdress() {
	return null;
	  
  }
  
  /**
   * 下单后选择是否付款
   * @return
   */
  public String payOrNot() {
	return null;
	  
  }
  
  /**
   * 删除订单（未付款）
   * @return
   */
  public String delOrders() {
	return null;
  }
 
}
