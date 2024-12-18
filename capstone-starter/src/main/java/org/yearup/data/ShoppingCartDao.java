package org.yearup.data;

import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import java.util.List;
import java.util.Map;

public interface ShoppingCartDao
{
    ShoppingCart getByUserId(int userId);
    // add additional method signatures here
    ShoppingCart create(int userId, int product_id, ShoppingCartItem shoppingCartItem);
    ShoppingCart delete(int userId);
}
