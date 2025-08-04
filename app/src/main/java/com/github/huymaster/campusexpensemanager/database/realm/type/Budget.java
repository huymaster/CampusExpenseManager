package com.github.huymaster.campusexpensemanager.database.realm.type;

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

	public Budget(String name, double amount, Date startDate) {
		this.name = name;
		this.amount = amount;
		this.startDate = startDate;
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
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(startDate);
		calendar.add(Calendar.MONTH, 1);

		endDate = calendar.getTime();
	}
}
