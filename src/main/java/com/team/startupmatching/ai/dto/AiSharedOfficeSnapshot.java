package com.team.startupmatching.ai.dto;

public class AiSharedOfficeSnapshot {
    private final long id;
    private final String name;
    private final String location;
    private final String description;
    private final long roomCount;
    private final long size;
    private final long maxCount;

    public AiSharedOfficeSnapshot(long id, String name, String location, String description, long roomCount, long size, long maxCount) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.description = description;
        this.roomCount = roomCount;
        this.size = size;
        this.maxCount = maxCount;
    }

    // Getter 메서드들...
    public long getId() { return id; }
    public String getName() { return name; }
    public String getLocation() { return location; }
    public String getDescription() { return description; }
    public long getRoomCount() { return roomCount; }
    public long getSize() { return size; }
    public long getMaxCount() { return maxCount; }
}