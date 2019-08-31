package cn.rookiex.redislock.exception;

/**
 * @Author : Rookiex
 * @Date : 2019/08/27
 * @Describe :
 */
public class InvocationException extends RuntimeException {
    public InvocationException() {
    }

    public InvocationException(String message) {
        super(message);
    }

    public InvocationException(String message, Throwable cause) {
        super(message, cause);
    }


}
