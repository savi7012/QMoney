
package com.crio.warmup.stock;


import com.crio.warmup.stock.dto.*;
import com.crio.warmup.stock.log.UncaughtExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import org.apache.logging.log4j.ThreadContext;
import java.time.LocalDate;
import java.util.Comparator;
import org.springframework.web.client.RestTemplate;
import java.util.*;

public class PortfolioManagerApplication {

  // TODO: CRIO_TASK_MODULE_JSON_PARSING
  // Task:
  // - Read the json file provided in the argument[0], The file is available in the classpath.
  // - Go through all of the trades in the given file,
  // - Prepare the list of all symbols a portfolio has.
  // - if "trades.json" has trades like
  // [{ "symbol": "MSFT"}, { "symbol": "AAPL"}, { "symbol": "GOOGL"}]
  // Then you should return ["MSFT", "AAPL", "GOOGL"]
  // Hints:
  // 1. Go through two functions provided - #resolveFileFromResources() and #getObjectMapper
  // Check if they are of any help to you.
  // 2. Return the list of all symbols in the same order as provided in json.

  // Note:
  // 1. There can be few unused imports, you will need to fix them to make the build pass.
  // 2. You can use "./gradlew build" to check if your code builds successfully.

  public static List<String> mainReadFile(String[] args) throws IOException, URISyntaxException {

    File file = resolveFileFromResources(args[0]);

    ObjectMapper objectMapper = getObjectMapper();

    PortfolioTrade[] trade = objectMapper.readValue(file, PortfolioTrade[].class);

    List<String> symbolList = new ArrayList<>();

    for (PortfolioTrade t : trade) {
      System.out.println(t.toString());
      symbolList.add(t.getSymbol());
    }
    return symbolList;
  }



  private static void printJsonObject(Object object) throws IOException {
    Logger logger = Logger.getLogger(PortfolioManagerApplication.class.getCanonicalName());
    ObjectMapper mapper = new ObjectMapper();
    logger.info(mapper.writeValueAsString(object));
  }

  private static File resolveFileFromResources(String filename) throws URISyntaxException {
    return Paths.get(Thread.currentThread().getContextClassLoader().getResource(filename).toURI())
        .toFile();

  }

