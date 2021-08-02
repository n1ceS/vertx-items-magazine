package com.example.starter.dao.impl;

import com.example.starter.dao.UserService;
import com.example.starter.model.User;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.ext.mongo.MongoClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.UUID;

public class UserServiceImpl implements UserService {

  private  static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

  MongoClient mongoClient;
  static BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

  public UserServiceImpl(MongoClient mongoClient) {
    this.mongoClient = mongoClient;
  }

  @Override
  public UserService save(User user, Handler<AsyncResult<String>> resultHandler) {
    user.setId(UUID.randomUUID().toString());
    user.setPassword(get_Bcrypt_Password(user.getPassword()));
    JsonObject json = JsonObject.mapFrom(user);

    mongoClient.save(User.DB_TABLE, json, res -> {
      if(res.succeeded()) {
        LOGGER.info("User created: {}", res.result());
        resultHandler.handle(Future.succeededFuture(json.getString("id")));
      } else {
        LOGGER.error("Account has not been created", res.cause());
        resultHandler.handle(Future.failedFuture(res.cause()));
      }
    });
    return this;
  }

  @Override
  public UserService checkCredentials(User user, Handler<AsyncResult<String>> resultHandler) {
    mongoClient.findWithOptions(User.DB_TABLE, new JsonObject().put("login", user.getLogin()), new FindOptions().setLimit(1).setBatchSize(1), res -> {
      if(res.result().size()>0) {
        LOGGER.info("User exists: {}", res.result());
        if(encoder.matches(user.getPassword(), res.result().get(0).getString("password"))) {
          String id = res.result().get(0).getString("id");
          resultHandler.handle(Future.succeededFuture(id));
        } else {
          LOGGER.error("Bad password given", res.cause());
          resultHandler.handle(Future.failedFuture(res.cause()));
        }
      } else {
        LOGGER.error("Cannot find user", res.cause());
        resultHandler.handle(Future.failedFuture(res.cause()));
      }
    });
    return this;
  }

  public UserService checkIfUserAlreadyExists(String username, Handler<AsyncResult<Boolean>> resultHandler) {
    mongoClient.findWithOptions(User.DB_TABLE, new JsonObject().put("login", username), new FindOptions().setLimit(1).setBatchSize(1), res -> {
      if(res.result().size()>0) {
        LOGGER.info("User exists: {}", res.result());
        resultHandler.handle(Future.succeededFuture(true));
      } else {
        resultHandler.handle(Future.succeededFuture(false));
      }
    });
    return this;
  }

  private static String get_Bcrypt_Password(String passwordToHash) {
    return encoder.encode(passwordToHash);
  }
}
