package com.example.starter.verticle;

import com.example.starter.dao.ItemService;
import com.example.starter.dao.UserService;
import com.example.starter.dao.impl.ItemServiceImpl;
import com.example.starter.dao.impl.UserServiceImpl;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.serviceproxy.ServiceBinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MongoVerticle extends AbstractVerticle {
  private static final Logger LOGGER = LoggerFactory.getLogger(MongoVerticle.class);

  @Override
  public void start() throws Exception {

    ConfigStoreOptions file = new ConfigStoreOptions()
      .setType("file").setConfig(new JsonObject()
      .put("path", "config.json"));
    ConfigRetrieverOptions cro = new ConfigRetrieverOptions().addStore(file);
    ConfigRetriever retriever = ConfigRetriever.create(vertx, cro);
    retriever.getConfig(conf -> {
      if(conf.failed())
      {
        LOGGER.error("FAILED TO RETRIEVE CONFIG!");
      } else {
        JsonObject dataSourceConfig = conf.result().getJsonObject("datasource");
        JsonObject mongoConfig = new JsonObject();

        mongoConfig.put("host", dataSourceConfig.getString("host"));
        mongoConfig.put("port", dataSourceConfig.getValue("port"));
        mongoConfig.put("db_name", dataSourceConfig.getString("db_name"));
        mongoConfig.put("trustAll", "true");

        final MongoClient mongoClient = MongoClient.createShared(vertx, mongoConfig);

        final ItemService itemService = new ItemServiceImpl(mongoClient);
        final UserService userService = new UserServiceImpl(mongoClient);


        new ServiceBinder(vertx)
          .setAddress("item-service-address")
          .register(ItemService.class, itemService);

        new ServiceBinder(vertx)
          .setAddress("user-service-address")
          .register(UserService.class, userService);
      }
    });
  }
}
