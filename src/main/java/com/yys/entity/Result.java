package com.yys.entity;


public class Result {

    int code;
    String msg;
    int count;
    Object data;

    public Result() {
    }

    public Result(int code, String msg, int count, Object data) {
        this.code = code;
        this.msg = msg;
        this.count = count;
        this.data = data;
    }

    public static Result success(int count,Object date){
        return new Result(200,"成功",count,date);
    }
    public static Result success(Object date){
        return new Result(200,"成功",0,date);
    }
    public static Result success(String msg,int count,Object date){
        return new Result(200,msg,count,date);
    }
    public static Result success(){
        return new Result(200,"成功",0,null);
    }
    public static Result error(String msg){
        return new Result(0,msg,0,null);
    }

    public static Result success(int code, String msg, int count, Object date) {
        return new Result(code,msg,count,date);
    }


    /**
     * 获取
     * @return code
     */
    public int getCode() {
        return code;
    }

    /**
     * 设置
     * @param code
     */


    /**
     * 获取
     * @return msg
     */
    public String getMsg() {
        return msg;
    }

    /**
     * 设置
     * @param msg
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }

    /**
     * 获取
     * @return count
     */
    public int getCount() {
        return count;
    }

    /**
     * 设置
     * @param count
     */
    public void setCount(int count) {
        this.count = count;
    }

    /**
     * 获取
     * @return data
     */
    public Object getData() {
        return data;
    }

    /**
     * 设置
     * @param data
     */
    public void setData(Object data) {
        this.data = data;
    }

    public String toString() {
        return "Result{code = " + code + ", msg = " + msg + ", count = " + count + ", data = " + data + "}";
    }
}
