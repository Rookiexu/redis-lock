package cn.rookiex.redislock.exception;

/**
 * @Author : Rookiex
 * @Date : 2019/08/27
 * @Describe :
 */
public class TimeoutException  extends RuntimeException {
    public TimeoutException() {
    }

    public TimeoutException(String message) {
        super(message);
    }

    public TimeoutException(String message, Throwable cause) {
        super(message, cause);
    }
}
