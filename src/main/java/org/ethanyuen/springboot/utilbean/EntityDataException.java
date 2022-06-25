package org.ethanyuen.springboot.utilbean;

/**
 * 实体数据异常：未找到、数量不对等
 */
public class EntityDataException extends RuntimeException{
    /*无参构造函数*/
    public EntityDataException(){
        super();
    }

    //用详细信息指定一个异常
    public EntityDataException(String message){
        super(message);
    }

    //用指定的详细信息和原因构造一个新的异常
    public EntityDataException(String message, Throwable cause){
        super(message,cause);
    }

    //用指定原因构造一个新的异常
    public EntityDataException(Throwable cause) {
        super(cause);
    }
}
