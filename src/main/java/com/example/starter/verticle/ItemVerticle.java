package com.example.starter.verticle;

import com.example.starter.dao.ItemService;
import com.example.starter.model.Item;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

public class ItemVerticle extends AbstractVerticle {
  private ItemService itemService;

  @Override
  public void start(Promise<Void> startPromise) throws Exception {

    itemService = ItemService.createProxy(vertx, "item-service-address");
    vertx.eventBus().consumer("items.getAll", getAllUserItems());
    vertx.eventBus().consumer("items.add", addItem());
  }

  private Handler<Message<Object>> addItem() {
    return handler -> {
            Item item = new Item((JsonObject) handler.body());
            itemService.save(item, rs -> {
              handler.reply(Json.encodePrettily(item));
            });
    };
  }

  private Handler<Message<Object>> getAllUserItems() {
    return handler -> {
      itemService.findAll((JsonObject) handler.body(), rs -> {
        handler.reply(Json.encodePrettily(rs.result()));
      });
    };
  }



}
