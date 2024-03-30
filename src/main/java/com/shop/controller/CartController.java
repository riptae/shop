package com.shop.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class CartController {

    // addCart
    @PostMapping("/cart/addcart")
    public @ResponseBody ResponseEntity addCart(@RequestBody @Valid CartItemDto cartItemDto, BindingResult bindingResult, Principal principal) {

        if (bindingResult.hasErrors()) {
            StringBuilder sb = new StringBuilder();
            List<FieldError> fieldErrorList = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrorList) {
                sb.append(fieldError.getDefaultMessage());
            }

            return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST);
        }

        try {
            Long cartId = cartService.addCart(cartItemDto, principal.getName());
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<Long>(cartId, HttpStatus.OK);

    }
    // getCartList
    @GetMapping("/cart")
    public String cart(Model model, Principal principal) {

        CartItemDto cartItemDto = cartService.getCart(principal.getName());
        model.addAttribute("cart", cartItemDto);

        return "/cart";

    }

    // UpdateCart
    @PatchMapping("/cart/update")
    public @ResponseBody ResponseEntity cartUpdate(Long cartItemId, int count, Principal principal) {
        if (!cartService.validateCart(cartItemId, principal.getName()))
            return new ResponseEntity<String>("수정 권한이 없습니다.", HttpStatus.FORBIDDEN);
        else if (count <= 0) {
            return new ResponseEntity<String>("수량을 정확히 입력해주세요", HttpStatus.BAD_REQUEST);
        }

        cartService.update(cartItemId, principal.getName());
        return new ResponseEntity<Long>(cartItemId, HttpStatus.OK);
    }

    // DeleteCart
    @DeleteMapping("/cart/delete")
    public @ResponseBody ResponseEntity cartDelete(Long cartItemId, Principal principal) {
        if (!cartService.validateCart(cartItemId, principal.getName()))
            return new ResponseEntity<String>("수정 권한이 없습니다.", HttpStatus.FORBIDDEN);

        cartService.delete(cartItemId, principal.getName());
        return new ResponseEntity<Long>(cartItemId, HttpStatus.OK);
    }

    // Order_inCart
    @PostMapping("cart/order")
    public @ResponseBody ResponseEntity cartOrder(@RequestBody @Valid CartItemDto cartItemDto, Principal principal) {



        Long orderId = cartService.orderCartItem();

        return new ResponseEntity<Long>(orderId, HttpStatus.OK);
    }

}
