/*
WEB CRAWLER SUPPORT
By Kyle Wyse
Created 19 April 2017
*/
import java.util.*;
import java.net.*;
import java.net.MalformedURLException;
import java.io.*;
import java.util.regex.*;

public class CrawlerSupport{
  //  .getHost()  and  "http://" + strHost + "/robots.txt"  and some general design inspiration 
  //                       from  https://cs.nyu.edu/courses/fall02/G22.3033-008/WebCrawler.java
  public static boolean robotSafe(URL site){
    String strHost = site.getHost();
    Scanner roboScan;
    String strRobot = "http://" + strHost + "/robots.txt";
    URL urlRobot;
    try{ 
      urlRobot = new URL(strRobot);
    }
    catch (MalformedURLException e){
     return false;
    }
    try{
      InputStream botStream = urlRobot.openStream();
      //look for "User-agent: *" then if not followed by "Disallow: /" check if site contains what ever is between "/" and "/"
      roboScan = new Scanner(botStream);
      while(roboScan.hasNext()){
        if(roboScan.nextLine().equals("User-agent: *")){
          while(roboScan.hasNext()){
            if(roboScan.next().equals("Disallow:") && site.toString().contains(roboScan.next())){
              System.out.println("Crawler not allowed on site: " + site);
              return false;
            }
          }
          break;
        }
      }
      botStream.close();
    }
    catch(IOException e){
      // if there is no robots.txt file, it is OK to search
      return true;
    }
    catch(Exception e){
      // something weird happened, skip this site
      System.out.println("Bad site: " + site + "\n" + e + "\nLoading next URL");
      return false;
    }
    return true;
  }
  
  
}