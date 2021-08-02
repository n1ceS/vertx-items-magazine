package com.example.starter.model;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;


@DataObject
public class Item {
  public static final String DB_TABLE = "items";

  private String id;
  private String owner;
  private String name;

  public Item() {
  }

  public Item(String id, String owner, String name) {
    this.id = id;
    this.owner = owner;
    this.name = name;
  }

  public Item(JsonObject jsonObject) {
    this.id = jsonObject.getString("id");
    this.owner = jsonObject.getString("owner");
    this.name = jsonObject.getString("name");
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getOwner() {
    return owner;
  }

  public void setOwner(String owner) {
    this.owner = owner;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }


  public JsonObject toJson() {
    return JsonObject.mapFrom(this);
  }

  @Override
  public String toString() {
    return Json.encodePrettily(this);
  }
}
