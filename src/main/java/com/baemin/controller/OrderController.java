package com.baemin.controller;

import java.io.IOError;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baemin.dto.Cart;
import com.baemin.dto.CartList;
import com.baemin.dto.OrderInfo;
import com.baemin.dto.OrderList;
import com.baemin.login.LoginService;
import com.baemin.service.OrderService;
import com.baemin.util.CreateOrderNum;
import com.baemin.util.FoodInfoFormJson;

@Controller
public class OrderController {
	
	@Autowired
	private OrderService orderService;
	
	// 주문 페이지 이동
	@GetMapping("/order")
	public String order(HttpSession session, Model model, @AuthenticationPrincipal LoginService user) {
		CartList cartList = (CartList) session.getAttribute("cartList");
		
		model.addAttribute("cartList", cartList);
		
		System.out.println(user);
		
		if(user != null) {
			model.addAttribute("user", user.getUser());
		}
		
		String orderNum = CreateOrderNum.createOrderNum();
		model.addAttribute("orderNum", orderNum);
		return "order/order";
	}
	
	// 주문 실행
	@ResponseBody
	@PostMapping("/order/payment-cash")
	public ResponseEntity<String> payment(HttpSession session, OrderInfo orderInfo, long totalPrice,
			@AuthenticationPrincipal LoginService user) throws IOException {
		
		CartList cartList = (CartList) session.getAttribute("cartList");
		
		long orderPriceCheck = orderService.orderPriceCheck(cartList);
		
		System.out.println("계산금액= " + totalPrice + " 실제 계산해야할 금액= " + orderPriceCheck);
		
		if(orderPriceCheck == totalPrice) {
			orderService.order(cartList, orderInfo, user, session);
			session.removeAttribute("cartList");
		}
		
		return new ResponseEntity<String>(HttpStatus.OK);
	}
	
	@GetMapping("/orderList")
	public String orderList(@AuthenticationPrincipal LoginService user, Model model) {
		if(user == null) {
			System.out.println("비로그인");
		}else {
			System.out.println("로그인");
			long userId = user.getUser().getId();
			
			List<OrderList> orderList = orderService.orderList(userId);
			
			if(orderList.size() == 0) {
				return "order/orderList";
			}
			
			List<List<Cart>> cartList = new ArrayList<List<Cart>>();
			
			for(int i=0; i<orderList.size(); i++) {
				cartList.add(FoodInfoFormJson.foodInfoFormJson(orderList.get(i).getFoodInfo()));
			}
			
			model.addAttribute("user", user.getUser());
			model.addAttribute("cartList", cartList);
			model.addAttribute("orderList", orderList);
		}
		return "order/orderList";
	}
}
