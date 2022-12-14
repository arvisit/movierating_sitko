package com.company.movierating.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import com.company.movierating.dao.BanDao;
import com.company.movierating.dao.UserDao;
import com.company.movierating.dao.connection.DataSource;
import com.company.movierating.dao.entity.Ban;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class BanDaoImpl implements BanDao {
    private static final String GET_BY_ID = """
            SELECT b.id, b.user_id, b.admin_id, b.start_date, b.end_date, b.reason 
            FROM bans b 
            WHERE b.id = ? AND b.deleted = FALSE""";
    private static final String GET_ALL = """
            SELECT b.id, b.user_id, b.admin_id, b.start_date, b.end_date, b.reason 
            FROM bans b 
            WHERE b.deleted = FALSE""";
    private static final String GET_ALL_PARTIALLY = """
            SELECT b.id, b.user_id, b.admin_id, b.start_date, b.end_date, b.reason 
            FROM bans b 
            WHERE b.deleted = FALSE 
            ORDER BY b.id LIMIT ? OFFSET ?""";
    private static final String GET_ALL_BY_USER_ID_PARTIALLY = """
            SELECT b.id, b.user_id, b.admin_id, b.start_date, b.end_date, b.reason 
            FROM bans b 
            WHERE b.user_id = ? AND b.deleted = FALSE 
            ORDER BY b.id LIMIT ? OFFSET ?""";
    private static final String GET_ALL_BY_ADMIN_ID_PARTIALLY = """
            SELECT b.id, b.user_id, b.admin_id, b.start_date, b.end_date, b.reason 
            FROM bans b 
            WHERE b.admin_id = ? AND b.deleted = FALSE 
            ORDER BY b.id LIMIT ? OFFSET ?""";
    private static final String CREATE = """
            INSERT INTO bans (user_id, admin_id, start_date, end_date, reason) 
            VALUES (?, ?, ?, ?, ?)""";
    private static final String UPDATE = """
            UPDATE bans SET end_date = ?, last_update = NOW() 
            WHERE id = ? AND deleted = FALSE""";
    private static final String DELETE = """
            UPDATE bans SET deleted = TRUE, last_update = NOW() 
            WHERE id = ? AND deleted = FALSE""";
    private static final String COUNT = """
            SELECT COUNT(b.id) AS total 
            FROM bans b 
            WHERE b.deleted = FALSE""";
    private static final String COUNT_BY_USER_ID = """
            SELECT COUNT(b.id) AS total 
            FROM bans b 
            WHERE b.deleted = FALSE AND b.user_id = ?""";
    private static final String COUNT_BY_ADMIN_ID = """
            SELECT COUNT(b.id) AS total 
            FROM bans b 
            WHERE b.deleted = FALSE AND b.admin_id = ?""";
    private static final String IS_BANNED = """
            SELECT COUNT(b.id) AS active_bans 
            FROM bans b 
            WHERE b.user_id = ? AND b.deleted = FALSE AND b.end_date > NOW()""";

    private DataSource dataSource;
    private UserDao userDao;

    public BanDaoImpl(DataSource dataSource, UserDao userDao) {
        this.dataSource = dataSource;
        this.userDao = userDao;
    }

    @Override
    public Ban getById(Long id) {
        try (Connection connection = dataSource.getConnection()) {
            return getById(id, connection);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public Ban getById(Long id, Connection connection) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(GET_BY_ID);
        statement.setLong(1, id);
        ResultSet result = statement.executeQuery();

        if (result.next()) {
            return process(result, connection);
        }
        return null;
    }

    @Override
    public List<Ban> getAll() {
        List<Ban> bans = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(GET_ALL);

            while (result.next()) {
                bans.add(process(result, connection));
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
        return bans;
    }

    @Override
    public List<Ban> getAll(int limit, long offset) {
        List<Ban> bans = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(GET_ALL_PARTIALLY);
            statement.setInt(1, limit);
            statement.setLong(2, offset);
            ResultSet result = statement.executeQuery();

            while (result.next()) {
                bans.add(process(result, connection));
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
        return bans;
    }

    @Override
    public List<Ban> getAllByUser(Long id, int limit, long offset) {
        List<Ban> bans = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(GET_ALL_BY_USER_ID_PARTIALLY);
            statement.setLong(1, id);
            statement.setInt(2, limit);
            statement.setLong(3, offset);
            ResultSet result = statement.executeQuery();

            while (result.next()) {
                bans.add(process(result, connection));
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
        return bans;
    }

    @Override
    public List<Ban> getAllByAdmin(Long id, int limit, long offset) {
        List<Ban> bans = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(GET_ALL_BY_ADMIN_ID_PARTIALLY);
            statement.setLong(1, id);
            statement.setInt(2, limit);
            statement.setLong(3, offset);
            ResultSet result = statement.executeQuery();

            while (result.next()) {
                bans.add(process(result, connection));
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
        return bans;
    }

    @Override
    public Ban create(Ban entity) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(CREATE, Statement.RETURN_GENERATED_KEYS);
            statement.setLong(1, entity.getUser().getId());
            statement.setLong(2, entity.getAdmin().getId());
            statement.setTimestamp(3, Timestamp.from(entity.getStartDate().toInstant()));
            statement.setTimestamp(4, Timestamp.from(entity.getEndDate().toInstant()));
            statement.setString(5, entity.getReason());
            statement.executeUpdate();

            ResultSet keys = statement.getGeneratedKeys();
            if (keys.next()) {
                Long id = keys.getLong("id");
                return getById(id, connection);
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public Ban update(Ban entity) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(UPDATE);
            statement.setTimestamp(1, Timestamp.from(entity.getEndDate().toInstant()));
            statement.setLong(2, entity.getId());
            statement.executeUpdate();

            return getById(entity.getId(), connection);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public boolean delete(Long id) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(DELETE);
            statement.setLong(1, id);
            int rowsDeleted = statement.executeUpdate();

            return rowsDeleted == 1;
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    @Override
    public Long count() {
        try (Connection connection = dataSource.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(COUNT);

            if (result.next()) {
                return result.getLong("total");
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
        throw new RuntimeException("Couldn't count bans");
    }

    @Override
    public Long countByUser(Long id) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(COUNT_BY_USER_ID);
            statement.setLong(1, id);
            ResultSet result = statement.executeQuery();

            if (result.next()) {
                return result.getLong("total");
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
        throw new RuntimeException("Couldn't count bans");
    }

    @Override
    public Long countByAdmin(Long id) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(COUNT_BY_ADMIN_ID);
            statement.setLong(1, id);
            ResultSet result = statement.executeQuery();

            if (result.next()) {
                return result.getLong("total");
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
        throw new RuntimeException("Couldn't count bans");
    }

    @Override
    public boolean isBanned(Long userId) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(IS_BANNED);
            statement.setLong(1, userId);
            ResultSet result = statement.executeQuery();

            if (result.next()) {
                return result.getLong("active_bans") > 0;
            }
            return false;
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
        throw new RuntimeException("Couldn't check if user banned");
    }

    private Ban process(ResultSet result, Connection connection) throws SQLException {
        Ban ban = new Ban();
        ban.setId(result.getLong("id"));
        ban.setUser(userDao.getById(result.getLong("user_id"), connection));
        ban.setAdmin(userDao.getById(result.getLong("admin_id"), connection));
        ban.setStartDate(result.getTimestamp("start_date").toLocalDateTime().atZone(ZoneId.systemDefault()));
        ban.setEndDate(result.getTimestamp("end_date").toLocalDateTime().atZone(ZoneId.systemDefault()));
        ban.setReason(result.getString("reason"));

        return ban;
    }

}
