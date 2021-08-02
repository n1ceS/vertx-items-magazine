package com.example.starter.dao;

import com.example.starter.dao.impl.ItemServiceImpl;
import com.example.starter.model.Item;
import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

import java.util.List;

@ProxyGen
public interface ItemService {

  @Fluent
  ItemService save(Item item, Handler<AsyncResult<Item>> resultHandler);

  @Fluent
  ItemService findAll(JsonObject username, Handler<AsyncResult<List<JsonObject>>> resultHandler);

  static ItemService createProxy(Vertx vertx, String address) {
    return  new ItemServiceVertxEBProxy(vertx, address);
  }

  static ItemService create(MongoClient mongoClient) {
    return new ItemServiceImpl(mongoClient);
  }
}
