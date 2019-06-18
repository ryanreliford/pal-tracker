package io.pivotal.pal.tracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTimeEntryRepository implements TimeEntryRepository {

    private long id = 1L;

    private Map<Long, TimeEntry> minimalDatabase = new HashMap<>();

    @Override
    public TimeEntry create(TimeEntry timeEntryToCreate) {
        timeEntryToCreate.setId(id++);
        minimalDatabase.put(timeEntryToCreate.getId(), timeEntryToCreate);
        return timeEntryToCreate;
    }

    @Override
    public TimeEntry find(long timeEntryId) {
        return minimalDatabase.get(timeEntryId);
    }

    @Override
    public List<TimeEntry> list() {
        return new ArrayList<TimeEntry>(minimalDatabase.values());
    }

    @Override
    public TimeEntry update(long timeEntryId, TimeEntry expected) {
        TimeEntry updated =  minimalDatabase.get(timeEntryId);
        if (updated == null) return null;
        updated.setDate(expected.getDate());
        updated.setHours(expected.getHours());
        updated.setProjectId(expected.getProjectId());
        updated.setUserId(expected.getUserId());
        return updated;
    }

    @Override
    public void delete(long timeEntryId) {
        minimalDatabase.remove(timeEntryId);
    }
}
