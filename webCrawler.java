/*
CSII FINAL PROJECT - WEB CRAWLER
By Kyle Wyse
Created 19 April 2017
Last edited 5 May 2017
NOTE: REQUIRES THE FOLLOWING FILES TO RUN:
(On the P-Drive: Acedemic/Kruse/cs240/bookFiles/A 07 assign)
BSTInterface
BSTNode
LinkedStack
LinkedUnbndQueue
LLNode
QueueInterface
QueueUnderflowException
StackInterface
StackUnderflowException
UnboundedQueueInterface
UnboundedStackInterface
*/

//24 October 2018
//NOTE: Also fails to handle an initial site w/ no links --> null pointer in the to-be-visited list

import java.util.*;
import java.net.*;
import java.net.MalformedURLException;
import java.io.*;
import java.util.regex.*;

public class webCrawler
{
  public static void main(String[] args) throws MalformedURLException, IOException
  {
    Scanner kybd = new Scanner(System.in);
    Scanner urlScan;
    InputStream stream; 
    /* Each of these collections will need to be regularly added to and searched. The unvisted URLS will also need 
     * regular removals, but not as frequently. There will also be a one-time traversal of each collection at the end.
     * Because the user is allowed to contine crawling indefinetely, collections with limited space would not be 
     * suitable. Because of how frequently elements need to be compared, I chose a tree implementation, which is O(logN).
     * I copied and made minor adjustments to the add and remove methods in BinarySearchTree to ensure unique inserts 
     * and deletion exacly where I want.
     */
    BinarySearchTree<String> emailsFound = new BinarySearchTree<String>();
    BinarySearchTree<String> visitedURLS = new BinarySearchTree<String>();
    BinarySearchTree<String> unvisitedURLS = new BinarySearchTree<String>();
    //Define regex patterns for parsing
    Pattern emailPattern = Pattern.compile("[\\w!#$%&'*+-/=?^_`{|}~]+@([\\w-]+\\.)+[\\w-]{2,4}?(?=[\\p{P}&&[^.]]|$)", Pattern.DOTALL);  
    Pattern webPattern = Pattern.compile("(https?://)+\\w+.+?(?=\"|$)", Pattern.DOTALL);
      //  .+?(?=abc)  from  http://stackoverflow.com/questions/7124778/how-to-match-anything-up-until-this-sequence-of-
      //                    characters-in-a-regular-expres
      //  [\\p{P}&&[^abc]]  from  http://stackoverflow.com/questions/24741797/java-regex-to-remove-specific-punctuation
      //  Other sources:
      //  http://regexlib.com/Search.aspx?k=email&AspxAutoDetectCookieSupport=1
      //  https://en.wikipedia.org/wiki/Email_address
    
    Matcher emailMatcher;
    Matcher webMatcher;
    //Prompt for URL
    URL site;
    do
    {
      System.out.println("Enter a URL");
      site = new URL(kybd.next());
      //site = new URL("http://jcsites.juniata.edu/faculty/kruse/cs240/assignments/PatternLab.htm");
    }
    while(!CrawlerSupport.robotSafe(site));
    
    boolean crawlNext = true;
    while(crawlNext)
    {
      if(CrawlerSupport.robotSafe(site)){
        try
        {
          System.out.println("Visiting site: " + site);
          //Parse the provided web-page for new emails and urls
          stream = site.openStream();
          System.out.println("Site Opened");
          urlScan = new Scanner(stream);
          String token;
          while( urlScan.hasNext() )
          {
            token = urlScan.next();
            //System.out.println(token);
            emailMatcher = emailPattern.matcher(token);
            webMatcher = webPattern.matcher(token);
            if(emailMatcher.find() && !emailsFound.contains(emailMatcher.group()))
            {
              System.out.println("New email: " + emailMatcher.group());
              emailsFound.add(emailMatcher.group());
            }
            else if(webMatcher.find() && !visitedURLS.contains(webMatcher.group()) 
                                      && !unvisitedURLS.contains(webMatcher.group()))
            {
              System.out.println("New URL: " + webMatcher.group());
              unvisitedURLS.add(webMatcher.group());
            }
          }
          stream.close();
          //Prompt for whether it should continue to another URL (from the structure tracking the unvisited URLs).
          System.out.println("Continue to next URL? Y/N");
          crawlNext = kybd.next().equalsIgnoreCase("Y");
        }
        catch(Exception e){
          System.out.println("Bad URL: " + site + "\n" + e + "\nLoading next URL");
        }
      }
      visitedURLS.add(site.toString());
      try{
        site = new URL(unvisitedURLS.remove2());
      }
      catch(NullPointerException e){
        System.out.println("No more urls to process.");
        crawlNext = false;
      }
    }
    //reset for inorder traversing
    emailsFound.reset(1);
    visitedURLS.reset(1);
    unvisitedURLS.reset(1);
    //On exit, ouput the emails, URLs visited, and URLs to be visited
    System.out.println("Emails stored: \n");
    for(int i = 0; i < emailsFound.size(); i++)
      System.out.println(emailsFound.getNext(1));
    
    System.out.println("\nURLs visited: \n");
    for(int i = 0; i < visitedURLS.size(); i++)
      System.out.println(visitedURLS.getNext(1));
    
    System.out.println("\nURLS to be processed: \n");
    for(int i = 0; i < unvisitedURLS.size(); i++)
      System.out.println(unvisitedURLS.getNext(1));
  }
}
/*  OUTPUT:
Welcome to DrJava.  Working directory is C:\Users\kyled\OneDrive - Juniata College\Computer Science II\Assignment_FINAL
> run webCrawler
Enter a URL 
 [DrJava Input Box]
Visiting site: http://jcsites.juniata.edu/faculty/kruse/cs240/assignments/PatternLab.htm 
Site Opened 
New URL: http://beginnersbook.com/2014/08/java-regex-tutorial/ 
New URL: https://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html 
New URL: https://docs.oracle.com/javase/7/docs/api/java/util/regex/Matcher.html 
New email: kruse@juniata.edu 
New email: helpe@juniata.edu 
New email: LCKruse@yahoo.com 
New URL: http://www.si.com 
New email: gwk@cfm.brown.edu 
New email: kruseb@msn.com 
New email: abc@juniata.edu 
New email: abc@yahoo.com 
New email: Jerry.Kruse@juniata.edu 
New email: Jerry-Kruse@juniata.edu 
New URL: http://www.cnn.com 
Continue to next URL? Y/N 
 [DrJava Input Box]
Visiting site: http://beginnersbook.com/2014/08/java-regex-tutorial/ 
Bad URL: http://beginnersbook.com/2014/08/java-regex-tutorial/
java.io.IOException: Server returned HTTP response code: 403 for URL: http://beginnersbook.com/2014/08/java-regex-tutorial/
Loading next URL 
Visiting site: https://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html 
Site Opened 
New URL: http://www.w3.org/TR/html4/loose.dtd 
New URL: http://www.unicode.org/reports/tr18/ 
New URL: http://www.unicode.org/unicode/standard/standard.html 
New URL: http://www.unicode.org/reports/tr18/#Default_Grapheme_Clusters 
New URL: http://www.oreilly.com/catalog/regex3/ 
New URL: http://bugreport.sun.com/bugreport/ 
New URL: http://docs.oracle.com/javase/7/docs/index.html 
New URL: http://download.oracle.com/otndocs/jcp/java_se-7-mrel-spec/license.html 
New URL: http://www.oracle.com/technetwork/java/redist-137594.html 
New URL: https://www.oracleimg.com/us/assets/metrics/ora_docs.js 
Continue to next URL? Y/N 
 [DrJava Input Box]
Visiting site: https://docs.oracle.com/javase/7/docs/api/java/util/regex/Matcher.html 
Site Opened 
Continue to next URL? Y/N 
 [DrJava Input Box]
Visiting site: http://www.w3.org/TR/html4/loose.dtd 
Site Opened 
New URL: http://www.w3.org/TR/1999/REC-html401-19991224 
New URL: http://www.w3.org/TR/html4/loose.dtd 
New URL: http://www.w3.org/TR/1999/REC-html401-19991224/strict.dtd 
New URL: http://www.w3.org/TR/1999/REC-html401-19991224/frameset.dtd 
Continue to next URL? Y/N 
 [DrJava Input Box]
Emails stored: 
 
Jerry-Kruse@juniata.edu 
Jerry.Kruse@juniata.edu 
LCKruse@yahoo.com 
abc@juniata.edu 
abc@yahoo.com 
gwk@cfm.brown.edu 
helpe@juniata.edu 
kruse@juniata.edu 
kruseb@msn.com 

URLs visited: 
 
http://beginnersbook.com/2014/08/java-regex-tutorial/ 
http://jcsites.juniata.edu/faculty/kruse/cs240/assignments/PatternLab.htm 
http://www.w3.org/TR/html4/loose.dtd 
https://docs.oracle.com/javase/7/docs/api/java/util/regex/Matcher.html 
https://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html 

URLS to be processed: 
 
http://bugreport.sun.com/bugreport/ 
http://docs.oracle.com/javase/7/docs/index.html 
http://download.oracle.com/otndocs/jcp/java_se-7-mrel-spec/license.html 
http://www.cnn.com 
http://www.oracle.com/technetwork/java/redist-137594.html 
> 
*/