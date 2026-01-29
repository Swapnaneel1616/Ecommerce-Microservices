package com.ecommerce.order.services;

import com.ecommerce.order.clients.ProductServiceClient;
import com.ecommerce.order.clients.UserServiceClient;
import com.ecommerce.order.dto.CartItemRequest;
import com.ecommerce.order.dto.ProductResponse;
import com.ecommerce.order.dto.UserResponse;
import com.ecommerce.order.models.CartItem;
//import com.app.ecom.model.Product;
//import com.app.ecom.model.User;
import com.ecommerce.order.repository.CartItemRepository;
//import com.app.ecom.repository.ProductRepository;
//import com.app.ecom.repository.UserRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CartService {

//    @Autowired
////    private ProductRepository productRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductServiceClient productServiceClient;
    @Autowired
    private UserServiceClient userServiceClient;
    int attempt = 0;

//    @CircuitBreaker(name = "productService" , fallbackMethod = "addToCartFallBack")
    @Retry(name = "productService" , fallbackMethod = "addToCartFallBack")
    public boolean addToCart(String userId, CartItemRequest request) {
        System.out.println("ATTEMPT COUNT" + ++attempt);
        //Look for product
        ProductResponse productResponse = productServiceClient.getProductDetails(request.getProductId());
        if(productResponse == null){
            return false;
        }
        if(productResponse.getStockQuantity()< request.getQuantity()){
            return false;
        }
        UserResponse userResponse = userServiceClient.getUserDetails(userId);
        if(userResponse == null){
            return false;
        }

        CartItem existingCartItem = cartItemRepository.findByUserIdAndProductId(userId , request.getProductId() );
        if(existingCartItem != null){
            //Update the quantity
            existingCartItem.setQuantity(existingCartItem.getQuantity() + request.getQuantity());
            existingCartItem.setPrice(BigDecimal.valueOf(1000.00));
            cartItemRepository.save(existingCartItem);
        }
        else{
            CartItem cartItem = new CartItem();
            cartItem.setUserId(userId);
            cartItem.setProductId(request.getProductId());
            cartItem.setQuantity(request.getQuantity());
            cartItem.setPrice(BigDecimal.valueOf(1000.00));
            cartItemRepository.save(cartItem);
        }
        return true;
    }

    public boolean addToCartFallBack(String userId, CartItemRequest request , Exception exception){
        System.out.println("Fallback Called ");
        return false;
    }

    public boolean deleteItemFromCart(String userId, String productId) {
//        Optional<Product> productOpt = productRepository.findById(productId);
//        Optional<User> userOpt = userRepository.findById(Long.valueOf(userId));
        CartItem cartItem = cartItemRepository.findByUserIdAndProductId(userId , productId);
        if(cartItem != null){
            cartItemRepository.delete(cartItem);
            return true;
        }
        return false;
    }

    public List<CartItem> getCart(String userId) {
        return cartItemRepository.findByUserId(userId);
    }

    public void clearCart(String userId) {
        cartItemRepository.deleteByUserId(userId);
    }
}
