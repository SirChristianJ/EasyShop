package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.CategoryDao;
import org.yearup.data.ProductDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.data.UserDao;
import org.yearup.models.*;

import java.security.Principal;

// convert this class to a REST controller
// only logged in users should have access to these actions
@RestController
@RequestMapping("cart")
@CrossOrigin

public class ShoppingCartController
{
    // a shopping cart requires
    private ShoppingCartDao shoppingCartDao;
    private UserDao userDao;
    private ProductDao productDao;


    @Autowired
    public ShoppingCartController(ShoppingCartDao shoppingCartDao, UserDao userDao, ProductDao productDao) {
        this.shoppingCartDao = shoppingCartDao;
        this.userDao = userDao;
        this.productDao = productDao;
    }

    private int retrieveUserId(Principal principal){
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated.");
        }

        // get the currently logged in username
        String userName = principal.getName();

        // find database user by userId
        User user = userDao.getByUserName(userName);
        return user.getId();
    }
    // each method in this controller requires a Principal object as a parameter
    @GetMapping("")
    @PreAuthorize("permitAll()")
    public ShoppingCart getCart(Principal principal) {
        try {
            int userid = retrieveUserId(principal);

            return shoppingCartDao.getByUserId(userid);
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }

    // add a POST method to add a product to the cart - the url should be
    // https://localhost:8080/cart/products/15 (15 is the productId to be added
    @PostMapping("/products/{product_id}")
    @PreAuthorize("permitAll()")
    public ShoppingCart addToCart(@PathVariable int product_id, Principal principal)
    {
        int userId = retrieveUserId(principal);

        ShoppingCartItem item = new ShoppingCartItem();
        item.setProduct(productDao.getById(product_id));
        item.setQuantity(1);

        return shoppingCartDao.create(userId, product_id, item);
    }

    // add a PUT method to update an existing product in the cart - the url should be
    // https://localhost:8080/cart/products/15 (15 is the productId to be updated)
    // the BODY should be a ShoppingCartItem - quantity is the only value that will be updated
    @PutMapping("/products/{product_id}")
    @PreAuthorize("permitAll()")
    public void updateCart(@PathVariable int product_id, @RequestBody int quantity, Principal principal){
        int userId = retrieveUserId(principal);
        shoppingCartDao.update(userId,product_id,quantity);
    }

    // add a DELETE method to clear all products from the current users cart
    // https://localhost:8080/cart
    // add annotation to call this method for a DELETE action - the url path must include the categoryId
    // add annotation to ensure that only an ADMIN can call this function
    @DeleteMapping("")
    @PreAuthorize("permitAll()")
    public ShoppingCart clearCart(Principal principal){
        int userId = retrieveUserId(principal);

        return shoppingCartDao.delete(userId);
    }

}
