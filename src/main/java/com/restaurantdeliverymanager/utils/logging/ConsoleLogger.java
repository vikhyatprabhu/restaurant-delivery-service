package com.restaurantdeliverymanager.utils.logging;

import java.util.Date;

public class ConsoleLogger
{

  private ConsoleLogger()
  {
    // private constructor
  }

  private String withSystemTime(String text){
      return new Date()+" "+text;
  }

  public synchronized void log(String text){
       System.out.println(withSystemTime(text));
  }


  private static class ConsoleLoggerSingleton
  {
    private static final ConsoleLogger INSTANCE = new ConsoleLogger();
  }

  public static  ConsoleLogger getInstance()
  {
    return ConsoleLoggerSingleton.INSTANCE;
  }
}

