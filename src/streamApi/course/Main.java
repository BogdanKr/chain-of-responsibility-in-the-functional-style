package streamApi.course;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class Main {

  /**
   * It represents a handler and has two methods: one for handling requests and other for combining
   * handlers
   */
  @FunctionalInterface
  interface RequestHandler {

    Request handle(Request req);

    // !!! write a method handle that accept request and returns new request here
    // it allows to use lambda expressions for creating handlers below
    default RequestHandler setSuccessor(RequestHandler requestHandler) {
      return (req) -> requestHandler.handle(handle(req));
    }
    // !!! write a default method for combining this and other handler single one
    // the order of execution may be any but you need to consider it when composing handlers
    // the method may has any name
  }

  /**
   * Accepts a request and returns new request with data wrapped in the tag
   * <transaction>...</transaction>
   */
  final static RequestHandler wrapInTransactionTag =
      (req) -> new Request(String.format("<transaction> \n %s \n </transaction>\n", req.getData()));

  /**
   * Accepts a request and returns a new request with calculated digest inside the tag
   * <digest>...</digest>
   */
  final static RequestHandler createDigest =
      (req) -> {
        String digest = "";
        try {

          digest = "CZVMYTgc3iiOdJjFP+6dhQ==";
        } catch (Exception ignored) {
        }
        return new Request(req.getData() + String.format("<digest>%s</digest>", digest));
      };

  /**
   * Accepts a request and returns a new request with data wrapped in the tag
   * <request>...</request>
   */
  final static RequestHandler wrapInRequestTag =
      (req) -> new Request(String.format("<request>\n%s\n</request>", req.getData()));

  /**
   * It should represents a chain of responsibility combined from another handlers. The format:
   * commonRequestHandler = handler1.setSuccessor(handler2.setSuccessor(...)) The combining method
   * setSuccessor may has another name
   */
  final static RequestHandler commonRequestHandler = wrapInTransactionTag.setSuccessor(createDigest)
      .setSuccessor(wrapInRequestTag);
// !!! write the combining of existing handlers here

  /**
   * Immutable class for representing requests. If you need to change the request data then create
   * new request.
   */
  static class Request {

    private final String data;

    public Request(String requestData) {
      this.data = requestData;
    }

    public String getData() {
      return data;
    }

    @Override
    public String toString() {
      return "Request{" +
          "data='" + data + '\'' +
          '}';
    }
  }

  public static void main(String[] args) {
    System.out.println(commonRequestHandler.handle(new Request("New request")).getData());
    BigInteger bigInteger = BigInteger.valueOf(7);
    boolean probablePrime = bigInteger.isProbablePrime((int) Math.log(7));
    System.out.println(probablePrime);

    long number = 12;
    boolean num = LongStream.rangeClosed(2, number).filter(x -> number % x == 0)
        .allMatch(x -> x == number);
    boolean num1 = LongStream.range(2, number).noneMatch(x -> number % x == 0);
    System.out.println(num1);
  }

  public static Stream<String> createBadWordsDetectingStream(String text, List<String> badWords) {
    return Arrays.stream(text.split(" "))
        .filter(badWords::contains)
        .distinct().sorted();
  }

  public static IntStream createFilteringStream(IntStream evenStream, IntStream oddStream) {
    List<Integer> newList = evenStream.boxed().collect(Collectors.toList());
    newList.addAll(oddStream.boxed().collect(Collectors.toList()));
    return newList.stream().mapToInt(Integer::new).sorted().skip(2).filter(x -> x % 3 == 0 && x % 5 == 0);
  }

  public static IntStream createFilteringStream2(IntStream evenStream, IntStream oddStream) {
    return IntStream.concat(evenStream, oddStream).filter(i -> i % 15 == 0).sorted().skip(2);
  }

}
