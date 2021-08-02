package com.example.starter.model;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

import java.util.UUID;

@DataObject
public class User {
  public static final String DB_TABLE = "users";

  private String id;
  private String login;
  private String password;

  public User(String id, String login, String password) {
    this.id = UUID.randomUUID().toString();
    this.login = login;
    this.password = password;
  }

  public User(JsonObject jsonObject) {
    this.id = jsonObject.getString("id");
    this.login = jsonObject.getString("login");
    this.password = jsonObject.getString("password");
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getLogin() {
    return login;
  }

  public void setLogin(String login) {
    this.login = login;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public JsonObject toJson() {
    return JsonObject.mapFrom(this);
  }

  @Override
  public String toString() {
    return Json.encodePrettily(this);
  }
}
