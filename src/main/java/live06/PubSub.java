package live06;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * 요번 시간 주제
 * <p>
 * Publisher::pub -> [Data1] -> op1::mapPub -> [Data2] -> Subscriber::logSub
 * <p>
 * flow를 생각해보면...
 * <- subscribe(logSub)
 * -> onSubscribe(s)
 * -> onNext(n)
 * -> onNext(n)
 * -> ...
 * -> onComplete()
 * <p>
 * 용어로보면..
 * pub -> (downstream) -> sub
 * pub <- (upstream) <- sub
 *
 * @author chanwook
 */
public class PubSub {

    public static void main(String[] args) {
        Publisher<Integer> pub = iterPub(Stream.iterate(1, a -> a + 1).limit(10).collect(toList()));

        /**
         * Case 1.onNext() 호출이 전체 갯수와 동일
         */
//        Publisher<Integer> mapPub = mapPub(pub, s -> s * 10);
//        Publisher<Integer> map2Pub = mapPub(mapPub, s -> -s);
//        Publisher<String> mapPub = mapPub(pub, s -> "[" + s + "]");
//        mapPub.subscribe(logSub());

        /**
         * Case 2. onNext()를 sum한 값을 받도록
         */
//        Publisher<Integer> sumPub = sumPub(pub);
//        sumPub.subscribe(logSub());

        /**
         * Case 3. reduce
         */
//        Publisher<String> reducePub = reducePub(pub, "0", (a, b) -> a + "-" + b);
        Publisher<StringBuilder> reducePub = reducePub(pub, new StringBuilder(), (a, b) -> a.append(b + ","));
        reducePub.subscribe(logSub());
    }

    private static <T, R> Publisher<R> reducePub(Publisher<T> pub, R init, BiFunction<R, T, R> bf) {
        return new Publisher<R>() {
            @Override
            public void subscribe(Subscriber<? super R> sub) {

                pub.subscribe(new DelegateSubAdapter<T, R>(sub) {
                    R result = init;

                    @Override
                    public void onNext(T t) {
                        result = bf.apply(result, t);
                    }

                    @Override
                    public void onComplete() {
                        sub.onNext(result);
                        sub.onComplete();
                    }
                });
            }
        };
    }

//    private static Publisher<Integer> sumPub(Publisher<Integer> pub) {
//        return new Publisher<Integer>() {
//            @Override
//            public void subscribe(Subscriber<? super Integer> sub) {
//                pub.subscribe(new DelegateSubAdapter(sub) {
//
//                    int sum = 0;
//
//                    @Override
//                    public void onNext(Integer integer) {
//                        sum += integer;
//                        // 다음 publisher로 넘기지 않음..
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        sub.onNext(sum); //한 번 호출하고 끝.
//                        sub.onComplete();
//                    }
//                });
//            }
//        };
//    }

    // T -> R
    private static <T, R> Publisher<R> mapPub(Publisher<T> pub, Function<T, R> function) {
        return new Publisher<R>() {
            @Override
            public void subscribe(Subscriber<? super R> subscriber) {
                pub.subscribe(new DelegateSubAdapter<T, R>(subscriber) {
                    @Override
                    public void onNext(T t) {
                        subscriber.onNext(function.apply(t));
                    }
                });
            }
        };
    }

    private static <T> Subscriber<T> logSub() {
        return new Subscriber<T>() {
            @Override
            public void onSubscribe(Subscription s) {
                System.out.println("onSubscribe");
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(T t) {
                System.out.println("onNext: " + t);
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("onError: " + e);
            }

            @Override
            public void onComplete() {
                System.out.println("onComplete");
            }
        };
    }

    private static Publisher<Integer> iterPub(final List<Integer> iterable) {
        return new Publisher<Integer>() {
            @Override
            public void subscribe(Subscriber<? super Integer> subscriber) {
                subscriber.onSubscribe(new Subscription() {
                    @Override
                    public void request(long n) {
                        try {
                            iterable.forEach(s -> subscriber.onNext(s));
                            subscriber.onComplete();
                        } catch (Exception e) {
                            subscriber.onError(e);
                        }
                    }

                    @Override
                    public void cancel() {

                    }
                });
            }
        };
    }
}
