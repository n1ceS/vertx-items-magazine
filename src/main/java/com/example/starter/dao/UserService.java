package com.example.starter.dao;

import com.example.starter.dao.impl.UserServiceImpl;
import com.example.starter.model.User;
import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

import io.vertx.ext.mongo.MongoClient;


@ProxyGen
public interface UserService {

      @Fluent
      UserService save(User user, Handler<AsyncResult<String>> resultHandler);

      @Fluent
      UserService checkCredentials(User user, Handler<AsyncResult<String>> resultHandler);

      @Fluent
      UserService checkIfUserAlreadyExists(String username, Handler<AsyncResult<Boolean>> resultHandler);

      static UserService createProxy(Vertx vertx, String address) {
         return new UserServiceVertxEBProxy(vertx, address);
      }

      static UserService create(MongoClient client) {
        return new UserServiceImpl(client);
      }

}
