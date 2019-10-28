package dev.tycho.DiscordRoleSync.database;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

import java.util.Date;
import java.util.UUID;

public class Link {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private String discordId;

    @DatabaseField
    private UUID uuid;

    @DatabaseField(dataType = DataType.DATE_STRING, format = "yyyy-MM-dd HH:mm:ss",
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private Date linkDate;

    public Link() {

    }

    public Link(String discordId, UUID uuid) {
        this.discordId = discordId;
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }
}
