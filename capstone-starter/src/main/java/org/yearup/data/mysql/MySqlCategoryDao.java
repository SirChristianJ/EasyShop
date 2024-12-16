package org.yearup.data.mysql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yearup.data.CategoryDao;
import org.yearup.models.Category;
import org.yearup.models.Product;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class MySqlCategoryDao extends MySqlDaoBase implements CategoryDao
{
    public MySqlCategoryDao(DataSource dataSource)
    {
        super(dataSource);
    }

    @Override
    public List<Category> getAllCategories()
    {
        List<Category> categories = new ArrayList<>();

        String sql =
                """
                SELECT * FROM easyshop.categories;
                """;

        try(Connection connection = super.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()){
                categories.add(new Category(resultSet.getInt("category_id"),
                                            resultSet.getString("name"),
                                            resultSet.getString("description")
                                            ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return categories;
    }

    @Override
    public Category getById(int categoryId)
    {
        // get category by id
        Category newCategory = null;

        String sql =
                """
                SELECT * FROM easyshop.categories
                WHERE category_id = ?;
                """;

        try(Connection connection = super.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1,categoryId);
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    newCategory = new Category(resultSet.getInt("category_id"),
                            resultSet.getString("name"),
                            resultSet.getString("description")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return newCategory;
    }

    @Override
    public List<Product> getProductsByCategoryID(int categoryId) {
        List<Product> products = new ArrayList<>();

        String sql =
                """
                SELECT * FROM easyshop.products
                WHERE category_id = ?;
                """;

        try(Connection connection = super.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1,categoryId);
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    products.add(new Product(resultSet.getInt("product_id"),
                                            resultSet.getString("name"),
                                            resultSet.getBigDecimal("price"),
                                            resultSet.getInt("category_id"),
                                            resultSet.getString("description"),
                                            resultSet.getString("color"),
                                            resultSet.getInt("stock"),
                                            resultSet.getBoolean("featured"),
                                            resultSet.getString("image_url")));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return products;
    }

    @Override
    public Category create(Category category)
    {
        // create a new category
        return null;
    }

    @Override
    public void update(int categoryId, Category category)
    {
        // update category
    }

    @Override
    public void delete(int categoryId)
    {
        // delete category
    }

    private Category mapRow(ResultSet row) throws SQLException
    {
        int categoryId = row.getInt("category_id");
        String name = row.getString("name");
        String description = row.getString("description");

        Category category = new Category()
        {{
            setCategoryId(categoryId);
            setName(name);
            setDescription(description);
        }};

        return category;
    }

}
