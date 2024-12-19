package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.models.Profile;
import org.yearup.data.ProfileDao;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;

@Component
public class MySqlProfileDao extends MySqlDaoBase implements ProfileDao
{
    public MySqlProfileDao(DataSource dataSource)
    {
        super(dataSource);
    }

    @Override
    public Profile create(Profile profile)
    {
        String sql = "INSERT INTO profiles (user_id, first_name, last_name, phone, email, address, city, state, zip) " +
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try(Connection connection = getConnection())
        {
            PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setInt(1, profile.getUserId());
            ps.setString(2, profile.getFirstName());
            ps.setString(3, profile.getLastName());
            ps.setString(4, profile.getPhone());
            ps.setString(5, profile.getEmail());
            ps.setString(6, profile.getAddress());
            ps.setString(7, profile.getCity());
            ps.setString(8, profile.getState());
            ps.setString(9, profile.getZip());

            ps.executeUpdate();

            return profile;
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Profile getProfile(int userId) {
        Profile profile = null;
        String sql =
                """
                    SELECT user_id,
                           first_name,
                           last_name,
                           phone,
                           email,
                           address,
                           city,
                           state,
                           zip
                    FROM easyshop.profiles
                    WHERE user_id = (?);
                """;
        try (Connection connection = super.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1,userId);
            try (ResultSet resultSet = preparedStatement.executeQuery()){
                while (resultSet.next()){
                    profile = new Profile(
                                    resultSet.getInt("user_id"),
                                    resultSet.getString("first_name"),
                                    resultSet.getString("last_name"),
                                    resultSet.getString("phone"),
                                    resultSet.getString("email"),
                                    resultSet.getString("address"),
                                    resultSet.getString("city"),
                                    resultSet.getString("state"),
                                    resultSet.getString("zip")
                    );
                }
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return profile;
    }

    @Override
    public void updateProfile(int userId, Profile profile) {
        String sql =
                """
                    UPDATE easyshop.profiles
                            SET
                                first_name = CASE WHEN ? = -1 THEN first_name ELSE ? END,
                                last_name = CASE WHEN ? = -1 THEN last_name ELSE ? END,
                                phone = CASE WHEN ? = -1 THEN phone ELSE ? END,
                                email = CASE WHEN ? = -1 THEN email ELSE ? END,
                                address = CASE WHEN ? = -1 THEN address ELSE ? END,
                                city = CASE WHEN ? = -1 THEN city ELSE ? END,
                                state = CASE WHEN ? = -1 THEN state ELSE ? END,
                                zip = CASE WHEN ? = -1 THEN zip ELSE ? END
                            WHERE user_id = ?;
                """;

        try(Connection connection = super.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1,profile.getFirstName()==null? -1:0);
                preparedStatement.setString(2,profile.getFirstName());
                preparedStatement.setInt(3,profile.getLastName()==null?-1:0);
                preparedStatement.setString(4,profile.getLastName());
                preparedStatement.setInt(5,profile.getPhone()==null?-1:0);
                preparedStatement.setString(6,profile.getPhone());
                preparedStatement.setInt(7,profile.getEmail()==null?-1:0);
                preparedStatement.setString(8,profile.getEmail());
                preparedStatement.setInt(9,profile.getAddress()==null?-1:0);
                preparedStatement.setString(10,profile.getAddress());
                preparedStatement.setInt(11,profile.getCity()==null?-1:0);
                preparedStatement.setString(12,profile.getCity());
                preparedStatement.setInt(13,profile.getState()==null?-1:0);
                preparedStatement.setString(14,profile.getState());
                preparedStatement.setInt(15,profile.getZip()==null?-1:0);
                preparedStatement.setString(16,profile.getZip());
                preparedStatement.setInt(17,userId);

                System.out.printf("Updated rows: %d", preparedStatement.executeUpdate());
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
