package io.pivotal.pal.tracker;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.simple.SimpleJdbcInsertOperations;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JdbcTimeEntryRepository implements TimeEntryRepository {
    private DataSource ds;

    private final JdbcTemplate jdbcTemplate;

    public JdbcTimeEntryRepository(DataSource ds) {
        this.ds = ds;
        this.jdbcTemplate = new JdbcTemplate(ds);
    }

    @Override
    public TimeEntry create(TimeEntry timeEntryToCreate) {
        String[] columns = {"project_id", "user_id", "date", "hours"};
        SimpleJdbcInsertOperations time_entries = new SimpleJdbcInsert(ds)
                .usingColumns(columns)
                .usingGeneratedKeyColumns("id")
                .withTableName("time_entries");
        Map<String, Object> params = new HashMap<>();
        params.put("project_id", timeEntryToCreate.getProjectId());
        params.put("user_id", timeEntryToCreate.getUserId());
        params.put("date", timeEntryToCreate.getDate());
        params.put("hours", timeEntryToCreate.getHours());
        timeEntryToCreate.setId(time_entries.executeAndReturnKey(params).longValue());

        return timeEntryToCreate;
    }

    @Override
    public TimeEntry find(long timeEntryId) {
        List<TimeEntry> queryResult = jdbcTemplate.query("select * from time_entries where id = " + timeEntryId, new MyRowMapper());
        return queryResult.size() > 0 ? queryResult.get(0) : null;
    }

    @Override
    public List<TimeEntry> list() {
        List<TimeEntry> queryResult = jdbcTemplate.query("select * from time_entries", new MyRowMapper());
        return queryResult;
    }

    @Override
    public TimeEntry update(long timeEntryId, TimeEntry expected) {
        PreparedStatement updateTimeEntry = null;

        try {
            updateTimeEntry = getConnection().prepareStatement(
                    "update time_entries set user_id = ?, project_id = ?, hours = ?, date = ? where id = ?");
            updateTimeEntry.setLong(1, expected.getUserId());
            updateTimeEntry.setLong(2, expected.getProjectId());
            updateTimeEntry.setInt(3, expected.getHours());
            updateTimeEntry.setDate(4, Date.valueOf(expected.getDate().toString()));
            updateTimeEntry.setLong(5, timeEntryId);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            updateTimeEntry.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new TimeEntry(timeEntryId, expected.getProjectId(), expected.getUserId(), expected.getDate(), expected.getHours());
    }

    @Override
    public void delete(long timeEntryId) {
        jdbcTemplate.update("delete from time_entries where id = ?", timeEntryId);
    }

    private Connection getConnection() {
        Connection result = null;
        try {
            result = jdbcTemplate.getDataSource().getConnection();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    static class MyRowMapper implements RowMapper<TimeEntry> {

        @Override
        public TimeEntry mapRow(ResultSet rs, int rowNum) throws SQLException {
            TimeEntry result = new TimeEntry();
            result.setId(rs.getLong("id"));
            result.setUserId(rs.getLong("user_id"));
            result.setProjectId(rs.getLong("project_id"));
            result.setHours(rs.getInt("hours"));
            result.setDate(rs.getDate("date").toLocalDate());
            return result;
        }
    }
}
