package com.example.starter;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.JWTOptions;
import io.vertx.ext.auth.PubSecKeyOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;

public class JWTGen {
  private static JWTAuth jwtAuth;

  public static JWTAuth getJWTInstance(Vertx vertx) {
    return getJWTAuth(vertx);
  }

  private static JWTAuth getJWTAuth(Vertx vertx) {
    if(jwtAuth == null) {
      jwtAuth = JWTAuth.create(vertx, new JWTAuthOptions()
        .addPubSecKey(new PubSecKeyOptions()
          .setAlgorithm("HS256")
          .setBuffer("$#S^$%HN&#T53")
        ));
    }

    return jwtAuth;
  }

  public static String generateToken(JsonObject data) {
    return jwtAuth.generateToken(data, new JWTOptions().setAlgorithm("RS256").setExpiresInMinutes(480));
  }
}
