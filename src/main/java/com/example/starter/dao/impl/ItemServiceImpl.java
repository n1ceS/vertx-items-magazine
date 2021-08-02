package com.example.starter.dao.impl;

import com.example.starter.dao.ItemService;
import com.example.starter.model.Item;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ItemServiceImpl implements ItemService {
  MongoClient mongoClient;
  private static final Logger LOGGER = LoggerFactory.getLogger(ItemServiceImpl.class);

  public ItemServiceImpl(final MongoClient client) {
    this.mongoClient = client;
  }

  @Override
  public ItemService save(Item item, Handler<AsyncResult<Item>> resultHandler) {
    item.setId(UUID.randomUUID().toString());

    JsonObject json = JsonObject.mapFrom(item);
    mongoClient.save(Item.DB_TABLE, json, res -> {
      if (res.succeeded()) {
        LOGGER.info("Item has been created: {}", res.result());
        resultHandler.handle(Future.succeededFuture(item));
      } else {
        LOGGER.error("Item has not been created: {}", res.result());
        resultHandler.handle(Future.failedFuture(res.cause()));
      }
    });
    return this;
  }

  @Override
  public ItemService findAll(JsonObject userid, Handler<AsyncResult<List<JsonObject>>> resultHandler) {
      mongoClient.find(Item.DB_TABLE, userid, res -> {
        if(res.succeeded()) {
          List<JsonObject> items = res.result().stream().map(it -> new JsonObject().put("id", it.getString("id")).put("name", it.getString("name"))).collect(Collectors.toList());
          resultHandler.handle(Future.succeededFuture(items));
        } else {
          LOGGER.error("Items not found", res.cause());
        }
      });
      return this;
  }
}
