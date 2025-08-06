package com.github.huymaster.campusexpensemanager.database.realm.type;

import androidx.annotation.Nullable;

import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Category extends RealmObject {
	@PrimaryKey
	private UUID id = UUID.randomUUID();
	@Required
	private String name;
	private String description;

	public Category() {
	}

	public Category(String name, String description) {
		this.name = name;
		this.description = description;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public boolean equals(@Nullable Object obj) {
		if (obj instanceof Category)
			return ((Category) obj).getId().equals(getId());
		return false;
	}
}
