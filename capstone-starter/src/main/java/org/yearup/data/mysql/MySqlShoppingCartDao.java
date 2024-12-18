package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.ShoppingCartDao;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Component
public class MySqlShoppingCartDao extends MySqlDaoBase implements ShoppingCartDao {

    public MySqlShoppingCartDao(DataSource dataSource)
    {
        super(dataSource);
    }

    @Override
    public ShoppingCart getByUserId(int userId) {
        System.out.println("getByUserId called");
        ShoppingCart shoppingCart = new ShoppingCart();
        String sql = """
            SELECT * FROM easyshop.shopping_cart
            INNER JOIN easyshop.products
                ON products.product_id = shopping_cart.product_id
            WHERE shopping_cart.user_id = ?;
    """;
        try (Connection connection = super.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, userId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    ShoppingCartItem shoppingCartItem = new ShoppingCartItem();
                    shoppingCartItem.setProduct(new Product(
                            resultSet.getInt("product_id"),
                            resultSet.getString("name"),
                            resultSet.getBigDecimal("price"),
                            resultSet.getInt("category_id"),
                            resultSet.getString("description"),
                            resultSet.getString("color"),
                            resultSet.getInt("stock"),
                            resultSet.getBoolean("featured"),
                            resultSet.getString("image_url"))
                    );
                    shoppingCartItem.setQuantity(resultSet.getInt("quantity"));
                    shoppingCart.add(shoppingCartItem);
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error fetching shopping cart for user ID: " + userId, ex);
        }

        return shoppingCart;
    }


    @Override
    public ShoppingCart create(int userId, ShoppingCartItem shoppingCartItem) {
        ShoppingCart shoppingCart = new ShoppingCart();
        Map<Integer,ShoppingCartItem> cartItemMap = new HashMap<>();
        int i = 0;
        String sql =
                """
                INSERT INTO easyshop.shopping_cart(user_id,product_id,quantity)
                VALUES(?,?,?);
                """;
        try(Connection connection = super.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1,userId);
            preparedStatement.setInt(2,shoppingCartItem.getProductId());
            preparedStatement.setInt(3,shoppingCartItem.getQuantity());

            System.out.printf("Updated rows: %d", preparedStatement.executeUpdate());
            try(ResultSet resultSet = preparedStatement.getResultSet()) {
                while (resultSet.next()) {
                    cartItemMap.put(++i, shoppingCartItem);
                }
            }
            shoppingCart.setItems(cartItemMap);

        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return shoppingCart;
    }
}
