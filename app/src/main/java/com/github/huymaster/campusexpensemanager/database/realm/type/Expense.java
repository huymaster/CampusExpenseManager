package com.github.huymaster.campusexpensemanager.database.realm.type;

import androidx.annotation.Nullable;

import java.util.Calendar;
import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Expense extends RealmObject {
	@PrimaryKey
	private UUID id = UUID.randomUUID();

	@Required
	private String name;

	@Required
	private Double amount;

	@Required
	private Long timestamp = Calendar.getInstance().getTimeInMillis();

	private Category category;

	public Expense() {
	}

	public Expense(String name, Double amount, Category category) {
		this.name = name;
		this.amount = amount;
		this.category = category;
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

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	@Override
	public boolean equals(@Nullable Object obj) {
		if (obj instanceof Expense)
			return ((Expense) obj).getId().equals(getId());
		return false;
	}
}
