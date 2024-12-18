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
        String sql =
            """
            SELECT
                    user_id,
                    products.product_id,
                    quantity,
                    products.name,
                    products.price,
                    products.category_id,
                    products.description,
                    products.color,
                    products.image_url,
                    products.stock,
                    products.featured
            FROM easyshop.shopping_cart
            INNER JOIN easyshop.products
                    ON	products.product_id = shopping_cart.product_id
            WHERE shopping_cart.user_id = ?
            GROUP BY(shopping_cart.product_id);
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
    public ShoppingCart create(int userId, int product_id,ShoppingCartItem shoppingCartItem) {
        System.out.println("Create is called");
        ShoppingCart shoppingCart = getByUserId(userId);


        String sql =
                """
                INSERT INTO easyshop.shopping_cart(user_id,product_id)
                VALUES(?,?)
                ON DUPLICATE KEY UPDATE shopping_cart.quantity = quantity +1;
                """;
        try(Connection connection = super.getConnection();

            PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1,userId);
            preparedStatement.setInt(2,product_id);

            System.out.printf("Updated rows: %d\n", preparedStatement.executeUpdate());

            shoppingCart.add(shoppingCartItem);

        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return shoppingCart;
    }

    @Override
    public ShoppingCart delete(int userId) {
        String sql =
                """
                DELETE FROM easyshop.shopping_cart WHERE user_id = (?);
                """;
        try(Connection connection = super.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1,userId);

            preparedStatement.executeUpdate();

            System.out.printf("Updated rows:%d\n", preparedStatement.executeUpdate());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return getByUserId(userId);
    }


}
