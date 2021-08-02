package com.example.starter.verticle;

import com.example.starter.dao.UserService;
import com.example.starter.model.User;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;


public class UserVerticle extends AbstractVerticle {
  private UserService userService;
  @Override
  public void start(Promise<Void> startPromise){
    userService = UserService.createProxy(vertx, "user-service-address");
    vertx.eventBus().consumer("users.register", addUser());
    vertx.eventBus().consumer("users.login", checkCredentials());
  }

  private Handler<Message<Object>> checkCredentials() {
    return handler -> {
      User user = new User((JsonObject) handler.body());
      userService.checkCredentials(user, rs -> {
        if(rs.succeeded()) {
          handler.reply(rs.result());
        }else {
          handler.reply(null);
        }
      });
    };
  }

  private Handler<Message<Object>> addUser() {
    return handler -> {
      User user = new User((JsonObject) handler.body());
      userService.checkIfUserAlreadyExists(user.getLogin(), rs -> {
        if(rs.result()) {
          handler.reply(rs.result());
        }else {
          userService.save(user, res -> {
            handler.reply(res.result());
          });
        }
      });

    };
  }
}
