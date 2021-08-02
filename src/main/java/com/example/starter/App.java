package com.example.starter;



import com.example.starter.verticle.ItemVerticle;
import com.example.starter.verticle.MongoVerticle;
import com.example.starter.verticle.UserVerticle;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.JWTAuthHandler;
import io.vertx.json.schema.Schema;
import io.vertx.json.schema.SchemaParser;
import io.vertx.json.schema.SchemaRouter;
import io.vertx.json.schema.SchemaRouterOptions;
import org.apache.logging.log4j.core.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;


public class App extends AbstractVerticle {

  private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

  private SchemaRouter schemaRouter;
  private SchemaParser schemaParser;
  private Schema itemSchema;
  private Schema userSchema;

  private static JWTAuth authProvider;
  private static Vertx vertx;

  public static void main(String[] args) {
    vertx = Vertx.vertx();
    authProvider = JWTGen.getJWTInstance(vertx);
    vertx.deployVerticle(new MongoVerticle());
    vertx.deployVerticle(new ItemVerticle());
    vertx.deployVerticle(new UserVerticle());
    vertx.deployVerticle(new App());
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {

    schemaRouter = SchemaRouter.create(vertx, new SchemaRouterOptions());
    schemaParser = SchemaParser.createDraft201909SchemaParser(schemaRouter);
    itemSchema = getSchemaFromJsonFile(schemaParser, "item-schema.json");
    userSchema = getSchemaFromJsonFile(schemaParser, "user-schema.json");

    Router router = Router.router(vertx);
    router.get("/items").handler(JWTAuthHandler.create(authProvider)).handler(this::getUserItems);
    router.route(("/items")).handler(BodyHandler.create());
    router.post("/items").handler(JWTAuthHandler.create(authProvider)).handler(this::addItem);
    router.route(("/login")).handler(BodyHandler.create());
    router.post("/login").handler(this::loginUser);
    router.route(("/register")).handler(BodyHandler.create());
    router.post("/register").handler(this::registerUser);


    vertx.createHttpServer().requestHandler(router).listen(8080);
  }

  private void registerUser(RoutingContext ctx) {
    userSchema.validateAsync(ctx.getBodyAsJson()).onComplete(ar -> {
      if (ar.succeeded()) {
        vertx.eventBus().request("users.register", ctx.getBodyAsJson(), rh -> {
          if (rh.succeeded()) {
            if(rh.result().body() == Boolean.TRUE) {
              ctx.response().setStatusCode(400).end("User with this login exists");
              return;
            }
            ctx.response().setStatusCode(204).end();
          }else {
            ctx.response().setStatusCode(400).end(rh.cause().getMessage());
          }
        });
      } else {
        ctx.response().setStatusCode(400).end(ar.cause().getMessage());
      }
    });
  }

  private void loginUser(RoutingContext ctx) {
    userSchema.validateAsync(ctx.getBodyAsJson()).onComplete(ar -> {
      if (ar.succeeded()) {
        vertx.eventBus().request("users.login", ctx.getBodyAsJson(), rh -> {
          if(rh.result().body() != null) { //
            String id = (String) rh.result().body();
            LOGGER.info(id);
            String token = authProvider.generateToken(new JsonObject().put("sub", id));
            ctx.response().setStatusCode(200).end(new JsonObject().put("token", token).toString());
          } else {
            ctx.response().setStatusCode(401).end("Bad Credentials");
          }
        });
      } else {
        ctx.response().setStatusCode(400).end(ar.cause().getMessage());
      }
    });
  }

  private void addItem(RoutingContext ctx) {
    itemSchema.validateAsync(ctx.getBodyAsJson()).onComplete(ar -> {
      if (ar.succeeded()) {
        String id = ctx.user().principal().getString("sub");
        JsonObject itemJson = ctx.getBodyAsJson();
        itemJson.put("owner", id);
        vertx.eventBus().request("items.add", itemJson, rh -> {
          if (rh.succeeded()) {
            ctx.response().setStatusCode(204).end();
          }else {
            ctx.response().setStatusCode(400).end(rh.cause().getMessage());
          }
        });
      } else {
        ctx.response().setStatusCode(400).end(ar.cause().getMessage());
      }
    });
  }

  private void getUserItems(RoutingContext ctx) {
    String id = ctx.user().principal().getString("sub");
    vertx.eventBus().request("items.getAll", new JsonObject().put("owner", id), rh -> {
        ctx.response().end((String) rh.result().body());
      });
  }

  private Schema getSchemaFromJsonFile(SchemaParser schemaParser, String filename) throws IOException {
    String jsonStr = IOUtils.toString(getFileReaderFromResource(filename));
    JsonObject jsonObj = new JsonObject(jsonStr);
    return schemaParser.parse(jsonObj);
  }

  private FileReader getFileReaderFromResource(String fileName) throws FileNotFoundException {
    ClassLoader classLoader = getClass().getClassLoader();
    URL resource = classLoader.getResource(fileName);
    if (resource == null) {
      throw new IllegalArgumentException("file not found! " + fileName);
    } else {
      return new FileReader(resource.getFile());
    }

  }
}
