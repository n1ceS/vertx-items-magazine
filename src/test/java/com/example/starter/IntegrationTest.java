package com.example.starter;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.junit5.VertxExtension;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.net.ServerSocket;


@ExtendWith(VertxExtension.class)
public class IntegrationTest {
//  Vertx vertx;
//  static int port;
//
//  @Before
//  public void setUp(TestContext context) throws IOException {
//    vertx = Vertx.vertx();
//    ServerSocket socket = new ServerSocket(0);
//    port = socket.getLocalPort();
//    socket.close();
//    DeploymentOptions options = new DeploymentOptions()
//      .setConfig(new JsonObject().put("http.port", port)
//      );
//    vertx.deployVerticle(App.class.getName(), options, context.asyncAssertSuccess());
//  }
//
//  @Test
//  public void testMyApplication(TestContext context) {
//    final Async async = context.async();
//    vertx.createHttpClient().request(port, "localhost", "/", response -> {
//      response.handler(body -> {
//        context.assertTrue(body.toString().contains("Hello"));
//        async.complete();
//      });
//    });
//  }
}