  private static ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }


  // TODO: CRIO_TASK_MODULE_JSON_PARSING
  // Follow the instructions provided in the task documentation and fill up the correct values for
  // the variables provided. First value is provided for your reference.
  // A. Put a breakpoint on the first line inside mainReadFile() which says
  // return Collections.emptyList();
  // B. Then Debug the test #mainReadFile provided in PortfoliomanagerApplicationTest.java
  // following the instructions to run the test.
  // Once you are able to run the test, perform following tasks and record the output as a
  // String in the function below.
  // Use this link to see how to evaluate expressions -
  // https://code.visualstudio.com/docs/editor/debugging#_data-inspection
  // 1. evaluate the value of "args[0]" and set the value
  // to the variable named valueOfArgument0 (This is implemented for your reference.)
  // 2. In the same window, evaluate the value of expression below and set it
  // to resultOfResolveFilePathArgs0
  // expression ==> resolveFileFromResources(args[0])
  // 3. In the same window, evaluate the value of expression below and set it
  // to toStringOfObjectMapper.
  // You might see some garbage numbers in the output. Dont worry, its expected.
  // expression ==> getObjectMapper().toString()
  // 4. Now Go to the debug window and open stack trace. Put the name of the function you see at
  // second place from top to variable functionNameFromTestFileInStackTrace
  // 5. In the same window, you will see the line number of the function in the stack trace window.
  // assign the same to lineNumberFromTestFileInStackTrace
  // Once you are done with above, just run the corresponding test and
  // make sure its working as expected. use below command to do the same.
  // ./gradlew test --tests PortfolioManagerApplicationTest.testDebugValues

  public static List<String> debugOutputs() {

    String valueOfArgument0 = "trades.json";
    String resultOfResolveFilePathArgs0 =
        "/home/crio-user/workspace/vishalsaini72-ME_QMONEY_V2/qmoney/bin/main/trades.json";
    String toStringOfObjectMapper = "com.fasterxml.jackson.databind.ObjectMapper@67c27493";
    String functionNameFromTestFileInStackTrace = "PortfolioManagerApplication.mainReadFile()";
    String lineNumberFromTestFileInStackTrace = "29:1";


    return Arrays.asList(
        new String[] {valueOfArgument0, resultOfResolveFilePathArgs0, toStringOfObjectMapper,
            functionNameFromTestFileInStackTrace, lineNumberFromTestFileInStackTrace});
  }


  // Note:
  // Remember to confirm that you are getting same results for annualized returns as in Module 3.

  // Note:
  // 1. You may need to copy relevant code from #mainReadQuotes to parse the Json.
  // 2. Remember to get the latest quotes from Tiingo API.



  // TODO: CRIO_TASK_MODULE_REST_API
  // Find out the closing price of each stock on the end_date and return the list
  // of all symbols in ascending order by its close value on end date.

  // Note:
  // 1. You may have to register on Tiingo to get the api_token.
  // 2. Look at args parameter and the module instructions carefully.
  // 2. You can copy relevant code from #mainReadFile to parse the Json.
  // 3. Use RestTemplate#getForObject in order to call the API,
  // and deserialize the results in List<Candle>

  public static List<String> mainReadQuotes(String[] args) throws IOException, URISyntaxException {


    File file = resolveFileFromResources(args[0]);

    ObjectMapper objectMapper = getObjectMapper();

    PortfolioTrade[] trades = objectMapper.readValue(file, PortfolioTrade[].class);

    RestTemplate restTemplate = new RestTemplate();

    LocalDate endDate = LocalDate.parse(args[1]);

    String token = "0997ea6a76b0b2c799dafd67b364b0e8ac08a903";

    ArrayList<TotalReturnsDto> totalReturnsDto = new ArrayList<>();


    for (PortfolioTrade trade : trades) {

      String url = prepareUrl(trade, endDate, token);

      TiingoCandle[] tiingoCandles = restTemplate.getForObject(url, TiingoCandle[].class);

      // System.out.println(tiingoCandles.toString());
      
      Double closePrice = tiingoCandles[tiingoCandles.length - 1].getClose();

      totalReturnsDto.add(new TotalReturnsDto(trade.getSymbol(), closePrice));
      // for (TiingoCandle tiingoCandle : tiingoCandles) {
        
      //   TotalReturnsDto.add(tiingoCandle);
      // }
    }

    TotalReturnsDtoComparator totalReturnsDtoComparator = new TotalReturnsDtoComparator();
    Collections.sort(totalReturnsDto, totalReturnsDtoComparator);

    ArrayList<String> sortedStocks = new ArrayList<>();

    for (TotalReturnsDto td : totalReturnsDto) {
      sortedStocks.add(td.getSymbol());

    }
    return sortedStocks;
  }

  // TODO:
  // After refactor, make sure that the tests pass by using these two commands
  // ./gradlew test --tests PortfolioManagerApplicationTest.readTradesFromJson
  // ./gradlew test --tests PortfolioManagerApplicationTest.mainReadFile
  public static List<PortfolioTrade> readTradesFromJson(String filename)
      throws IOException, URISyntaxException {
    
    ObjectMapper objectMapper = getObjectMapper();
    File file = resolveFileFromResources(filename);
    PortfolioTrade[] trades = objectMapper.readValue(file, PortfolioTrade[].class);

    ArrayList<PortfolioTrade> ptd = new ArrayList<>();
    for (PortfolioTrade t : trades) {
      ptd.add(t);
    }
    return ptd;

  }

  // TODO:
  // Build the Url using given parameters and use this function in your code to cann the API.
  public static String prepareUrl(PortfolioTrade trade, LocalDate endDate, String token) {

    String url = String.format(
        "https://api.tiingo.com/tiingo/daily/%s/prices?startDate=%s&endDate=%s&token=%s",
         trade.getSymbol(), trade.getPurchaseDate(), endDate, token);

    return url;
    // return Collections.emptyList();

  }
  


  public static void main(String[] args) throws Exception {
    Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
    ThreadContext.put("runId", UUID.randomUUID().toString());

    // printJsonObject(mainReadFile(args));

    // printJsonObject(mainReadQuotes(args));
    List<PortfolioTrade> trades = readTradesFromJson("assessments/trades.json");
    System.out.println(trades);


  }
}


class TotalReturnsDtoComparator implements Comparator<TotalReturnsDto> {
  
  @Override
  public int compare(TotalReturnsDto candle1, TotalReturnsDto candle2) {
    if (candle1.getClosingPrice() == candle2.getClosingPrice()) {
      return 0;
    }
    else if (candle1.getClosingPrice() > candle2.getClosingPrice()) {
      return 1;
    }
    else {
      return -1;
    }
  }
}

