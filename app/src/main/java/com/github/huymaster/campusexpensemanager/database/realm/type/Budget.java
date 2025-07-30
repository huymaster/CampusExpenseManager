package com.github.huymaster.campusexpensemanager.database.realm.type;

import java.time.temporal.ChronoField;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Budget extends RealmObject {
    @Required
    @PrimaryKey
    private UUID id = UUID.randomUUID();

    @Required
    private String name;

    @Required
    private Double amount;

    @Required
    private Date startDate;

    @Required
    private Date endDate;

    public Budget() {
        startDate = Calendar.getInstance().getTime();
        setEndDate(startDate);
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
        setEndDate(startDate);
    }

    public Date getEndDate() {
        return endDate;
    }

    private void setEndDate(Date startDate) {
        int day = startDate.toInstant().get(ChronoField.DAY_OF_MONTH);
        int month = startDate.toInstant().get(ChronoField.MONTH_OF_YEAR);
        int year = startDate.toInstant().get(ChronoField.YEAR);

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        calendar.add(Calendar.MONTH, 1);

        endDate = calendar.getTime();
    }
}
