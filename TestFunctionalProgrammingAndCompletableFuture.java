package com.dremio.plugins.dremiocatalog.store;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

public class TestFunctionalProgramming {
  public static void main(String[] args) {
    System.out.println("You are in main thread - " + Thread.currentThread().getName());
    Function<Boolean, CompletionStage<Boolean>> function = (b) -> {
      System.out.println("You are in main thread - " + Thread.currentThread().getName());
      return CompletableFuture.supplyAsync(() -> {
        try {
          Thread.sleep(5000);
        } catch (InterruptedException e) {
          System.out.println("Interrupted in another thread - " + Thread.currentThread().getName());
          throw new RuntimeException(e);
        }
        System.out.println("(After sleep) You are in another thread - " + Thread.currentThread().getName());
        return b;
      });
    };
    CompletionStage<Boolean> completionStage = function.apply(true).whenCompleteAsync((result, throwable) -> {
      try {
        Thread.sleep(10000);
      } catch (InterruptedException e) {
        System.out.println("Interrupted in another thread - " + Thread.currentThread().getName());
        throw new RuntimeException(e);
      }
      System.out.println("(After completion) You are in another thread - " + Thread.currentThread().getName());
      if (throwable != null) {
        System.out.println("Exception occurred - " + throwable.getMessage());
      } else {
        System.out.println("Result is - " + result);
      }
    });

    CompletionStage<Void> completionStage1 = CompletableFuture.runAsync(new UserInfoThread(new UserInfo("John", "dvdsvdsfv", "svsdvds")));

    //If we don't use below join() method, then the main thread will not wait for the completion of the completionStage thread.
    CompletableFuture.allOf(completionStage.toCompletableFuture(), completionStage1.toCompletableFuture()).join();
  }
}
class UserInfoThread implements Runnable {

  UserInfo userInfo;
  UserInfoThread(UserInfo userInfo) {
    this.userInfo = userInfo;
  }

  @Override
  public void run() {
    userInfo.getValue();
  }
}

class UserInfo {
  private String name;
  private String email;
  private String password;

  public UserInfo(String name, String email, String password) {
    this.name = name;
    this.email = email;
    this.password = password;
  }

  public int getValue() {
    UserInfo userInfo = new UserInfo("John", "john@dremio.com", "password");
    System.out.println("UserInfo: " + userInfo);
    System.out.println("UserInfo#Hash: " + userInfo.hashCode());
    int value = 1;
    for (int i = 0; i < 1000; i++) {
      value += 1;
    }

    System.out.println("value: " + value);

    return value;
  }
}
