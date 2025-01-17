package com.gieman.tttracker.vo;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class Result<T> implements Serializable {

    final private boolean success;
    final private T data;
    final private String msg;

    Result(boolean success, T data) {
        this.success = success;
        this.data = data;
        this.msg = null;
    }

    Result(boolean success, String msg) {
        this.success = success;
        this.data = null;
        this.msg = msg;
    }
    
    public boolean isSuccess() {
        return success;
    }

    public T getData() {
        return data;
    }

    public String getMsg() {
        return msg;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder("\"Result{\"");
        sb.append("success=").append(success);
        sb.append(", msg=").append(msg);

        sb.append(", data=");

        if(data == null){

            sb.append("null");

        } else if(data instanceof List){

            List castList = (List) data;
            if(castList.isEmpty()){

                sb.append("empty list");

            } else {
                Object firstItem = castList.get(0);

                sb.append("List of ").append(firstItem.getClass());
            }

        } else {
            sb.append(data.toString());
        }

        sb.append("}");

        return sb.toString();

    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (this.success ? 1 : 0);
        hash = 89 * hash + Objects.hashCode(this.data);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Result<?> other = (Result<?>) obj;
        if (this.success != other.success) {
            return false;
        }
        return Objects.deepEquals(this.data, other.data);
    }
}
